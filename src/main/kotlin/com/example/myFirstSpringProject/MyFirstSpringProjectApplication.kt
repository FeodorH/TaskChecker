package com.example.myFirstSpringProject

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MyFirstSpringProjectApplication

fun main(args: Array<String>) {
    runApplication<MyFirstSpringProjectApplication>(*args)
}
