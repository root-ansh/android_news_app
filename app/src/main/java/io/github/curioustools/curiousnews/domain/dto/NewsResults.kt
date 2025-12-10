package io.github.curioustools.curiousnews.domain.dto

import androidx.annotation.Keep
import androidx.compose.runtime.Immutable
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.util.fastJoinToString
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.TimeZone

@Keep
@Serializable
@Immutable
data class NewsResults(
    @SerializedName("articles") val articles: List<NewsItem> = listOf(),
    @SerializedName("status") val status: String = "",
    @SerializedName("totalResults") val totalResults: Int = 0,
) {
    @Keep
    @Serializable
    @Immutable
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

    @Keep
    @Serializable
    @Immutable
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