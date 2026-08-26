package com.regt17fd.manpower.data.repository

import com.regt17fd.manpower.auth.PasswordHasher
import com.regt17fd.manpower.data.local.dao.AuditDao
import com.regt17fd.manpower.data.local.dao.UserDao
import com.regt17fd.manpower.data.local.entity.AuditLog
import com.regt17fd.manpower.data.local.entity.User
import javax.inject.Inject
import javax.inject.Singleton

sealed class LoginResult {
    data class Success(val user: User) : LoginResult()
    object InvalidCredentials : LoginResult()
}

@Singleton
class AuthRepository @Inject constructor(
    private val userDao: UserDao,
    private val auditDao: AuditDao,
    private val passwordHasher: PasswordHasher,
) {
    suspend fun login(username: String, password: String): LoginResult {
        val user = userDao.findByUsername(username.trim()) ?: return LoginResult.InvalidCredentials
        if (!passwordHasher.verify(password, user.passwordHash)) return LoginResult.InvalidCredentials
        auditDao.insert(AuditLog(userId = user.id, userName = user.fullName, action = "LOGIN", detail = "Signed in"))
        return LoginResult.Success(user)
    }

    suspend fun changePassword(user: User, newPassword: String) {
        val updated = user.copy(passwordHash = passwordHasher.hash(newPassword), mustChangePassword = false)
        userDao.update(updated)
        auditDao.insert(AuditLog(userId = user.id, userName = user.fullName, action = "PASSWORD_CHANGE", detail = "Password changed"))
    }
}
