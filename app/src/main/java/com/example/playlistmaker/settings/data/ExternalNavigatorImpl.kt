package com.example.playlistmaker.settings.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.domain.api.ExternalNavigator
import com.example.playlistmaker.settings.domain.model.EmailData

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {

    override fun shareLink(link: String) {
        val title = context.getString(R.string.share_title)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, link)
            type = "text/plain"
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val intent = Intent.createChooser(shareIntent, title).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    override fun openLink(link: String) {
        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(link)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    override fun sendEmail(email: EmailData) {
        val mailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email.address))
            putExtra(Intent.EXTRA_SUBJECT, email.subject)
            putExtra(Intent.EXTRA_TEXT, email.message)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(mailIntent)
    }

    override fun sharePlaylist(playlist: String) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, playlist)
            type = "text/plain"
        }
        val share = Intent.createChooser(intent, null)
        share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(share)
    }
}