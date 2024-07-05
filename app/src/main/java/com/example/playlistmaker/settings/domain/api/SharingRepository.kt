package com.example.playlistmaker.settings.domain.api

import com.example.playlistmaker.settings.domain.model.EmailData

interface SharingRepository {
    fun getShareAppLink(): String
    fun getTermsLink(): String
    fun getSupportEmailData(): EmailData
}