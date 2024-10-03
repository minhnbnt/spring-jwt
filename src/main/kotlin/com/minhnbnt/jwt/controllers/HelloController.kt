package com.minhnbnt.jwt.controllers

import com.minhnbnt.jwt.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController
@Autowired constructor(val userService: UserService) {

    @GetMapping("/api/hello/")
    fun greeting(authentication: Authentication?): Map<String, String> {

        val user = userService.getUserByAuthentication(authentication)

        var message = "Hello, world!"
        if (user.isPresent) {
            val username = user.get().username
            message = String.format("Hello, your username is %s.", username)
        }

        return mapOf("message" to message)
    }
}