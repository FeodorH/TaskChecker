package com.example.myFirstSpringProject

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import jakarta.persistence.EntityExistsException
import jakarta.persistence.EntityNotFoundException
import org.hibernate.validator.internal.util.Contracts.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime
import java.util.Optional
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExtendWith(MockKExtension::class)
class TaskServiceTest {
    @MockK
    lateinit var taskRepository: TaskRepository
    lateinit var taskService: TaskService

    val now = LocalDateTime.now()

    @BeforeEach
    fun setUp() {
        taskService = TaskService(taskRepository)
    }

    @Test
    fun getAllTasksTest() {
        val expected = listOf(
            TaskResponse(1L, "Task1", "Theme1", "Author1", "Desc1", true, now),
            TaskResponse(2L, "Task2", "Theme2", null, "Desc2", false, now)
        )

        val entities = listOf(
            TaskEntity(
                id = 1,
                title = "Task1",
                theme = "Theme1",
                author = "Author1",
                description = "Desc1",
                isStarted = true,
                updateAt = now
            ),
            TaskEntity(
                id = 2,
                title = "Task2",
                theme = "Theme2",
                author = null,
                description = "Desc2",
                isStarted = false,
                updateAt = now
            )
        )
        every { taskRepository.findAll() } returns entities

        val result = taskService.getAllTasks()

        assertEquals(expected, result)
        verify(exactly = 1) { taskRepository.findAll() }
    }

    @Test
    fun getAllThemesTest() {
        val expected = listOf("Theme1", "Theme2", "Theme3")
        every { taskRepository.findAllThemes() } returns expected

        val result = taskService.getAllThemes()

        assertEquals(expected, result)
        verify(exactly = 1) { taskRepository.findAllThemes() }
    }

    @Test
    fun getTaskByIdTest() {
        val id = 1L
        val entity = TaskEntity(
            id = 1L,
            title = "Task1",
            theme = "Theme1",
            author = "Author1",
            description = "Desc1",
            isStarted = true,
            updateAt = now
        )
        val expected = TaskResponse(1L, "Task1", "Theme1", "Author1", "Desc1", true, now)

        every { taskRepository.findById(id) } returns Optional.of(entity)

        val result = taskService.getTaskById(id)

        assertEquals(expected, result)
        verify(exactly = 1) { taskRepository.findById(id) }
    }

    @Test
    fun getTaskByIdErrorTest() {
        val id = 999L
        every { taskRepository.findById(id) } returns Optional.empty()

        val exception = assertThrows<EntityNotFoundException> {
            taskService.getTaskById(id)
        }

        assertEquals("Entity with id $id not found", exception.message)
        verify(exactly = 1) { taskRepository.findById(id) }
    }

    @Test
    fun getTaskByTitleTest() {
        val title = "Task1"
        val entities = listOf(
            TaskEntity(
                id = 1,
                title = title,
                theme = "Theme1",
                author = "Author1",
                description = "Desc1",
                isStarted = true,
                updateAt = now
            )
        )
        val expected = listOf(
            Task(title = title, theme = "Theme1", author = "Author1", description = "Desc1", isStarted = true)
        )
        every { taskRepository.findByTitle(title) } returns entities

        val result = taskService.getTaskByTitle(title)

        assertEquals(expected, result)
        verify(exactly = 1) { taskRepository.findByTitle(title) }
    }

    @Test
    fun getTaskByThemeTest() {
        val theme = "Theme1"
        val entities = listOf(
            TaskEntity(
                id = 1,
                title = "Task1",
                theme = theme,
                author = "Author1",
                description = "Desc1",
                isStarted = false,
                updateAt = now
            )
        )
        val expected = listOf(
            Task(title = "Task1", theme = theme, author = "Author1", description = "Desc1", isStarted = false)
        )
        every { taskRepository.findByTheme(theme) } returns entities

        val result = taskService.getTaskByTheme(theme)

        assertEquals(expected, result)
        verify(exactly = 1) { taskRepository.findByTheme(theme) }
    }

    @Test
    fun getTaskByAuthorTest() {
        val author = "Author1"
        val entities = listOf(
            TaskEntity(
                id = 1,
                title = "Task1",
                theme = "Theme1",
                author = author,
                description = "Desc1",
                isStarted = true,
                updateAt = now
            )
        )
        val expected = listOf(
            Task(title = "Task1", theme = "Theme1", author = author, description = "Desc1", isStarted = true)
        )
        every { taskRepository.findByAuthor(author) } returns entities

        val result = taskService.getTaskByAuthor(author)

        assertEquals(expected, result)
        verify(exactly = 1) { taskRepository.findByAuthor(author) }
    }

    @Test
    fun getTaskByNullAuthorTest() {
        val author: String? = null
        every { taskRepository.findByAuthor(author) } returns emptyList()

        val result = taskService.getTaskByAuthor(author)

        assertTrue(result.isEmpty())
        verify(exactly = 1) { taskRepository.findByAuthor(author) }
    }

    @Test
    fun getStartedTasksTest() {
        val entities = listOf(
            TaskEntity(
                id = 1,
                title = "Task1",
                theme = "Theme1",
                author = "Author1",
                description = "Desc1",
                isStarted = true,
                updateAt = now
            )
        )
        val expected = listOf(
            Task(title = "Task1", theme = "Theme1", author = "Author1", description = "Desc1", isStarted = true)
        )
        every { taskRepository.findByIsStartedTrue() } returns entities

        val result = taskService.getStartedTasks()

        assertEquals(expected, result)
        verify(exactly = 1) { taskRepository.findByIsStartedTrue() }
    }

    @Test
    fun postTaskTest() {
        val task = Task("New Task", "Theme", "Author", "Desc", false)
        val entity = TaskEntity(
            id = 1,
            title = "New Task",
            theme = "Theme",
            author = "Author",
            description = "Desc",
            isStarted = false,
            updateAt = now
        )
        val expected = TaskResponse(1L, "New Task", "Theme", "Author", "Desc", false, now)

        every {
            taskRepository.existsByTitleAndThemeAndAuthorAndDescription(
                task.title,
                task.theme,
                task.author,
                task.description
            )
        } returns false
        every { taskRepository.save(any()) } returns entity

        val result = taskService.postTask(task)

        assertEquals(expected, result)
        verify(exactly = 1) {
            taskRepository.existsByTitleAndThemeAndAuthorAndDescription(
                task.title,
                task.theme,
                task.author,
                task.description
            )
        }
        verify(exactly = 1) { taskRepository.save(any()) }
    }

    @Test
    fun postTaskAlreadyExistsTest() {
        val task = Task("Existing", "Theme", "Author", "Desc", false)
        every {
            taskRepository.existsByTitleAndThemeAndAuthorAndDescription(
                task.title,
                task.theme,
                task.author,
                task.description
            )
        } returns true

        val exception = assertThrows<EntityExistsException> {
            taskService.postTask(task)
        }

        assertEquals("Task already exists", exception.message)
        verify(exactly = 1) {
            taskRepository.existsByTitleAndThemeAndAuthorAndDescription(
                task.title,
                task.theme,
                task.author,
                task.description
            )
        }
        verify(exactly = 0) { taskRepository.save(any()) }
    }

    @Test
    fun putTaskByIdTest() {
        val id = 1L
        val task = Task("Updated", "Theme", "Author", "Desc", true)
        val existing = TaskEntity(
            id = 1,
            title = "Old",
            theme = "Theme",
            author = "Author",
            description = "Old Desc",
            isStarted = false,
            updateAt = now
        )
        val updated = TaskEntity(
            id = 1,
            title = "Updated",
            theme = "Theme",
            author = "Author",
            description = "Desc",
            isStarted = true,
            updateAt = now
        )
        val expected = TaskResponse(1L, "Updated", "Theme", "Author", "Desc", true, now)

        every { taskRepository.findById(id) } returns Optional.of(existing)
        every { taskRepository.save(any()) } returns updated

        val result = taskService.putTaskById(id, task)

        assertEquals(expected, result)
        verify(exactly = 1) { taskRepository.findById(id) }
        verify(exactly = 1) { taskRepository.save(any()) }
    }

    @Test
    fun putTaskByIdErrorTest() {
        val id = 999L
        val task = Task("Updated", "Theme", "Author", "Desc", true)
        every { taskRepository.findById(id) } returns Optional.empty()

        val exception = assertThrows<EntityNotFoundException> {
            taskService.putTaskById(id, task)
        }

        assertEquals("Task with ID=$id not found", exception.message)
        verify(exactly = 1) { taskRepository.findById(id) }
        verify(exactly = 0) { taskRepository.save(any()) }
    }

    @Test
    fun deleteTaskTest() {
        val id = 1L
        every { taskRepository.existsById(id) } returns true
        every { taskRepository.deleteById(id) } returns Unit

        taskService.deleteTask(id)

        verify(exactly = 1) { taskRepository.existsById(id) }
        verify(exactly = 1) { taskRepository.deleteById(id) }
    }

    @Test
    fun deleteTaskErrorTest() {
        val id = 999L
        every { taskRepository.existsById(id) } returns false

        val exception = assertThrows<EntityNotFoundException> {
            taskService.deleteTask(id)
        }

        assertEquals("Task with ID $id not found", exception.message)
        verify(exactly = 1) { taskRepository.existsById(id) }
        verify(exactly = 0) { taskRepository.deleteById(id) }
    }
}