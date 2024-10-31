package com.minhnbnt.jwt.dtos.tokens

import jakarta.validation.constraints.NotEmpty

data class TokenRefreshDto(

    @field:NotEmpty
    val refresh: String
)