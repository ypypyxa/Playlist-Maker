package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.player.data.Favorites
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.network.MusicApi
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.settings.data.ExternalNavigatorImpl
import com.example.playlistmaker.settings.domain.api.ExternalNavigator
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    val baseUrl = "https://itunes.apple.com"

    single<MusicApi> {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MusicApi::class.java)
    }

    factory { Gson() }

    single<NetworkClient> {
        RetrofitNetworkClient(get(), androidContext())
    }

    single<Favorites> {
        Favorites(androidContext().getSharedPreferences(Favorites.FAVORITES, Context.MODE_PRIVATE))
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }
}