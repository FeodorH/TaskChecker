package com.example.myFirstSpringProject

import com.example.myFirstSpringProject.controller.TaskController
import com.example.myFirstSpringProject.model.TaskRequest
import com.example.myFirstSpringProject.model.TaskResponse
import com.example.myFirstSpringProject.model.ThemeResponse
import com.example.myFirstSpringProject.service.TaskService
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
import org.springframework.http.ResponseEntity
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@ExtendWith(MockKExtension::class)
class TaskControllerTest {
    @MockK
    lateinit var taskService: TaskService
    lateinit var taskController: TaskController

    @BeforeEach
    fun setUp() {
        taskController = TaskController(taskService)
    }

    @Test
    fun getAllTasksTest() {
        val now = LocalDateTime.now()
        val theme1 = ThemeResponse(1, "Theme1", null, now)
        val theme2 = ThemeResponse(2, "Theme2", null, now)
        val expectedTasks = listOf(
            TaskResponse(1, theme1, "Task1", "Author1", "Desc1", true, now),
            TaskResponse(2, theme2, "Task2", null, "Desc2", false, now)
        )
        every { taskService.getAllTasks() } returns expectedTasks

        val result = taskController.getAllTasks()

        assertEquals(expectedTasks, result)
        verify { taskService.getAllTasks() }
    }

    @Test
    fun getTaskByIdTest() {
        val id = 1L
        val now = LocalDateTime.now()
        val theme = ThemeResponse(1, "Theme1", null, now)
        val expected = TaskResponse(id, theme, "Task1", "Author1", "Desc1", true, now)
        every { taskService.getTaskById(id) } returns expected

        val response = taskController.getTasksById(id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expected, response.body)
        verify { taskService.getTaskById(id) }
    }

    @Test
    fun getTasksByTitleTest() {
        val title = "Test"
        val now = LocalDateTime.now()
        val theme = ThemeResponse(1, "Theme1", null, now)
        val expected = listOf(TaskResponse(1, theme, "Test Task 1", "Author1", "Desc1", false, now))
        every { taskService.getTaskByTitle(title) } returns expected

        val result = taskController.getTasksByTitle(title)

        assertEquals(expected, result)
        verify { taskService.getTaskByTitle(title) }
    }

    @Test
    fun getTasksByThemeTest() {
        val themeName = "Work"
        val now = LocalDateTime.now()
        val theme = ThemeResponse(1, themeName, null, now)
        val expected = listOf(TaskResponse(1, theme, "Task1", "Author1", "Desc1", false, now))
        every { taskService.getTaskByTheme(themeName) } returns expected

        val result = taskController.getTasksByTheme(themeName)

        assertEquals(expected, result)
        verify { taskService.getTaskByTheme(themeName) }
    }

    @Test
    fun getTasksByNullAuthorTest() {
        val author = null
        every { taskService.getTaskByAuthor(author) } returns emptyList()

        val result = taskController.getTasksByAuthor(author)

        assertEquals(emptyList<TaskResponse>(), result)
        verify { taskService.getTaskByAuthor(author) }
    }

    @Test
    fun getStartedTasksTest() {
        val now = LocalDateTime.now()
        val theme = ThemeResponse(1, "Theme", null, now)
        val expected = listOf(TaskResponse(1, theme, "Started Task", "Author", "Desc", true, now))
        every { taskService.getStartedTasks() } returns expected

        val result = taskController.startedTasks()

        assertEquals(expected, result)
        verify { taskService.getStartedTasks() }
    }

    @Test
    fun getTaskByIdErrorTest() {
        val id = 999L
        every { taskService.getTaskById(id) } throws EntityNotFoundException("Task not found")

        assertThrows<EntityNotFoundException> {
            taskController.getTasksById(id)
        }
    }

    @Test
    fun postTaskTest() {
        val now = LocalDateTime.now()
        val taskRequest = TaskRequest("New Task", "Theme", "Author", "Desc", false)
        val theme = ThemeResponse(1, "Theme", null, now)
        val saved = TaskResponse(1, theme, "New Task", "Author", "Desc", false, now)
        every { taskService.postTask(taskRequest) } returns saved

        val response = taskController.postTask(taskRequest)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(saved, response.body)
        verify { taskService.postTask(taskRequest) }
    }

    @Test
    fun postTaskAlreadyExistsTest() {
        val taskRequest = TaskRequest("Existing", "Theme", "Author", "Desc", false)
        every { taskService.postTask(taskRequest) } throws EntityExistsException("Task already exists")

        assertThrows<EntityExistsException> {
            taskController.postTask(taskRequest)
        }
    }

    @Test
    fun putTaskTest() {
        val id = 1L
        val now = LocalDateTime.now()
        val taskRequest = TaskRequest("Updated", "Theme", "Author", "Desc", true)
        val theme = ThemeResponse(1, "Theme", null, now)
        val updated = TaskResponse(id, theme, "Updated", "Author", "Desc", true, now)
        every { taskService.putTaskById(id, taskRequest) } returns updated

        val response = taskController.putFirstTask(id, taskRequest)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(updated, response.body)
        verify { taskService.putTaskById(id, taskRequest) }
    }

    @Test
    fun deleteTaskTest() {
        val id = 1L
        every { taskService.deleteTaskById(id) } returns Unit

        val response = taskController.deleteTaskById(id)

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
        assertNull(response.body)
        verify { taskService.deleteTaskById(id) }
    }

    @Test
    fun deleteTaskErrorTest() {
        val id = 999L
        every { taskService.deleteTaskById(id) } throws EntityNotFoundException("Task not found")

        assertThrows<EntityNotFoundException> {
            taskController.deleteTaskById(id)
        }
    }
}