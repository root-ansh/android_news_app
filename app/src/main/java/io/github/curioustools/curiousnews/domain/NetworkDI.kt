package io.github.curioustools.curiousnews.domain

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.curioustools.curiousnews.data.SharedPrefs
import io.github.curioustools.curiousnews.commons.isDebugApp
import io.github.curioustools.curiousnews.domain.repo.NewsApiRepo
import io.github.curioustools.curiousnews.domain.repo.NewsApiRepoImpl
import io.github.curioustools.curiousnews.domain.service.NewsApiService
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkDI{
    @Binds
    abstract fun bindRepo(repoImpl: NewsApiRepoImpl): NewsApiRepo

    companion object{

        @Provides
        @Singleton
        fun getRetrofit(@ApplicationContext ctx: Context): Retrofit {
            return NetworkClient.getRetrofit(ctx)
        }

        @Provides
        fun getAppApiService(retrofit: Retrofit): NewsApiService {
            return retrofit.create(NewsApiService::class.java)
        }
    }

}