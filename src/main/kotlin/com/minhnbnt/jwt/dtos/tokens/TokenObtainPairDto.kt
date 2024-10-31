package com.minhnbnt.jwt.dtos.tokens

data class TokenObtainPairDto(
    val access: String,
    val refresh: String
)