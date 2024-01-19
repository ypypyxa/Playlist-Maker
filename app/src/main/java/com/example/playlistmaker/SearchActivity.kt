package com.example.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.view.isVisible

class SearchActivity : AppCompatActivity() {
    private var textValue: String = TEXT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val backButton = findViewById<ImageButton>(R.id.back)
        val clearButton = findViewById<ImageView>(R.id.clearButton)
        val editText = findViewById<EditText>(R.id.editText)

        savedInstanceState?.let {
            textValue = savedInstanceState.getString(EDITED_TEXT, TEXT)
        }
        editText.setText(textValue)

        backButton.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            editText.setText("")
            hideKeyboard()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                textValue = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                //empty
            }
        }
        editText.addTextChangedListener(simpleTextWatcher)
    }

    private fun hideKeyboard() {
        val view = currentFocus ?: return
        val method = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        method.hideSoftInputFromWindow(view.windowToken, 0)
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDITED_TEXT, textValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Вторым параметром мы передаём значение по умолчанию
        textValue = savedInstanceState.getString(EDITED_TEXT, TEXT)

    }
    companion object {
        private const val EDITED_TEXT = "EDITED_TEXT"
        private const val TEXT = ""
    }
}