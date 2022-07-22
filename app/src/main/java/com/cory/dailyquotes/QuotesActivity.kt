package com.cory.dailyquotes

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import java.util.*
import kotlin.collections.HashMap

class QuotesActivity : AppCompatActivity() {

    private lateinit var quotesAdapter: QuotesAdapter
    private val dataList = ArrayList<HashMap<String, String>>()
    private lateinit var gridLayoutManager: GridLayoutManager

    lateinit var image: Bitmap

    override fun onResume() {
        super.onResume()
        loadIntoList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quotes)

        val materialToolbarQuotes = findViewById<MaterialToolbar>(R.id.materialToolBarQuotes)
        materialToolbarQuotes.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.addPeople -> {
                    val addQuoteIntent = Intent(this, AddQuoteActivity::class.java)
                    addQuoteIntent.putExtra("personID", intent.getStringExtra("id"))
                    startActivity(addQuoteIntent)

                    //QuotesDBHelper(this, null).insertRow(intent.getStringExtra("id").toString(), "lorem", Date().toString())
                    //loadIntoList()
                    true
                }
                else -> false
            }
        }

        materialToolbarQuotes.setNavigationOnClickListener {
            finishAfterTransition()
        }

        gridLayoutManager = GridLayoutManager(this, 1)
        quotesAdapter = QuotesAdapter(this, dataList)

        loadIntoList()
    }

fun loadIntoList() {
    val nameTextView = findViewById<TextView>(R.id.nameTextView)
    val bioTextView = findViewById<TextView>(R.id.bioTextView)
    val personImageView = findViewById<ImageView>(R.id.personImageImageView)

    val dbHandler = PeopleDBHelper(applicationContext, null)

    val cursor = dbHandler.getRow(intent.getStringExtra("id")!!)
    cursor.moveToFirst()

    nameTextView.text =
        cursor.getString(cursor.getColumnIndex(PeopleDBHelper.COLUMN_NAME))
    if (cursor.getString(cursor.getColumnIndex(PeopleDBHelper.COLUMN_BIO)) == "" || cursor.getString(cursor.getColumnIndex(PeopleDBHelper.COLUMN_BIO)) == null) {
        bioTextView.text = "There is no bio"
    }
    else {
        bioTextView.text = cursor.getString(cursor.getColumnIndex(PeopleDBHelper.COLUMN_BIO))
    }
    try {
        if (!cursor.getBlob(cursor.getColumnIndex(PeopleDBHelper.COLUMN_IMAGE))
                .contentEquals(ByteArray(0))) {
            image = BitmapFactory.decodeByteArray(
                cursor.getBlob(cursor.getColumnIndex(PeopleDBHelper.COLUMN_IMAGE)),
                0,
                cursor.getBlob(cursor.getColumnIndex(PeopleDBHelper.COLUMN_IMAGE)).size
            )
            personImageView.setImageBitmap(image)
        }
        else {
            personImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_outline_person_24))
        }
    } catch (e : Exception) {
        e.printStackTrace()
        personImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_outline_person_24))
    }

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

        dataList.add(map)

        cursorQuotes.moveToNext()
    }
    val recyclerView = findViewById<RecyclerView>(R.id.quotesRecyclerView)
    gridLayoutManager = GridLayoutManager(this, 1)
    recyclerView?.layoutManager = gridLayoutManager
    recyclerView?.adapter = quotesAdapter
}
}