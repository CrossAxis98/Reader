package com.example.reader.di

import com.example.reader.network.BooksApi
import com.example.reader.repository.BookRepository
import com.example.reader.utils.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideBookRepository(api: BooksApi) = BookRepository(api)

    @Singleton
    @Provides
    fun provideBookApi(): BooksApi {
        return Retrofit.Builder().
        baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BooksApi::class.java)
    }

}