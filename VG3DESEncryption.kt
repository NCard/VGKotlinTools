package vgTools

import java.lang.Exception
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESedeKeySpec
import javax.crypto.spec.IvParameterSpec



class VG3DESEncryption(
    private val key: ByteArray,
    var keyIv: ByteArray = key,
    var paddingMode: PaddingMode = PaddingMode.NO_PADDING,
    private val desMode: DesMode = DesMode.EDE
) {
    private val spec = DESedeKeySpec(key)
    private val keyFactory = SecretKeyFactory.getInstance(desMode.value)
    private val desKey = keyFactory.generateSecret(spec)

    private var cipher: Cipher? = null

    enum class DesMode(val value: String) {
        EDE("desede")
    }

    enum class EncryptionMode(val value: Int) {
        ENCODE(Cipher.ENCRYPT_MODE),
        DECODE(Cipher.DECRYPT_MODE)
    }

    enum class PaddingMode(val value: String) {
        PKCS5Padding("PKCS5Padding"),
        NO_PADDING("NoPadding")
    }

    enum class EncryptionType(val value: String) {
        ECB("ECB"),
        CBC("CBC");

        fun isCBC() = this == CBC
    }

    @Throws(Exception::class)
    fun ecb(data: ByteArray, ecbMode: EncryptionMode = EncryptionMode.ENCODE): ByteArray  = build(data, ecbMode, EncryptionType.ECB)

    @Throws(Exception::class)
    fun cbc(data: ByteArray, cbcMode: EncryptionMode = EncryptionMode.ENCODE): ByteArray  = build(data, cbcMode, EncryptionType.CBC)

    @Throws(Exception::class)
    private fun build(data: ByteArray, encryptionMode: EncryptionMode, encryptionType: EncryptionType): ByteArray {
        val ivParameterSpec = IvParameterSpec(keyIv)
        instantiationCipher(encryptionType)

        cipherInit(encryptionType, encryptionMode, ivParameterSpec)

        return cipher!!.doFinal(data)
    }

    private fun cipherInit(encryptionType: EncryptionType, encryptionMode: EncryptionMode, ivParameterSpec: IvParameterSpec) {
        if (encryptionType.isCBC()) cipher!!.init(encryptionMode.value, desKey, ivParameterSpec)
        else cipher!!.init(encryptionMode.value, desKey)
    }

    private fun instantiationCipher (encryptionType: EncryptionType) {
        cipher = Cipher.getInstance("${desMode.value}/${encryptionType.value}/${paddingMode.value}")
    }
}