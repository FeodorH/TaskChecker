package com.example.myFirstSpringProject

import com.example.myFirstSpringProject.model.ThemeEntity
import com.example.myFirstSpringProject.repository.ThemeRepository
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@DataJpaTest
class ThemeRepositoryTest {

    @Autowired
    private lateinit var themeRepository: ThemeRepository

    private val now = LocalDateTime.now()

    @BeforeEach
    fun setUp() {
        themeRepository.deleteAll()
    }

    @Test
    fun findByThemeTitleTest() {
        val theme = ThemeEntity(themeTitle = "Work", description = "Work tasks", updateAt = now)
        themeRepository.save(theme)

        val found = themeRepository.findByThemeTitle("Work")

        assertNotNull(found)
        assertEquals("Work", found?.themeTitle)
        assertEquals("Work tasks", found?.description)
    }

    @Test
    fun findByThemeTitleNotFoundTest() {
        val found = themeRepository.findByThemeTitle("Unknown")
        assertNull(found)
    }

    @Test
    fun existsByThemeTitleAndDescriptionTest() {
        val theme = ThemeEntity(themeTitle = "Home", description = "Home chores", updateAt = now)
        themeRepository.save(theme)

        val exists = themeRepository.existsByThemeTitleAndDescription("Home", "Home chores")
        assertTrue(exists)
    }

    @Test
    fun existsByThemeTitleAndDescriptionNotFoundTest() {
        val exists = themeRepository.existsByThemeTitleAndDescription("Home", "Different desc")
        assertFalse(exists)
    }

    @Test
    fun existsByThemeTitleAndDescriptionNullDescriptionTest() {
        val theme = ThemeEntity(themeTitle = "NullDesc", description = null, updateAt = now)
        themeRepository.save(theme)

        val exists = themeRepository.existsByThemeTitleAndDescription("NullDesc", null)
        assertTrue(exists)
    }
}