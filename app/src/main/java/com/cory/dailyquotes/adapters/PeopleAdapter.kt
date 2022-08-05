package com.cory.dailyquotes.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.cory.dailyquotes.*
import com.cory.dailyquotes.DB.PeopleDBHelper
import com.cory.dailyquotes.DB.QuotesDBHelper
import com.cory.dailyquotes.intents.MainActivity
import com.cory.dailyquotes.intents.QuotesActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.ByteArrayOutputStream


class PeopleAdapter(
    val context: Context,
    private val dataList: ArrayList<HashMap<String, String>>,
    private val imageDataList: ArrayList<HashMap<String, ByteArray>>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var name = itemView.findViewById<TextView>(R.id.row_person_name)!!

        @SuppressLint("Range")
        fun bind(position: Int) {
            val dataItem = dataList[position]

            name.text = dataItem["personName"]
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.people_list_item, parent, false)
        )
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val dataItem = dataList[position]

        val imageDataItem = imageDataList[position]

        val personImage = holder.itemView.findViewById<ImageView>(R.id.personImage)

        //if (dataItem["location"] != "internet") {

            if (!imageDataItem["personImage"].contentEquals(ByteArray(0))) {
                val image = BitmapFactory.decodeByteArray(
                    imageDataItem["personImage"],
                    0,
                    imageDataItem["personImage"]!!.size
                )

                Glide.with(context)
                    .load(image)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(personImage)
            } else {
                Glide.with(context)
                    .load(ContextCompat.getDrawable(context, R.drawable.ic_outline_person_24))
                    .dontAnimate()
                    .dontTransform()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(personImage)
            }
        /*} else {

            Glide.with(context)
                .load(dataItem["personImage"])
                .dontAnimate()
                .dontTransform()
                .error(ContextCompat.getDrawable(context, R.drawable.ic_outline_person_24))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(personImage)
        }*/

        holder.itemView.findViewById<ConstraintLayout>(R.id.personItemConstraintLayout)
            .setOnClickListener {
                val quotesIntent = Intent(context, QuotesActivity::class.java)
                quotesIntent.putExtra("id", dataItem["id"])
                quotesIntent.putExtra("location", dataItem["location"])
                if (!imageDataItem["personImage"].contentEquals(ByteArray(0))) {
                    val bs = ByteArrayOutputStream()
                    val image = BitmapFactory.decodeByteArray(
                        imageDataItem["personImage"],
                        0,
                        imageDataItem["personImage"]!!.size
                    )

                    val bitmap = personImage.drawable.toBitmap(400, 400)
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    quotesIntent.putExtra("image", stream.toByteArray())

                } else {
                    val bitmap = personImage.drawable.toBitmap(200, 200)
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    quotesIntent.putExtra("image", stream.toByteArray())
                }
                val pair1 = Pair.create<View, String>(
                    holder.itemView.findViewById<ImageView>(R.id.personImage),
                    "people_image"
                )
                val pair2 = Pair.create<View, String>(
                    holder.itemView.findViewById<CardView>(R.id.personImageCardView),
                    "people_card_view"
                )
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    (context as AppCompatActivity),
                    pair1,
                    pair2
                )
                context.startActivity(quotesIntent, options.toBundle())
            }

        holder.itemView.findViewById<ConstraintLayout>(R.id.personItemConstraintLayout)
            .setOnLongClickListener {
                val dialog = BottomSheetDialog(context)
                val optionsView = LayoutInflater.from(context)
                    .inflate(R.layout.people_options_bottom_sheet_layout, null)
                dialog.setContentView(optionsView)

                optionsView.findViewById<TextView>(R.id.deletePeopleTextView).text =
                    "Delete ${dataItem["personName"]}"

                val editConstraint =
                    optionsView.findViewById<ConstraintLayout>(R.id.editPersonConstraint)
                val deleteConstraint =
                    optionsView.findViewById<ConstraintLayout>(R.id.deletePeopleConstraintlayout)
                val deleteAllConstraint =
                    optionsView.findViewById<ConstraintLayout>(R.id.deleteAllPeopleConstraintlayout)

                editConstraint.visibility = View.GONE

                /*if (dataItem["location"] == "internet") {
                    editConstraint.visibility = View.GONE
                }
                else {
                    editConstraint.visibility = View.VISIBLE
                }*/

                editConstraint.setOnClickListener {

                }
                deleteConstraint.setOnClickListener {
                    val materialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
                    materialAlertDialogBuilder.setTitle("Delete ${dataItem["personName"]}?")
                    materialAlertDialogBuilder.setMessage("Would you like to delete ${dataItem["personName"]}? This will delete them and all their quotes. Continue?")
                    materialAlertDialogBuilder.setPositiveButton("Yes") { _, _ ->
                        QuotesDBHelper(context, null).deleteRow(dataItem["id"].toString())
                        PeopleDBHelper(context, null).deleteRow(dataItem["id"].toString())
                        dataList.removeAt(holder.adapterPosition)
                        imageDataList.removeAt(holder.adapterPosition)
                        notifyItemRemoved(holder.adapterPosition)
                        dialog.dismiss()

                        val runnable = Runnable {
                            (context as MainActivity).peopleTextViewVisibility()
                        }
                        MainActivity().runOnUiThread(runnable)
                    }
                    materialAlertDialogBuilder.setNegativeButton("No", null)
                    materialAlertDialogBuilder.show()
                }
                deleteAllConstraint.setOnClickListener {
                    val materialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
                    materialAlertDialogBuilder.setTitle("Delete all?")
                    materialAlertDialogBuilder.setMessage("Would you like to delete all people? This will delete them and all their quotes. Continue?")
                    materialAlertDialogBuilder.setPositiveButton("Yes") { _, _ ->
                        QuotesDBHelper(context, null).deleteAll()
                        PeopleDBHelper(context, null).deleteAll()
                        notifyItemRangeRemoved(0, dataList.count())
                        dataList.clear()
                        imageDataList.clear()
                        dialog.dismiss()

                        val runnable = Runnable {
                            (context as MainActivity).peopleTextViewVisibility()
                        }
                        MainActivity().runOnUiThread(runnable)
                    }
                    materialAlertDialogBuilder.setNegativeButton("No", null)
                    materialAlertDialogBuilder.show()
                }

                dialog.show()
                true
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