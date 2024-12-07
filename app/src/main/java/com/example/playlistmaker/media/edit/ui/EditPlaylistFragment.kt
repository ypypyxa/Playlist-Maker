package com.example.playlistmaker.media.edit.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.InputType.TYPE_CLASS_TEXT
import android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.view.inputmethod.EditorInfo.IME_ACTION_NEXT
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.common.domain.models.Playlist
import com.example.playlistmaker.databinding.FragmentEditPlaylistBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlaylistFragment : Fragment() {

    private val viewModel by viewModel<EditPlaylistViewModel>()

    private lateinit var binding: FragmentEditPlaylistBinding

    private lateinit var backButton: ImageButton
    private lateinit var editButton: MaterialButton
    private lateinit var playlistNameInputLayout: TextInputLayout
    private lateinit var descriptionInputLayout: TextInputLayout
    private lateinit var playlistNameEditText: TextInputEditText
    private lateinit var playlistDescriptionEditText: TextInputEditText
    private lateinit var playlistImage: ShapeableImageView
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var oldPlaylist: Playlist

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View? {
        binding = FragmentEditPlaylistBinding.inflate(inflater, container, false)

        oldPlaylist = arguments?.getSerializable(ARGS_EDIT_PLAYLIST) as Playlist

        backButton = binding.backButton
        editButton = binding.editButton
        playlistImage = binding.dottedPlaceholder


        playlistNameInputLayout = binding.playlistName
        descriptionInputLayout = binding.playlistDescription

        playlistNameEditText = playlistNameInputLayout.editText as TextInputEditText
        playlistDescriptionEditText = descriptionInputLayout.editText as TextInputEditText

        playlistNameEditText.imeOptions = IME_ACTION_NEXT
        playlistNameEditText.setRawInputType(TYPE_CLASS_TEXT or TYPE_TEXT_FLAG_CAP_SENTENCES)
        playlistDescriptionEditText.imeOptions = IME_ACTION_NEXT
        playlistDescriptionEditText.setRawInputType(TYPE_CLASS_TEXT or TYPE_TEXT_FLAG_CAP_SENTENCES)

        setupListeners()
        observeViewModel()
        viewModel.initPlaylist(oldPlaylist)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                playlistImage.setImageURI(uri)
                viewModel.setPlaylistUri(uri)
            }
        }

        // Инициализация запроса разрешений
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Разрешение предоставлено, запускаем выбор изображения
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                // Разрешение отклонено
                showPermissionDeniedDialog()
            }
        }

        render()
    }

    private fun setupListeners() {
        updateDescriptionImeOptions()

        playlistImage.setOnClickListener {
            requestPhotoPermission()
        }

        playlistNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setPlaylistName(s.toString())
                updateDescriptionImeOptions()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        playlistDescriptionEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setPlaylistDescription(s.toString())
                updateDescriptionImeOptions()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        editButton.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                "playlistUpdated",
                bundleOf("playlistId" to oldPlaylist.playlistId)
            )
            viewModel.updatePlaylist(oldPlaylist)
            Toast.makeText(
                requireContext(),
                "${requireContext().getString(R.string.playlist)}" +
                        " ${playlistNameEditText.text}" +
                        " ${requireContext().getString(R.string.edited)}.",
                Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        backButton.setOnClickListener {
            if (viewModel.isEdited()) {
                val flag = viewModel.isEdited()
                dialog()
            } else {
                findNavController().popBackStack()
            }
        }

        // Обработка системной кнопки "Назад"
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (viewModel.isEdited()) {
                    val flag = viewModel.isEdited()
                    dialog()
                } else {
                    findNavController().popBackStack()
                }
            }
        })
    }

    private fun render() {
        binding.nameEdit.setText(oldPlaylist.playlistName)
        viewModel.setPlaylistName(oldPlaylist.playlistName)
        binding.descriptionEdit.setText(oldPlaylist.playlistDescription)
        viewModel.setPlaylistDescription(oldPlaylist.playlistDescription)
        if (oldPlaylist.artworkUri != "0") {
            playlistImage.setImageURI(oldPlaylist.artworkUri.toUri())
            viewModel.setPlaylistUri(oldPlaylist.artworkUri.toUri())
        }
    }

    private fun requestPhotoPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        requestPermissionLauncher.launch(permission)
    }

    private fun showPermissionDeniedDialog() {
        MaterialAlertDialogBuilder(requireContext(), R.style.alertStyle)
            .setTitle(requireContext().getString(R.string.permission_denied))
            .setMessage(requireContext().getString(R.string.permission_denied_message))
            .setPositiveButton(requireContext().getString(R.string.open_settings)) { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", requireContext().packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton(requireContext().getString(R.string.cancel), null)
            .show()
    }

    fun dialog() {
        MaterialAlertDialogBuilder(requireContext(), R.style.alertStyle)
            .setTitle(requireContext().getString(R.string.cancel_creating_playlist))
            .setMessage(requireContext().getString(R.string.unsaved_changes_will_be_lost))
            .setNegativeButton(requireContext().getString(R.string.cancel)) { _, _ -> }
            .setPositiveButton(requireContext().getString(R.string.end)) { _, _ -> findNavController().popBackStack() }
            .show()
    }

    private fun updateDescriptionImeOptions() {
        playlistDescriptionEditText.imeOptions = if (playlistNameEditText.text.isNullOrBlank()) {
            IME_ACTION_NEXT
        } else {
            IME_ACTION_DONE
        }
    }

    private fun observeViewModel() {
        viewModel.isEditButtonEnabled.observe(viewLifecycleOwner, Observer { isEnabled ->
            editButton.isEnabled = isEnabled
        })
    }

    companion object {
        const val ARGS_EDIT_PLAYLIST = "EDIT_PLAYLIST"

        fun createArgs(playlist: Playlist): Bundle =
            bundleOf(ARGS_EDIT_PLAYLIST to playlist)
    }
}