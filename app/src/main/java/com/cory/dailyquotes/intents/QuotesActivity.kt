package com.cory.dailyquotes.intents

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cory.dailyquotes.DB.PeopleDBHelper
import com.cory.dailyquotes.DB.QuotesDBHelper
import com.cory.dailyquotes.R
import com.cory.dailyquotes.adapters.QuotesAdapter
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import java.util.*
import kotlin.collections.HashMap

class QuotesActivity : AppCompatActivity() {

    private lateinit var quotesAdapter: QuotesAdapter
    private val dataList = ArrayList<HashMap<String, String>>()
    private lateinit var gridLayoutManager: GridLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quotes)

        //val appBarQuotes = findViewById<AppBarLayout>(R.id.appBarLayoutQuotes)
        val materialToolbarQuotes = findViewById<MaterialToolbar>(R.id.materialToolBarQuotes)

        materialToolbarQuotes.setNavigationOnClickListener {
            finishAfterTransition()
        }

        gridLayoutManager = GridLayoutManager(this, 1)
        quotesAdapter = QuotesAdapter(this, dataList)

        val addQuotesFAB = findViewById<FloatingActionButton>(R.id.fabAddQuote)
        addQuotesFAB.setOnClickListener {
            val dialog = BottomSheetDialog(this)
            val addView = layoutInflater.inflate(R.layout.add_quotes_bottom_sheet_layout, null)
            dialog.setContentView(addView)
            dialog.setCancelable(false)

            val quoteInputEditText = addView.findViewById<TextInputEditText>(R.id.quote)
            val addQuoteButton = addView.findViewById<Button>(R.id.addQuoteButton)
            val cancelButton = addView.findViewById<Button>(R.id.cancelButton)

            addQuoteButton.setOnClickListener {
                if (quoteInputEditText.text.toString() == "") {
                    Toast.makeText(this, "Quote required", Toast.LENGTH_SHORT).show()
                }
                else {
                    if (QuotesDBHelper(this, null).checkIfExistsForASpecificPerson(quoteInputEditText.text.toString().trim().replace("'", "''"), intent.getStringExtra("id").toString())) {
                        Toast.makeText(this, "Quote already exists", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        QuotesDBHelper(this, null).insertRow(
                            intent.getStringExtra("id").toString(),
                            quoteInputEditText.text.toString().trim(),
                            Date().toString()
                        )
                        loadIntoList()
                        dialog.dismiss()
                    }
                }
            }
            cancelButton.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

        loadIntoList()
    }

fun loadIntoList() {
    val nameTextView = findViewById<TextView>(R.id.nameTextView)
    val bioTextView = findViewById<TextView>(R.id.bioTextView)
    val personImageView = findViewById<ImageView>(R.id.personImageImageView)

    val dbHandler = PeopleDBHelper(applicationContext, null)

    val cursor = dbHandler.getRow(intent.getStringExtra("id")!!)
    cursor.moveToFirst()

    val mCollapsingToolbar = findViewById<CollapsingToolbarLayout>(R.id.collapsingToolBarQuotes)

    mCollapsingToolbar.title = cursor.getString(cursor.getColumnIndex(PeopleDBHelper.COLUMN_NAME))
    mCollapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent))
    mCollapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.black))

    nameTextView.text =
        cursor.getString(cursor.getColumnIndex(PeopleDBHelper.COLUMN_NAME))
    //val id = PeopleDBHelper(applicationContext, null).getPersonID(nameTextView.text.toString())
    if (cursor.getString(cursor.getColumnIndex(PeopleDBHelper.COLUMN_BIO)) == "" || cursor.getString(cursor.getColumnIndex(
            PeopleDBHelper.COLUMN_BIO
        )) == null) {
        bioTextView.text = "There is no bio"
    }
    else {
        bioTextView.text = cursor.getString(cursor.getColumnIndex(PeopleDBHelper.COLUMN_BIO))
    }
    /*if (intent.getStringExtra("location") != "internet") {
        try {
            if (!cursor.getBlob(cursor.getColumnIndex(PeopleDBHelper.COLUMN_IMAGE))
                    .contentEquals(ByteArray(0))
            ) {
                image = BitmapFactory.decodeByteArray(
                    cursor.getBlob(cursor.getColumnIndex(PeopleDBHelper.COLUMN_IMAGE)),
                    0,
                    cursor.getBlob(cursor.getColumnIndex(PeopleDBHelper.COLUMN_IMAGE)).size
                )
                personImageView.setImageBitmap(image)
            } else {
                personImageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_outline_person_24
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            personImageView.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_outline_person_24
                )
            )
        }
    }
    else {
        Glide.with(applicationContext)
            .load(cursor.getString(cursor.getColumnIndex(PeopleDBHelper.COLUMN_FETCHED_IMAGE)))
            .into(personImageView)
    }*/

    val image = BitmapFactory.decodeByteArray(intent.getByteArrayExtra("image"), 0, intent.getByteArrayExtra("image")!!.size)
    personImageView.setImageBitmap(image)

    dataList.clear()

    val dbHandlerQuotes = QuotesDBHelper(applicationContext, null)
    val cursorQuotes = dbHandlerQuotes.getAllForAPerson(intent.getStringExtra("id")!!)
    cursorQuotes!!.moveToFirst()

    if (cursorQuotes.count == 0) {
        val noQuotesStoredTextView = findViewById<TextView>(R.id.noQuotesStoredTextView)
        noQuotesStoredTextView.visibility = View.VISIBLE
    }
    else {
        val noQuotesStoredTextView = findViewById<TextView>(R.id.noQuotesStoredTextView)
        noQuotesStoredTextView.visibility = View.GONE
    }

    while(!cursorQuotes.isAfterLast) {
        val map = HashMap<String, String>()

        map["id"] = cursorQuotes.getString(cursorQuotes.getColumnIndex(QuotesDBHelper.COLUMN_ID))

        map["quote"] =
            cursorQuotes.getString(cursorQuotes.getColumnIndex(QuotesDBHelper.COLUMN_QUOTE))
        map["personID"] = cursorQuotes.getString(cursorQuotes.getColumnIndex(QuotesDBHelper.COLUMN_PERSON_ID))

        dataList.add(map)

        cursorQuotes.moveToNext()
    }
    val recyclerView = findViewById<RecyclerView>(R.id.quotesRecyclerView)
    gridLayoutManager = GridLayoutManager(this, 1)
    recyclerView?.layoutManager = gridLayoutManager
    recyclerView?.adapter = quotesAdapter
}

    fun textViewVisibility() {
        val dbHandlerQuotes = QuotesDBHelper(applicationContext, null)
        val cursorQuotes = dbHandlerQuotes.getAllForAPerson(intent.getStringExtra("id")!!)
        cursorQuotes!!.moveToFirst()

        if (cursorQuotes.count == 0) {
            val noQuotesStoredTextView = findViewById<TextView>(R.id.noQuotesStoredTextView)
            noQuotesStoredTextView.visibility = View.VISIBLE
        }
        else {
            val noQuotesStoredTextView = findViewById<TextView>(R.id.noQuotesStoredTextView)
            noQuotesStoredTextView.visibility = View.GONE
        }
    }
}