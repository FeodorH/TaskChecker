package com.example.myFirstSpringProject.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

data class ThemeRequest(
    @field:NotBlank(message = "Theme is required")
    @field:Size(max = 50, message = "Theme must be at most 50 characters")
    val theme: String,
    val description: String?
)

data class ThemeResponse(
    val id: Long,
    val theme: String,
    val description: String?,
    val updateAt: LocalDateTime
)

@Entity
@Table(name = "theme")
data class ThemeEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false, length = 50)
    var theme: String,
    @Column()
    var description: String? = null,
    @UpdateTimestamp @Column(updatable = true)
    var updateAt: LocalDateTime = LocalDateTime.now()
)
