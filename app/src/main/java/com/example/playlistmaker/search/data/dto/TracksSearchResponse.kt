package com.example.playlistmaker.search.data.dto

class TracksSearchResponse(val searchType: String,
                           val expression: String,
                           val results: List<TrackDto>) : Response()