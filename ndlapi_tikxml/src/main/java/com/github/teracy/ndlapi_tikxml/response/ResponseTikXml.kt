package com.github.teracy.ndlapi_tikxml.response

import com.github.teracy.ndlapi.response.Book
import com.github.teracy.ndlapi.response.Item
import com.tickaroo.tikxml.annotation.*

/**
 * OpenSearch APIのResponse（TikXML実装）
 */
@Xml(name = "rss")
internal class ResponseTikXml {
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
    var items: List<ResponseTikXmlItem> = ArrayList()
}

@Xml(name = "item")
internal class ResponseTikXmlItem {
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
    var subjects: List<ResponseTikXmlSubject> = ArrayList()

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
    var creators: List<ResponseTikXmlCreator> = ArrayList()
}

@Xml(name = "dc:subject")
internal class ResponseTikXmlSubject {
    @Attribute(name = "xsi:type")
    var xsiType: String? = null
    @TextContent
    var name: String = ""
}

@Xml(name = "dc:creator")
internal class ResponseTikXmlCreator {
    @TextContent
    var name: String = ""
}

/**
 * ResponseTikXml→Bookへの変換
 */
internal fun ResponseTikXml.convert(): Book {
    return Book(
        totalResults = totalResults,
        startIndex = startIndex,
        itemsPerPage = itemsPerPage,
        items = items.map(ResponseTikXmlItem::convert)
    )
}

/**
 * ResponseSimpleXmlItem→Itemへの変換
 */
internal fun ResponseTikXmlItem.convert(): Item {
    val filteredSubject = subjects.filter { n -> n.xsiType?.isEmpty() ?: true }
    return Item(
        link = link,
        title = title,
        subject = if (filteredSubject.isEmpty()) null else filteredSubject[0].name,
        volume = volume,
        edition = edition,
        seriesTitle = seriesTitle,
        publisher = publisher,
        creators = creators.map(ResponseTikXmlCreator::name)
    )
}
