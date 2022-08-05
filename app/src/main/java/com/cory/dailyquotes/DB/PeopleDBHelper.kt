package com.cory.dailyquotes.DB

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class PeopleDBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE " + TABLE_NAME + " ("
                    + COLUMN_ID + " INTEGER PRIMARY KEY, "
                    + COLUMN_NAME + " TEXT, "
                    + COLUMN_BIO + " TEXT, "
                    + COLUMN_LOCATION + " TEXT, "
                    + COLUMN_FETCHED_IMAGE + " TEXT, "
                    + COLUMN_IMAGE + " BLOB );")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        try {
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_NAME TEXT DEFAULT \"\" NOT NULL")
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        try {
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_BIO TEXT DEFAULT \"\" NOT NULL")
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        try {
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_IMAGE BLOB")
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        try {
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_LOCATION TEXT DEFAULT \"\" NOT NULL")
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        try {
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_FETCHED_IMAGE TEXT DEFAULT \"\" NOT NULL")
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertRow(
        personName: String,
        personBio: String,
        personImage: ByteArray,
        location: String
    ) {
        val values = ContentValues()
        values.put(COLUMN_NAME, personName)
        values.put(COLUMN_BIO, personBio)
        values.put(COLUMN_IMAGE, personImage)
        values.put(COLUMN_LOCATION, location)

        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun insertFetchedRow(
        personName: String,
        personBio: String,
        personImage: String
    ) {
        val values = ContentValues()
        values.put(COLUMN_NAME, personName)
        values.put(COLUMN_BIO, personBio)
        values.put(COLUMN_FETCHED_IMAGE, personImage)
        values.put(COLUMN_LOCATION, "internet")

        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun checkIfExists(personName: String): Boolean {
        val db = this.writableDatabase
        var cursor: Cursor? = null
        val checkQuery =
            "SELECT " + COLUMN_NAME + " FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME + "= '" + personName + "'"
        cursor = db.rawQuery(checkQuery, null)
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun getPersonID(personName: String): String {
        val db = this.writableDatabase
        var cursor: Cursor? = null
        val checkQuery =
            "SELECT " + COLUMN_ID + " FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME + "= '" + personName + "'"
        cursor = db.rawQuery(checkQuery, null)
        //cursor.close()
        cursor!!.moveToFirst()
        return cursor.getString(cursor.getColumnIndex(COLUMN_ID))
    }

    /*fun insertRestoreRow(
        classID: String,
        className: String,
        classTime: String
    ) {
        val values = ContentValues()
        values.put(COLUMN_CLASS_NAME, className)
        values.put(COLUMN_CLASS_TIME, classTime)
        values.put(COLUMN_CLASS_ID, classID)

        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }*/

    /*fun update(
        id: String,
        className: String,
        classTime: String
    ) {
        val values = ContentValues()
        values.put(COLUMN_CLASS_NAME, className)
        values.put(COLUMN_CLASS_TIME, classTime)

        val db = this.writableDatabase

        db.update(TABLE_NAME, values, "$COLUMN_ID=?", arrayOf(id))

    }*/

    /*fun classIDUpdate(
        id: String,
        primaryKey: String
    ) {
        val values = ContentValues()
        values.put(COLUMN_CLASS_ID, id)

        val db = this.writableDatabase

        db.update(TABLE_NAME, values, "$COLUMN_ID=?", arrayOf(primaryKey))

    }*/

    fun getCount(): Int {
        val db = this.readableDatabase
        return DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM $TABLE_NAME", null).toInt()

    }

    fun deleteRow(row_id: String) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(row_id))
        db.close()

    }

    fun getRow(row_id: String): Cursor {

        val db = this.writableDatabase

        return db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID=$row_id", null)

    }

    fun getAllRow(): Cursor? {
        val db = this.writableDatabase

       // return db.rawQuery("SELECT $COLUMN_ID, $COLUMN_CLASS_NAME, $COLUMN_CLASS_TIME FROM $TABLE_NAME ORDER BY $COLUMN_CLASS_NAME asc", null)

        return db.query(
            TABLE_NAME,
            arrayOf(
                COLUMN_ID,
                COLUMN_NAME,
                COLUMN_BIO,
                COLUMN_IMAGE,
                COLUMN_LOCATION,
                COLUMN_FETCHED_IMAGE
            ),
            null,
            null,
            null,
            null,
            COLUMN_NAME
        )
    }

    fun deleteAll() {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, null, null)
        db.execSQL("delete from $TABLE_NAME")
        db.close()
    }

    companion object {
        const val DATABASE_VERSION = 3
        const val DATABASE_NAME = "people.db"
        const val TABLE_NAME = "people"

        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "personName"
        const val COLUMN_BIO = "personBio"
        const val COLUMN_IMAGE = "personImage"
        const val COLUMN_FETCHED_IMAGE = "personFetchedImage"
        const val COLUMN_LOCATION = "personLocation"
    }
}
