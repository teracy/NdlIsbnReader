package com.github.teracy.ndlapi.response

/**
 * 国立国会図書館サーチ検索結果
 */
data class Book(
    /**
     * 全件数
     */
    val totalResults: Int,
    /**
     * このレスポンスでの開始位置
     */
    val startIndex: Int,
    /**
     * 1ページ当たりの件数（1ページで納まる場合はnull）
     */
    val itemsPerPage: Int?,
    /**
     * 詳細情報リスト
     */
    val items: List<Item>
)

/**
 * 詳細情報
 */
data class Item(
    /**
     * 国会図書館の詳細情報へのリンク
     */
    val link: String?,
    /**
     * 書名
     */
    val title: String?,
    /**
     * 別タイトル
     */
    val subject: String?,
    /**
     * 巻次
     */
    val volume: String?,
    /**
     * 版
     */
    val edition: String?,
    /**
     * シリーズ名
     */
    val seriesTitle: String?,
    /**
     * 出版社
     */
    val publisher: String?,
    /**
     * 著者
     */
    val creators: List<String>?
)
