package com.example.myFirstSpringProject.service

import com.example.myFirstSpringProject.model.TaskRequest
import com.example.myFirstSpringProject.model.TaskEntity
import com.example.myFirstSpringProject.model.TaskResponse
import com.example.myFirstSpringProject.model.ThemeEntity
import com.example.myFirstSpringProject.repository.TaskRepository
import com.example.myFirstSpringProject.repository.ThemeRepository
import jakarta.persistence.EntityExistsException
import jakarta.persistence.EntityNotFoundException
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional
class TaskService(
    private val taskRepository: TaskRepository,
    private val themeRepository: ThemeRepository
) {
    fun getAllTasks(): List<TaskResponse> = taskRepository.findAll().map { TaskMapper.toTaskResponse(it) }

    fun getTaskById(id : Long) : TaskResponse {
        val entity = taskRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Task with ID $id not found") }
        return TaskMapper.toTaskResponse(entity)
    }

    fun getTaskByTitle(title: String) : List<TaskResponse> =
        taskRepository.findByTitle(title).map{ TaskMapper.toTaskResponse(it)}

    fun getTaskByTheme(theme: String) : List<TaskResponse> =
        taskRepository.findByTheme_ThemeTitle(theme).map{ TaskMapper.toTaskResponse(it)}

    fun getTaskByAuthor(author: String?) : List<TaskResponse> =
        taskRepository.findByAuthor(author).map{ TaskMapper.toTaskResponse(it)}

    fun getStartedTasks() : List<TaskResponse> =
        taskRepository.findByIsStartedTrue().map{ TaskMapper.toTaskResponse(it)}

    fun postTask(
        task: TaskRequest
    ): TaskResponse {
        if(taskRepository.existsByTitleAndAuthorAndDescription(task.title, task.author, task.description)){
            throw EntityExistsException("Task already exists")
        }
        val theme = themeRepository.findByThemeTitle(task.theme)
        val entity : TaskEntity
        if(theme != null){
            entity = taskRepository.save(TaskMapper.toTaskEntity(task,theme))
        }else{
            val theme = themeRepository.save(
                ThemeEntity(themeTitle = task.theme)
            )
            entity = taskRepository.save(TaskMapper.toTaskEntity(task,theme))
        }
        return TaskMapper.toTaskResponse(entity)
    }

    fun putTaskById(
        id: Long,
        task: TaskRequest
    ): TaskResponse {
        val existing = taskRepository.findById(id)
            .orElseThrow{EntityNotFoundException("Task with ID $id not found")}

        val theme = themeRepository.findByThemeTitle(task.theme)
            ?: themeRepository.save(ThemeEntity(themeTitle = task.theme))

        existing.title = task.title
        existing.theme = theme
        existing.author = task.author
        existing.description = task.description
        existing.isStarted = task.isStarted

        val updated = taskRepository.save(existing)
        return TaskMapper.toTaskResponse(updated)
    }

    fun deleteTaskById(id: Long) =
        if (taskRepository.existsById(id))taskRepository.deleteById(id)
    else throw EntityNotFoundException("Task with ID $id not found")
}

object TaskMapper {
    fun toTaskResponse(entity: TaskEntity): TaskResponse =
        TaskResponse(
            id = entity.id,
            theme = ThemeMapper.toThemeResponse(entity.theme),
            title = entity.title,
            author = entity.author,
            description = entity.description,
            isStarted = entity.isStarted,
            updateAt = entity.updateAt
        )

    fun toTaskEntity(task: TaskRequest, theme: ThemeEntity): TaskEntity =
        TaskEntity(
            title = task.title,
            theme = theme,
            author = task.author,
            description = task.description,
            isStarted = task.isStarted
        )
}
