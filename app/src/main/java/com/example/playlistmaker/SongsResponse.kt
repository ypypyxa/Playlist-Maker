package com.example.playlistmaker

class SongsResponse(val searchType: String,
                    val expression: String,
                    val results: List<Track>)