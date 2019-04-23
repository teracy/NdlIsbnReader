package com.github.teracy.ndlapi.response

import com.tickaroo.tikxml.annotation.*

/**
 * OpenSearch APIのResponse（TikXML版）
 */
@Xml(name = "rss")
class OpenSearchResponseTikXml {
    @Path("channel")
    @PropertyElement(name = "openSearch:totalResults")
    var totalResults: Int = 0
    @Path("channel")
    @PropertyElement(name = "openSearch:startIndex")
    var startIndex: Int = 0
    @Path("channel")
    @PropertyElement(name = "openSearch:itemsPerPage")
    var itemsPerPage: Int = 0

    @Path("channel")
    @Element
    var items: List<Item> = ArrayList()

    @Xml(name = "item")
    class Item {
        /**
         * 国会図書館の詳細情報へのリンク（要trim）
         */
        @PropertyElement(name = "link")
        var link: String = ""

        /**
         * 書名
         */
        @PropertyElement(name = "dc:title")
        var title: String = ""

        /**
         * 別タイトル
         */
        @Element
        var subjects: List<Subject> = ArrayList()

        /**
         * 巻次
         */
        @PropertyElement(name = "dcndl:volume")
        var volume: String = ""

        /**
         * 版
         */
        @PropertyElement(name = "dcndl:edition")
        var edition: String = ""

        /**
         * シリーズ名
         */
        @PropertyElement(name = "dcndl:seriesTitle")
        var seriesTitle: String = ""

        /**
         * 出版社
         */
        @PropertyElement(name = "dc:publisher")
        var publisher: String = ""

        /**
         * 著者
         */
        @Element(name = "dc:creator")
        var creators: List<Creator> = ArrayList()

        @Xml(name = "dc:subject")
        class Subject {
            @Attribute(name = "xsi:type")
            var xsiType: String? = null
            @TextContent
            var name: String = ""
        }

        @Xml(name = "dc:creator")
        class Creator {
            @TextContent
            var name: String = ""
        }
    }
}
