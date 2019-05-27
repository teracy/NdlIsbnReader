package com.github.teracy.ndlapi_tikxml

import com.github.teracy.ndlapi.OpenSearchApiClient
import com.github.teracy.ndlapi.response.Book
import com.github.teracy.ndlapi_tikxml.response.ResponseTikXml
import com.github.teracy.ndlapi_tikxml.response.convert
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import javax.inject.Inject

/**
 * Open Search APIクライアントのTikXML実装
 */
class OpenSearchApiClientImplTikXml @Inject constructor(okHttpClient: OkHttpClient) : OpenSearchApiClient {
    private val tikXml = TikXml.Builder().exceptionOnUnreadXml(false).build()
    private val service: OpenSearchApiService = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl("http://iss.ndl.go.jp")
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .addConverterFactory(TikXmlConverterFactory.create(tikXml))
        .build()
        .create(OpenSearchApiService::class.java)

    override suspend fun search(isbn: String): Book {
        val responseTikXml = service.getOpenSearchResponseAsync(isbn).await()
        return responseTikXml.convert()
    }
}

internal interface OpenSearchApiService {
    @Headers("connection: close")
    @GET("/api/opensearch")
    fun getOpenSearchResponseAsync(@Query("isbn") isbn: String): Deferred<ResponseTikXml>
}
