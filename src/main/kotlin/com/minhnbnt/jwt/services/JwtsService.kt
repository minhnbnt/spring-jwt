package com.minhnbnt.jwt.services

import com.minhnbnt.jwt.dtos.UserDto
import com.minhnbnt.jwt.dtos.tokens.TokenObtainPairDto
import com.minhnbnt.jwt.dtos.tokens.TokenRefreshResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant


@Service
class JwtsService
@Autowired constructor(

    @Value("\${jwts.access-token-lifetime}")
    val accessTokenLifetime: Duration,

    @Value("\${jwts.refresh-token-lifetime}")
    val refreshTokenLifetime: Duration,

    val jwtEncoder: JwtEncoder,

    val authenticationManager: AuthenticationManager

) {

    private fun generateToken(auth: Authentication, lifeTime: Duration): Jwt {

        val issued = Instant.now()
        val expiration = issued + lifeTime

        val claimsSet = JwtClaimsSet.builder()
            .subject(auth.name)
            .issuedAt(issued)
            .expiresAt(expiration)
            .build()

        val parameters = JwtEncoderParameters.from(claimsSet)

        return jwtEncoder.encode(parameters)
    }

    fun tokenObtainPair(dto: UserDto): TokenObtainPairDto {

        val auth = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                dto.username, dto.password
            )
        )

        return TokenObtainPairDto(
            access = generateToken(auth, accessTokenLifetime).tokenValue,
            refresh = generateToken(auth, refreshTokenLifetime).tokenValue
        )
    }

    fun refreshToken(refreshToken: String): TokenRefreshResponseDto {

        val auth = authenticationManager.authenticate(
            BearerTokenAuthenticationToken(refreshToken)
        )

        val accessToken = generateToken(auth, accessTokenLifetime)
        return TokenRefreshResponseDto(accessToken.tokenValue)
    }
}