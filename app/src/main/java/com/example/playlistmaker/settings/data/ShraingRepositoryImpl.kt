package com.example.playlistmaker.settings.data

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.common.domain.models.Playlist
import com.example.playlistmaker.settings.domain.api.SharingRepository
import com.example.playlistmaker.settings.domain.model.EmailData

class SharingRepositoryImpl(private val context: Context) : SharingRepository {
    override fun getShareAppLink(): String {
        return context.getString(R.string.yp_url)
    }
    override fun getTermsLink(): String {
        return context.getString(R.string.offer_url)
    }
    override fun getSupportEmailData(): EmailData {
        return EmailData(
            subject = context.getString(R.string.message_subject_to_support),
            message =context.getString(R.string.message_to_support),
            address = context.getString(R.string.support_email)
        )
    }

    override fun sharePlaylist(playlist: String): String {
        return playlist
    }
}