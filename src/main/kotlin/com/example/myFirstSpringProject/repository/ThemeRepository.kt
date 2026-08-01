package com.example.myFirstSpringProject.repository

import com.example.myFirstSpringProject.model.ThemeEntity
import com.example.myFirstSpringProject.model.ThemeResponse
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ThemeRepository : JpaRepository<ThemeEntity, Long> {

    fun existsByThemeAndDescription(
        theme: String,
        description: String?) : Boolean

    fun findByTheme(theme: String): List<ThemeEntity>

}