package com.example.playlistmaker.media.playlists.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class CreatePlaylistFragment : androidx.fragment.app.Fragment() {

    private lateinit var binding: FragmentCreatePlaylistBinding

    private val viewModel: CreatePlaylistViewModel by viewModels()

    private lateinit var backButton: ImageButton
    private lateinit var createButton: MaterialButton
    private lateinit var playlistNameInputLayout: TextInputLayout
    private lateinit var descriptionInputLayout: TextInputLayout
    private lateinit var playlistNameEditText: TextInputEditText
    private lateinit var playlistDescriptionEditText: TextInputEditText
    private lateinit var playlistImage: ShapeableImageView
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>

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

        playlistNameEditText.imeOptions = android.view.inputmethod.EditorInfo.IME_ACTION_NEXT
        playlistNameEditText.setRawInputType(android.text.InputType.TYPE_CLASS_TEXT)
        playlistDescriptionEditText.imeOptions = android.view.inputmethod.EditorInfo.IME_ACTION_NEXT
        playlistDescriptionEditText.setRawInputType(android.text.InputType.TYPE_CLASS_TEXT)

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
    }

    private fun setupListeners() {
        updateDescriptionImeOptions()

        playlistImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
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
            viewModel.isEdited()
        }

        backButton.setOnClickListener {
            if (viewModel.isEdited()) {
                dialog()
            } else {
                findNavController().popBackStack()
            }
        }
    }

    fun dialog() {
        MaterialAlertDialogBuilder(requireContext(), R.style.alertStyle)
            .setTitle(requireContext().getString(R.string.cancel_creating_playlist))
            .setMessage(requireContext().getString(R.string.unsaved_changes_will_be_lost))
            .setNegativeButton(requireContext().getString(R.string.no)) { _, _ -> }
            .setPositiveButton(requireContext().getString(R.string.yes)) { _, _ -> findNavController().popBackStack() }
            .show()
    }

    private fun updateDescriptionImeOptions() {
        playlistDescriptionEditText.imeOptions = if (playlistNameEditText.text.isNullOrBlank()) {
            android.view.inputmethod.EditorInfo.IME_ACTION_NEXT
        } else {
            android.view.inputmethod.EditorInfo.IME_ACTION_DONE
        }
    }

    private fun observeViewModel() {
        viewModel.isCreateButtonEnabled.observe(viewLifecycleOwner, Observer { isEnabled ->
            createButton.isEnabled = isEnabled
        })
    }
}