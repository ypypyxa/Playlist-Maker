package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.common.domain.models.Track
import com.example.playlistmaker.search.domain.api.SearchInteractor
import com.example.playlistmaker.search.domain.api.SearchRepository
import com.example.playlistmaker.search.domain.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchInteractorImpl(private val repository: SearchRepository) : SearchInteractor {

    override fun searchTracks(expression: String): Flow<Pair<List<Track>?, String?>> {
        return repository.searchTracks(expression).map { result ->
            when(result) {
                is Resource.Success -> { Pair(result.data, null) }
                is Resource.Error -> { Pair(null, result.message) }
            }
        }
    }
}