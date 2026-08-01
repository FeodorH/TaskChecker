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

data class TaskRequest(
    @field:NotBlank(message = "Title is required")
    @field:Size(max = 50, message = "Title must be at most 50 characters")
    val title: String,

    @field:NotBlank(message = "Theme is required")
    @field:Size(max = 50, message = "Theme must be at most 50 characters")
    val theme: String,

    @field:Size(max = 50, message = "Author must be at most 50 characters")
    val author: String? = null,

    val description: String? = null,

    val isStarted: Boolean = false
)

data class TaskResponse(
    val id: Long,
    val themeId: Long,
    val title: String,
    val author: String?,
    val theme: String,
    val description: String?,
    val isStarted: Boolean,
    val updateAt: LocalDateTime
)

@Entity
@Table(name = "tasks")
data class TaskEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @Id                                                  //TODO
    val themeId: Long,
    @Column(nullable = false, length = 50)
    var title: String = "",
    @Column(nullable = true, length = 50)
    var author: String? = null,
    @Column(nullable = true)
    var description: String? = null,
    @Column(nullable = false)
    var isStarted: Boolean = false,
    @UpdateTimestamp @Column(updatable = true)
    var updateAt: LocalDateTime = LocalDateTime.now()
)