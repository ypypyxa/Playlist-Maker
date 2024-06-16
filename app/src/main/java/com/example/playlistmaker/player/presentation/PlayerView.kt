package com.example.playlistmaker.player.presentation

interface PlayerView {
    fun setTrackName(trackName: String)
    fun setAlbum(album: String)
    fun setArtistName(artistName: String)
    fun setPlayTime(playTime: String)
    fun setReleaseDate(releaseDate: String)
    fun setTrackTime(trackTime: String)
    fun setTrackImage(artworkUrl512: String)
    fun setGenre(genre: String)
    fun setCountry(country: String)

    fun enablePlayPause(isEnabled: Boolean)
    fun setPlayPause(isPlaying: Boolean)

    fun showAlbumGroup(isVisible: Boolean)
    fun showCountryGroup(isVisible: Boolean)
    fun showGenreGroup(isVisible: Boolean)
    fun showReleaseGroup(isVisible: Boolean)
    fun showTrackTimeGroup(isVisible: Boolean)
}