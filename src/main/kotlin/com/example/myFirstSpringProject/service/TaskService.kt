package com.example.myFirstSpringProject.service

import com.example.myFirstSpringProject.model.TaskRequest
import com.example.myFirstSpringProject.model.TaskEntity
import com.example.myFirstSpringProject.model.TaskResponse
import com.example.myFirstSpringProject.model.ThemeEntity
import com.example.myFirstSpringProject.model.ThemeRequest
import com.example.myFirstSpringProject.model.ThemeResponse
import com.example.myFirstSpringProject.repository.TaskRepository
import com.example.myFirstSpringProject.repository.ThemeRepository
import jakarta.persistence.EntityExistsException
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional
class TaskService(
    val taskRepository: TaskRepository,
    val themeRepository: ThemeRepository
) {
    fun getAllTasks(): List<TaskResponse> = taskRepository.findAll().map { TaskMapper.toTaskResponse(it) }

    fun getAllThemes(): List<ThemeResponse> = themeRepository.findAll().map { ThemeMapper.toThemeResponse(it) }

    fun getTaskById(id : Long) : TaskResponse {
        val entity = taskRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Task with id $id not found") }
        return TaskMapper.toTaskResponse(entity)
    }

    fun getTaskByTitle(title: String) : List<TaskResponse> =
        taskRepository.findByTitle(title).map{ TaskMapper.toTaskResponse(it)}

    fun getTaskByTheme(theme: String) : List<TaskResponse> =
        taskRepository.findByTheme(theme).map{ TaskMapper.toTaskResponse(it)}

    fun getTaskByAuthor(author: String?) : List<TaskResponse> =
        taskRepository.findByAuthor(author).map{ TaskMapper.toTaskResponse(it)}

    fun getStartedTasks() : List<TaskResponse> =
        taskRepository.findByIsStartedTrue().map{ TaskMapper.toTaskResponse(it)}

    fun getThemeById(id: Long) : ThemeResponse {
        val entity = themeRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Theme with id $id not found") }
        return ThemeMapper.toThemeResponse(entity)
    }

    fun getThemeByTheme(theme: String) : List<ThemeResponse> =
        themeRepository.findByTheme(theme).map{ ThemeMapper.toThemeResponse(it)}

    fun postTask(
        task: TaskRequest
    ): TaskResponse {
        //TODO: trigger here too
        if(taskRepository.existsByTitleAndThemeAndAuthorAndDescription(task.title, task.theme, task.author, task.description)){
            throw EntityExistsException("Task already exists")
        }
        val entity = taskRepository.save(TaskMapper.toTaskEntity(task))
        return TaskMapper.toTaskResponse(entity)
    }

    fun postTheme(theme: ThemeRequest) : ThemeResponse {
        if(themeRepository.existsByThemeAndDescription(theme.theme, theme.description)){
            throw EntityExistsException("Theme already exists")
        }
        val entity = themeRepository.save(ThemeMapper.toThemeEntity(theme))
        return ThemeMapper.toThemeResponse(entity)
    }

    fun putTaskById(
        id: Long,
        task: TaskRequest
    ): TaskResponse {
        val result : TaskEntity = taskRepository.findById(id)
            .orElseThrow { throw EntityNotFoundException("Task with ID=$id not found") }
            .run{
            this.title=task.title
                //TODO: trigger here
            this.author=task.author
            this.description=task.description
            this.isStarted=task.isStarted
            this
        }

        taskRepository.save(result)
        return TaskMapper.toTaskResponse(result)
    }

    fun putThemeById(
        id: Long,
        theme: ThemeRequest
    ): ThemeResponse {
        val result = themeRepository.findById(id)
            .orElseThrow { throw EntityNotFoundException("Theme with ID=$id not found") }
            .run{
                this.theme = theme.theme
                this.description = theme.description
                this
            }

        themeRepository.save(result)
        return ThemeMapper.toThemeResponse(result)
    }

    fun deleteTaskById(id: Long) =
        if (taskRepository.existsById(id))taskRepository.deleteById(id)
    else throw EntityNotFoundException("Task with ID $id not found")

    fun deleteThemeById(id: Long) =
        if(themeRepository.existsById(id))themeRepository.deleteById(id)
    else throw EntityNotFoundException("Theme with ID $id not found")
}



object TaskMapper {
    fun toTaskResponse(entity: TaskEntity): TaskResponse =
        TaskResponse(
            id = entity.id,
            themeId = entity.themeId,
            title = entity.title,
            //TODO:entity found by ThemeId
            author = entity.author,
            description = entity.description,
            isStarted = entity.isStarted,
            updateAt = entity.updateAt
        )

    fun toTaskEntity(task: TaskRequest): TaskEntity =
        TaskEntity(
            title = task.title,
            theme = task.theme,//TODO: bind with themes.id
            author = task.author,
            description = task.description,
            isStarted = task.isStarted
        )
}

object ThemeMapper {
    fun toThemeResponse(entity: ThemeEntity): ThemeResponse =
        ThemeResponse(
            id = entity.id,
            theme = entity.theme,
            description = entity.description,
            updateAt = entity.updateAt
        )

    fun toThemeEntity(theme: ThemeRequest): ThemeEntity =
        ThemeEntity(
            theme = theme.theme,
            description = theme.description
        )
}