package io.github.curioustools.curiousnews.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.curioustools.curiousnews.data.entitiy.NewsEntity
import io.github.curioustools.curiousnews.data.AppDatabase

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