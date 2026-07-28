package com.example.myFirstSpringProject.exceptionHandler

import com.example.myFirstSpringProject.model.ExceptionResponse
import jakarta.persistence.EntityExistsException
import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import java.time.Instant

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFound(ex: EntityNotFoundException, request: WebRequest): ResponseEntity<ExceptionResponse> {
        val response = ExceptionResponse(
            status = HttpStatus.NOT_FOUND.value(),
            title = "Not Found",
            message = ex.message,
            path = request.getDescription(false)
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
    }

    @ExceptionHandler(EntityExistsException::class)
    fun handleEntityExists(ex: EntityExistsException, request: WebRequest): ResponseEntity<ExceptionResponse> {
        val response = ExceptionResponse(
            status = HttpStatus.CONFLICT.value(),
            title = "Conflict",
            message = ex.message,
            path = request.getDescription(false)
        )
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException, request: WebRequest): ResponseEntity<ExceptionResponse> {
        val errors = ex.bindingResult.fieldErrors.associate {
            it.field to (it.defaultMessage ?: "Invalid value")
        }
        val response = ExceptionResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            title = "Validation Failed",
            message = "Invalid request data",
            path = request.getDescription(false),
            details = errors
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    // Общий обработчик для всех остальных исключений (можно добавить по желанию)
    @ExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception, request: WebRequest): ResponseEntity<ExceptionResponse> {
        val response = ExceptionResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            title = "Internal Server Error",
            message = ex.message ?: "Unexpected error",
            path = request.getDescription(false)
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
    }
}