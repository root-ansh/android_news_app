package io.github.curioustools.curiousnews.domain.service

import io.github.curioustools.curiousnews.domain.dto.NewsResults
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

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