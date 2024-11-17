package com.example.playlistmaker.media.create.ui

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.domain.models.Playlist
import com.example.playlistmaker.common.domain.api.PlaylistInteractor
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class CreatePlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val application: Application
) : AndroidViewModel(application) {

    private var editedName = ""
    private var editedDescription = ""
    private var editedUri = "0".toUri()

    private val _isCreateButtonEnabled = MutableLiveData(false)
    val isCreateButtonEnabled: LiveData<Boolean> = _isCreateButtonEnabled

    fun createNewPlaylist() {
        saveCover()
        createPlaylist()
    }
    private fun saveCover() {
        if (editedUri.toString() != "0") {

            val filePath = File(getApplication<Application>().getExternalFilesDir(Environment.DIRECTORY_PICTURES), STORAGE_DIR)
            if (!filePath.exists()){
                filePath.mkdirs()
            }

            // Генерируем случайное имя файла
            var file: File
            do {
                val fileName = generateCustomUUID(prefix = PREFIX, extension = EXTENSION, length = 10)
                file = File(filePath, fileName)
            } while (file.exists()) // Повторяем, пока файл с этим именем существует

            val inputStream = getApplication<Application>().contentResolver.openInputStream(editedUri)
            val outputStream = FileOutputStream(file)

            BitmapFactory
                .decodeStream(inputStream)
                .compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

            editedUri = file.toUri()
        }
    }

    private fun createPlaylist() {
        val playlist = Playlist(
            playlistId = 0,
            playlistName = editedName,
            playlistDescription = editedDescription,
            artworkUri = editedUri,
            tracks = emptyList(),
            tracksCount = 0
        )

        savePlaylist(playlist)
    }

    private fun savePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.addPlaylist(playlist)
        }
    }

    fun setPlaylistName(name: String) {
        _isCreateButtonEnabled.value = name.isNotBlank()
        editedName = name
    }
    fun setPlaylistDescription(description: String) {
        editedDescription = description
    }
    fun setPlaylistUri(uri: Uri) {
        editedUri = uri
    }

    fun isEdited(): Boolean {
            when {
                editedName?.isEmpty() == false -> return true
                editedDescription?.isEmpty() == false -> return true
                editedUri?.toString()?.isEmpty() == false ->
                    return editedUri != "0".toUri()
                else -> return false
            }
    }

    private fun generateCustomUUID(prefix: String = "", extension: String = "", length: Int = 8): String {
        val randomPart = UUID.randomUUID().toString().replace("-", "").take(length) // Генерируем случайную часть с указанной длиной
        return "$prefix$randomPart$extension" // Объединяем префикс, случайную часть и суффикс
    }

    companion object {
        private const val STORAGE_DIR = "Playlist Covers"
        private const val EXTENSION = ".jpg"
        private const val PREFIX = "img_"
    }
}