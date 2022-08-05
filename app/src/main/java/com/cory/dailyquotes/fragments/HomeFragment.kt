package com.cory.dailyquotes.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cory.dailyquotes.DB.PeopleDBHelper
import com.cory.dailyquotes.DB.QuotesDBHelper
import com.cory.dailyquotes.R
import com.cory.dailyquotes.adapters.PeopleAdapter
import com.cory.dailyquotes.classes.ManagePermissions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import java.io.ByteArrayOutputStream
import java.lang.NullPointerException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class HomeFragment : Fragment() {

    private lateinit var managePermissions: ManagePermissions

    private lateinit var recyclerViewState : Parcelable

    private lateinit var image : Bitmap

    var peopleArray = ArrayList<String>()
    var peopleIDArray = ArrayList<String>()
    var peopleImageArray = ArrayList<ByteArray>()

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

        val floatingActionButtonPeople = view.findViewById<FloatingActionButton>(R.id.fabAddPeopleOrQuote)
        floatingActionButtonPeople.setOnClickListener {
            val dialog = BottomSheetDialog(requireContext())
            val addView = layoutInflater.inflate(R.layout.add_people_bottom_sheet, null)
            dialog.setContentView(addView)

            val addPersonConstraintLayout = addView.findViewById<ConstraintLayout>(R.id.addAPersonConstraint)
            val addAQuoteConstraintLayout = addView.findViewById<ConstraintLayout>(R.id.addAQuoteConstraintlayout)

            addPersonConstraintLayout.setOnClickListener {
                val addAPersonDialog = BottomSheetDialog(requireContext())
                val addPersonView = layoutInflater.inflate(R.layout.add_a_person_bottom_sheet_layout, null)
                addAPersonDialog.setContentView(addPersonView)
                addAPersonDialog.setCancelable(false)

                val nameTextInputEditText = addPersonView.findViewById<TextInputEditText>(R.id.name)
                val bioTextInputEditText = addPersonView.findViewById<TextInputEditText>(R.id.bio)

                val addImageButton = addPersonView.findViewById<Button>(R.id.addImage)

                addImageButton.setOnClickListener {
                    val list = listOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)

                    managePermissions =
                        ManagePermissions(
                            requireActivity(),
                            list,
                            123
                        )

                    if (managePermissions.checkPermissions(requireContext())) {
                        val pickerIntent = Intent(Intent.ACTION_PICK)
                        pickerIntent.type = "image/*"

                        showImagePicker.launch(pickerIntent)
                    }
                    else {
                        managePermissions.showAlert(requireContext())
                    }
                }

                val addPersonButton = addPersonView.findViewById<Button>(R.id.addPerson)
                val cancelButton = addPersonView.findViewById<Button>(R.id.cancelPersonButton)

                addPersonButton.setOnClickListener {

                    if (nameTextInputEditText.text?.toString() == "") {
                        Toast.makeText(requireContext(), "Name is required", Toast.LENGTH_SHORT).show()
                    }
                    else if (PeopleDBHelper(requireContext(), null).checkIfExists(nameTextInputEditText.text.toString().trim())) {
                        Toast.makeText(requireContext(), "That name already exists", Toast.LENGTH_SHORT).show()
                    }
                        else {
                            recyclerViewState = view.findViewById<RecyclerView>(R.id.peopleRecyclerView).layoutManager!!.onSaveInstanceState()!!
                        try {
                            val stream = ByteArrayOutputStream()
                            val resized = Bitmap.createScaledBitmap(image, 200, 200, false)
                            resized.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                            val image2 = stream.toByteArray()
                            PeopleDBHelper(requireContext(), null).insertRow(
                                nameTextInputEditText.text.toString().trim(),
                                bioTextInputEditText.text.toString().trim(),
                                image2,
                                "entered"
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                            PeopleDBHelper(requireContext(), null).insertRow(
                                nameTextInputEditText.text.toString(),
                                bioTextInputEditText.text.toString(),
                                ByteArray(0),
                                "entered"
                            )
                        }
                        addAPersonDialog.dismiss()
                        loadIntoList()
                        view.findViewById<RecyclerView>(R.id.peopleRecyclerView).layoutManager!!.onRestoreInstanceState(recyclerViewState)
                    }
                }
                cancelButton.setOnClickListener {
                    addAPersonDialog.dismiss()
                }

                addAPersonDialog.show()
                dialog.dismiss()
            }

            if (PeopleDBHelper(requireContext(), null).getCount() == 0) {
                addAQuoteConstraintLayout.isEnabled = false
                addAQuoteConstraintLayout.setBackgroundColor(ContextCompat.getColor(requireContext(),
                    R.color.disabledConstrainLayoutBackground
                ))
            }
            addAQuoteConstraintLayout.setOnClickListener {

                peopleIDArray.clear()
                peopleArray.clear()
                peopleImageArray.clear()

                val dbHandler = PeopleDBHelper(requireActivity().applicationContext, null)

                val cursor = dbHandler.getAllRow()
                cursor!!.moveToFirst()

                if (dbHandler.getCount() > 0) {
                    while (!cursor.isAfterLast) {
                        peopleIDArray.add(cursor.getString(cursor.getColumnIndex(PeopleDBHelper.COLUMN_ID)))
                        peopleArray.add(cursor.getString(cursor.getColumnIndex(PeopleDBHelper.COLUMN_NAME)))
                        peopleImageArray.add(cursor.getBlob(cursor.getColumnIndex(PeopleDBHelper.COLUMN_NAME)))

                        cursor.moveToNext()

                    }
                }

                var position = 0

                val addAQuoteDialog = BottomSheetDialog(requireContext())
                val addQuoteView = layoutInflater.inflate(R.layout.add_quotes_home_bottom_sheet_layout, null)
                addAQuoteDialog.setContentView(addQuoteView)
                addAQuoteDialog.setCancelable(false)

                val addQuoteButton = addQuoteView.findViewById<Button>(R.id.addQuoteButton)
                val quoteInputEditText = addQuoteView.findViewById<TextInputEditText>(R.id.quote)
                val cancelButton = addQuoteView.findViewById<Button>(R.id.cancelButton)

                val peopleMenu =
                    addQuoteView.findViewById<MaterialAutoCompleteTextView>(R.id.peopleAutoComplete)

                val adapter =
                    ArrayAdapter(requireContext(), R.layout.list_item, peopleArray)
                peopleMenu.setAdapter(adapter)
                peopleMenu.setText(peopleArray.elementAt(0), false)

                peopleMenu.onItemClickListener = AdapterView.OnItemClickListener { p0, p1, p2, p3 -> position = p2 }

                addQuoteButton.setOnClickListener {
                    if (quoteInputEditText.text.toString().trim() == "" || quoteInputEditText.text == null) {
                        Toast.makeText(requireContext(), "A quote is required", Toast.LENGTH_SHORT).show()
                    }
                    if (QuotesDBHelper(requireContext(), null).checkIfExistsForASpecificPerson(quoteInputEditText.text.toString().trim().replace("'", "''"), peopleIDArray.elementAt(position))) {
                        Toast.makeText(requireContext(), "Quote already exists", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        QuotesDBHelper(requireContext(), null).insertRow(peopleIDArray.elementAt(position), quoteInputEditText.text.toString().trim(), Date().toString())
                        addAQuoteDialog.dismiss()
                    }
                }

                cancelButton.setOnClickListener {
                    addAQuoteDialog.dismiss()
                }

                addAQuoteDialog.show()
                dialog.dismiss()
            }
            dialog.show()
        }

        gridLayoutManager = GridLayoutManager(requireContext(), 1)
        peopleAdapter = PeopleAdapter(requireContext(), dataList, imageDataList)

        loadIntoList()
    }

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
           // if (cursor.getString(cursor.getColumnIndex(PeopleDBHelper.COLUMN_LOCATION)) != "internet") {
                map["id"] = cursor.getString(cursor.getColumnIndex(PeopleDBHelper.COLUMN_ID))

                map["personName"] =
                    cursor.getString(cursor.getColumnIndex(PeopleDBHelper.COLUMN_NAME))
                map["location"] = cursor.getString(cursor.getColumnIndex(PeopleDBHelper.COLUMN_LOCATION))
                try {
                    imageMap["personImage"] =
                        cursor.getBlob(cursor.getColumnIndex(PeopleDBHelper.COLUMN_IMAGE))
                } catch (e: Exception) {
                    e.printStackTrace()
                    imageMap["personImage"] = ByteArray(0)
                }
                dataList.add(map)
                imageDataList.add(imageMap)
            /*}
            else {
                map["id"] = cursor.getString(cursor.getColumnIndex(PeopleDBHelper.COLUMN_ID))

                map["personName"] =
                    cursor.getString(cursor.getColumnIndex(PeopleDBHelper.COLUMN_NAME))

                map["personImage"] =
                        cursor.getString(cursor.getColumnIndex(PeopleDBHelper.COLUMN_FETCHED_IMAGE))

                map["location"] = cursor.getString(cursor.getColumnIndex(PeopleDBHelper.COLUMN_LOCATION))
                imageMap["personImage"] = ByteArray(0)
                imageDataList.add(imageMap)
                dataList.add(map)
            }*/
            cursor.moveToNext()
        }
        val recyclerView = activity?.findViewById<RecyclerView>(R.id.peopleRecyclerView)
        recyclerView?.layoutManager = gridLayoutManager
        recyclerView?.adapter = peopleAdapter
    }

    fun textViewVisibility() {
        val dbHandler = PeopleDBHelper(requireContext(), null)

        if (dbHandler.getCount() > 0) {
            val noClassesStoredTextView = activity?.findViewById<TextView>(R.id.noPeopleStoredTextView)
            noClassesStoredTextView?.visibility = View.GONE
        } else {
            val noClassesStoredTextView = activity?.findViewById<TextView>(R.id.noPeopleStoredTextView)
            noClassesStoredTextView?.visibility = View.VISIBLE

        }
    }

    val showImagePicker = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            // doSomeOperations();
            val data = result.data
            val source = ImageDecoder.createSource(requireContext().contentResolver, result.data!!.data!!)
            val bitmap = ImageDecoder.decodeBitmap(source)
            image = bitmap
        }
    }
}