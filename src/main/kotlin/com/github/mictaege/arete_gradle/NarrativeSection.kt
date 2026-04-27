package com.github.mictaege.arete_gradle

import com.github.mictaege.arete.Narrative
import net.sourceforge.plantuml.FileFormat
import net.sourceforge.plantuml.FileFormatOption
import net.sourceforge.plantuml.SourceStringReader
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.UncheckedIOException
import java.net.URI
import java.util.*

class NarrativeSection(annotation: Narrative) {
    val header = annotation.header
    val lines = annotation.value.toList()
    
    private val parser = Parser.builder().build()
    private val renderer = HtmlRenderer.builder().build()

    val formattedLines = lines.map { l -> 
        val document = parser.parse(l.trim())
        renderer.render(document).trim()
    }

    val pictures: List<Picture> = annotation.imageResourcePath
        .filter { it.isNotBlank() }
        .map { Picture(it) }
    val hasPictures: Boolean = pictures.isNotEmpty()

    val diagrams: List<PlantUmlDiagram> = annotation.plantUml
        .filter { it.isNotBlank() }
        .map { PlantUmlDiagram(it.trimIndent()) }
    val hasDiagrams: Boolean = diagrams.isNotEmpty()

    val images: List<Image> = pictures + diagrams
    val hasImages: Boolean = images.isNotEmpty()

    val attachments: List<Attachment> = annotation.attachmentResourcePath
        .filter { it.isNotBlank() }
        .map { Attachment(it) }
    val hasAttachments: Boolean = attachments.isNotEmpty()

    val resources: List<Resource> = images + attachments
    val hasResource: Boolean = resources.isNotEmpty()
}

interface Resource {
    val fileName: String
    fun readResource(): ByteArray
}

interface Image: Resource

class Picture(val imagePath: String): Image {
    val imageUri: URI? = imagePath.let { javaClass.classLoader?.getResource(it)?.toURI() }
    override val fileName: String = imageUri?.let { File(it) }?.name ?: imagePath.split("/").last()

    override fun readResource(): ByteArray {
        imagePath.let { path ->
            val inputStream = javaClass.classLoader?.getResourceAsStream(path)
            requireNotNull(inputStream) { "Image not found: $path" }
            try {
                return inputStream.readAllBytes()
            } catch (e: IOException) {
                throw UncheckedIOException("Failed to read image: $path", e)
            }
        }
    }
}

class PlantUmlDiagram(val diagramSrc: String): Image {
    override val fileName: String = "diagram-${UUID.randomUUID()}.png"
    override fun readResource(): ByteArray {
        try {
            val effectiveSrc = ensureMetadata(diagramSrc)
            val reader = SourceStringReader(effectiveSrc)
            val outputStream = ByteArrayOutputStream()
            val fileFormatOption = FileFormatOption(FileFormat.PNG)
            reader.outputImage(outputStream, fileFormatOption)
            return outputStream.toByteArray()
        } catch (e: IOException) {
            throw UncheckedIOException("Failed to generate PlantUML diagram", e)
        }
    }

    private fun ensureMetadata(src: String): String {
        val metadata = listOf(
            "!pragma layout smetana",
            "!theme ${AreteColorSchemeProvider.colorScheme.arete_plantuml_theme}",
            "scale 32"
        )

        val metadataLineRegex = Regex("""^\s*(!pragma\s+layout\b.*|!theme\b.*|scale\b.*)\s*$""")
        val startLineRegex = Regex("""^\s*@start\w*\b.*$""")

        val lines = src.lines()
            .filterNot { it.matches(metadataLineRegex) }
            .toMutableList()

        val startIndex = lines.indexOfFirst { it.matches(startLineRegex) }

        return if (startIndex >= 0) {
            lines.addAll(startIndex + 1, metadata)
            lines.joinToString("\n")
        } else {
            (metadata + lines).joinToString("\n")
        }
    }
}

class Attachment(val filePath: String): Resource {
    val fileUri: URI? = filePath.let { javaClass.classLoader?.getResource(it)?.toURI() }
    override val fileName: String = fileUri?.let { File(it) }?.name ?: filePath.split("/").last()

    override fun readResource(): ByteArray {
        filePath.let { path ->
            val inputStream = javaClass.classLoader?.getResourceAsStream(path)
            requireNotNull(inputStream) { "File not found: $path" }
            try {
                return inputStream.readAllBytes()
            } catch (e: IOException) {
                throw UncheckedIOException("Failed to read file: $path", e)
            }
        }
    }
}
