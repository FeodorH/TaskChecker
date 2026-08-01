package com.example.myFirstSpringProject

import com.example.myFirstSpringProject.controller.TaskController
import com.example.myFirstSpringProject.model.TaskResponse
import com.example.myFirstSpringProject.service.TaskService
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import jakarta.persistence.EntityExistsException
import jakarta.persistence.EntityNotFoundException
import org.junit.jupiter.api.BeforeEach
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
        val expectedTasks = listOf(
            TaskResponse(1L, "Task1", "Theme1", "Author1", "Desc1", true, LocalDateTime.now()),
            TaskResponse(2L, "Task2", "Theme2", null, "Desc2", false, LocalDateTime.now())
        )
        every { taskService.getAllTasks() } returns expectedTasks

        val result: List<TaskResponse> = taskController.getAllTasks()

        assertEquals(expectedTasks, result)
        verify { taskService.getAllTasks() }
    }

    @Test
    fun getAllThemesTest() {
        val expectedThemes = listOf("Theme1", "Theme2")
        every { taskService.getAllThemes() } returns expectedThemes

        val result: List<String> = taskController.getAllThemes()

        assertEquals(expectedThemes, result)
        verify { taskService.getAllThemes() }
    }

    @Test
    fun getTaskByIdTest() {
        val id = 1L
        val expected = TaskResponse(id, "Task1", "Theme1", "Author1", "Desc1", true, LocalDateTime.now())
        every { taskService.getTaskById(id) } returns expected

        val response: ResponseEntity<TaskResponse> = taskController.getTasksById(id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expected, response.body)
        verify { taskService.getTaskById(id) }
    }

    @Test
    fun getTasksByTitleTest() {
        val title = "Test"
        val expected = listOf(Task("Test Task 1", "Theme1", "Author1", "Desc1", false))
        every { taskService.getTaskByTitle(title) } returns expected

        val result = taskController.getTasksByTitle(title)

        assertEquals(expected, result)
        verify { taskService.getTaskByTitle(title) }
    }

    @Test
    fun getTasksByThemeTest() {
        val theme = "Work"
        val expected = listOf(Task("Task1", "Work", "Author1", "Desc1", false))
        every { taskService.getTaskByTheme(theme) } returns expected

        val result = taskController.getTasksByTheme(theme)

        assertEquals(expected, result)
        verify { taskService.getTaskByTheme(theme) }
    }

    @Test
    fun getTasksByNullAuthorTest() {
        val author = null
        val expected = listOf<Task>()
        every { taskService.getTaskByAuthor(author) } returns expected

        val result = taskController.getTasksByAuthor(author)

        assertEquals(expected, result)
        verify { taskService.getTaskByAuthor(author) }
    }

    @Test
    fun getStartedTasksTest() {
        val expected = listOf(Task("Started Task", "Theme", "Author", "Desc", true))
        every { taskService.getStartedTasks() } returns expected

        val result = taskController.startedTasks()

        assertEquals(expected, result)
        verify { taskService.getStartedTasks() }
    }

    @Test
    fun getTaskByIdErrorTest() {
        val id = 999L
        every { taskService.getTaskById(id) } throws EntityNotFoundException("Task not found")

        val response: ResponseEntity<TaskResponse> = taskController.getTasksById(id)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
        verify { taskService.getTaskById(id) }
    }

    @Test
    fun postTaskTest() {
        val taskRequest = Task("New Task", "Theme", "Author", "Desc", false)
        val saved = TaskResponse(1L, "New Task", "Theme", "Author", "Desc", false, LocalDateTime.now())
        every { taskService.postTask(taskRequest) } returns saved

        val response: ResponseEntity<TaskResponse> = taskController.postTask(taskRequest)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(saved, response.body)
        verify { taskService.postTask(taskRequest) }
    }

    @Test
    fun postTaskAlreadyExistsTest() {
        val taskRequest = Task("Existing", "Theme", "Author", "Desc", false)
        every { taskService.postTask(taskRequest) } throws EntityExistsException("Task already exists")

        val response: ResponseEntity<TaskResponse> = taskController.postTask(taskRequest)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertNull(response.body)
    }

    @Test
    fun putTaskTest() {
        val id = 1L
        val taskRequest = Task("Updated", "Theme", "Author", "Desc", true)
        val updated = TaskResponse(id, "Updated", "Theme", "Author", "Desc", true, LocalDateTime.now())
        every { taskService.putTaskById(id, taskRequest) } returns updated

        val response: ResponseEntity<TaskResponse> = taskController.putFirstTask(id, taskRequest)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(updated, response.body)
        verify { taskService.putTaskById(id, taskRequest) }
    }

    @Test
    fun deleteTaskTest() {
        val id = 1L
        every { taskService.deleteTask(id) } returns Unit

        val response: ResponseEntity<Unit> = taskController.deleteTaskById(id)

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
        assertNull(response.body)
        verify { taskService.deleteTask(id) }
    }

    @Test
    fun deleteTaskErrorTest() {
        val id = 999L
        every { taskService.deleteTask(id) } throws EntityNotFoundException("Task not found")

        val response: ResponseEntity<Unit> = taskController.deleteTaskById(id)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
    }
}