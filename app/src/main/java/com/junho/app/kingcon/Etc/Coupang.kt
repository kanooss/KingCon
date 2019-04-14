package com.junho.app.kingcon.Etc

import android.os.StrictMode
import android.util.Log
import cz.msebera.android.httpclient.HttpHost
import cz.msebera.android.httpclient.client.methods.RequestBuilder
import cz.msebera.android.httpclient.entity.StringEntity
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder
import cz.msebera.android.httpclient.util.EntityUtils
import org.apache.commons.codec.binary.Hex

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import java.nio.charset.Charset
import java.security.GeneralSecurityException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone


//쿠팡 api 키 받아오기
object Coupang {
    private val ALGORITHM = "HmacSHA256"
    private val STANDARD_CHARSET = Charset.forName("UTF-8")
    //TODO 아직 일반 상품 데이터는 못가져오는듯
    /**
     * Generate HMAC signature
     * @param method
     * @param uri http request uri
     * @param secretKey secret key that Coupang partner granted for calling open api
     * @param accessKey access key that Coupang partner granted for calling open api
     * @return HMAC signature
     */
    private val DOMAIN = "https://api-gateway.coupang.com"
    private val ACCESS_KEY = "aee6fe7a-187b-4ee3-b57a-379d58def69e"
    private val SECRET_KEY = "715f0f7530d0ba544d9be0469df9d6c40dc700f8"

    private val REQUEST_JSON =
        "{\"coupangUrls\": [\"https://www.coupang.com/np/search?component=&q=good&channel=user\",\"https://www.coupang.com/np/coupangglobal\"]}"

    fun getDeepLink() {
        val URL = "/v2/providers/affiliate_open_api/apis/openapi/deeplink"
        val REQUEST_METHOD = "POST"

        val parts = URL.split("\\?".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (parts.size > 2) {
            throw RuntimeException("incorrect uri format")
        } else {
            val path = parts[0]
            var query = ""
            if (parts.size == 2) {
                query = parts[1]
            }

            val dateFormatGmt = SimpleDateFormat("yyMMdd'T'HHmmss'Z'")
            dateFormatGmt.timeZone = TimeZone.getTimeZone("GMT")
            val datetime = dateFormatGmt.format(Date())
            val message = datetime + REQUEST_METHOD + path + query

            val signature: String
            try {
                val signingKey = SecretKeySpec(SECRET_KEY.toByteArray(STANDARD_CHARSET), ALGORITHM)
                val mac = Mac.getInstance(ALGORITHM)
                mac.init(signingKey)
                val rawHmac = mac.doFinal(message.toByteArray(STANDARD_CHARSET))
                signature = String(Hex.encodeHex(rawHmac))
            } catch (e: GeneralSecurityException) {
                throw IllegalArgumentException("Unexpected error while creating hash: " + e.message, e)
            }

            val authorization = String.format(
                "CEA algorithm=%s, access-key=%s, signed-date=%s, signature=%s",
                "HmacSHA256",
                ACCESS_KEY,
                datetime,
                signature
            )

            // Send request
            val entity = StringEntity(REQUEST_JSON, "UTF-8")
            entity.setContentEncoding("UTF-8")
            entity.setContentType("application/json")

            val host = HttpHost.create(DOMAIN)
            val request = RequestBuilder
                .post(URL).setEntity(entity)
                .addHeader("Authorization", authorization)
                .build()
            if (android.os.Build.VERSION.SDK_INT > 9) {
                val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
                StrictMode.setThreadPolicy(policy)
            }
            val httpResponse = HttpClientBuilder.create().build().execute(host, request)

            Log.d("!!!dddd", EntityUtils.toString(httpResponse.entity))
        }
    }

    fun getPL() {
        val URL = "/v2/providers/affiliate_open_api/apis/openapi/products?productId=27613130"
        val REQUEST_METHOD = "GET"

        val parts = URL.split("\\?".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (parts.size > 2) {
            throw RuntimeException("incorrect uri format")
        } else {
            val path = parts[0]
            var query = ""
            if (parts.size == 2) {
                query = parts[1]
            }

            val dateFormatGmt = SimpleDateFormat("yyMMdd'T'HHmmss'Z'")
            dateFormatGmt.timeZone = TimeZone.getTimeZone("GMT")
            val datetime = dateFormatGmt.format(Date())
            val message = datetime + REQUEST_METHOD + path + query

            val signature: String
            try {
                val signingKey = SecretKeySpec(SECRET_KEY.toByteArray(STANDARD_CHARSET), ALGORITHM)
                val mac = Mac.getInstance(ALGORITHM)
                mac.init(signingKey)
                val rawHmac = mac.doFinal(message.toByteArray(STANDARD_CHARSET))
                signature = String(Hex.encodeHex(rawHmac))
            } catch (e: GeneralSecurityException) {
                throw IllegalArgumentException("Unexpected error while creating hash: " + e.message, e)
            }

            val authorization = String.format(
                "CEA algorithm=%s, access-key=%s, signed-date=%s, signature=%s",
                "HmacSHA256",
                ACCESS_KEY,
                datetime,
                signature
            )

            // Send request
            val entity = StringEntity(REQUEST_JSON, "UTF-8")
            entity.setContentEncoding("UTF-8")
            entity.setContentType("application/json")

            val host = HttpHost.create(DOMAIN)
            val request = RequestBuilder
                .get(URL).setEntity(entity)
                .addHeader("Authorization", authorization)
                .build()
            if (android.os.Build.VERSION.SDK_INT > 9) {
                val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
                StrictMode.setThreadPolicy(policy)
            }
            val httpResponse = HttpClientBuilder.create().build().execute(host, request)

            Log.d("!!!dddd", EntityUtils.toString(httpResponse.entity))
        }
    }
}