package com.minhnbnt.jwt.configs

import com.minhnbnt.jwt.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig
@Autowired constructor(

    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder

) {

    private fun loadByUserName(username: String) =
        userRepository.findByUsername(username)
            .orElseThrow { UsernameNotFoundException("User not found") }


    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {

        http {

            csrf { disable() }

            authorizeHttpRequests {
                authorize(anyRequest, permitAll)
            }

            oauth2ResourceServer {
                jwt {

                    jwtAuthenticationConverter = Converter { source ->

                        val username = source.subject
                        val user = loadByUserName(username)

                        UsernamePasswordAuthenticationToken(
                            user, source, user.authorities
                        )
                    }
                }
            }
        }

        return http.build()
    }


    @Bean
    fun daoAuthenticationProvider(): DaoAuthenticationProvider {

        val authProvider = DaoAuthenticationProvider()

        authProvider.setUserDetailsService(this::loadByUserName)
        authProvider.setPasswordEncoder(passwordEncoder)

        return authProvider
    }
}