package com.example.myFirstSpringProject

import com.example.myFirstSpringProject.model.TaskEntity
import com.example.myFirstSpringProject.model.TaskRequest
import com.example.myFirstSpringProject.model.TaskResponse
import com.example.myFirstSpringProject.model.ThemeEntity
import com.example.myFirstSpringProject.model.ThemeResponse
import com.example.myFirstSpringProject.repository.TaskRepository
import com.example.myFirstSpringProject.repository.ThemeRepository
import com.example.myFirstSpringProject.service.TaskService
import com.example.myFirstSpringProject.service.ThemeMapper
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
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime
import java.util.Optional
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExtendWith(MockKExtension::class)
class TaskServiceTest {
    @MockK
    lateinit var taskRepository: TaskRepository
    @MockK
    lateinit var themeRepository: ThemeRepository
    lateinit var taskService: TaskService

    val now = LocalDateTime.now()

    @BeforeEach
    fun setUp() {
        taskService = TaskService(taskRepository, themeRepository)
    }

    @Test
    fun getAllTasksTest() {
        val now = LocalDateTime.now()
        val theme1 = ThemeEntity(id = 1, themeTitle = "Theme1", updateAt = now)
        val theme2 = ThemeEntity(id = 2, themeTitle = "Theme2", updateAt = now)

        val entities = listOf(
            TaskEntity(id = 1, theme = theme1, title = "Task1", author = "Author1", description = "Desc1", isStarted = true, updateAt = now),
            TaskEntity(id = 2, theme = theme2, title = "Task2", author = null, description = "Desc2", isStarted = false, updateAt = now)
        )
        val pageable: Pageable = PageRequest.of(0, 10)
        val entityPage = PageImpl(entities, pageable, entities.size.toLong())

        val expectedContent = listOf(
            TaskResponse(1, ThemeMapper.toThemeResponse(theme1), "Task1", "Author1", "Desc1", true, now),
            TaskResponse(2, ThemeMapper.toThemeResponse(theme2), "Task2", null, "Desc2", false, now)
        )
        val expectedPage = PageImpl(expectedContent, pageable, expectedContent.size.toLong())

        every { taskRepository.findAll(pageable) } returns entityPage

        val result: Page<TaskResponse> = taskService.getAllTasks(pageable)

        assertEquals(expectedPage.content, result.content)
        assertEquals(expectedPage.totalElements, result.totalElements)
        verify(exactly = 1) { taskRepository.findAll(pageable) }
    }

    @Test
    fun getTaskByIdTest() {
        val id = 1L
        val theme = ThemeEntity(id = 1, themeTitle = "Theme1", updateAt = now)
        val entity = TaskEntity(id = 1, theme = theme, title = "Task1", author = "Author1", description = "Desc1", isStarted = true, updateAt = now)
        val expected = TaskResponse(1, ThemeMapper.toThemeResponse(theme), "Task1", "Author1", "Desc1", true, now)

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

        assertEquals("Task with ID $id not found", exception.message)
        verify(exactly = 1) { taskRepository.findById(id) }
    }

    @Test
    fun getTaskByTitleTest() {
        val title = "Task1"
        val theme = ThemeEntity(id = 1, themeTitle = "Theme1", updateAt = now)
        val entities = listOf(TaskEntity(id = 1, theme = theme, title = title, author = "Author1", description = "Desc1", isStarted = true, updateAt = now))
        val expected = listOf(TaskResponse(1, ThemeMapper.toThemeResponse(theme), title, "Author1", "Desc1", true, now))

        every { taskRepository.findByTitle(title) } returns entities

        val result = taskService.getTaskByTitle(title)

        assertEquals(expected, result)
        verify(exactly = 1) { taskRepository.findByTitle(title) }
    }

    @Test
    fun getTaskByThemeTest() {
        val themeName = "Theme1"
        val theme = ThemeEntity(id = 1, themeTitle = themeName, updateAt = now)
        val entities = listOf(TaskEntity(id = 1, theme = theme, title = "Task1", author = "Author1", description = "Desc1", isStarted = false, updateAt = now))
        val expected = listOf(TaskResponse(1, ThemeMapper.toThemeResponse(theme), "Task1", "Author1", "Desc1", false, now))

        every { taskRepository.findByTheme_ThemeTitle(themeName) } returns entities

        val result = taskService.getTaskByTheme(themeName)

        assertEquals(expected, result)
        verify(exactly = 1) { taskRepository.findByTheme_ThemeTitle(themeName) }
    }

    @Test
    fun getTaskByAuthorTest() {
        val author = "Author1"
        val theme = ThemeEntity(id = 1, themeTitle = "Theme1", updateAt = now)
        val entities = listOf(TaskEntity(id = 1, theme = theme, title = "Task1", author = author, description = "Desc1", isStarted = true, updateAt = now))
        val expected = listOf(TaskResponse(1, ThemeMapper.toThemeResponse(theme), "Task1", author, "Desc1", true, now))

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
        val theme = ThemeEntity(id = 1, themeTitle = "Theme1", updateAt = now)
        val entities = listOf(TaskEntity(id = 1, theme = theme, title = "Task1", author = "Author1", description = "Desc1", isStarted = true, updateAt = now))
        val expected = listOf(TaskResponse(1, ThemeMapper.toThemeResponse(theme), "Task1", "Author1", "Desc1", true, now))

        every { taskRepository.findByIsStartedTrue() } returns entities

        val result = taskService.getStartedTasks()

        assertEquals(expected, result)
        verify(exactly = 1) { taskRepository.findByIsStartedTrue() }
    }

    @Test
    fun postTaskTest() {
        val request = TaskRequest("New Task", "New Theme", "Author", "Desc", false)
        val themeEntity = ThemeEntity(id = 1, themeTitle = "New Theme", updateAt = now)
        val taskEntity = TaskEntity(id = 1, theme = themeEntity, title = "New Task", author = "Author", description = "Desc", isStarted = false, updateAt = now)
        val expected = TaskResponse(1, ThemeMapper.toThemeResponse(themeEntity), "New Task", "Author", "Desc", false, now)

        every { taskRepository.existsByTitleAndAuthorAndDescription(request.title, request.author, request.description) } returns false
        every { themeRepository.findByThemeTitle(request.theme) } returns null
        every { themeRepository.save(any()) } returns themeEntity
        every { taskRepository.save(any()) } returns taskEntity

        val result = taskService.postTask(request)

        assertEquals(expected, result)
        verify(exactly = 1) { taskRepository.existsByTitleAndAuthorAndDescription(request.title, request.author, request.description) }
        verify(exactly = 1) { themeRepository.findByThemeTitle(request.theme) }
        verify(exactly = 1) { themeRepository.save(any()) }
        verify(exactly = 1) { taskRepository.save(any()) }
    }

    @Test
    fun postTaskAlreadyExistsTest() {
        val request = TaskRequest("Existing", "Theme", "Author", "Desc", false)
        every { taskRepository.existsByTitleAndAuthorAndDescription(request.title, request.author, request.description) } returns true

        val exception = assertThrows<EntityExistsException> {
            taskService.postTask(request)
        }

        assertEquals("Task already exists", exception.message)
        verify(exactly = 1) { taskRepository.existsByTitleAndAuthorAndDescription(request.title, request.author, request.description) }
        verify(exactly = 0) { themeRepository.findByThemeTitle(any()) }
        verify(exactly = 0) { taskRepository.save(any()) }
    }

    @Test
    fun putTaskByIdTest() {
        val id = 1L
        val request = TaskRequest("Updated", "New Theme", "Author", "Desc", true)
        val existingTheme = ThemeEntity(id = 1, themeTitle = "Old Theme", updateAt = now)
        val newTheme = ThemeEntity(id = 2, themeTitle = "New Theme", updateAt = now)
        val existingTask = TaskEntity(id = id, theme = existingTheme, title = "Old", author = "Author", description = "Old Desc", isStarted = false, updateAt = now)
        val updatedTask = TaskEntity(id = id, theme = newTheme, title = "Updated", author = "Author", description = "Desc", isStarted = true, updateAt = now)
        val expected = TaskResponse(id, ThemeMapper.toThemeResponse(newTheme), "Updated", "Author", "Desc", true, now)

        every { taskRepository.findById(id) } returns Optional.of(existingTask)
        every { themeRepository.findByThemeTitle(request.theme) } returns null
        every { themeRepository.save(any()) } returns newTheme
        every { taskRepository.save(any()) } returns updatedTask

        val result = taskService.putTaskById(id, request)

        assertEquals(expected, result)
        verify(exactly = 1) { taskRepository.findById(id) }
        verify(exactly = 1) { themeRepository.findByThemeTitle(request.theme) }
        verify(exactly = 1) { themeRepository.save(any()) }
        verify(exactly = 1) { taskRepository.save(any()) }
    }

    @Test
    fun putTaskByIdErrorTest() {
        val id = 999L
        val request = TaskRequest("Updated", "Theme", "Author", "Desc", true)
        every { taskRepository.findById(id) } returns Optional.empty()

        val exception = assertThrows<EntityNotFoundException> {
            taskService.putTaskById(id, request)
        }

        assertEquals("Task with ID $id not found", exception.message)
        verify(exactly = 1) { taskRepository.findById(id) }
        verify(exactly = 0) { taskRepository.save(any()) }
    }

    @Test
    fun deleteTaskTest() {
        val id = 1L
        every { taskRepository.existsById(id) } returns true
        every { taskRepository.deleteById(id) } returns Unit

        taskService.deleteTaskById(id)

        verify(exactly = 1) { taskRepository.existsById(id) }
        verify(exactly = 1) { taskRepository.deleteById(id) }
    }

    @Test
    fun deleteTaskErrorTest() {
        val id = 999L
        every { taskRepository.existsById(id) } returns false

        val exception = assertThrows<EntityNotFoundException> {
            taskService.deleteTaskById(id)
        }

        assertEquals("Task with ID $id not found", exception.message)
        verify(exactly = 1) { taskRepository.existsById(id) }
        verify(exactly = 0) { taskRepository.deleteById(id) }
    }
}