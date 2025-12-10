package io.github.curioustools.curiousnews.data

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.curioustools.curiousnews.data.cache.NewsApiCache
import io.github.curioustools.curiousnews.data.cache.NewsApiCacheImpl
import io.github.curioustools.curiousnews.data.dao.NewsDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppDatabaseDI{

    @Binds
    abstract fun bindCache(cacheImpl: NewsApiCacheImpl): NewsApiCache

    companion object{
        @Singleton @Provides
        fun providesRoomDatabase(@ApplicationContext context: Context): AppDatabase {
            return AppDatabase.instance(context)
        }

        @Provides @Singleton
        fun providesNewsDao(appDatabase: AppDatabase): NewsDao {
            return appDatabase.newsDao()
        }

        @Provides
        @Singleton
        fun provideSharedPrefs(@ApplicationContext context: Context, ): SharedPrefs {
            return SharedPrefs(context)

        }


    }

}