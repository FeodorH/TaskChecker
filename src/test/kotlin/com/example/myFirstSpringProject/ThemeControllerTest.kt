package com.example.myFirstSpringProject

import com.example.myFirstSpringProject.controller.ThemeController
import com.example.myFirstSpringProject.model.ThemeRequest
import com.example.myFirstSpringProject.model.ThemeResponse
import com.example.myFirstSpringProject.service.ThemeService
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import jakarta.persistence.EntityExistsException
import jakarta.persistence.EntityNotFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@ExtendWith(MockKExtension::class)
class ThemeControllerTest {
    @MockK
    lateinit var themeService: ThemeService
    lateinit var themeController: ThemeController

    @BeforeEach
    fun setUp() {
        themeController = ThemeController(themeService)
    }

    @Test
    fun getAllThemesTest() {
        val now = LocalDateTime.now()
        val expected = listOf(
            ThemeResponse(1, "Theme1", null, now),
            ThemeResponse(2, "Theme2", "Desc", now)
        )
        every { themeService.getAllThemes() } returns expected

        val result = themeController.getAllThemes()

        assertEquals(expected, result)
        verify { themeService.getAllThemes() }
    }

    @Test
    fun getThemeByIdTest() {
        val id = 1L
        val now = LocalDateTime.now()
        val expected = ThemeResponse(id, "Theme1", null, now)
        every { themeService.getThemeById(id) } returns expected

        val response = themeController.getThemeById(id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expected, response.body)
        verify { themeService.getThemeById(id) }
    }

    @Test
    fun getThemeByIdErrorTest() {
        val id = 999L
        every { themeService.getThemeById(id) } throws EntityNotFoundException("Theme not found")

        assertThrows<EntityNotFoundException> {
            themeController.getThemeById(id)
        }
    }

    @Test
    fun postThemeTest() {
        val now = LocalDateTime.now()
        val request = ThemeRequest("New Theme", "Desc")
        val saved = ThemeResponse(1, "New Theme", "Desc", now)
        every { themeService.postTheme(request) } returns saved

        val response = themeController.postTheme(request)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(saved, response.body)
        verify { themeService.postTheme(request) }
    }

    @Test
    fun postThemeAlreadyExistsTest() {
        val request = ThemeRequest("Existing", "Desc")
        every { themeService.postTheme(request) } throws EntityExistsException("Theme already exists")

        assertThrows<EntityExistsException> {
            themeController.postTheme(request)
        }
    }

    @Test
    fun putThemeTest() {
        val id = 1L
        val now = LocalDateTime.now()
        val request = ThemeRequest("Updated", "New Desc")
        val updated = ThemeResponse(id, "Updated", "New Desc", now)
        every { themeService.putThemeById(id, request) } returns updated

        val response = themeController.putTheme(id, request)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(updated, response.body)
        verify { themeService.putThemeById(id, request) }
    }

    @Test
    fun putThemeErrorTest() {
        val id = 999L
        val request = ThemeRequest("Updated", "Desc")
        every { themeService.putThemeById(id, request) } throws EntityNotFoundException("Theme not found")

        assertThrows<EntityNotFoundException> {
            themeController.putTheme(id, request)
        }
    }

    @Test
    fun deleteThemeTest() {
        val id = 1L
        every { themeService.deleteThemeById(id) } returns Unit

        val response = themeController.deleteThemeById(id)

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
        assertNull(response.body)
        verify { themeService.deleteThemeById(id) }
    }

    @Test
    fun deleteThemeErrorTest() {
        val id = 999L
        every { themeService.deleteThemeById(id) } throws EntityNotFoundException("Theme not found")

        assertThrows<EntityNotFoundException> {
            themeController.deleteThemeById(id)
        }
    }
}