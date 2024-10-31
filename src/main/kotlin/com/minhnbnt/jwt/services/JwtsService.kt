package com.minhnbnt.jwt.services

import com.minhnbnt.jwt.dtos.UserDto
import com.minhnbnt.jwt.dtos.tokens.TokenObtainPairDto
import com.minhnbnt.jwt.dtos.tokens.TokenRefreshResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.*
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken
import org.springframework.stereotype.Service
import java.time.Instant


@Service
class JwtsService
@Autowired constructor(

    @Value("\${jwts.algorithm}")
    val algorithm: String,

    @Value("\${jwts.access-token-lifetime}")
    val accessTokenLifetime: Long,

    @Value("\${jwts.refresh-token-lifetime}")
    val refreshTokenLifetime: Long,

    val jwtEncoder: JwtEncoder,

    val authenticationManager: AuthenticationManager

) {

    private fun generateToken(auth: Authentication, isRefresh: Boolean): Jwt {

        var lifetime = accessTokenLifetime
        if (isRefresh) {
            lifetime = refreshTokenLifetime
        }

        val issued = Instant.now()
        val expiration = issued.plusSeconds(lifetime)

        val claimsSet = JwtClaimsSet.builder()
            .subject(auth.name)
            .issuedAt(issued)
            .expiresAt(expiration)
            .build()

        val parameter = JwtEncoderParameters.from(
            JwsHeader.with(MacAlgorithm.valueOf(algorithm)).build(),
            claimsSet
        )

        return jwtEncoder.encode(parameter)
    }


    fun tokenObtainPair(dto: UserDto): TokenObtainPairDto {

        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                dto.username, dto.password
            )
        )

        return TokenObtainPairDto(
            access = generateToken(authentication, false).tokenValue,
            refresh = generateToken(authentication, true).tokenValue
        )
    }

    fun refreshToken(refreshToken: String): TokenRefreshResponseDto {

        val authentication = authenticationManager.authenticate(
            BearerTokenAuthenticationToken(refreshToken)
        )

        val accessToken = generateToken(authentication, false)
        return TokenRefreshResponseDto(accessToken.tokenValue)
    }
}