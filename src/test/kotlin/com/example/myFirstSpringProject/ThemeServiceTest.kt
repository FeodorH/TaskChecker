package com.example.myFirstSpringProject

import com.example.myFirstSpringProject.model.ThemeEntity
import com.example.myFirstSpringProject.model.ThemeRequest
import com.example.myFirstSpringProject.model.ThemeResponse
import com.example.myFirstSpringProject.repository.ThemeRepository
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
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime
import java.util.Optional
import kotlin.test.Test
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class ThemeServiceTest {
    @MockK
    lateinit var themeRepository: ThemeRepository
    lateinit var themeService: ThemeService

    private val now = LocalDateTime.now()

    @BeforeEach
    fun setUp() {
        themeService = ThemeService(themeRepository)
    }

    @Test
    fun getAllThemesTest() {
        val now = LocalDateTime.now()
        val entities = listOf(
            ThemeEntity(id = 1, themeTitle = "Theme1", updateAt = now),
            ThemeEntity(id = 2, themeTitle = "Theme2", description = "Desc", updateAt = now)
        )
        val pageable: Pageable = PageRequest.of(0, 10)
        val entityPage = PageImpl(entities, pageable, entities.size.toLong())

        val expectedContent = listOf(
            ThemeResponse(1, "Theme1", null, now),
            ThemeResponse(2, "Theme2", "Desc", now)
        )
        val expectedPage = PageImpl(expectedContent, pageable, expectedContent.size.toLong())

        every { themeRepository.findAll(pageable) } returns entityPage

        val result: Page<ThemeResponse> = themeService.getAllThemes(pageable)

        assertEquals(expectedPage.content, result.content)
        assertEquals(expectedPage.totalElements, result.totalElements)
        verify(exactly = 1) { themeRepository.findAll(pageable) }
    }

    @Test
    fun getThemeByIdTest() {
        val id = 1L
        val entity = ThemeEntity(id = id, themeTitle = "Theme1", updateAt = now)
        val expected = ThemeResponse(id, "Theme1", null, now)
        every { themeRepository.findById(id) } returns Optional.of(entity)

        val result = themeService.getThemeById(id)

        assertEquals(expected, result)
        verify(exactly = 1) { themeRepository.findById(id) }
    }

    @Test
    fun getThemeByIdErrorTest() {
        val id = 999L
        every { themeRepository.findById(id) } returns Optional.empty()

        val exception = assertThrows<EntityNotFoundException> {
            themeService.getThemeById(id)
        }

        assertEquals("Theme with id $id not found", exception.message)
        verify(exactly = 1) { themeRepository.findById(id) }
    }

    @Test
    fun postThemeTest() {
        val request = ThemeRequest("New Theme", "Desc")
        val entity = ThemeEntity(id = 1, themeTitle = "New Theme", description = "Desc", updateAt = now)
        val expected = ThemeResponse(1, "New Theme", "Desc", now)

        every { themeRepository.existsByThemeTitleAndDescription(request.themeTitle, request.description) } returns false
        every { themeRepository.save(any()) } returns entity

        val result = themeService.postTheme(request)

        assertEquals(expected, result)
        verify(exactly = 1) { themeRepository.existsByThemeTitleAndDescription(request.themeTitle, request.description) }
        verify(exactly = 1) { themeRepository.save(any()) }
    }

    @Test
    fun postThemeAlreadyExistsTest() {
        val request = ThemeRequest("Existing", "Desc")
        every { themeRepository.existsByThemeTitleAndDescription(request.themeTitle, request.description) } returns true

        val exception = assertThrows<EntityExistsException> {
            themeService.postTheme(request)
        }

        assertEquals("Theme already exists", exception.message)
        verify(exactly = 1) { themeRepository.existsByThemeTitleAndDescription(request.themeTitle, request.description) }
        verify(exactly = 0) { themeRepository.save(any()) }
    }

    @Test
    fun putThemeByIdTest() {
        val id = 1L
        val request = ThemeRequest("Updated", "New Desc")
        val existing = ThemeEntity(id = id, themeTitle = "Old", description = "Old Desc", updateAt = now)
        val updated = ThemeEntity(id = id, themeTitle = "Updated", description = "New Desc", updateAt = now)
        val expected = ThemeResponse(id, "Updated", "New Desc", now)

        every { themeRepository.findById(id) } returns Optional.of(existing)
        every { themeRepository.save(any()) } returns updated

        val result = themeService.putThemeById(id, request)

        assertEquals(expected, result)
        verify(exactly = 1) { themeRepository.findById(id) }
        verify(exactly = 1) { themeRepository.save(any()) }
    }

    @Test
    fun putThemeByIdErrorTest() {
        val id = 999L
        val request = ThemeRequest("Updated", "Desc")
        every { themeRepository.findById(id) } returns Optional.empty()

        val exception = assertThrows<EntityNotFoundException> {
            themeService.putThemeById(id, request)
        }

        assertEquals("Theme with ID $id not found", exception.message)
        verify(exactly = 1) { themeRepository.findById(id) }
        verify(exactly = 0) { themeRepository.save(any()) }
    }

    @Test
    fun deleteThemeByIdTest() {
        val id = 1L
        every { themeRepository.existsById(id) } returns true
        every { themeRepository.deleteById(id) } returns Unit

        themeService.deleteThemeById(id)

        verify(exactly = 1) { themeRepository.existsById(id) }
        verify(exactly = 1) { themeRepository.deleteById(id) }
    }

    @Test
    fun deleteThemeByIdErrorTest() {
        val id = 999L
        every { themeRepository.existsById(id) } returns false

        val exception = assertThrows<EntityNotFoundException> {
            themeService.deleteThemeById(id)
        }

        assertEquals("Theme with ID $id not found", exception.message)
        verify(exactly = 1) { themeRepository.existsById(id) }
        verify(exactly = 0) { themeRepository.deleteById(id) }
    }
}