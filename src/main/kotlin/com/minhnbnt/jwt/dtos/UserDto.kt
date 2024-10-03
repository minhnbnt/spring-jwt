package com.minhnbnt.jwt.dtos

import jakarta.validation.constraints.NotEmpty


data class UserDto(

    @field:NotEmpty
    val username: String,

    @field:NotEmpty
    val password: String
)