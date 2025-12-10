package io.github.curioustools.curiousnews

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import java.util.UUID
import javax.inject.Inject

@Dao
interface NewsDao {

    @Query("SELECT * FROM ${AppDatabase.TABLE_NEWS}")
    suspend fun getAllNewsEntity(): List<NewsEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addNewsEntity(newsEntity: NewsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateNewsEntity(newsEntity: NewsEntity)

    @Query("DELETE FROM ${AppDatabase.TABLE_NEWS}")
    suspend fun clearAllNewsEntity()

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

interface NewsApiCache {
    suspend fun getCachedNewsList(): List<NewsResults.NewsItem>
    suspend fun addNewsEntity(response: NewsResults.NewsItem)
    suspend fun clearAllNewsEntity()

    suspend fun updateNewsEntity(response: NewsResults.NewsItem)
}


fun NewsResults.NewsItem.toEntity() = NewsEntity(
    author = author.orEmpty(),
    content = content,
    description = description.orEmpty(),
    publishedAt = publishedAt,
    source = source.name,
    title = title.ifBlank { UUID.randomUUID().toString() },
    url = url.orEmpty(),
    urlToImage = urlToImage.orEmpty(),
    bookmarked = isBookmarked
)

fun NewsEntity.toDTOModel() = NewsResults.NewsItem(
    author = author,
    content = content,
    description = description,
    publishedAt = publishedAt,
    source = NewsResults.Source(source),
    title = title,
    url = url,
    urlToImage = urlToImage,
    isBookmarked = bookmarked
)


@Entity(tableName = AppDatabase.TABLE_NEWS)
data class NewsEntity(
    @PrimaryKey val title: String = "",
    val author: String = "",
    val content: String = "",
    val description: String = "",
    val publishedAt: String = "",
    val source: String = "",
    val url: String = "",
    val urlToImage: String = "",
    val bookmarked : Boolean
)








