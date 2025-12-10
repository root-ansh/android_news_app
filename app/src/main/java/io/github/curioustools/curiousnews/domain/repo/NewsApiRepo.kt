package io.github.curioustools.curiousnews.domain.repo

import io.github.curioustools.curiousnews.domain.BaseResponse
import io.github.curioustools.curiousnews.domain.dto.NewsRequest
import io.github.curioustools.curiousnews.domain.dto.NewsResults
import io.github.curioustools.curiousnews.domain.executeAndUnify
import io.github.curioustools.curiousnews.domain.service.NewsApiService
import javax.inject.Inject

interface NewsApiRepo{
    suspend fun getNewsList(request: NewsRequest): BaseResponse<NewsResults>
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