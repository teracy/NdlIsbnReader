package com.github.teracy.ndlapi_tikxml

import com.github.teracy.ndlapi.OpenSearchApiClient
import com.github.teracy.ndlapi.response.Book
import com.github.teracy.ndlapi_tikxml.response.ResponseTikXml
import com.github.teracy.ndlapi_tikxml.response.convert
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
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
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(TikXmlConverterFactory.create(tikXml))
        .build()
        .create(OpenSearchApiService::class.java)

    override fun search(isbn: String): Single<Book> {
        return service.getOpenSearchResponse(isbn).map(ResponseTikXml::convert)
    }
}

internal interface OpenSearchApiService {
    @Headers("connection: close")
    @GET("/api/opensearch")
    fun getOpenSearchResponse(@Query("isbn") isbn: String): Single<ResponseTikXml>
}
