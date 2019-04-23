package com.github.teracy.ndlapi

import com.github.teracy.ndlapi.response.OpenSearchResponse
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import javax.inject.Inject

class OpenSearchApiClient @Inject constructor(okHttpClient: OkHttpClient) : OpenSearchApi {
    private val service: OpenSearchApiService = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl("http://iss.ndl.go.jp")
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(SimpleXmlConverterFactory.create())
        .build()
        .create(OpenSearchApiService::class.java)

    override fun search(isbn: String): Single<OpenSearchResponse> {
        return service.getOpenSearchResponse(isbn)
    }
}

internal interface OpenSearchApi {
    fun search(isbn: String): Single<OpenSearchResponse>
}

internal interface OpenSearchApiService {
    @Headers("connection: close")
    @GET("/api/opensearch")
    fun getOpenSearchResponse(@Query("isbn") isbn: String): Single<OpenSearchResponse>
}
