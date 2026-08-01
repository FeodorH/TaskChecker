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
import java.time.Instant
import java.time.LocalDateTime

data class ExceptionResponse(
    val title: String,
    val message: String?,
    val status: Int,
    val path: String,
    val timestamp: Instant = Instant.now(),
    val details: Map<String, String>? = null
)