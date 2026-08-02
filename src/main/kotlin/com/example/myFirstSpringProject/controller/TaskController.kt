package com.example.myFirstSpringProject.controller

import com.example.myFirstSpringProject.model.TaskRequest
import com.example.myFirstSpringProject.model.TaskResponse
import com.example.myFirstSpringProject.model.ThemeRequest
import com.example.myFirstSpringProject.model.ThemeResponse
import com.example.myFirstSpringProject.service.TaskService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/tasks")
class TaskController(
    private val taskService: TaskService
) {
    @GetMapping
    fun getAllTasks(
    @PageableDefault(size = 10, sort = ["title"], direction = Sort.Direction.ASC) pageable: Pageable
    ): Page<TaskResponse> = taskService.getAllTasks(pageable)

    @GetMapping("/by-id")
    fun getTasksById(@RequestParam(value = "id") id: Long) : ResponseEntity<TaskResponse> =
        ResponseEntity.ok(taskService.getTaskById(id))

    @GetMapping("/by-title")
    fun getTasksByTitle(@RequestParam(value = "title") title: String) : List<TaskResponse> =
        taskService.getTaskByTitle(title)

    @GetMapping("/by-theme")
    fun getTasksByTheme(@RequestParam(value = "theme") theme: String) : List<TaskResponse> =
        taskService.getTaskByTheme(theme)

    @GetMapping("/by-author")
    fun getTasksByAuthor(@RequestParam(value = "author") author: String?) : List<TaskResponse> =
        taskService.getTaskByAuthor(author)

    @GetMapping("/is-started")
    fun startedTasks() : List<TaskResponse> =
        taskService.getStartedTasks()

    @PostMapping
    fun postTask(@Valid @RequestBody task: TaskRequest) : ResponseEntity<TaskResponse> {
        val result = taskService.postTask(task)
        return ResponseEntity.status(HttpStatus.CREATED).body(result)
        }

    @PutMapping("/{id}")
    fun putFirstTask(@PathVariable id:Long,
                     @Valid @RequestBody task: TaskRequest) : ResponseEntity<TaskResponse> =
        ResponseEntity.ok(taskService.putTaskById(id,task))

    @DeleteMapping("/{id}")
    fun deleteTaskById(@PathVariable(value = "id") id: Long) : ResponseEntity<Unit> {
        taskService.deleteTaskById(id)
        return ResponseEntity.noContent().build()
    }
}