package com.cory.dailyquotes

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toDrawable
import com.google.android.material.textfield.TextInputEditText
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.*

class AddPersonActivity : AppCompatActivity() {

    private lateinit var image : Bitmap

    private lateinit var managePermissions: ManagePermissions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_person)

        val nameTextInputEditText = findViewById<TextInputEditText>(R.id.name)
        val bioTextInputEditText = findViewById<TextInputEditText>(R.id.bio)

        val addImageButton = findViewById<Button>(R.id.addImage)

        addImageButton.setOnClickListener {
            val list = listOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)

            managePermissions =
                ManagePermissions(
                    this,
                    list,
                    123
                )

            if (managePermissions.checkPermissions(this)) {
                val pickerIntent = Intent(Intent.ACTION_PICK)
                pickerIntent.type = "image/*"

                showImagePicker.launch(pickerIntent)
            }
            else {
                managePermissions.showAlert(this)
            }
        }

        val addPersonButton = findViewById<Button>(R.id.addPerson)

        addPersonButton.setOnClickListener {

            if (nameTextInputEditText.text?.toString() == "") {
                Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show()
            } else {
                try {
                    val stream = ByteArrayOutputStream()
                    image.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    val image2 = stream.toByteArray()
                    PeopleDBHelper(this, null).insertRow(
                        nameTextInputEditText.text.toString(),
                        bioTextInputEditText.text.toString(),
                        image2
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    PeopleDBHelper(this, null).insertRow(
                        nameTextInputEditText.text.toString(),
                        bioTextInputEditText.text.toString(),
                        ByteArray(0)
                    )
                }

                finishAfterTransition()
            }
        }
    }

    val showImagePicker = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            // doSomeOperations();
            val data = result.data
            val source = ImageDecoder.createSource(this.contentResolver, result.data!!.data!!)
            val bitmap = ImageDecoder.decodeBitmap(source)
            image = bitmap
        }
    }

    fun getRealPathFromURI(contentURI: Uri) : String {
        var result = ""
        val cursor = this.contentResolver?.query(contentURI, null, null, null, null)
        if (cursor == null) {
            result = contentURI.path.toString()
        }
        else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }
}