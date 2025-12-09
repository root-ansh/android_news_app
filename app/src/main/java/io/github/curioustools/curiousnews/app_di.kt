package io.github.curioustools.curiousnews

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.Keep
import androidx.annotation.RequiresPermission
import androidx.annotation.WorkerThread
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Authenticator
import okhttp3.Cache
import okhttp3.Call
import okhttp3.CertificatePinner
import okhttp3.ConnectionPool
import okhttp3.ConnectionSpec
import okhttp3.CookieJar
import okhttp3.Dispatcher
import okhttp3.Dns
import okhttp3.EventListener
import okhttp3.Interceptor
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import timber.log.Timber
import java.io.IOException
import java.net.CookieManager
import java.net.CookiePolicy
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.ProxySelector
import java.net.Socket
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import javax.net.SocketFactory
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager
import kotlin.also
import kotlin.collections.forEach
import kotlin.let




@Module
@InstallIn(SingletonComponent::class)
object AppDI {

    @Provides
    fun provideSharedPrefs(@ApplicationContext context: Context, ): SharedPrefs {
        return SharedPrefs(context)

    }

    @Provides
    fun getRetrofit(@ApplicationContext ctx: Context): Retrofit {
        val client = if (isDebugApp()) {
            getMyUnsafeOkHttpBuilderDEBUG(null, null, ctx).build()
        } else {
            getMyUnsafeOkHttpBuilderPROD(null, null, ctx).build()
        }
        return getRetrofitBuilder(AppApiService.BASE, client).addScalerConvertor().addGsonConvertor().build()
    }


//    @Provides
//    fun providesLoginUseCase(@ApplicationContext context: Context): LoginUseCase{
//        return DI.getLoginUseCase(context)
//    }

}

@Module
@InstallIn(SingletonComponent::class)
abstract class AppApisDI{
    @Binds
    abstract fun bindRepo(repoImpl: AppApiRepoImpl): AppApiRepo


    companion object{
        @Provides
        fun getAppApiService(retrofit: Retrofit): AppApiService {
            return retrofit.create(AppApiService::class.java)
        }
    }

}


//todo fix


fun getGsonBuilder(serializeNulls: Boolean = false, pretty: Boolean = false, ): GsonBuilder {
    val builder = GsonBuilder()
    if (serializeNulls) builder.serializeNulls()
    if (pretty) builder.setPrettyPrinting()
    return builder
}

fun getGsonObject(builder: GsonBuilder = getGsonBuilder()): Gson = builder.create()



fun getSocketFactoryAndUnsafeTrustManager(): Pair<SSLSocketFactory, X509TrustManager> {
    val trustAllCerts = // a trust manager that does not validate certificate chains
        object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate?>?, authType: String?) {
            }

            override fun checkServerTrusted(chain: Array<X509Certificate?>?, authType: String?) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        }

    // Install the all-trusting trust manager and  Create an ssl socket factory with our all-trusting manager
    val sslSocketFactory: SSLSocketFactory = SSLContext.getInstance("SSL").let {
        it.init(null, arrayOf(trustAllCerts), SecureRandom())
        it.socketFactory
    }
    return Pair(sslSocketFactory, trustAllCerts)
}

fun getOkHttpClientBuilder(
    authenticator: Authenticator? = null,
    cache: Cache? = null,
    callTimeout: Pair<Long, TimeUnit>? = null,
    certificatePinner: CertificatePinner? = null,
    connectionPool: ConnectionPool? = null,
    connectionSpecList: List<ConnectionSpec>? = null,
    connectTimeout: Pair<Long, TimeUnit>? = null,
    cookieJar: CookieJar? = null,
    dispatcher: Dispatcher? = null,
    dns: Dns? = null,
    elFactory: EventListener.Factory? = null,
    eventListener: EventListener? = null,
    followRedirects: Boolean? = null,
    followSSLRedirects: Boolean? = null,
    hostnameVerifier: HostnameVerifier? = null,
    interceptors: List<Interceptor> = listOf(),
    minCompress: Long? = null,
    networkInterceptors: List<Interceptor> = listOf(),
    pingInterval: Pair<Long, TimeUnit>? = null,
    protocols: List<Protocol>? = null,
    proxy: Proxy? = null,
    proxyAuthenticator: Authenticator? = null,
    proxySelector: ProxySelector? = null,
    readTimeout: Pair<Long, TimeUnit>? = null,
    retryOnConnectionFailure: Boolean? = null,
    socketFactory: SocketFactory? = null,
    sslSocketFactory: Pair<SSLSocketFactory, X509TrustManager>? = null,
    writeTimeout: Pair<Long, TimeUnit>? = null, ): OkHttpClient.Builder {
    return OkHttpClient.Builder().let { builder ->
        authenticator?.let { builder.authenticator(it) }
        cache?.let { builder.cache(cache) }
        callTimeout?.let { builder.callTimeout(it.first, it.second) }
        certificatePinner?.let { builder.certificatePinner(it) }
        connectionPool?.let { builder.connectionPool(it) }
        connectionSpecList?.let { builder.connectionSpecs(it) }
        connectTimeout?.let { builder.connectTimeout(it.first, it.second) }
        cookieJar?.let { builder.cookieJar(it) }
        dispatcher?.let { builder.dispatcher(it) }
        dns?.let { builder.dns(it) }
        elFactory?.let { builder.eventListenerFactory(it) }
        eventListener?.let { builder.eventListener(it) }
        followRedirects?.let { builder.followRedirects(it) }
        followSSLRedirects?.let { builder.followSslRedirects(it) }
        hostnameVerifier?.let { builder.hostnameVerifier(it) }
        interceptors.forEach { builder.addInterceptor(it) }
        minCompress?.let { builder.minWebSocketMessageToCompress(it) }
        networkInterceptors.forEach { builder.addNetworkInterceptor(it) }
        pingInterval?.let { builder.pingInterval(it.first, it.second) }
        pingInterval?.let { builder.pingInterval(it.first, it.second) }
        protocols?.let { builder.protocols(it) }
        proxy?.let { builder.proxy(it) }
        proxyAuthenticator?.let { builder.proxyAuthenticator(it) }
        proxySelector?.let { builder.proxySelector(it) }
        readTimeout?.let { builder.readTimeout(it.first, it.second) }
        retryOnConnectionFailure?.let { builder.retryOnConnectionFailure(it) }
        socketFactory?.let { builder.socketFactory(it) }
        sslSocketFactory?.let { builder.sslSocketFactory(it.first, it.second) }
        writeTimeout?.let { builder.writeTimeout(it.first, it.second) }
        builder
    }
}

fun getMyOkHttpBuilderBasic(
    sslSocketFactory: Pair<SSLSocketFactory, X509TrustManager>? = null,
    cookieJar: CookieJar? = null,
    retryOnConnectionFailure: Boolean? = null
) =  getOkHttpClientBuilder(
    readTimeout = Pair(1L, TimeUnit.MINUTES),
    writeTimeout = Pair(1L, TimeUnit.MINUTES),
    connectTimeout = Pair(1L, TimeUnit.MINUTES),
    sslSocketFactory = sslSocketFactory,
    cookieJar = cookieJar,
    retryOnConnectionFailure = retryOnConnectionFailure
)
fun getMyUnsafeOkHttpBuilderPROD(appHeaders: HashMap<String, String>?, skipAllHeaderEnabled: Boolean?, ctx: Context?): OkHttpClient.Builder {
    val cookieJar =
        JavaNetCookieJar(CookieManager().also { it.setCookiePolicy(CookiePolicy.ACCEPT_ALL) })

    val builder =  getMyOkHttpBuilderBasic(getSocketFactoryAndUnsafeTrustManager(), cookieJar,true)

    if(appHeaders!=null && skipAllHeaderEnabled!=null) builder.addHeaderInterceptor(appHeaders, skipAllHeaderEnabled)
    if(ctx!=null) builder.addInternetCheckInterceptor(ctx)

    return builder
}

fun getMyUnsafeOkHttpBuilderDEBUG(appHeaders: HashMap<String, String>?, skipAllHeaderEnabled: Boolean?, ctx: Context?): OkHttpClient.Builder {
    val builder = getMyUnsafeOkHttpBuilderPROD(appHeaders, skipAllHeaderEnabled,ctx)
    builder.addNetworkInterceptor(StethoInterceptor())
    builder.addLoggingInterceptor()

    return builder


}


fun getRetrofitBuilder(
    baseUrl: String,
    client: OkHttpClient,
    convertorFactories: List<Converter.Factory> = listOf(),
    callAdapterFactories: List<CallAdapter.Factory> = listOf(),
    callbackExecutor: Executor? = null,
    callFactory: Call.Factory? = null,
    validateEagerly: Boolean? = null, ): Retrofit.Builder {
    return Retrofit.Builder().also { builder ->
        convertorFactories.forEach { builder.addConverterFactory(it) }
        callAdapterFactories.forEach { builder.addCallAdapterFactory(it) }
        baseUrl.let { builder.baseUrl(baseUrl) }
        client.let { builder.client(it) }
        callFactory?.let { builder.callFactory(it) }
        callbackExecutor?.let { builder.callbackExecutor(it) }
        validateEagerly?.let { builder.validateEagerly(it) }
    }
}

fun OkHttpClient.Builder.addHeaderInterceptor(appHeaders: HashMap<String, String>, skipAllHeaderEnabled: Boolean): OkHttpClient.Builder {
    addInterceptor(HeaderInterceptor(appHeaders, skipAllHeaderEnabled))
    return this
}

fun OkHttpClient.Builder.addInternetCheckInterceptor(context: Context): OkHttpClient.Builder {
    //addInterceptor(InternetCheckInterceptor(context))
    return this
}

fun OkHttpClient.Builder.addLoggingInterceptor(severity: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BODY): OkHttpClient.Builder {
    addInterceptor(HttpLoggingInterceptor().also { it.level = severity })
    return this
}

fun Retrofit.Builder.addGsonConvertor(gson: Gson =getGsonObject()): Retrofit.Builder {
    addConverterFactory(GsonConverterFactory.create(gson))
    return this
}



fun Retrofit.Builder.addScalerConvertor(): Retrofit.Builder {
    addConverterFactory(ScalarsConverterFactory.create())
    return this
}


fun isDebugApp():Boolean{
    return BuildConfig.DEBUG
}
fun log(key: String, value: Any? = null, tag: String = "CUSTOM_LOGS") {
    val msg = if (value == null) key else "$key:$value"
    Timber.tag(tag).i(msg)
}



class HeaderInterceptor(
    private val appHeaders: HashMap<String, String>,
    private val skipAllHeaderEnabled: Boolean = false,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        return synchronized(this) {
            val originalRequest = chain.request()
            val requestBuilder =
                if (originalRequest.header(HEADER_SKIP_ALL).contentEquals(HEADER_SKIP_ALL) && skipAllHeaderEnabled) {
                    originalRequest.newBuilder().removeHeader(HEADER_SKIP_ALL)
                }
                else {
                    originalRequest.newBuilder().apply {
                        appHeaders.forEach { (key, value) ->
                            header(key, value)
                        }
                    }
                }

            chain.proceed(requestBuilder.build())
        }
    }

    companion object {
        val OS_PAIR = Pair("x-os", "android")
        val CONTENT_TYPE_PAIR = Pair("Content-Type", "application/json")
        val API_AUTH_PAIR = Pair("app-id", "abcd")//todo use gradle properties/gradle keystore/gradle keychain for this
        const val HEADER_SKIP_ALL = "x-skip-all"
        const val HEADER_AUTH = "Authorization"
        const val HEADER_APP_VERSION = "x-app-version"
        const val ACCESS_COOKIE = "x-access-cookie"
        const val CACHE_CONTROL = "cache-control"
        const val CACHE_CONTROL_NO_CACHE = "no-cache"
        const val HEADER_TIME_ZONE = "x-timezone-offset"
        const val HEADER_USER_KIND = "x-user-kind"
        const val HEADER_APPLICATION_NAME = "x-application-name"
        const val APPLICATION_NAME = "APP_NAME"
    }

}
class InternetCheckInterceptor(private val context: Context? = null) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        if (context == null) return chain.proceed(chain.request())

        val resp = when {
            !isConnectedToInternetProvider(context) -> throw Exception(BaseStatus.NO_INTERNET_CONNECTION.msg)
            !isReceivingInternetPackets() -> throw IOException(BaseStatus.NO_INTERNET_PACKETS_RECEIVED.msg)
            else -> chain.proceed(chain.request())
        }
        return resp
    }

    companion object {
        @JvmStatic
        @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
        fun isConnectedToInternetProvider(ctx: Context): Boolean {
            val cm = ctx.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            cm ?: return false
            val currentNetwork = cm.activeNetwork ?: return false
            val capabilities = cm.getNetworkCapabilities(currentNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }

        @JvmStatic
        @WorkerThread
        fun isReceivingInternetPackets(): Boolean {
            val dnsPort = 53
            val googleIp = "8.8.8.8"
            val timeOut = 1500
            return runCatching {
                val socket = Socket()
                val inetAddress = InetSocketAddress(googleIp, dnsPort)
                socket.connect(inetAddress, timeOut)
                socket.close()
                true
            }.getOrDefault(false)
        }
    }
}

@Keep
sealed class BaseResponse<T>(open val status: BaseStatus) {

    @Keep
    data class Success<T>(val body: T) : BaseResponse<T>(BaseStatus.SUCCESS)

    @Keep
    data class Failure<T>(
        val body: T? = null,
        override val status: BaseStatus,
        var exception: Throwable = Exception(status.msg)
    ) : BaseResponse<T>(status)
}

@Keep
enum class BaseStatus(val code: Int, val msg: String) {
    SUCCESS(200, "SUCCESS"),
    NO_INTERNET_CONNECTION(1001, "No Internet found"),
    NO_INTERNET_PACKETS_RECEIVED(1002, "We are unable to connect to our server. Please check with your internet service provider"),
    USER_NOT_FOUND(400, "User Not Found"),
    APP_NULL_RESPONSE_BODY(888, "No Response found"),
    SERVER_FAILURE(500, "server failure"),
    SERVER_DOWN_502(502, "server down 502"),
    SERVER_DOWN_503(503, "server down 503"),
    SERVER_DOWN_504(504, "server down 504"),
    UNRECOGNISED(-1, "unrecognised error in networking");

    companion object {
        fun getStatusOrDefault(code: Int? = null): BaseStatus = entries.firstOrNull { it.code == code } ?: UNRECOGNISED

        fun getStatusFromException(t: Throwable): BaseStatus = entries.firstOrNull { it.msg.contentEquals(t.message) } ?: UNRECOGNISED

    }
}


@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.M)
fun isAndroidGTEquals23M() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M


/**
 * Retrofit provides a response of format Response(isSuccessful:True/False, body:T/null,...)
 * it treats all failures as null . this Response object on its own is enough to know about the
 * json response, but for convenience we can use a unified sealed class for handling high level
 * distinctions,such as success, failure, token expire failure etc.
 * */
fun <T> retrofit2.Call<T>.executeAndUnify(): BaseResponse<T> {
    return try {
        val response: Response<T?> = this.execute()

        when {
            response.isSuccessful -> {
                when (val body = response.body()) {
                    null -> BaseResponse.Failure(body, BaseStatus.APP_NULL_RESPONSE_BODY)
                    else -> BaseResponse.Success(body)
                }
            }
            else -> {
                val code = response.code()
                val body = response.body()
                val status = BaseStatus.getStatusOrDefault(code)
                val exception = Exception(status.msg)
                val resp = BaseResponse.Failure(body, status, exception)
                resp.exception = exception
                resp
            }
        }
    }
    catch (t: Throwable) {
        BaseResponse.Failure(null, BaseStatus.getStatusFromException(t), t)
    }

}

fun <DTO, RESP> BaseResponse<DTO>.convertTo(successConvertor: (DTO) -> RESP): BaseResponse<RESP> {
    return when (this) {
        is BaseResponse.Failure -> BaseResponse.Failure(null, this.status,this.exception)
        is BaseResponse.Success -> BaseResponse.Success(successConvertor.invoke(this.body))
    }

}




