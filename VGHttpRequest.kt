package vgTools

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object SSLSocketClient {

    @Throws(Exception::class)
    fun httpsRequest(requestUrl: String, requestMethod: String = "POST", data: String?, headers: MutableMap<String, String> = mutableMapOf()): String {
        var buffer: StringBuffer? = null
        //建立SSLContext
        val sslContext = SSLContext.getInstance("SSL")
        val tm = arrayOf<TrustManager>(MyX509TrustManager())
        //初始化
        sslContext.init(null, tm, java.security.SecureRandom())
        //獲取SSLSocketFactory物件
        val ssf = sslContext.socketFactory
        val url = URL(requestUrl)
        val conn = url.openConnection() as HttpsURLConnection
        for (header in headers) {
            conn.setRequestProperty(header.key, header.value)
        }
        conn.doOutput = true
        conn.doInput = true
        conn.useCaches = false
        conn.requestMethod = requestMethod

        //設置當前實例使用的SSLSoctetFactory
        conn.sslSocketFactory = ssf
        conn.connect()

        //往伺服器端寫內容
        if (null != data) {
            val os = conn.outputStream
            os.write(data.toByteArray(charset("utf-8")))
            os.close()
        }
        //讀取伺服器端返回的内容
        val `is` = conn.inputStream
        val isr = InputStreamReader(`is`, "utf-8")
        val br = BufferedReader(isr)
        buffer = StringBuffer()
        var line: String? = br.readLine()
        do {
            buffer.append(line)
            line = br.readLine()
        }while (line != null)

        return buffer.toString()
    }
}

class MyX509TrustManager : X509TrustManager {

    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}

    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}

    override fun getAcceptedIssuers(): Array<X509Certificate>? { return null }

}