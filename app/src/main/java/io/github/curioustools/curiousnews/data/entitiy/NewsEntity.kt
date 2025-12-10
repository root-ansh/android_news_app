package io.github.curioustools.curiousnews.data.entitiy

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.curioustools.curiousnews.data.AppDatabase
import io.github.curioustools.curiousnews.domain.dto.NewsResults
import java.util.UUID

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
