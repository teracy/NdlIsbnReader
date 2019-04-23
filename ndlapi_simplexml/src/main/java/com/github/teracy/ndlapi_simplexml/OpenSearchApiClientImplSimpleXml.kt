package com.github.teracy.ndlapi_simplexml

import com.github.teracy.ndlapi.OpenSearchApiClient
import com.github.teracy.ndlapi.response.Book
import com.github.teracy.ndlapi_simplexml.response.ResponseSimpleXml
import com.github.teracy.ndlapi_simplexml.response.convert
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import javax.inject.Inject

/**
 * Open Search APIクライアントのSimpleXml実装
 */
class OpenSearchApiClientImplSimpleXml @Inject constructor(okHttpClient: OkHttpClient) : OpenSearchApiClient {
    private val service: OpenSearchApiService = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl("http://iss.ndl.go.jp")
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(SimpleXmlConverterFactory.create())
        .build()
        .create(OpenSearchApiService::class.java)

    override fun search(isbn: String): Single<Book> {
        return service.getOpenSearchResponse(isbn).map { it.convert() }
    }
}

internal interface OpenSearchApiService {
    @Headers("connection: close")
    @GET("/api/opensearch")
    fun getOpenSearchResponse(@Query("isbn") isbn: String): Single<ResponseSimpleXml>
}
