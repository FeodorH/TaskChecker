package com.example.myFirstSpringProject.controller

import com.example.myFirstSpringProject.model.ThemeRequest
import com.example.myFirstSpringProject.model.ThemeResponse
import com.example.myFirstSpringProject.service.ThemeService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/themes")
class ThemeController(
    private val themeService: ThemeService
) {

    @GetMapping
    fun getAllThemes(
        @PageableDefault(size = 10, sort = ["themeTitle"], direction = Sort.Direction.ASC) pageable: Pageable
    ): Page<ThemeResponse> = themeService.getAllThemes(pageable)

    @GetMapping("/by-id")
    fun getThemeById(@RequestParam(value = "id") id: Long) : ResponseEntity<ThemeResponse> =
        ResponseEntity<ThemeResponse>.ok(themeService.getThemeById(id))

    @GetMapping("/by-themeTitle")
    fun getThemeByThemeTitle(@RequestParam(value = "themeTitle") themeTitle: String) : ResponseEntity<ThemeResponse> =
        ResponseEntity<ThemeResponse>.ok(themeService.getThemeByThemeTitle(themeTitle))

    @PostMapping
    fun postTheme(@Valid @RequestBody theme: ThemeRequest) : ResponseEntity<ThemeResponse> {
        val result = themeService.postTheme(theme)
        return ResponseEntity.status(HttpStatus.CREATED).body(result)
    }

    @PutMapping("/{id}")
    fun putTheme(@PathVariable id: Long,
                 @Valid @RequestBody theme: ThemeRequest) : ResponseEntity<ThemeResponse> {
        return ResponseEntity.ok(themeService.putThemeById(id,theme))
    }

    @DeleteMapping("/{id}")
    fun deleteThemeById(@PathVariable id: Long) : ResponseEntity<Unit> {
        themeService.deleteThemeById(id)
        return ResponseEntity.noContent().build()
    }
}