package com.example.myFirstSpringProject.repository

import com.example.myFirstSpringProject.model.ThemeEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ThemeRepository : JpaRepository<ThemeEntity, Long> {

    fun existsByThemeTitleAndDescription(
        themeTitle: String,
        description: String?) : Boolean

    fun findByThemeTitle(themeTitle: String): ThemeEntity?

}