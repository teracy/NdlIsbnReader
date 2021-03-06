package com.github.teracy.ndlapi_simplexml.response

import com.github.teracy.ndlapi.response.Book
import com.github.teracy.ndlapi.response.Item
import org.simpleframework.xml.*

/**
 * OpenSearch APIのResponse（SimpleXML実装）
 */
@Root(name = "channel", strict = false)
internal class ResponseSimpleXml {
    @get:Path("channel")
    @get:Namespace(prefix = "openSearch", reference = "")
    @get:Element(name = "totalResults", required = false)
    @set:Path("channel")
    @set:Namespace(prefix = "openSearch", reference = "")
    @set:Element(name = "totalResults", required = false)
    var totalResults: Int = 0

    @get:Path("channel")
    @get:Namespace(prefix = "openSearch", reference = "")
    @get:Element(name = "startIndex", required = false)
    @set:Path("channel")
    @set:Namespace(prefix = "openSearch", reference = "")
    @set:Element(name = "startIndex", required = false)
    var startIndex: Int = 0

    @get:Path("channel")
    @get:Namespace(prefix = "openSearch", reference = "")
    @get:Element(name = "itemsPerPage", required = false)
    @set:Path("channel")
    @set:Namespace(prefix = "openSearch", reference = "")
    @set:Element(name = "itemsPerPage", required = false)
    var itemsPerPage: Int = 0

    // NOTE: "item"はElementに包まれずに"channel"の直下に複数存在するため、inline属性必要
    // https://stackoverflow.com/questions/24266510/simple-xml-valuerequiredexception
    @get:Path("channel")
    @get:ElementList(inline = true, required = false)
    @set:Path("channel")
    @set:ElementList(inline = true, required = false)
    var items: List<ResponseSimpleXmlItem> = ArrayList()
}

@Root(name = "item", strict = false)
internal class ResponseSimpleXmlItem {
    // NOTE: Elementとして定義すると上位で既に定義済みの"link"と被るため、PathとTextで定義する
    // https://stackoverflow.com/questions/31999265/parsing-xml-feed-die-with-element-is-already-used
    /**
     * 国会図書館の詳細情報へのリンク（要trim）
     */
    @get:Path("link")
    @get:Text(required = false)
    @set:Path("link")
    @set:Text(required = false)
    var link: String = ""

    /**
     * 書名
     */
    @get:Namespace(prefix = "dc", reference = "")
    @get:Path("title")
    @get:Text(required = false)
    @set:Namespace(prefix = "dc", reference = "")
    @set:Path("title")
    @set:Text(required = false)
    var title: String = ""

    /**
     * 別タイトル
     */
    @get:Namespace(prefix = "dc", reference = "")
    @get:ElementList(entry = "subject", inline = true, required = false)
    @set:Namespace(prefix = "dc", reference = "")
    @set:ElementList(entry = "subject", inline = true, required = false)
    var subjects: List<ResponseSimpleXmlSubject> = ArrayList()

    /**
     * 巻次
     */
    @get:Namespace(prefix = "dcndl", reference = "")
    @get:Element(name = "volume", required = false)
    @set:Namespace(prefix = "dcndl", reference = "")
    @set:Element(name = "volume", required = false)
    var volume: String = ""

    /**
     * 版
     */
    @get:Namespace(prefix = "dcndl", reference = "")
    @get:Element(name = "edition", required = false)
    @set:Namespace(prefix = "dcndl", reference = "")
    @set:Element(name = "edition", required = false)
    var edition: String = ""

    /**
     * シリーズ名
     */
    @get:Namespace(prefix = "dcndl", reference = "")
    @get:Element(name = "seriesTitle", required = false)
    @set:Namespace(prefix = "dcndl", reference = "")
    @set:Element(name = "seriesTitle", required = false)
    var seriesTitle: String = ""

    /**
     * 出版社
     */
    @get:Namespace(prefix = "dc", reference = "")
    @get:Element(name = "publisher", required = false)
    @set:Namespace(prefix = "dc", reference = "")
    @set:Element(name = "publisher", required = false)
    var publisher: String = ""

    /**
     * 著者
     */
    @get:Namespace(prefix = "dc", reference = "")
    @get:ElementList(entry = "creator", inline = true, required = false)
    @set:Namespace(prefix = "dc", reference = "")
    @set:ElementList(entry = "creator", inline = true, required = false)
    var creators: List<String> = ArrayList()
}

internal class ResponseSimpleXmlSubject {
    @get:Namespace(prefix = "xsi", reference = "")
    @get:Attribute(name = "type", required = false)
    @set:Namespace(prefix = "xsi", reference = "")
    @set:Attribute(name = "type", required = false)
    var xsiType: String? = null

    @get:Text(required = false)
    @set:Text(required = false)
    var name: String = ""
}

/**
 * ResponseSimpleXml→Bookへの変換
 */
internal fun ResponseSimpleXml.convert(): Book {
    return Book(
        totalResults = totalResults,
        startIndex = startIndex,
        itemsPerPage = if (itemsPerPage == 0) null else itemsPerPage,
        items = items.map(ResponseSimpleXmlItem::convert)
    )
}

/**
 * ResponseSimpleXmlItem→Itemへの変換
 */
internal fun ResponseSimpleXmlItem.convert(): Item {
    val filteredSubject = subjects.filter { n -> n.xsiType?.isEmpty() ?: true }
    return Item(
        link = link,
        title = title,
        subject = if (filteredSubject.isEmpty()) null else filteredSubject[0].name,
        volume = volume,
        edition = edition,
        seriesTitle = seriesTitle,
        publisher = publisher,
        creators = creators
    )
}
