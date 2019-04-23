package com.github.teracy.ndlapi

import com.github.teracy.ndlapi.response.Book
import io.reactivex.Single

/**
 * Open Search APIクライアントinterface
 */
interface OpenSearchApiClient {
    fun search(isbn: String): Single<Book>
}
