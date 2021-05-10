package com.github.mictaege.arete_gradle

import java.io.File

fun File.createAndWrite(content: String) {
    if (!this.parentFile.exists()) {
        this.parentFile.mkdirs()
    }
    if(this.exists()) {
        this.delete()
    }
    this.createNewFile()
    this.writeText(content)
}