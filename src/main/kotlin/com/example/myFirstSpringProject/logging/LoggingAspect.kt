package com.example.myFirstSpringProject.logging

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Aspect
@Component
class LoggingAspect {

    private val logger = LoggerFactory.getLogger("AspectLogger")

    //for all packages
    @Pointcut("execution(* com.example.myFirstSpringProject..*.*(..))")
    fun serviceLayerMethods() {
    }

    @Before("serviceLayerMethods()")
    fun loggingBefore(joinPoint: JoinPoint) {
        val methodName = joinPoint.signature.name
        val fullClassName = joinPoint.signature.declaringTypeName
        val className = fullClassName.substring(fullClassName.lastIndexOf('.') + 1)
        val packageName = fullClassName.substringBeforeLast('.')

        logger.info("Called $className.$methodName in package $packageName\n")
        print("Called $className.$methodName in package $packageName\n")
    }

    @AfterReturning(pointcut = "serviceLayerMethods()", returning = "result")
    fun logAfterReturning(joinPoint: JoinPoint, result: Any?) {
        val methodName = joinPoint.signature.name
        val className = joinPoint.target.javaClass.simpleName

        logger.info("$className.$methodName returned with: $result")
        println("$className.$methodName returned with: $result")
    }

    @AfterThrowing(pointcut = "serviceLayerMethods()", throwing = "ex")
    fun logAfterThrowing(joinPoint: JoinPoint, ex: Exception) {
        val methodName = joinPoint.signature.name
        val className = joinPoint.target.javaClass.simpleName

        logger.error("ERROR! $className.$methodName failed by exception: ${ex.message}")
        println("ERROR! $className.$methodName failed by exception: ${ex.message}")
    }
}