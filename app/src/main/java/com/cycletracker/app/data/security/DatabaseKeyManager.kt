package com.cycletracker.app.data.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages the SQLCipher database passphrase.
 *
 * Strategy:
 *  1. Generate a random 32-byte passphrase once.
 *  2. Encrypt it with an AES-256-GCM key stored in Android Keystore
 *     (hardware-backed on supported devices; never leaves the secure element).
 *  3. Store the encrypted blob in SharedPreferences (safe to store — useless
 *     without the Keystore key).
 *
 * Because allowBackup="false" is set in the manifest, neither the database
 * nor SharedPreferences is included in cloud backups.
 */
@Singleton
class DatabaseKeyManager @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: Context
) {
    private companion object {
        const val KEYSTORE_ALIAS = "luna_cycle_db_key_v1"
        const val PREFS_NAME = "luna_secure_prefs"
        const val PREF_ENCRYPTED_PASSPHRASE = "enc_db_pass"
        const val GCM_TAG_LENGTH = 128
        const val GCM_IV_LENGTH = 12
    }

    private val keyStore = KeyStore.getInstance("AndroidKeyStore").also { it.load(null) }

    fun getOrCreatePassphrase(): ByteArray {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val stored = prefs.getString(PREF_ENCRYPTED_PASSPHRASE, null)
        return if (stored != null) {
            decrypt(Base64.decode(stored, Base64.NO_WRAP))
        } else {
            val passphrase = ByteArray(32).also { SecureRandom().nextBytes(it) }
            val encrypted = encrypt(passphrase)
            prefs.edit()
                .putString(PREF_ENCRYPTED_PASSPHRASE, Base64.encodeToString(encrypted, Base64.NO_WRAP))
                .apply()
            passphrase
        }
    }

    private fun masterKey(): SecretKey {
        if (!keyStore.containsAlias(KEYSTORE_ALIAS)) {
            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
                .apply {
                    init(
                        KeyGenParameterSpec.Builder(
                            KEYSTORE_ALIAS,
                            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                        )
                            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                            .setKeySize(256)
                            .setUserAuthenticationRequired(false)
                            .build()
                    )
                }
                .generateKey()
        }
        return keyStore.getKey(KEYSTORE_ALIAS, null) as SecretKey
    }

    private fun encrypt(data: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, masterKey())
        val iv = cipher.iv          // 12 bytes
        val ciphertext = cipher.doFinal(data)
        return iv + ciphertext      // prepend IV for decryption
    }

    private fun decrypt(blob: ByteArray): ByteArray {
        val iv = blob.copyOfRange(0, GCM_IV_LENGTH)
        val ciphertext = blob.copyOfRange(GCM_IV_LENGTH, blob.size)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, masterKey(), GCMParameterSpec(GCM_TAG_LENGTH, iv))
        return cipher.doFinal(ciphertext)
    }
}
