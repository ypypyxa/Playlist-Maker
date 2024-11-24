package com.example.playlistmaker.media.edit.ui

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.domain.api.PlaylistInteractor
import com.example.playlistmaker.common.domain.models.Playlist
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class EditPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val application: Application
) : AndroidViewModel(application) {

    private var editedName = ""
    private var editedDescription = ""
    private var editedUri = "0".toUri()

    private lateinit var oldPlaylist: Playlist

    private val _isEditButtonEnabled = MutableLiveData(false)
    val isEditButtonEnabled: LiveData<Boolean> = _isEditButtonEnabled

    fun updatePlaylist(oldPlaylist: Playlist) {
        updateEditButtonState()
        saveCover()
        createPlaylist()
    }
    private fun saveCover() {
        if (editedUri != oldPlaylist.artworkUri.toUri()) {
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

                // Удаляем файл обложки плейлиста, если он есть
                val artworkUri = oldPlaylist.artworkUri.toUri()
                if (artworkUri != null && artworkUri.scheme == "file") {
                    val file = File(artworkUri.path!!)
                    if (file.exists()) {
                        val deleted = file.delete()
                        if (!deleted) {
                            // Логируем или обрабатываем ситуацию, если файл не удалось удалить
                            Log.e("PlaylistViewModel", "Failed to delete file: ${file.absolutePath}")
                        }
                    }
                }
            }
        }
    }

    fun initPlaylist(playlist: Playlist) {
        oldPlaylist = playlist
    }

    private fun createPlaylist() {
        val playlist = Playlist(
            playlistId = oldPlaylist.playlistId,
            playlistName = editedName,
            playlistDescription = editedDescription,
            artworkUri = editedUri.toString(),
            tracks = oldPlaylist.tracks,
            tracksCount = oldPlaylist.tracksCount
        )
        savePlaylist(playlist)
    }

    private fun savePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.updatePlaylist(playlist)
        }
    }

    fun setPlaylistName(name: String) {
        editedName = name
        updateEditButtonState()
    }
    fun setPlaylistDescription(description: String) {
        editedDescription = description
        updateEditButtonState()
    }
    fun setPlaylistUri(uri: Uri) {
        editedUri = uri
        updateEditButtonState()
    }

    private fun updateEditButtonState() {
        // Проверяем, инициализирован ли oldPlaylist
        val isPlaylistInitialized = ::oldPlaylist.isInitialized

        // Проверяем, заполнено ли имя
        val isNameNotBlank = editedName.isNotBlank()

        // Проверяем, изменились ли данные
        val isDataChanged = isPlaylistInitialized && (
                editedName != oldPlaylist.playlistName ||
                        editedDescription != oldPlaylist.playlistDescription ||
                        editedUri != oldPlaylist.artworkUri.toUri()
                )

        // Кнопка активна, если имя не пустое и данные изменились
        _isEditButtonEnabled.value = isNameNotBlank && isDataChanged
    }

    fun isEdited(): Boolean {
        if (!::oldPlaylist.isInitialized) {
            return false
        }

        // Возвращаем true, если данные изменились
        return editedName != oldPlaylist.playlistName ||
                editedDescription != oldPlaylist.playlistDescription ||
                editedUri != oldPlaylist.artworkUri.toUri()
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