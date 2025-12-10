package io.github.curioustools.curiousnews.domain.usecase

import io.github.curioustools.curiousnews.data.cache.NewsApiCache
import io.github.curioustools.curiousnews.domain.BaseUseCase
import io.github.curioustools.curiousnews.domain.repo.NewsApiRepo
import javax.inject.Inject

class ClearCacheUseCase @Inject constructor(
    private val repo: NewsApiRepo,
    private val cache: NewsApiCache
) : BaseUseCase<Unit, Unit>() {
    override suspend fun execute(params: Unit) {
        cache.clearAllNewsEntity()
        return
    }

}