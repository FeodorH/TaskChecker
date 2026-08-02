package com.example.myFirstSpringProject.repository

import com.example.myFirstSpringProject.model.TaskEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface TaskRepository : JpaRepository<TaskEntity, Long> {

    override fun findAll(pageable: Pageable): Page<TaskEntity>

    fun findByTitle(title: String): List<TaskEntity>

    fun findByTheme_ThemeTitle(theme: String): List<TaskEntity>

    fun findByAuthor(author: String?): List<TaskEntity>

    fun findByIsStartedTrue(): List<TaskEntity>

    fun existsByTitleAndAuthorAndDescription(
        title: String,
        author: String?,
        description: String?
    ): Boolean
}