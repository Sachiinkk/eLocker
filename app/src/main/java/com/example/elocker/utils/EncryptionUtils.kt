package com.example.elocker.utils

import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.KeyFactory
import java.security.PublicKey
import java.security.Security
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import android.util.Base64

fun encryptAadhaar(Aadhaar: String, base64PublicKey: String): String {
    Security.addProvider(BouncyCastleProvider())

    val keyBytes = Base64.decode(base64PublicKey.trim(), Base64.DEFAULT)
    val keySpec = X509EncodedKeySpec(keyBytes)
    val publicKey: PublicKey = KeyFactory.getInstance("RSA").generatePublic(keySpec)

    val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
    cipher.init(Cipher.ENCRYPT_MODE, publicKey)

    val encryptedBytes = cipher.doFinal(Aadhaar.trim().toByteArray())
    return Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
}
