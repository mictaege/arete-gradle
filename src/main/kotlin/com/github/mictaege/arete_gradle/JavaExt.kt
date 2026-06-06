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

fun List<TestTag>.compareListOfTags(other: List<TestTag>): Int {
    val commonSize = minOf(size, other.size)

    for (index in 0 until commonSize) {
        val comparison = this[index].compareTo(other[index])
        if (comparison != 0) {
            return comparison
        }
    }

    return size.compareTo(other.size)
}