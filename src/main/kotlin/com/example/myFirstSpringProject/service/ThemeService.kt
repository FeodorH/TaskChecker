package com.example.myFirstSpringProject.service

import com.example.myFirstSpringProject.model.ThemeEntity
import com.example.myFirstSpringProject.model.ThemeRequest
import com.example.myFirstSpringProject.model.ThemeResponse
import com.example.myFirstSpringProject.repository.ThemeRepository
import jakarta.persistence.EntityExistsException
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ThemeService(private val themeRepository: ThemeRepository) {

    fun getAllThemes(): List<ThemeResponse> = themeRepository.findAll().map { ThemeMapper.toThemeResponse(it) }

    fun getThemeById(id: Long) : ThemeResponse {
        val entity = themeRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Theme with id $id not found") }
        return ThemeMapper.toThemeResponse(entity)
    }

    fun getThemeByThemeTitle(themeTitle: String) : ThemeResponse{
        val entity = themeRepository.findByThemeTitle(themeTitle)
            ?:throw EntityNotFoundException("Theme with themeTitle $themeTitle not found")
        return ThemeMapper.toThemeResponse(entity)
    }

    fun postTheme(themeTitle: ThemeRequest) : ThemeResponse {
        if(themeRepository.existsByThemeTitleAndDescription(themeTitle.themeTitle, themeTitle.description)){
            throw EntityExistsException("Theme already exists")
        }
        val entity = themeRepository.save(ThemeMapper.toThemeEntity(themeTitle))
        return ThemeMapper.toThemeResponse(entity)
    }

    fun putThemeById(
        id: Long,
        theme: ThemeRequest
    ): ThemeResponse {
        val result = themeRepository.findById(id)
            .orElseThrow { throw EntityNotFoundException("Theme with ID=$id not found") }
            .run{
                this.themeTitle = theme.themeTitle
                this.description = theme.description
                this
            }

        themeRepository.save(result)
        return ThemeMapper.toThemeResponse(result)
    }

    fun deleteThemeById(id: Long) =
        if(themeRepository.existsById(id))themeRepository.deleteById(id)
        else throw EntityNotFoundException("Theme with ID $id not found")
}

object ThemeMapper {
    fun toThemeResponse(entity: ThemeEntity): ThemeResponse =
        ThemeResponse(
            id = entity.id,
            themeTitle = entity.themeTitle,
            description = entity.description,
            updateAt = entity.updateAt
        )

    fun toThemeEntity(theme: ThemeRequest): ThemeEntity =
        ThemeEntity(
            themeTitle = theme.themeTitle,
            description = theme.description
        )
}