package com.github.mictaege.arete_gradle

import com.github.mictaege.arete.Narrative
import net.sourceforge.plantuml.FileFormat
import net.sourceforge.plantuml.FileFormatOption
import net.sourceforge.plantuml.SourceStringReader
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.UncheckedIOException
import java.net.URI
import java.util.*

class NarrativeSection(annotation: Narrative) {
    val header = annotation.header
    val lines = annotation.value.toList()

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
    override val fileName: String = "diagram-${UUID.randomUUID()}.svg"
    override fun readResource(): ByteArray {
        try {
            val effectiveSrc = ensureSmetana(ensureTheme(diagramSrc))
            val reader = SourceStringReader(effectiveSrc)
            val outputStream = ByteArrayOutputStream()
            val fileFormatOption = FileFormatOption(FileFormat.SVG)
            reader.outputImage(outputStream, fileFormatOption)
            return outputStream.toByteArray()
        } catch (e: IOException) {
            throw UncheckedIOException("Failed to generate PlantUML diagram", e)
        }
    }

    private fun ensureTheme(src: String): String = if (src.contains("!theme")) {
        src
    } else {
        "@startuml\n!theme " + AretePlugin.colorScheme.arete_plantuml_theme + "\n" + src.removePrefix("@startuml\n")
    }

    private fun ensureSmetana(src: String): String = if (src.contains("!pragma layout smetana")) {
        src
    } else {
        "@startuml\n!pragma layout smetana\n" + src.removePrefix("@startuml\n")
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
