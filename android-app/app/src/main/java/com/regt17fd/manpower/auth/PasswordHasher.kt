package com.regt17fd.manpower.auth

import at.favre.lib.crypto.bcrypt.BCrypt
import javax.inject.Inject
import javax.inject.Singleton

/** Thin wrapper so no other file imports the BCrypt library directly. */
@Singleton
class PasswordHasher @Inject constructor() {
    fun hash(plainPassword: String): String =
        BCrypt.withDefaults().hashToString(12, plainPassword.toCharArray())

    fun verify(plainPassword: String, hash: String): Boolean =
        BCrypt.verifyer().verify(plainPassword.toCharArray(), hash).verified
}
