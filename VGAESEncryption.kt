package vgTools

import org.apache.shiro.codec.Base64
import org.apache.shiro.codec.Hex
import org.jetbrains.annotations.TestOnly
import java.nio.charset.Charset
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class VGAESEncryption(
        key: ByteArray,
        private val iv: ByteArray,
        private val bitMode: Int = 128
) {
    private val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    private val secretKeySpec: SecretKey = SecretKeySpec(key, "AES")


    enum class EncryptionMode(val value: Int) {
        ENCRYPT(Cipher.ENCRYPT_MODE),
        DECRYPT(Cipher.DECRYPT_MODE)
    }

    fun build(data: ByteArray, encryptionMode: EncryptionMode = EncryptionMode.ENCRYPT): ByteArray {
        cipher.init(encryptionMode.value, secretKeySpec, IvParameterSpec(iv))
        return cipher.doFinal(data)
    }
}