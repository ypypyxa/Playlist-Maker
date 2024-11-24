package com.example.playlistmaker.settings.domain.api

import com.example.playlistmaker.settings.domain.model.EmailData

interface ExternalNavigator {
    fun shareLink(link: String)
    fun openLink(link: String)
    fun sendEmail(email: EmailData)
    fun sharePlaylist(playlist: String)
}