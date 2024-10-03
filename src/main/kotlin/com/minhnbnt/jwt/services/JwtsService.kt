package com.minhnbnt.jwt.services

import com.minhnbnt.jwt.dtos.UserDto
import com.minhnbnt.jwt.models.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.*
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
    val userService: UserService,
    val authenticationProvider: DaoAuthenticationProvider

) {

    private fun generateToken(user: User, isRefresh: Boolean): Jwt {
        var lifetime = accessTokenLifetime
        if (isRefresh) {
            lifetime = refreshTokenLifetime
        }

        val issued = Instant.now()
        val expiration = issued.plusSeconds(lifetime)

        val claimsSet = JwtClaimsSet.builder()
            .subject(user.getUsername())
            .issuedAt(issued)
            .expiresAt(expiration)
            .build()

        val parameter = JwtEncoderParameters.from(
            JwsHeader.with(MacAlgorithm.valueOf(algorithm)).build(),
            claimsSet
        )

        return jwtEncoder.encode(parameter)
    }


    fun tokenObtainPair(dto: UserDto): Map<String, String> {
        val authentication = authenticationProvider.authenticate(
            UsernamePasswordAuthenticationToken(
                dto.username, dto.password
            )
        )

        val user = userService.getUserByAuthentication(authentication)
            .orElseThrow { BadCredentialsException("Principal is not of User type.") }

        return mapOf(
            "access" to generateToken(user, false).getTokenValue(),
            "refresh" to generateToken(user, true).getTokenValue()
        )
    }
}