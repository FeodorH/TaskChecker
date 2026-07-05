package com.example.myFirstSpringProject

import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
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

    @GetMapping
    fun getAllThemes() : List<String> = taskService.getAllThemes()

    @GetMapping
    fun getTasksById(@RequestParam(value = "id") id: Long) : ResponseEntity<TaskResponse>{
        return try {
            ResponseEntity.ok(taskService.getTaskById(id))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
        }
    }

    @GetMapping
    fun getTasksByTitle(@RequestParam(value = "title") title: String) : List<Task> =
        taskService.getTaskByTitle(title)

    @GetMapping
    fun getTasksByTheme(@RequestParam(value = "theme") theme: String) : List<Task> =
        taskService.getTaskByTheme(theme)
    @GetMapping
    fun getTasksByAuthor(@RequestParam(value = "author") author: String) : List<Task> =
        taskService.getTaskByAuthor(author)
    @GetMapping
    fun startedTasks() : List<Task> =
        taskService.getStartedTasks()

    @PostMapping
    fun postTask(@RequestParam(value = "title") title: String,
                 @RequestParam(value = "title") theme: String,
                 @RequestParam(value = "author") author: String,//необяз
                 @RequestParam(value = "description") description: String,
                 @RequestParam(value = "started") isStarted : Boolean) : ResponseEntity<TaskResponse> {//необязательный параметр
            return try {
                ResponseEntity.ok(taskService.postTask(title, theme, author, description, isStarted))
            } catch (e: EntityNotFoundException) {
                ResponseEntity.status(HttpStatus.CONFLICT).body(null)
            }
        }

    @PutMapping
    fun putFirstTask(@RequestParam(value = "id") id:Long,
                @RequestParam(value = "title") title: String,
                @RequestParam(value = "title") theme: String,//необяз
                @RequestParam(value = "author") author: String,//необяз
                @RequestParam(value = "description") description: String,//необяз
                @RequestParam(value = "started") isStarted : Boolean) : ResponseEntity<TaskResponse>{
        return try {
            ResponseEntity.ok(taskService.putTaskById(id,title,theme,author, description, isStarted))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
        }
    }

    @DeleteMapping
    fun deleteTaskById(@RequestParam(value = "id") id: Long) {
        try {
            ResponseEntity.ok(taskService.deleteTask(id))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
        }
    }

}