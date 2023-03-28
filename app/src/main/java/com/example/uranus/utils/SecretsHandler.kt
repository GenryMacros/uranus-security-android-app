package com.example.uranus.utils

import com.beust.klaxon.Klaxon
import network.api.interfaces.LoginResponseBody
import java.security.*
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
            val tokenSignature = Base64.getDecoder().decode(Base64.getDecoder().decode(splittedToken[2]))
            if (this.isSignatureOk(clientPublic, tokenSignature)) {
                return responseBody
            }
            return null
        }
    }

    private fun isSignatureOk(public: String, signature: ByteArray): Boolean {
        val signatureSHA256 = Signature.getInstance("SHA256withRSA")
        val spec = X509EncodedKeySpec(Base64.getDecoder().decode(public))
        val publicKey = KeyFactory.getInstance("RSA").generatePublic(spec)
        signatureSHA256.initVerify(publicKey)

        return try {
            signatureSHA256.verify(signature)
            true
        } catch (e: SignatureException) {
            false
        }
    }
}
