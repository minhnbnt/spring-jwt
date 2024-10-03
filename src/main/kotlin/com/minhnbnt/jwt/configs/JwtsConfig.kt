package com.minhnbnt.jwt.configs

import com.nimbusds.jose.jwk.source.ImmutableSecret
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import java.util.*
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec


@Configuration
class JwtsConfig
@Autowired constructor(

    @Value("\${jwts.secret-key}")
    val secretKey: String

) {

    @Bean
    fun getSecretKey(): SecretKey {
        val secretByte = Base64.getDecoder().decode(secretKey)
        return SecretKeySpec(secretByte, "RSA")
    }

    @Bean
    fun jwtDecoder(): JwtDecoder {
        return NimbusJwtDecoder.withSecretKey(getSecretKey()).build()
    }

    @Bean
    fun jwtEncoder(): JwtEncoder {
        val jwkSource = ImmutableSecret<SecurityContext>(getSecretKey())
        return NimbusJwtEncoder(jwkSource)
    }
}