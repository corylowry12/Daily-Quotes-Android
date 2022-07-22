package com.cory.dailyquotes

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class PeopleAdapter(
    val context: Context,
    private val dataList: ArrayList<HashMap<String, String>>,
    private val imageDataList: ArrayList<HashMap<String, ByteArray>>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var name = itemView.findViewById<TextView>(R.id.row_person_name)!!
        var personImage = itemView.findViewById<ImageView>(R.id.personImage)

        @SuppressLint("Range")
        fun bind(position: Int) {

            val dataItem = dataList[position]
            val imageDataItem = imageDataList[position]
            name.text = dataItem["personName"]

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
            }
            else {
                Glide.with(context)
                    .load(ContextCompat.getDrawable(context, R.drawable.ic_outline_person_24))
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(personImage)
            }
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

        holder.itemView.findViewById<ConstraintLayout>(R.id.personItemConstraintLayout).setOnClickListener {
            val quotesIntent = Intent(context, QuotesActivity::class.java)
            quotesIntent.putExtra("id", dataItem["id"])

            val pair1 = Pair.create<View, String>(holder.itemView.findViewById<ImageView>(R.id.personImage), "people_image")
            val pair2 = Pair.create<View, String>(holder.itemView.findViewById<CardView>(R.id.personImageCardView), "people_card_view")
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation((context as AppCompatActivity), pair1, pair2)
            context.startActivity(quotesIntent, options.toBundle())
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