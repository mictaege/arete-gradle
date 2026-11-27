package com.github.mictaege.arete_gradle

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RefsGroupingTest {

    private fun link(value: String, url: String, project: String = "") = RefLink(value, url, project)

    @Test
    fun groupsRefsByProjectAttribute() {
        val groups = groupRefLinks(
            listOf(
                link("PA-1", "https://mytracker.org/PA-1", "mytracker.org/PA-"),
                link("PA-2", "https://mytracker.org/PA-2", "mytracker.org/PA-"),
                link("PB-3", "https://mytracker.org/PB-3", "mytracker.org/PB-"),
            )
        )

        assertEquals(2, groups.size)
        val paGroup = groups.first { it.key == "mytracker.org/PA-" }
        assertEquals(listOf("PA-1", "PA-2"), paGroup.links.map { it.value })
        val pbGroup = groups.first { it.key == "mytracker.org/PB-" }
        assertEquals(listOf("PB-3"), pbGroup.links.map { it.value })
    }

    @Test
    fun projectGroupsRefsEvenAcrossDifferentUrls() {
        val groups = groupRefLinks(
            listOf(
                link("PA-1", "https://mytracker.org/PA-1", "PA"),
                link("PA-2", "https://other.org/PA-2", "PA"),
                link("PB-3", "https://mytracker.org/PB-3", "PB"),
            )
        )

        assertEquals(2, groups.size)
        assertEquals(listOf("PA-1", "PA-2"), groups.first { it.key == "PA" }.links.map { it.value }.sorted())
    }

    @Test
    fun fallsBackToLongestCommonPrefixWhenProjectIsBlank() {
        val groups = groupRefLinks(
            listOf(
                link("PA-1", "https://mytracker.org/PA-1"),
                link("PA-2", "https://mytracker.org/PA-2"),
                link("PB-3", "https://mytracker.org/PB-3"),
            )
        )

        assertEquals(2, groups.size)
        val paGroup = groups.first { it.key == "https://mytracker.org/PA-" }
        assertEquals(listOf("PA-1", "PA-2"), paGroup.links.map { it.value })
        assertEquals("https://mytracker.org/PB-3", groups[1].key)
    }

    @Test
    fun neverFallsBackBelowTheLastSlashBoundary() {
        val groups = groupRefLinks(
            listOf(
                link("x-1", "https://a.org/x-1"),
                link("y-2", "https://b.org/y-2"),
            )
        )

        assertEquals(2, groups.size)
        assertEquals("https://a.org/x-1", groups[0].key)
        assertEquals("https://b.org/y-2", groups[1].key)
    }

    @Test
    fun linksWithoutProjectAndUrlFormSeparateEmptyKeyGroup() {
        val groups = groupRefLinks(
            listOf(
                link("PROJ-123", ""),
                link("PA-1", "https://mytracker.org/PA-1", "mytracker.org/PA-"),
            )
        )

        assertEquals(2, groups.size)
        assertEquals("mytracker.org/PA-", groups[0].key)
        assertEquals("", groups[1].key)
        assertEquals(listOf("PROJ-123"), groups[1].links.map { it.value })
    }

    @Test
    fun emptyInputYieldsNoGroups() {
        assertEquals(emptyList<RefGroup>(), groupRefLinks(emptyList()))
    }

    @Test
    fun refLinkComparesByProjectThenUrlThenValue() {
        val sorted = listOf(
            link("b", "https://x.org/", "p2"),
            link("a", "https://x.org/"),
            link("a", "https://a.org/", "p1"),
        ).sorted()

        assertEquals(
            listOf(link("a", "https://x.org/"), link("a", "https://a.org/", "p1"), link("b", "https://x.org/", "p2")),
            sorted,
        )
    }
}
