package com.example.myFirstSpringProject.service

import com.example.myFirstSpringProject.model.Task
import com.example.myFirstSpringProject.model.TaskEntity
import com.example.myFirstSpringProject.model.TaskResponse
import com.example.myFirstSpringProject.repository.TaskRepository
import jakarta.persistence.EntityExistsException
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.apache.catalina.mapper.Mapper
import org.apache.coyote.Response
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
@Transactional
class TaskService(
    val taskRepository: TaskRepository
) {
    fun getAllTasks(): List<TaskResponse> = taskRepository.findAll().map { TaskMapper.toResponse(it) }

    fun getAllThemes(): List<String> = taskRepository.findAllThemes()

    fun getTaskById(id : Long) : TaskResponse {
        val entity = taskRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Entity with id $id not found") }
        return TaskMapper.toResponse(entity)
    }

    fun getTaskByTitle(title: String) : List<Task> = taskRepository.findByTitle(title).map{ TaskMapper.toTask(it)}

    fun getTaskByTheme(theme: String) : List<Task> = taskRepository.findByTheme(theme).map{ TaskMapper.toTask(it)}

    fun getTaskByAuthor(author: String?) : List<Task> = taskRepository.findByAuthor(author).map{ TaskMapper.toTask(it)}

    fun getStartedTasks() : List<Task> = taskRepository.findByIsStartedTrue().map{ TaskMapper.toTask(it)}

    fun postTask(
        task: Task
    ): TaskResponse {
        if(taskRepository.existsByTitleAndThemeAndAuthorAndDescription(task.title, task.theme, task.author, task.description)){
            throw EntityExistsException("Task already exists")
        }
        val entity = taskRepository.save(TaskMapper.toEntity(task))
        return TaskMapper.toResponse(entity)
    }

    fun putTaskById(
        id: Long,
        task: Task
    ): TaskResponse {
        val result : TaskEntity = taskRepository.findById(id)
            .orElseThrow { throw EntityNotFoundException("Task with ID=$id not found") }
            .run{
            this.title=task.title
            this.theme=task.theme
            this.author=task.author
            this.description=task.description
            this.isStarted=task.isStarted
            this
        }

        taskRepository.save(result)
        return TaskMapper.toResponse(result)
    }

    fun deleteTask(id: Long) =
        if (taskRepository.existsById(id))taskRepository.deleteById(id)
    else throw EntityNotFoundException("Task with ID $id not found")
}

object TaskMapper {
    fun toResponse(entity: TaskEntity): TaskResponse =
        TaskResponse(
            id = entity.id,
            title = entity.title,
            theme = entity.theme,
            author = entity.author,
            description = entity.description,
            isStarted = entity.isStarted,
            updateAt = entity.updateAt
        )

    fun toTask(entity: TaskEntity): Task =
        Task(
            title = entity.title,
            theme = entity.theme,
            author = entity.author,
            description = entity.description,
            isStarted = entity.isStarted,
        )

    fun toEntity(task: Task): TaskEntity =
        TaskEntity(
            title = task.title,
            theme = task.theme,
            author = task.author,
            description = task.description,
            isStarted = task.isStarted
        )
}