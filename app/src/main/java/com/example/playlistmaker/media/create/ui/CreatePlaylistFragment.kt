package com.example.playlistmaker.media.create.ui

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
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.view.inputmethod.EditorInfo.IME_ACTION_NEXT
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

import org.koin.androidx.viewmodel.ext.android.viewModel

class CreatePlaylistFragment : Fragment() {

    private val viewModel by viewModel<CreatePlaylistViewModel>()

    private lateinit var binding: FragmentCreatePlaylistBinding

    private lateinit var backButton: ImageButton
    private lateinit var createButton: MaterialButton
    private lateinit var playlistNameInputLayout: TextInputLayout
    private lateinit var descriptionInputLayout: TextInputLayout
    private lateinit var playlistNameEditText: TextInputEditText
    private lateinit var playlistDescriptionEditText: TextInputEditText
    private lateinit var playlistImage: ShapeableImageView
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View? {
        binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)

        backButton = binding.backButton
        createButton = binding.createButton
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

        createButton.setOnClickListener {
            viewModel.createNewPlaylist()
            Toast.makeText(
                requireContext(),
                "${requireContext().getString(R.string.playlist)}" +
                    " ${playlistNameEditText.text}" +
                    " ${requireContext().getString(R.string.created)}.",
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
        viewModel.isCreateButtonEnabled.observe(viewLifecycleOwner, Observer { isEnabled ->
            createButton.isEnabled = isEnabled
        })
    }
}