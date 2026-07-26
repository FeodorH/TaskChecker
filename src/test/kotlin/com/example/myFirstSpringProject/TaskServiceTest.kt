package com.example.myFirstSpringProject

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class TaskServiceTest {
    @MockK
    lateinit var taskRepository: TaskRepository
    lateinit var taskService: TaskService

    @BeforeEach
    fun setUp() {
        taskService = TaskService(taskRepository)
    }

    @Test
    fun getAllTasksTest(){
        val now = LocalDateTime.now()
        val expected = listOf(
            TaskResponse(1L, "Task1", "Theme1", "Author1", "Desc1", true, now),
            TaskResponse(2L, "Task2", "Theme2", null, "Desc2", false, now)
        )

        val entities = listOf(
            TaskEntity(id = 1, title = "Task1", theme = "Theme1", author = "Author1", description = "Desc1", isStarted = true, updateAt = now),
            TaskEntity(id = 2, title = "Task2", theme = "Theme2", author = null, description = "Desc2", isStarted = false, updateAt = now)
        )
        every { taskRepository.findAll() } returns entities

        val result = taskService.getAllTasks()

        assertEquals(expected, result)
        verify(exactly = 1) { taskRepository.findAll() }
    }
}