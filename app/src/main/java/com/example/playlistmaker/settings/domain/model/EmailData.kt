package com.example.playlistmaker.settings.domain.model

import okhttp3.Address

class EmailData (
    val subject :String,
    val message :String,
    val address: String)