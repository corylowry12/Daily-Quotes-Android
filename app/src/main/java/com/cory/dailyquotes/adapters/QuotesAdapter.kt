package com.cory.dailyquotes.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.cory.dailyquotes.intents.QuotesActivity
import com.cory.dailyquotes.DB.QuotesDBHelper
import com.cory.dailyquotes.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class QuotesAdapter(
    val context: Context,
    private val dataList: ArrayList<HashMap<String, String>>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var quote = itemView.findViewById<TextView>(R.id.row_quote)!!

        @SuppressLint("Range")
        fun bind(position: Int) {

            val dataItem = dataList[position]
            quote.text = dataItem["quote"]

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.quotes_list_item, parent, false)
        )
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val dataItem = dataList[position]

        holder.itemView.findViewById<CardView>(R.id.cardViewQuotesItem).setOnLongClickListener {
            val dialog = BottomSheetDialog(context)
            val optionsView = LayoutInflater.from(context).inflate(R.layout.quotes_options_bottom_sheet_layout, null)
            dialog.setContentView(optionsView)

            val editConstraint = optionsView.findViewById<ConstraintLayout>(R.id.editPersonConstraint)
            val deleteConstraint = optionsView.findViewById<ConstraintLayout>(R.id.deletePeopleConstraintlayout)
            val deleteAllConstraint = optionsView.findViewById<ConstraintLayout>(R.id.deleteAllPeopleConstraintlayout)

            editConstraint.setOnClickListener {

            }
            deleteConstraint.setOnClickListener {
                val materialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
                materialAlertDialogBuilder.setTitle("Delete Quote?")
                materialAlertDialogBuilder.setMessage("Would you like to delete this quote?")
                materialAlertDialogBuilder.setPositiveButton("Yes") { _, _ ->
                    QuotesDBHelper(context, null).deleteRow(dataItem["id"].toString())
                    dataList.removeAt(holder.adapterPosition)
                    notifyItemRemoved(holder.adapterPosition)
                    dialog.dismiss()

                    val runnable = Runnable {
                        (context as QuotesActivity).textViewVisibility()
                    }
                    QuotesActivity().runOnUiThread(runnable)
                }
                materialAlertDialogBuilder.setNegativeButton("No", null)
                materialAlertDialogBuilder.show()
            }
            deleteAllConstraint.setOnClickListener {
                val materialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
                materialAlertDialogBuilder.setTitle("Delete all?")
                materialAlertDialogBuilder.setMessage("Would you like to delete all quotes for this person?")
                materialAlertDialogBuilder.setPositiveButton("Yes") { _, _ ->
                    QuotesDBHelper(context, null).deleteAllForASpecificPerson(dataItem["personID"].toString())

                    notifyItemRangeRemoved(0, dataList.count())
                    dataList.clear()
                    dialog.dismiss()

                    val runnable = Runnable {
                        (context as QuotesActivity).textViewVisibility()
                    }
                    QuotesActivity().runOnUiThread(runnable)
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