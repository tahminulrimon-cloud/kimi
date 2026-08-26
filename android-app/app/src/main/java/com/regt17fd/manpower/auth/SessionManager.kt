package com.regt17fd.manpower.auth

import com.regt17fd.manpower.data.local.entity.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

const val SESSION_IDLE_TIMEOUT_MS = 15 * 60 * 1000L

/**
 * Holds the signed-in user for the process lifetime and enforces the
 * 15-minute inactivity auto-logout from the spec. [MainActivity] calls
 * [touch] on every user interaction (Compose pointer input pass-through)
 * and a background check compares against [lastActivityAt].
 */
@Singleton
class SessionManager @Inject constructor() {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _rememberMe = MutableStateFlow(false)

    @Volatile private var lastActivityAt: Long = System.currentTimeMillis()

    fun signIn(user: User, rememberMe: Boolean) {
        _currentUser.value = user
        _rememberMe.value = rememberMe
        touch()
    }

    fun signOut() {
        _currentUser.value = null
    }

    fun updateUser(user: User) {
        _currentUser.value = user
    }

    fun touch() {
        lastActivityAt = System.currentTimeMillis()
    }

    /** Called periodically; signs the user out if idle beyond the timeout. */
    fun checkIdleTimeout() {
        val user = _currentUser.value ?: return
        if (_rememberMe.value) return
        if (System.currentTimeMillis() - lastActivityAt > SESSION_IDLE_TIMEOUT_MS) {
            signOut()
        }
    }
}
