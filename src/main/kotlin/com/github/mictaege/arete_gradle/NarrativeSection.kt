package com.github.mictaege.arete_gradle

import com.github.mictaege.arete.Narrative
import java.io.File
import java.io.IOException
import java.io.UncheckedIOException
import java.net.URI

class NarrativeSection(annotation: Narrative) {
    val header = annotation.header
    val lines = annotation.value.toList()
    val images: List<Image> = annotation.imageResourcePath
        .filter { it.isNotBlank() }
        .map { Image(it) }
    val hasImages: Boolean = images.isNotEmpty()
}

class Image(val imagePath: String) {
    val imageUri: URI? = imagePath.let { javaClass.classLoader?.getResource(it)?.toURI() }
    val imageFileName: String? = imageUri?.let { File(it) }?.name

    fun readImage(): ByteArray? {
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