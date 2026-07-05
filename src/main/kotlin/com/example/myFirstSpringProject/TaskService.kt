package com.example.myFirstSpringProject

import jakarta.persistence.EntityExistsException
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.apache.coyote.Response
import org.springframework.stereotype.Service

@Service
@Transactional
class TaskService(//TODO add DI!!!
    val taskRepository: TaskRepository,
    val taskMapper: TaskMapper = TaskMapper
) {
    fun getAllTasks(): List<TaskResponse> = taskRepository.findAll().map { taskMapper.toResponse(it) }

    fun getAllThemes(): List<String> = taskRepository.findAllThemes()

    fun getTaskById(id : Long) : TaskResponse {
        val result : TaskResponse
        if(taskRepository.existsById(id)){
            result = TaskMapper.toResponse( taskRepository.findById(id).get())
        }else{
            throw EntityNotFoundException("Entity with id $id not found")
        }
        return result
    }

    fun getTaskByTitle(title: String) : List<Task> = taskRepository.findByTitle(title).map{ TaskMapper.toTask(it)}

    fun getTaskByTheme(theme: String) : List<Task> = taskRepository.findByTheme(theme).map{ TaskMapper.toTask(it)}

    fun getTaskByAuthor(author: String) : List<Task> = taskRepository.findByAuthor(author).map{ TaskMapper.toTask(it)}

    fun getStartedTasks() : List<Task> = taskRepository.findByIsStartedTrue().map{ TaskMapper.toTask(it)}

    fun postTask(
        title: String,
        theme: String,
        author: String,
        description: String,
        isStarted: Boolean = false
    ): TaskResponse {
        if(taskRepository.existsByTitleAndThemeAndAuthorAndDescription(title, theme, author,description)){
            throw EntityExistsException("Task already exists")
        }
        val task = Task(title, theme, author, description, isStarted)
        val entity = taskRepository.save(TaskMapper.toEntity(task))
        return TaskMapper.toResponse(entity)
    }

    fun putTaskById(
        id: Long,
        title: String,
        theme: String,
        author: String,
        description: String,
        isStarted: Boolean = false
    ): TaskResponse {
        val result : TaskResponse
     if(taskRepository.existsById(id)){
          result = TaskMapper.toResponse(taskRepository.saveById(
             taskEntity = TaskMapper.toEntity(Task(title,theme,author,description,isStarted)),
             id = id))
     }else{
         throw EntityNotFoundException("Task with ID=$id not found")
     }
        return result
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