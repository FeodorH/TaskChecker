package com.example.myFirstSpringProject.repository

import com.example.myFirstSpringProject.model.TaskEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface TaskRepository : JpaRepository<TaskEntity, Long> {

    fun findByTitle(title: String): List<TaskEntity>

    fun findByTheme(theme: String): List<TaskEntity>

    fun findByAuthor(author: String?): List<TaskEntity>

    fun findByIsStartedTrue(): List<TaskEntity>

    fun existsByTitleAndThemeAndAuthorAndDescription(
        title: String,
        theme: String,
        author: String?,
        description: String?
    ): Boolean
}