package com.minhnbnt.jwt.services

import com.minhnbnt.jwt.dtos.UserDto
import com.minhnbnt.jwt.models.User
import com.minhnbnt.jwt.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException


@Service
class UserService
@Autowired constructor(

    val repository: UserRepository,
    val passwordEncoder: PasswordEncoder

) {

    fun createUser(dto: UserDto) {

        if (repository.existsByUsername(dto.username)) {
            throw ResponseStatusException(
                HttpStatus.CONFLICT,
                "Username already exists. Please try a different one."
            )
        }

        val user = User(
            username = dto.username,
            password = passwordEncoder.encode(dto.password)
        )

        repository.save(user)
    }

    fun getUserByAuthentication(authentication: Authentication?): User? {

        val principal = authentication?.principal

        return if (principal is User) {
            principal
        } else {
            null
        }
    }
}