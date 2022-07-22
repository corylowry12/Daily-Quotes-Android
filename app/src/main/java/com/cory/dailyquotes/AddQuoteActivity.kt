package com.cory.dailyquotes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class AddQuoteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_quote)

        val quoteEditText = findViewById<TextInputEditText>(R.id.quote)

        val addQuoteButton = findViewById<Button>(R.id.addQuote)

        addQuoteButton.setOnClickListener {
            QuotesDBHelper(this, null).insertRow(intent.getStringExtra("personID").toString(), quoteEditText.text.toString(), Date().toString())
            finish()
        }
    }
}