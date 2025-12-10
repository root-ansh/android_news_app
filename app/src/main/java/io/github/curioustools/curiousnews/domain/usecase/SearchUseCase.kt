package io.github.curioustools.curiousnews.domain.usecase

import io.github.curioustools.curiousnews.domain.BaseResponse
import io.github.curioustools.curiousnews.domain.BaseUseCase
import io.github.curioustools.curiousnews.domain.dto.NewsRequest
import io.github.curioustools.curiousnews.domain.dto.NewsResults
import io.github.curioustools.curiousnews.commons.log
import io.github.curioustools.curiousnews.domain.repo.NewsApiRepo
import javax.inject.Inject

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