package com.minhnbnt.jwt.controllers

import com.minhnbnt.jwt.dtos.tokens.TokenRefreshDto
import com.minhnbnt.jwt.dtos.UserDto
import com.minhnbnt.jwt.services.JwtsService
import com.minhnbnt.jwt.services.UserService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/user/")
class UserController
@Autowired constructor(

    val userService: UserService,
    val jwtsService: JwtsService

) {

    @PostMapping("/register/")
    @ResponseStatus(HttpStatus.CREATED)
    fun registerUser(@Valid @RequestBody dto: UserDto) =
        userService.createUser(dto)

    @PostMapping("/token/")
    fun obtainToken(@Valid @RequestBody dto: UserDto) =
        jwtsService.tokenObtainPair(dto)

    @PostMapping("/token/refresh/")
    fun refreshToken(@Valid @RequestBody dto: TokenRefreshDto) =
        jwtsService.refreshToken(dto.refresh)
}