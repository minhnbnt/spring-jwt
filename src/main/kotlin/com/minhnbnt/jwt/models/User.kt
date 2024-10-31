package com.minhnbnt.jwt.models

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false, unique = true)
    private var username: String,

    @Column(nullable = false)
    private var password: String

) : UserDetails {

    override fun equals(other: Any?) =
        other is User && this.id == other.id

    override fun hashCode() = this.id.hashCode()

    override fun getUsername() = username
    override fun getPassword() = password

    override fun getAuthorities()
        : MutableCollection<out GrantedAuthority> = ArrayList()
}