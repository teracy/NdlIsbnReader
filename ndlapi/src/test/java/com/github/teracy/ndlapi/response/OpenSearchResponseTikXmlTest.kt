package com.github.teracy.ndlapi.response

import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.http.GET
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class OpenSearchResponseTikXmlTest {
    private interface Service {
        @GET("/opensearch")
        fun openSearch(): Call<OpenSearchResponseTikXml>
    }

    @get:Rule
    val server = MockWebServer()
    private var service: Service? = null

    @Before
    fun setUp() {
        val tikXml = TikXml.Builder().exceptionOnUnreadXml(false).build()
        service = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(TikXmlConverterFactory.create(tikXml))
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
        Assertions.assertThat(body).isNotNull
        Assertions.assertThat(body!!.totalResults).isEqualTo(1)
        Assertions.assertThat(body.startIndex).isEqualTo(1)
        Assertions.assertThat(body.itemsPerPage).isEqualTo(0)
        Assertions.assertThat(body.items).isNotEmpty
        Assertions.assertThat(body.items.size).isEqualTo(1)

        val item0 = body.items[0]
        Assertions.assertThat(item0.link.trim()).isEqualTo("http://iss.ndl.go.jp/books/R100000002-I000011301234-00")
        Assertions.assertThat(item0.title).isEqualTo("Gitによるバージョン管理")
        // subjects自体は空でないが、全てxsi:type属性があるもののはず
        Assertions.assertThat(item0.subjects.size).isEqualTo(2)
        Assertions.assertThat(item0.subjects.filter { n -> n.xsiType?.isEmpty() ?: true }).isEmpty()

        Assertions.assertThat(item0.volume).isEmpty()
        Assertions.assertThat(item0.edition).isEmpty()
        Assertions.assertThat(item0.seriesTitle).isEmpty()
        Assertions.assertThat(item0.publisher).isEqualTo("オーム社")
        Assertions.assertThat(item0.creators).isNotEmpty
        Assertions.assertThat(item0.creators.size).isEqualTo(3)
        Assertions.assertThat(item0.creators[0].name).isEqualTo("岩松, 信洋")
        Assertions.assertThat(item0.creators[1].name).isEqualTo("上川, 純一")
        Assertions.assertThat(item0.creators[2].name).isEqualTo("まえだ, こうへい")
    }

    /**
     * 複数件のitemを持つ検索結果のデシリアライズのテスト
     */
    @Test
    fun deserializeTest_has_multi_items() {
        val xml = readFile("isbn_9784563005641.xml")

        server.enqueue(MockResponse().setBody(xml))
        val body = service!!.openSearch().execute().body()
        Assertions.assertThat(body).isNotNull
        Assertions.assertThat(body!!.totalResults).isEqualTo(2)
        Assertions.assertThat(body.startIndex).isEqualTo(1)
        Assertions.assertThat(body.itemsPerPage).isEqualTo(0)
        Assertions.assertThat(body.items).isNotEmpty
        Assertions.assertThat(body.items.size).isEqualTo(2)

        // region item1件目
        val item0 = body.items[0]
        Assertions.assertThat(item0.link.trim()).isEqualTo("http://iss.ndl.go.jp/books/R100000002-I000001897466-00")
        Assertions.assertThat(item0.title).isEqualTo("技術者のための高等数学")
        // subjects自体は空でなく、xsi:type属性がないものが1件あるはず
        Assertions.assertThat(item0.subjects.size).isEqualTo(4)
        val item0FilteredSubjects = item0.subjects.filter { n -> n.xsiType?.isEmpty() ?: true }
        Assertions.assertThat(item0FilteredSubjects).isNotEmpty
        Assertions.assertThat(item0FilteredSubjects.size).isEqualTo(1)
        Assertions.assertThat(item0FilteredSubjects[0].name).isEqualTo("複素関数論")

        Assertions.assertThat(item0.volume).isEqualTo("4")
        Assertions.assertThat(item0.edition).isEqualTo("第5版")
        Assertions.assertThat(item0.seriesTitle).isEmpty()
        Assertions.assertThat(item0.publisher).isEqualTo("培風館")
        Assertions.assertThat(item0.creators).isNotEmpty
        Assertions.assertThat(item0.creators.size).isEqualTo(1)
        Assertions.assertThat(item0.creators[0].name).isEqualTo("Kreyszig, Erwin")
        // endregion

        // region item2件目
        val item1 = body.items[1]
        Assertions.assertThat(item1.link.trim()).isEqualTo("http://iss.ndl.go.jp/books/R100000001-I078284198-00")
        Assertions.assertThat(item1.title).isEqualTo("複素関数論")
        // subjects自体が空のはず
        Assertions.assertThat(item1.subjects).isEmpty()

        Assertions.assertThat(item1.volume).isEmpty()
        Assertions.assertThat(item1.edition).isEqualTo("第5版")
        Assertions.assertThat(item1.seriesTitle).isEqualTo("技術者のための高等数学 ; 4")
        Assertions.assertThat(item1.publisher).isEqualTo("培風館")
        Assertions.assertThat(item1.creators).isNotEmpty
        Assertions.assertThat(item1.creators.size).isEqualTo(3)
        Assertions.assertThat(item1.creators[0].name).isEqualTo("E.クライツィグ")
        Assertions.assertThat(item1.creators[1].name).isEqualTo("丹生慶四郎")
        Assertions.assertThat(item1.creators[2].name).isEqualTo("阿部寛治")
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
        Assertions.assertThat(body).isNotNull
        Assertions.assertThat(body!!.totalResults).isEqualTo(0)
        Assertions.assertThat(body.startIndex).isEqualTo(1)
        Assertions.assertThat(body.itemsPerPage).isEqualTo(0)
        Assertions.assertThat(body.items).isEmpty()
    }
}
