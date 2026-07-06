package com.example.myFirstSpringProject

import jakarta.persistence.EntityExistsException
import jakarta.persistence.EntityNotFoundException
import jakarta.validation.Valid
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
    fun getAllTasks() : List<TaskResponse> = taskService.getAllTasks()

    @GetMapping(("/themes"))
    fun getAllThemes() : List<String> = taskService.getAllThemes()

    @GetMapping("/by-id")
    fun getTasksById(@RequestParam(value = "id") id: Long) : ResponseEntity<TaskResponse>{
        return try {
            ResponseEntity.ok(taskService.getTaskById(id))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
        }
    }

    @GetMapping("/by-title")
    fun getTasksByTitle(@RequestParam(value = "title") title: String) : List<Task> =
        taskService.getTaskByTitle(title)

    @GetMapping("/by-theme")
    fun getTasksByTheme(@RequestParam(value = "theme") theme: String) : List<Task> =
        taskService.getTaskByTheme(theme)
    @GetMapping("/by-author")
    fun getTasksByAuthor(@RequestParam(value = "author") author: String?) : List<Task> =
        taskService.getTaskByAuthor(author)
    @GetMapping("/is-started")
    fun startedTasks() : List<Task> =
        taskService.getStartedTasks()

    @PostMapping
    fun postTask(@Valid @RequestBody task: Task) : ResponseEntity<TaskResponse> {
            return try {
                val result = taskService.postTask(task)
                ResponseEntity.status(HttpStatus.CREATED).body(result)
            } catch (e: EntityExistsException) {
                ResponseEntity.status(HttpStatus.CONFLICT).body(null)
            }
        }

    @PutMapping("/{id}")
    fun putFirstTask(@PathVariable id:Long,
                     @Valid @RequestBody task: Task) : ResponseEntity<TaskResponse>{
        return try {
            ResponseEntity.ok(taskService.putTaskById(id,task))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
        }
    }

    @DeleteMapping("/{id}")
    fun deleteTaskById(@PathVariable(value = "id") id: Long) {
        try {
            taskService.deleteTask(id)
            ResponseEntity.ok()
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
        }
    }

}