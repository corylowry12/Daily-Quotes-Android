package com.cory.dailyquotes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton


class HomeFragment : Fragment() {

   private lateinit var recyclerViewState : Parcelable

    private lateinit var peopleAdapter: PeopleAdapter
    private val dataList = ArrayList<HashMap<String, String>>()
    private val imageDataList = ArrayList<HashMap<String, ByteArray>>()

    private lateinit var gridLayoutManager: GridLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val floatingActionButtonPeople = view.findViewById<FloatingActionButton>(R.id.fabPeople)
        floatingActionButtonPeople.setOnClickListener {
            val dialog = BottomSheetDialog(requireContext())
            val addGradeView = layoutInflater.inflate(R.layout.add_people_bottom_sheet, null)

            dialog.setContentView(addGradeView)
            dialog.show()
        }

        gridLayoutManager = GridLayoutManager(requireContext(), 1)
        peopleAdapter = PeopleAdapter(requireContext(), dataList, imageDataList)

        loadIntoList()

        val recyclerView = activity?.findViewById<RecyclerView>(R.id.peopleRecyclerView)
        recyclerView?.setOnScrollChangeListener { view, i, i2, i3, i4 ->
            recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()!!
        }

        val peopleToolbar = view.findViewById<MaterialToolbar>(R.id.materialToolBarPeople)
        peopleToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.addPeople -> {
                   // PeopleDBHelper(requireContext(), null).insertRow("Hi my guy 3", "Avid Programmer", "")
                   // loadIntoList()

                    val intent = Intent(requireContext(), AddPersonActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

   /* override fun onResume() {
        super.onResume()
       loadIntoList()
        val recyclerView = activity?.findViewById<RecyclerView>(R.id.peopleRecyclerView)
        try {
            recyclerView?.layoutManager?.onRestoreInstanceState(recyclerViewState)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }*/

    fun loadIntoList() {

        val dbHandler = PeopleDBHelper(requireContext(), null)

        if (dbHandler.getCount() > 0) {
            val noClassesStoredTextView = activity?.findViewById<TextView>(R.id.noPeopleStoredTextView)
            noClassesStoredTextView?.visibility = View.GONE
        } else {
            val noClassesStoredTextView = activity?.findViewById<TextView>(R.id.noPeopleStoredTextView)
            noClassesStoredTextView?.visibility = View.VISIBLE

        }

        dataList.clear()
        imageDataList.clear()

        val cursor = dbHandler.getAllRow()
        cursor?.moveToFirst()

        while (!cursor!!.isAfterLast) {
            val map = HashMap<String, String>()
            val imageMap = HashMap<String, ByteArray>()
            map["id"] = cursor.getString(cursor.getColumnIndex(PeopleDBHelper.COLUMN_ID))

            map["personName"] =
                cursor.getString(cursor.getColumnIndex(PeopleDBHelper.COLUMN_NAME))
            try {
                imageMap["personImage"] =
                    cursor.getBlob(cursor.getColumnIndex(PeopleDBHelper.COLUMN_IMAGE))
            } catch (e : Exception) {
                e.printStackTrace()
                imageMap["personImage"] = ByteArray(0)
            }
            dataList.add(map)
            imageDataList.add(imageMap)

            cursor.moveToNext()
        }
        val recyclerView = activity?.findViewById<RecyclerView>(R.id.peopleRecyclerView)
        try {
            gridLayoutManager = GridLayoutManager(requireContext(), 1)
            recyclerView?.layoutManager = gridLayoutManager
        } catch (e: Exception) {
            e.printStackTrace()
        }
        recyclerView?.adapter = peopleAdapter
    }

    fun reloadIntoList(context: Context) {
        val dbHandler = PeopleDBHelper(context, null)

        dataList.clear()

        val cursor = dbHandler.getAllRow()
        cursor?.moveToFirst()

        while (!cursor!!.isAfterLast) {
            val map = HashMap<String, String>()
            val imageMap = HashMap<String, ByteArray>()
            map["id"] = cursor.getString(cursor.getColumnIndex(PeopleDBHelper.COLUMN_ID))

            map["personName"] =
                cursor.getString(cursor.getColumnIndex(PeopleDBHelper.COLUMN_NAME))
            try {
                imageMap["personImage"] =
                    cursor.getBlob(cursor.getColumnIndex(PeopleDBHelper.COLUMN_IMAGE))
            } catch (e : Exception) {
                e.printStackTrace()
                imageMap["personImage"] = ByteArray(0)
            }
            dataList.add(map)
            imageDataList.add(imageMap)

            cursor.moveToNext()
        }
        val recyclerView = activity?.findViewById<RecyclerView>(R.id.peopleRecyclerView)
        try {
            gridLayoutManager = GridLayoutManager(requireContext(), 1)
            recyclerView?.layoutManager = gridLayoutManager
        } catch (e: Exception) {
            e.printStackTrace()
        }
        recyclerView?.adapter = peopleAdapter
    }
}