package com.minhnbnt.jwt.repositories

import com.minhnbnt.jwt.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun existsByUsername(username: String): Boolean
    fun findByUsername(username: String): User?
}