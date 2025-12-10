package io.github.curioustools.curiousnews

import androidx.annotation.Keep
import androidx.compose.runtime.Immutable
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.util.fastJoinToString
import com.google.gson.annotations.SerializedName
import io.github.curioustools.curiousnews.NewsApiService.Companion.FIELD_VAL_LANG
import io.github.curioustools.curiousnews.NewsApiService.Companion.FIELD_VAL_SORT
import kotlinx.serialization.Serializable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.TimeZone
import javax.inject.Inject
import kotlin.collections.sortedByDescending


interface NewsApiService {
    @GET(PATH)
    fun getNewsResults(
        @Query(FIELD_Q) search: String,
        @Query(FIELD_PAGE) pageNum: Int,
        @Query(FIELD_PAGE_SIZE) resultSize: Int,
        @Query(FIELD_API_KEY) apiKey: String,
        @Query(FIELD_LANGUAGE) language: String,
        @Query(FIELD_SORT) sortBy: String,
    ): Call<NewsResults>

    companion object {
        const val BASE = "https://newsapi.org" + "/"
        const val PATH = "v2/everything"
        const val FIELD_Q = "q"
        const val FIELD_PAGE = "page"
        const val FIELD_API_KEY = "apiKey"
        const val FIELD_PAGE_SIZE = "pageSize"
        const val FIELD_LANGUAGE = "language"
        const val FIELD_SORT = "sortBy"
        const val FIELD_VAL_SORT = "relevancy"
        const val FIELD_VAL_LANG = "en"
    }
}


class NewsApiRepoImpl @Inject constructor (private val apiService: NewsApiService): NewsApiRepo {
    override suspend fun getNewsList(request: NewsRequest): BaseResponse<NewsResults> {
        return  apiService.getNewsResults(
            search = request.search,
            pageNum = request.pageNum,
            resultSize = request.resultSize,
            apiKey = request.apiKey,
            language = request.language,
            sortBy = request.sortBy
        ).executeAndUnify()
    }

}
interface NewsApiRepo{
    suspend fun getNewsList(request: NewsRequest): BaseResponse<NewsResults>
}

class UpdateBookmarksUseCase @Inject constructor(
    private val repo: NewsApiRepo,
    private val cache: NewsApiCache
) : BaseUseCase<Unit, NewsResults.NewsItem>() {
    override suspend fun execute(params: NewsResults.NewsItem) {
        cache.updateNewsEntity(params)
        return
    }


    data class Params(val request: NewsRequest, val cachedOnly: Boolean = false)

}

class NewsListUseCase @Inject constructor(
    private val repo: NewsApiRepo,
    private val cache: NewsApiCache
) : BaseUseCase<BaseResponse.Success<NewsResults>, NewsListUseCase.Params>() {

    data class Params(val request: NewsRequest, val cachedOnly: Boolean = false)

    override suspend fun execute(params: Params): BaseResponse.Success<NewsResults> {
        log("request_info : pagenum : ${params.request.pageNum} | query: ${params.request.search} |cached Only = ${params.cachedOnly}")
        val origCache = cache.getCachedNewsList()
        if (params.cachedOnly && origCache.isNotEmpty()) {
            return BaseResponse.Success(NewsResults(articles = origCache.sortedByDescending { it.timeStamp() }))
        } else {
            val freshResults = repo.getNewsList(params.request)
            when(freshResults){
                is BaseResponse.Failure -> {
                    return BaseResponse.Success(NewsResults(articles = origCache.sortedByDescending { it.timeStamp() }))
                }
                is BaseResponse.Success -> {
                    freshResults.body.articles.forEach { cache.addNewsEntity(it) }
                    val newCache = cache.getCachedNewsList()
                    return freshResults.copy(body = freshResults.body.copy(articles = newCache.sortedByDescending { it.timeStamp() }))

                }
            }
        }
    }
}

class SearchUseCase @Inject constructor(
    private val repo: NewsApiRepo,
) : BaseUseCase<BaseResponse.Success<NewsResults>, SearchUseCase.Params>() {

    data class Params(val request: NewsRequest, val cachedList: List<NewsResults.NewsItem> = listOf())

    override suspend fun execute(params: Params): BaseResponse.Success<NewsResults> {
        log("request_info : params: ${params.request} | cache:${params.cachedList.size}")
        val freshResults = repo.getNewsList(params.request)
        when(freshResults){
            is BaseResponse.Failure -> {
                return BaseResponse.Success(NewsResults())
            }
            is BaseResponse.Success -> {
                val finalResults = freshResults.body.articles.map { resp ->
                    val cachedRes = params.cachedList.firstOrNull { it.title.equals(resp.title,true) }
                    cachedRes?:resp
                }
                return freshResults.copy(body = freshResults.body.copy(articles = finalResults.sortedByDescending { it.timeStamp() }))
            }
        }
    }
}


class ClearCacheUseCase @Inject constructor(
    private val repo: NewsApiRepo,
    private val cache: NewsApiCache
) : BaseUseCase<Unit, Unit>() {
    override suspend fun execute(params: Unit) {
        cache.clearAllNewsEntity()
        return
    }

}



@Keep @Serializable @Immutable
data class NewsRequest(
    val search: String,
    val  pageNum: Int,
    val resultSize: Int,
    val  apiKey: String,
    val  language: String,
    val  sortBy: String
){
    companion object{
        fun all(pageNum: Int = 1): NewsRequest {
            return NewsRequest(
                search = "Business",
                pageNum = pageNum,
                resultSize = 10,
                apiKey = BuildConfig.NEWS_API_KEY,
                language = FIELD_VAL_LANG,
                sortBy = FIELD_VAL_SORT
            )
        }
        fun query(query: String,pageNum: Int = 1): NewsRequest {
            return NewsRequest(
                search = query,
                pageNum = pageNum,
                resultSize = 50,
                apiKey = BuildConfig.NEWS_API_KEY,
                language = FIELD_VAL_LANG,
                sortBy = FIELD_VAL_SORT
            )
        }
    }
}

@Keep @Serializable @Immutable
data class NewsResults(
    @SerializedName("articles") val articles: List<NewsItem> = listOf(),
    @SerializedName("status") val status: String = "",
    @SerializedName("totalResults") val totalResults: Int = 0,
) {
    @Keep @Serializable @Immutable
    data class NewsItem(
        @SerializedName("author") val author: String? = null,
        @SerializedName("content") val content: String = "",
        @SerializedName("description") val description: String? = null,
        @SerializedName("publishedAt") val publishedAt: String = "",
        @SerializedName("source") val source: Source = Source(),
        @SerializedName("title") val title: String = "",
        @SerializedName("url") val url: String? = null,
        @SerializedName("urlToImage") val urlToImage: String? = null,
        val isLoading: Boolean = false,
        var isBookmarked: Boolean = false,
    ) {
        fun publishedOn(): String {
            return runCatching {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                val outputFormat = SimpleDateFormat("dd MMM yyyy, h:mm a")

                val date = inputFormat.parse(publishedAt)
                outputFormat.format(date!!)
            }.getOrElse { publishedAt }
        }
        fun timeStamp(): Long {
            return runCatching {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                val date = inputFormat.parse(publishedAt)!!
                date.time
            }.getOrElse { System.currentTimeMillis() }
        }
        fun info() = "From: ${source.name} • Published on: ${publishedOn()}"

        fun toShareMsg() = """
            $title
            ${urlToImage.orEmpty()}
            Read more at $url
            or Read Summary at curiousnews://post-summary/${title}
        """.trimIndent()

    }

    @Keep @Serializable @Immutable
    data class Source(@SerializedName("name") val name: String = "")

    companion object{

        fun lorem(i:Int) = LoremIpsum(i).values.toList().fastJoinToString()

        fun loading() =
            NewsResults(
                articles = (1..5).map {  NewsItem(isLoading = true, title = it.toString()) }
            )
        fun mock() =
            NewsResults(
                articles = (1..5).map {  NewsItem(
                    isLoading = false, title = lorem(5),
                    author = lorem(3),
                    content = lorem(50),
                    description = lorem(20),
                    publishedAt = lorem(2),
                    source = Source(lorem(2)),
                    url = lorem(1),
                    urlToImage = lorem(1),
                    isBookmarked = false
                ) }
            )

    }

}

/**
{
"status": "ok",
"totalResults": 61568,
"articles": [
{
"source": {
"id": null,
"name": "The Daily Dot"
},
"author": "Susan LaMarca",
"title": "“Just a marketing ploy”: People say the illusion is over after an air fryer truther’s video goes viral",
"description": "A clip revealing the inner workings of an air fryer had people running to check if their most magical appliance was also pretty much just a single electric burner.\n\n\nTikToker @welcometheekidd‘s air fryer truther clip from Nov. 19, 2025, went viral with 1.1 mi…",
"url": "https://www.dailydot.com/news/what-the-inside-of-an-air-fryer-really-looks-like/",
"urlToImage": "https://uploads.dailydot.com/2025/12/what-does-air-fryer-inside-look-like.png?auto=compress&fm=png&w=2000&h=1000",
"publishedAt": "2025-12-08T11:30:00Z",
"content": "A clip revealing the inner workings of an air fryer had people running to check if their most magical appliance was also pretty much just a single electric burner.\r\nTikToker @welcometheekidds air fry… [+2176 chars]"
},
{
"source": {
"id": null,
"name": "Mediagazer.com"
},
"author": null,
"title": "After the building fire, Hong Kong summoned AFP, FT, NYT, AP, Bloomberg, and WSJ journalists, telling them to avoid \"trouble making\", and arrested a commentator (Committee to Protect Journalists)",
"description": "Committee to Protect Journalists:\nAfter the building fire, Hong Kong summoned AFP, FT, NYT, AP, Bloomberg, and WSJ journalists, telling them to avoid “trouble making”, and arrested a commentator  —  Chinese and Hong Kong authorities must immediately stop hara…",
"url": "https://mediagazer.com/251208/p5",
"urlToImage": "https://cpj.org/wp-content/uploads/2025/12/AFP__20251203__86X64G3__v3__HighRes__HongKongChinaFire-1.jpg?fit=4096,4096&strip=all&quality=80",
"publishedAt": "2025-12-08T11:30:00Z",
"content": "Mediagazer presents the day's must-read media news on a single page.\r\nThe media business is in tumult: from the production side to\r\nthe distribution side, new technologies are upending the industry.\r… [+416 chars]"
},
{
"source": {
"id": null,
"name": "The Star Online"
},
"author": "The Star Online",
"title": "Senator proposes RM100mil fine for social media platforms violating minimum age limit",
"description": "KUALA LUMPUR: A senator proposed that social media service providers be fined RM100mil if they fail to comply with the minimum age policy of 16 years for opening new accounts. Read full story",
"url": "https://www.thestar.com.my/news/nation/2025/12/08/senator-proposes-rm100mil-fine-for-social-media-platforms-violating-minimum-age-limit",
"urlToImage": "https://apicms.thestar.com.my/uploads/images/2025/12/08/3661302.jpg",
"publishedAt": "2025-12-08T11:29:00Z",
"content": "KUALA LUMPUR: A senator proposed that social media service providers be fined RM100mil if they fail to comply with the minimum age policy of 16 years for opening new accounts.\r\nDatuk Seri S. Vell Paa… [+2147 chars]"
},
{
"source": {
"id": null,
"name": "Hospitality Net"
},
"author": "STR",
"title": "STR Weekly Insights: 16-29 November 2025",
"description": "All financial figures in U.S. dollar constant currency. Highlights U.S. RevPAR decrease all due to hurricane markets Fortnight U.S. occupancy flat, ADR up, excluding hurricane markets Thanksgiving Day room demand second highest ever Global RevPAR up...",
"url": "https://www.hospitalitynet.org/news/4130091.html",
"urlToImage": "https://www.hospitalitynet.org/HN-icon.jpg",
"publishedAt": "2025-12-08T11:28:00Z",
"content": "All financial figures in U.S. dollar constant currency.\r\nHighlights\r\n<ul><li>U.S. RevPAR decrease all due to hurricane markets</li><li>Fortnight U.S. occupancy flat, ADR up, excluding hurricane marke… [+10843 chars]"
},
{
"source": {
"id": null,
"name": "Thefly.com"
},
"author": null,
"title": "Repsol, HitecVision to merge joint venture with TotalEnergies' UK business",
"description": "See the rest of the story here.\n\nthefly.com provides the latest financial news as it breaks. Known as a leader in market intelligence, The Fly's real-time, streaming news feed keeps individual investors, professional money managers, active traders, and corpor…",
"url": "https://thefly.com/permalinks/entry.php/id4251757/REPYY;TTE-Repsol-HitecVision-to-merge-joint-venture-with-TotalEnergies-UK-business",
"urlToImage": "https://thefly.com/images/meta/hotstocks.jpg",
"publishedAt": "2025-12-08T11:26:43Z",
"content": "Earnings calls, analyst events, roadshows and more"
}
]
}
 */