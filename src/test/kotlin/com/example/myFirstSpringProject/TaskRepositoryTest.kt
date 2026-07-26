package com.example.myFirstSpringProject

import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private lateinit var repository: TaskRepository

    private val now = LocalDateTime.now()

    @BeforeEach
    fun setUp() {
        repository.deleteAll()
    }

    @Test
    fun findAllThemesTest() {
        val task1 = TaskEntity(
            title = "Task1", theme = "Work", author = "John",
            description = "Desc1", isStarted = true, updateAt = now
        )
        val task2 = TaskEntity(
            title = "Task2", theme = "Work", author = "Jane",
            description = "Desc2", isStarted = false, updateAt = now
        )
        val task3 = TaskEntity(
            title = "Task3", theme = "Home", author = "Bob",
            description = "Desc3", isStarted = true, updateAt = now
        )
        repository.saveAll(listOf(task1, task2, task3))

        val result = repository.findAllThemes()

        assertEquals(2, result.size)
        assertTrue(result.containsAll(listOf("Work", "Home")))
    }

    @Test
    fun findByTitleTest() {
        val task1 = TaskEntity(
            title = "Learn Kotlin", theme = "Education", author = "Alex",
            description = "Study", isStarted = true, updateAt = now
        )
        val task2 = TaskEntity(
            title = "Learn Spring", theme = "Education", author = "Alex",
            description = "Study", isStarted = false, updateAt = now
        )
        val task3 = TaskEntity(
            title = "Learn Kotlin", theme = "Work", author = "Maria",
            description = "Practice", isStarted = false, updateAt = now
        )
        repository.saveAll(listOf(task1, task2, task3))

        val result = repository.findByTitle("Learn Kotlin")

        assertEquals(2, result.size)
        assertEquals("Learn Kotlin", result[0].title)
        assertEquals("Learn Kotlin", result[1].title)
        assertEquals(setOf("Education", "Work"), result.map { it.theme }.toSet())
    }

    @Test
    fun findByThemeTest() {
        val task1 = TaskEntity(
            title = "Task1", theme = "Work", author = "John",
            description = "Desc1", isStarted = true, updateAt = now
        )
        val task2 = TaskEntity(
            title = "Task2", theme = "Home", author = "Jane",
            description = "Desc2", isStarted = false, updateAt = now
        )
        repository.saveAll(listOf(task1, task2))

        val result = repository.findByTheme("Home")

        assertEquals(1, result.size)
        assertEquals("Home", result[0].theme)
    }

    @Test
    fun findByAuthorTest() {
        val task1 = TaskEntity(
            title = "Task1", theme = "Work", author = "John",
            description = "Desc1", isStarted = true, updateAt = now
        )
        val task2 = TaskEntity(
            title = "Task2", theme = "Home", author = "Jane",
            description = "Desc2", isStarted = false, updateAt = now
        )
        val task3 = TaskEntity(
            title = "Task3", theme = "Work", author = null,
            description = "Desc3", isStarted = false, updateAt = now
        )
        repository.saveAll(listOf(task1, task2, task3))

        val result = repository.findByAuthor("John")

        assertEquals(1, result.size)
        assertEquals("John", result[0].author)
    }

    @Test
    fun findByAuthorNullTest() {
        val task1 = TaskEntity(
            title = "Task1", theme = "Work", author = null,
            description = "Desc1", isStarted = true, updateAt = now
        )
        val task2 = TaskEntity(
            title = "Task2", theme = "Home", author = "Jane",
            description = "Desc2", isStarted = false, updateAt = now
        )
        repository.saveAll(listOf(task1, task2))

        val result = repository.findByAuthor(null)

        assertEquals(1, result.size)
        assertNull(result[0].author)
    }

    @Test
    fun findByIsStartedTrueTest() {
        val task1 = TaskEntity(
            title = "Task1", theme = "Work", author = "John",
            description = "Desc1", isStarted = true, updateAt = now
        )
        val task2 = TaskEntity(
            title = "Task2", theme = "Home", author = "Jane",
            description = "Desc2", isStarted = false, updateAt = now
        )
        repository.saveAll(listOf(task1, task2))

        val result = repository.findByIsStartedTrue()

        assertEquals(1, result.size)
        assertTrue(result[0].isStarted)
    }

    @Test
    fun existsByTitleAndThemeAndAuthorAndDescriptionTest() {
        val task = TaskEntity(
            title = "Unique", theme = "Test", author = "Sam",
            description = "Check", isStarted = false, updateAt = now
        )
        repository.save(task)

        val exists = repository.existsByTitleAndThemeAndAuthorAndDescription(
            title = "Unique",
            theme = "Test",
            author = "Sam",
            description = "Check"
        )

        assertTrue(exists)
    }

    @Test
    fun existsByTitleAndThemeAndAuthorAndDescriptionNotFoundTest() {
        val exists = repository.existsByTitleAndThemeAndAuthorAndDescription(
            title = "Nonexistent",
            theme = "Test",
            author = "Sam",
            description = "Check"
        )

        assertFalse(exists)
    }

    @Test
    fun existsByTitleAndThemeAndAuthorAndDescriptionNullAuthorTest() {
        val task = TaskEntity(
            title = "NullAuthor", theme = "Test", author = null,
            description = "Check", isStarted = false, updateAt = now
        )
        repository.save(task)

        val exists = repository.existsByTitleAndThemeAndAuthorAndDescription(
            title = "NullAuthor",
            theme = "Test",
            author = null,
            description = "Check"
        )

        assertTrue(exists)
    }
}