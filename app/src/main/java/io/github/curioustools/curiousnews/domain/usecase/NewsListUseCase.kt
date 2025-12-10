package io.github.curioustools.curiousnews.domain.usecase

import io.github.curioustools.curiousnews.data.cache.NewsApiCache
import io.github.curioustools.curiousnews.domain.BaseResponse
import io.github.curioustools.curiousnews.domain.BaseUseCase
import io.github.curioustools.curiousnews.domain.dto.NewsRequest
import io.github.curioustools.curiousnews.domain.dto.NewsResults
import io.github.curioustools.curiousnews.commons.log
import io.github.curioustools.curiousnews.domain.repo.NewsApiRepo
import javax.inject.Inject
import kotlin.collections.isNotEmpty
import kotlin.collections.sortedByDescending

class NewsListUseCase @Inject constructor(
    private val repo: NewsApiRepo,
    private val cache: NewsApiCache
) : BaseUseCase<BaseResponse.Success<NewsResults>, NewsListUseCase.Params>() {

    data class Params(val request: NewsRequest, val cachedOnly: Boolean = false)

    override suspend fun execute(params: Params): BaseResponse.Success<NewsResults> {
        log("request_info : pagenum : ${params.request.pageNum} | query: ${params.request.search} |cached Only = ${params.cachedOnly}")
        val origCache: List<NewsResults.NewsItem> = cache.getCachedNewsList()
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