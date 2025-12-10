package io.github.curioustools.curiousnews.data.cache

import io.github.curioustools.curiousnews.data.dao.NewsDao
import io.github.curioustools.curiousnews.data.entitiy.toDTOModel
import io.github.curioustools.curiousnews.data.entitiy.toEntity
import io.github.curioustools.curiousnews.domain.dto.NewsResults
import javax.inject.Inject

interface NewsApiCache {
    suspend fun getCachedNewsList(): List<NewsResults.NewsItem>
    suspend fun addNewsEntity(response: NewsResults.NewsItem)
    suspend fun clearAllNewsEntity()

    suspend fun updateNewsEntity(response: NewsResults.NewsItem)
}
class NewsApiCacheImpl @Inject constructor(private val cacheDao: NewsDao) : NewsApiCache {
    override suspend fun getCachedNewsList(): List<NewsResults.NewsItem> {
        return cacheDao.getAllNewsEntity().map { it.toDTOModel() }
    }

    override suspend fun addNewsEntity(response: NewsResults.NewsItem) {
        cacheDao.addNewsEntity(response.toEntity())
    }

    override suspend fun clearAllNewsEntity() {
        cacheDao.clearAllNewsEntity()
    }

    override suspend fun updateNewsEntity(response: NewsResults.NewsItem) {
        cacheDao.updateNewsEntity(response.toEntity())
    }
}