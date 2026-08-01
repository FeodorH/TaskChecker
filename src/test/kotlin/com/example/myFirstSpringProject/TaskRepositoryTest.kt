package com.example.myFirstSpringProject

import com.example.myFirstSpringProject.model.TaskEntity
import com.example.myFirstSpringProject.model.ThemeEntity
import com.example.myFirstSpringProject.repository.TaskRepository
import com.example.myFirstSpringProject.repository.ThemeRepository
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
    private lateinit var taskRepository: TaskRepository
    @Autowired
    private lateinit var themeRepository: ThemeRepository

    private val now = LocalDateTime.now()

    @BeforeEach
    fun setUp() {
        taskRepository.deleteAll()
        themeRepository.deleteAll()
    }

    private fun createTheme(title: String): ThemeEntity =
        themeRepository.save(ThemeEntity(themeTitle = title))

    @Test
    fun findByTitleTest() {
        val theme1 = createTheme("Education")
        val theme2 = createTheme("Work")

        val task1 = TaskEntity(
            title = "Learn Kotlin",
            theme = theme1,
            author = "Alex",
            description = "Study",
            isStarted = true,
            updateAt = now
        )
        val task2 = TaskEntity(title = "Learn Spring", theme = theme1, author = "Alex", description = "Study", isStarted = false, updateAt = now)
        val task3 = TaskEntity(title = "Learn Kotlin", theme = theme2, author = "Maria", description = "Practice", isStarted = false, updateAt = now)

        taskRepository.saveAll(listOf(task1, task2, task3))

        val result = taskRepository.findByTitle("Learn Kotlin")

        assertEquals(2, result.size)
        assertEquals("Learn Kotlin", result[0].title)
        assertEquals("Learn Kotlin", result[1].title)
        assertEquals(setOf("Education", "Work"), result.map { it.theme.themeTitle }.toSet())
    }

    @Test
    fun findByTheme_ThemeTitleTest() {
        val themeWork = createTheme("Work")
        val themeHome = createTheme("Home")

        val task1 = TaskEntity(title = "Task1", theme = themeWork, author = "John", description = "Desc1", isStarted = true, updateAt = now)
        val task2 = TaskEntity(title = "Task2", theme = themeHome, author = "Jane", description = "Desc2", isStarted = false, updateAt = now)
        taskRepository.saveAll(listOf(task1, task2))

        val result = taskRepository.findByTheme_ThemeTitle("Home")

        assertEquals(1, result.size)
        assertEquals("Home", result[0].theme.themeTitle)
    }

    @Test
    fun findByAuthorTest() {
        val theme = createTheme("Work")
        val task1 = TaskEntity(title = "Task1", theme = theme, author = "John", description = "Desc1", isStarted = true, updateAt = now)
        val task2 = TaskEntity(title = "Task2", theme = theme, author = "Jane", description = "Desc2", isStarted = false, updateAt = now)
        val task3 = TaskEntity(title = "Task3", theme = theme, author = null, description = "Desc3", isStarted = false, updateAt = now)
        taskRepository.saveAll(listOf(task1, task2, task3))

        val result = taskRepository.findByAuthor("John")

        assertEquals(1, result.size)
        assertEquals("John", result[0].author)
    }

    @Test
    fun findByAuthorNullTest() {
        val theme = createTheme("Work")
        val task1 = TaskEntity(title = "Task1", theme = theme, author = null, description = "Desc1", isStarted = true, updateAt = now)
        val task2 = TaskEntity(title = "Task2", theme = theme, author = "Jane", description = "Desc2", isStarted = false, updateAt = now)
        taskRepository.saveAll(listOf(task1, task2))

        val result = taskRepository.findByAuthor(null)

        assertEquals(1, result.size)
        assertNull(result[0].author)
    }

    @Test
    fun findByIsStartedTrueTest() {
        val theme = createTheme("Work")
        val task1 = TaskEntity(title = "Task1", theme = theme, author = "John", description = "Desc1", isStarted = true, updateAt = now)
        val task2 = TaskEntity(title = "Task2", theme = theme, author = "Jane", description = "Desc2", isStarted = false, updateAt = now)
        taskRepository.saveAll(listOf(task1, task2))

        val result = taskRepository.findByIsStartedTrue()

        assertEquals(1, result.size)
        assertTrue(result[0].isStarted)
    }

    @Test
    fun existsByTitleAndAuthorAndDescriptionTest() {
        val theme = createTheme("Test")
        val task = TaskEntity(title = "Unique", theme = theme, author = "Sam", description = "Check", isStarted = false, updateAt = now)
        taskRepository.save(task)

        val exists = taskRepository.existsByTitleAndAuthorAndDescription(
            title = "Unique",
            author = "Sam",
            description = "Check"
        )

        assertTrue(exists)
    }

    @Test
    fun existsByTitleAndAuthorAndDescriptionNotFoundTest() {
        val exists = taskRepository.existsByTitleAndAuthorAndDescription(
            title = "Nonexistent",
            author = "Sam",
            description = "Check"
        )

        assertFalse(exists)
    }

    @Test
    fun existsByTitleAndAuthorAndDescriptionNullAuthorTest() {
        val theme = createTheme("Test")
        val task = TaskEntity(title = "NullAuthor", theme = theme, author = null, description = "Check", isStarted = false, updateAt = now)
        taskRepository.save(task)

        val exists = taskRepository.existsByTitleAndAuthorAndDescription(
            title = "NullAuthor",
            author = null,
            description = "Check"
        )

        assertTrue(exists)
    }
}