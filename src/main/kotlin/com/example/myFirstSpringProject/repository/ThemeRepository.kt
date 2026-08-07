package com.example.myFirstSpringProject.repository

import com.example.myFirstSpringProject.model.TaskEntity
import com.example.myFirstSpringProject.model.ThemeEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ThemeRepository : JpaRepository<ThemeEntity, Long> {

    override fun findAll(pageable: Pageable): Page<ThemeEntity>

    fun existsByThemeTitleAndDescription(
        themeTitle: String,
        description: String?
    ): Boolean

    fun findByThemeTitle(themeTitle: String): ThemeEntity?

}