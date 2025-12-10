package io.github.curioustools.curiousnews.domain.usecase

import io.github.curioustools.curiousnews.data.cache.NewsApiCache
import io.github.curioustools.curiousnews.domain.BaseUseCase
import io.github.curioustools.curiousnews.domain.dto.NewsRequest
import io.github.curioustools.curiousnews.domain.dto.NewsResults
import io.github.curioustools.curiousnews.domain.repo.NewsApiRepo
import javax.inject.Inject

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