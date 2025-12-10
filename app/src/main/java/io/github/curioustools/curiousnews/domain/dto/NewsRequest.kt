package io.github.curioustools.curiousnews.domain.dto

import androidx.annotation.Keep
import androidx.compose.runtime.Immutable
import io.github.curioustools.curiousnews.domain.service.NewsApiService
import kotlinx.serialization.Serializable
import io.github.curioustools.curiousnews.BuildConfig


@Keep
@Serializable
@Immutable
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
                language = NewsApiService.FIELD_VAL_LANG,
                sortBy = NewsApiService.FIELD_VAL_SORT
            )
        }
        fun query(query: String,pageNum: Int = 1): NewsRequest {
            return NewsRequest(
                search = query,
                pageNum = pageNum,
                resultSize = 10,
                apiKey = BuildConfig.NEWS_API_KEY,
                language = NewsApiService.FIELD_VAL_LANG,
                sortBy = NewsApiService.FIELD_VAL_SORT
            )
        }
    }
}