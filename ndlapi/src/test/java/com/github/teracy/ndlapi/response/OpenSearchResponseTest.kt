// NOTE: SimpleXmlConverterFactoryはdeprecatedだが、テストクラスなので気にせず利用する
// https://github.com/square/retrofit/issues/2733
@file:Suppress("DEPRECATION")

package com.github.teracy.ndlapi.response

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import retrofit2.http.GET
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class OpenSearchResponseTest {
    private interface Service {
        @GET("/opensearch")
        fun openSearch(): Call<OpenSearchResponse>
    }

    @get:Rule
    val server = MockWebServer()
    private var service: Service? = null

    @Before
    fun setUp() {
        service = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(SimpleXmlConverterFactory.createNonStrict())
            .build()
            .create(Service::class.java)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    // NOTE: javaClass.classLoaderはnullableだが、気にせず利用する
    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private fun readFile(fileName: String): String {
        try {
            val classLoader = javaClass.classLoader
            val resource = classLoader.getResource(fileName)
            val file = File(resource.path)
            FileInputStream(file.path).use {
                return it.bufferedReader().use(BufferedReader::readText)
            }
        } catch (e: IOException) {
            return ""
        }
    }

    /**
     * 1件のitemを持つ検索結果のデシリアライズのテスト
     */
    @Test
    fun deserializeTest_has_single_item() {
        val xml = readFile("isbn_9784274068645.xml")

        server.enqueue(MockResponse().setBody(xml))
        val body = service!!.openSearch().execute().body()
        assertThat(body).isNotNull
        assertThat(body!!.totalResults).isEqualTo(1)
        assertThat(body.startIndex).isEqualTo(1)
        assertThat(body.itemsPerPage).isEqualTo(0)
        assertThat(body.items).isNotEmpty
        assertThat(body.items.size).isEqualTo(1)

        val item0 = body.items[0]
        assertThat(item0.link.trim()).isEqualTo("http://iss.ndl.go.jp/books/R100000002-I000011301234-00")
        assertThat(item0.title).isEqualTo("Gitによるバージョン管理")
        // subjects自体は空でないが、全てxsi:type属性があるもののはず
        assertThat(item0.subjects.size).isEqualTo(2)
        assertThat(item0.subjects.filter { n -> n.xsiType?.isEmpty() ?: true }).isEmpty()

        assertThat(item0.volume).isEmpty()
        assertThat(item0.edition).isEmpty()
        assertThat(item0.seriesTitle).isEmpty()
        assertThat(item0.publisher).isEqualTo("オーム社")
        assertThat(item0.creators).isNotEmpty
        assertThat(item0.creators.size).isEqualTo(3)
        assertThat(item0.creators[0]).isEqualTo("岩松, 信洋")
        assertThat(item0.creators[1]).isEqualTo("上川, 純一")
        assertThat(item0.creators[2]).isEqualTo("まえだ, こうへい")
    }

    /**
     * 複数件のitemを持つ検索結果のデシリアライズのテスト
     */
    @Test
    fun deserializeTest_has_multi_items() {
        val xml = readFile("isbn_9784563005641.xml")

        server.enqueue(MockResponse().setBody(xml))
        val body = service!!.openSearch().execute().body()
        assertThat(body).isNotNull
        assertThat(body!!.totalResults).isEqualTo(2)
        assertThat(body.startIndex).isEqualTo(1)
        assertThat(body.itemsPerPage).isEqualTo(0)
        assertThat(body.items).isNotEmpty
        assertThat(body.items.size).isEqualTo(2)

        // region item1件目
        val item0 = body.items[0]
        assertThat(item0.link.trim()).isEqualTo("http://iss.ndl.go.jp/books/R100000002-I000001897466-00")
        assertThat(item0.title).isEqualTo("技術者のための高等数学")
        // subjects自体は空でなく、xsi:type属性がないものが1件あるはず
        assertThat(item0.subjects.size).isEqualTo(4)
        val item0FilteredSubjects = item0.subjects.filter { n -> n.xsiType?.isEmpty() ?: true }
        assertThat(item0FilteredSubjects).isNotEmpty
        assertThat(item0FilteredSubjects.size).isEqualTo(1)
        assertThat(item0FilteredSubjects[0].subjectName).isEqualTo("複素関数論")

        assertThat(item0.volume).isEqualTo("4")
        assertThat(item0.edition).isEqualTo("第5版")
        assertThat(item0.seriesTitle).isEmpty()
        assertThat(item0.publisher).isEqualTo("培風館")
        assertThat(item0.creators).isNotEmpty
        assertThat(item0.creators.size).isEqualTo(1)
        assertThat(item0.creators[0]).isEqualTo("Kreyszig, Erwin")
        // endregion

        // region item2件目
        val item1 = body.items[1]
        assertThat(item1.link.trim()).isEqualTo("http://iss.ndl.go.jp/books/R100000001-I078284198-00")
        assertThat(item1.title).isEqualTo("複素関数論")
        // subjects自体が空のはず
        assertThat(item1.subjects).isEmpty()

        assertThat(item1.volume).isEmpty()
        assertThat(item1.edition).isEqualTo("第5版")
        assertThat(item1.seriesTitle).isEqualTo("技術者のための高等数学 ; 4")
        assertThat(item1.publisher).isEqualTo("培風館")
        assertThat(item1.creators).isNotEmpty
        assertThat(item1.creators.size).isEqualTo(3)
        assertThat(item1.creators[0]).isEqualTo("E.クライツィグ")
        assertThat(item1.creators[1]).isEqualTo("丹生慶四郎")
        assertThat(item1.creators[2]).isEqualTo("阿部寛治")
        // endregion
    }

    /**
     * 該当する書籍が存在しないISBNの検索結果のデシリアライズのテスト
     */
    @Test
    fun deserializeTest_invalid_isbn() {
        val xml = readFile("isbn_9784563005555.xml")

        server.enqueue(MockResponse().setBody(xml))
        val body = service!!.openSearch().execute().body()
        assertThat(body).isNotNull
        assertThat(body!!.totalResults).isEqualTo(0)
        assertThat(body.startIndex).isEqualTo(1)
        assertThat(body.itemsPerPage).isEqualTo(0)
        assertThat(body.items).isEmpty()
    }
}
