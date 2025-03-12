package com.minhnbnt.jwt.configs

import com.minhnbnt.jwt.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig
@Autowired constructor(val userRepository: UserRepository) {

    private fun loadByUserName(username: String) =
        userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("User not found")

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {

        http {

            csrf { disable() }

            authorizeHttpRequests {
                authorize(anyRequest, permitAll)
            }

            httpBasic { }

            oauth2ResourceServer { jwt { } }
        }

        return http.build()
    }

    @Bean
    fun daoAuthenticationProvider(passwordEncoder: PasswordEncoder)
        : DaoAuthenticationProvider {

        val authProvider = DaoAuthenticationProvider(passwordEncoder)
        authProvider.setUserDetailsService(this::loadByUserName)

        return authProvider
    }

    @Bean
    fun authenticationManager(providers: List<AuthenticationProvider>)
        : AuthenticationManager = ProviderManager(providers)

    @Bean
    fun passwordEncoder(): PasswordEncoder =
        Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8()
}