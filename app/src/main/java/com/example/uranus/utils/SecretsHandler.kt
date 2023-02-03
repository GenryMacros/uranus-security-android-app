package com.example.uranus.utils

import com.beust.klaxon.Klaxon
import network.interfaces.LoginResponseBody
import java.security.KeyFactory
import java.security.Signature
import java.security.SignatureException
import java.security.spec.MGF1ParameterSpec
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.PSSParameterSpec
import java.security.spec.X509EncodedKeySpec
import java.util.*



class SecretsHandler {

    fun parseToken(token: String, clientPublic: String): LoginResponseBody? {
        val splittedToken: List<String> = token.split('.')
        val decodedBody = String(Base64.getDecoder().decode(splittedToken[1]))
        val responseBody = Klaxon().parse<LoginResponseBody>(decodedBody)

        if (responseBody == null) {
            return null
        } else {
            val time = System.currentTimeMillis() / 1000
            if (time > responseBody.expiration_date) {
                return null
            }
            val tokenSignature = splittedToken[2].toByteArray()
            if (this.isSignatureOk(clientPublic, tokenSignature)) {
                return responseBody
            }
            return null
        }
    }

    private fun isSignatureOk(public: String, signature: ByteArray): Boolean {
        val signatureSHA256 = Signature.getInstance("SHA256withRSA")
        val publicKey = KeyFactory.getInstance("RSA").generatePublic(PKCS8EncodedKeySpec(Base64.getDecoder().decode(public)))
        signatureSHA256.initVerify(publicKey)

        return try {
            signatureSHA256.verify(signature)
            true
        } catch (e: SignatureException) {
            false
        }
    }
}

fun main() {
    val token = "eyJ0eXAiOiAiSldUIiwgImFsZyI6ICJIUzI1NiJ9.eyJleHBpcmF0aW9uX2RhdGUiOiAxNjc1NDY2MjQ4LCAiZmlyc3RfbmFtZSI6ICJIZW5yeSIsICJsYXN0X25hbWUiOiAiSHVtYW5vaWQiLCAiaWQiOiAxfQ==.RT+M+OZuPEA+o+YIXYRbt2p9MtWYt6F/SJCH6uld4lyD+IALYtswwp1xaZS7KeSAqAEsliLgDEncQAP9QpYQOA=="
    val publicKey = "AAAAB3NzaC1yc2EAAAADAQABAAAAQQC5i/5eeUkEYxsRV4HiP4PiI0kXrsUt+HgLWFkdcGAuYLZetpO4nPVjMhdZwO+Vck0e4HwjPEvCUt5/al1eJX5B"
    val secretsHandler = SecretsHandler()
    val data = secretsHandler.parseToken(token, publicKey)
}