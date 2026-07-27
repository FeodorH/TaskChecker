package com.example.myFirstSpringProject.logging

import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class ApplicationActivityLogging : ApplicationRunner {
    private val logger = LoggerFactory.getLogger("AspectLogger")

    override fun run(args: ApplicationArguments?) {
        logger.info("Application started successfully!")
    }

    @PreDestroy
    fun onShutdown() {
        logger.info("Application is shutting down...")
    }
}