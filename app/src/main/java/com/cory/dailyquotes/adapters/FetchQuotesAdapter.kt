package com.cory.dailyquotes.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.cory.dailyquotes.DB.PeopleDBHelper
import com.cory.dailyquotes.DB.QuotesDBHelper
import com.cory.dailyquotes.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@OptIn(DelicateCoroutinesApi::class)
class FetchQuotesAdapter(
    val context: Context,
    private val dataList: ArrayList<HashMap<String, String>>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var name = itemView.findViewById<TextView>(R.id.row_person_name)!!
        val personImage = itemView.findViewById<ImageView>(R.id.personImage)!!

        @SuppressLint("Range")
        fun bind(position: Int) {

            val dataItem = dataList[position]
            name.text = dataItem["a"]

            Glide.with(context)
                .load(dataItem["i"])
                .dontAnimate()
                .dontTransform()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.drawable.ic_outline_person_24)
                .into(personImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.fetch_quotes_list_item, parent, false)
        )
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val dataItem = dataList[position]

        holder.itemView.findViewById<CardView>(R.id.cardViewFetchedQuoteItem).setOnClickListener {
            val viewQuoteDialog = BottomSheetDialog(context)
            val viewQuoteView =
                LayoutInflater.from(context).inflate(R.layout.fetched_quote_bottom_sheet, null)
            viewQuoteDialog.setContentView(viewQuoteView)

            viewQuoteView.findViewById<TextView>(R.id.fetchedQuoteTextView).text = dataItem["q"]
            viewQuoteView.findViewById<TextView>(R.id.fetchedQuoteAuthorTextView).text =
                "- ${dataItem["a"]} "

            viewQuoteView.findViewById<Button>(R.id.addFetchedQuoteButton).setOnClickListener {
                if (PeopleDBHelper(context, null).checkIfExists(dataItem["a"].toString())) {
                    if (QuotesDBHelper(context, null).checkIfExistsForASpecificPerson(
                            dataItem["q"].toString().replace("'", "''"), PeopleDBHelper(
                                context,
                                null
                            ).getPersonID(dataItem["a"].toString())
                        )) {
                        Toast.makeText(context, "Quote already exists", Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    if (PeopleDBHelper(context, null).checkIfExists(dataItem["a"].toString())) {
                        QuotesDBHelper(context, null).insertRow(PeopleDBHelper(context, null).getPersonID(dataItem["a"].toString()), dataItem["q"].toString(), Date().toString())
                    }
                    else {
                        GlobalScope.launch(Dispatchers.IO) {
                            try {
                                val url = URL(dataItem["i"].toString())
                                val con = url.openConnection()
                                con.doInput = true
                                con.connect()

                                val input = con.getInputStream()
                                val bitmap = BitmapFactory.decodeStream(input)
                                input.close()
                                val stream = ByteArrayOutputStream()
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                                val image2 = stream.toByteArray()
                                PeopleDBHelper(context, null).insertRow(
                                    dataItem["a"].toString(),
                                    "",
                                    image2,
                                    "internet"
                                )
                                QuotesDBHelper(
                                    context,
                                    null
                                ).insertRow(
                                    PeopleDBHelper(
                                        context,
                                        null
                                    ).getPersonID(dataItem["a"].toString()),
                                    dataItem["q"].toString(),
                                    Date().toString()
                                )
                            } catch (e: FileNotFoundException) {
                                PeopleDBHelper(context, null).insertRow(
                                    dataItem["a"].toString(),
                                    "",
                                    ByteArray(0),
                                    "internet"
                                )
                                QuotesDBHelper(
                                    context,
                                    null
                                ).insertRow(
                                    PeopleDBHelper(
                                        context,
                                        null
                                    ).getPersonID(dataItem["a"].toString()),
                                    dataItem["q"].toString(),
                                    Date().toString()
                                )
                            }
                        }
                    }
                }
                viewQuoteDialog.dismiss()
            }

            viewQuoteDialog.show()
        }

        (holder as ViewHolder).bind(position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(true)
    }
}