package com.example.couponcore.service

import com.example.couponcore.domain.User
import com.example.couponcore.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) {

    fun saveAllUser(users: Collection<User>) {
        return userRepository.batchInsert(users)
    }
}