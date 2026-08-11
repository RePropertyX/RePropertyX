package com.github.repropertyx.android

import androidx.lifecycle.SavedStateHandle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SavedStatePropertyXTest {

    @Test
    fun `SavedStateHandle byProperty delegates read and write operations`() {
        val savedStateHandle = SavedStateHandle()
        var searchFilter: String? by savedStateHandle.byProperty("filter")
        var pageIndex: Int by savedStateHandle.byProperty("page", default = 1)

        assertNull(searchFilter)
        assertEquals(1, pageIndex)

        searchFilter = "Android"
        pageIndex = 2

        assertEquals("Android", searchFilter)
        assertEquals(2, pageIndex)
        assertEquals("Android", savedStateHandle.get<String>("filter"))
        assertEquals(2, savedStateHandle.get<Int>("page"))
    }
}
