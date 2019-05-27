package com.github.teracy.ndlapi

import com.github.teracy.ndlapi.response.Book

/**
 * Open Search APIクライアントinterface
 */
interface OpenSearchApiClient {
    suspend fun search(isbn: String): Book
}
