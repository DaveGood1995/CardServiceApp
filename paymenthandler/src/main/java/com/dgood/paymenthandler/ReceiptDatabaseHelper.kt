package com.dgood.paymenthandler

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.dgood.paymenthandler.model.response.Receipt
import com.dgood.paymenthandler.model.response.TransactionResponse
import com.google.gson.Gson

// Define a class called ReceiptDatabaseHelper that extends SQLiteOpenHelper
class ReceiptDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // Called when the database is first created
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    // Called when the database needs to be upgraded
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    // Function to insert a receipt into the database
    fun insertReceipt(receipt: Receipt): Long {
        // Create ContentValues to hold the receipt data
        val values = ContentValues().apply {
            put(ReceiptEntry.COLUMN_FORMATTED_RECEIPT, receipt.formattedReceipt)
            put(
                ReceiptEntry.COLUMN_TRANSACTION_RESPONSE,
                Gson().toJson(receipt.transactionResponse)
            )
            put(ReceiptEntry.COLUMN_ORDER_ID, receipt.orderId)
            put(ReceiptEntry.COLUMN_TIMESTAMP, receipt.timestamp)
            put(ReceiptEntry.COLUMN_AMOUNT, receipt.amount)
            put(ReceiptEntry.COLUMN_CURRENCY, receipt.currency)
        }

        // Insert the values into the database and return the row ID
        return writableDatabase.insert(ReceiptEntry.TABLE_NAME, null, values)
    }

    // Function to retrieve a receipt by its ID from the database
    fun getReceiptById(receiptId: Long?): Receipt? {
        val selection = "${ReceiptEntry._ID} = ?"
        val selectionArgs = arrayOf(receiptId.toString())

        val cursor = readableDatabase.query(
            ReceiptEntry.TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        return if (cursor.moveToFirst()) {
            // Extract data from the cursor and create a Receipt object
            val formattedReceipt =
                cursor.getString(cursor.getColumnIndexOrThrow(ReceiptEntry.COLUMN_FORMATTED_RECEIPT))
            val transactionResponseJson =
                cursor.getString(cursor.getColumnIndexOrThrow(ReceiptEntry.COLUMN_TRANSACTION_RESPONSE))
            val transactionResponse =
                Gson().fromJson(transactionResponseJson, TransactionResponse::class.java)
            val orderId =
                cursor.getString(cursor.getColumnIndexOrThrow(ReceiptEntry.COLUMN_ORDER_ID))
            val timestamp =
                cursor.getString(cursor.getColumnIndexOrThrow(ReceiptEntry.COLUMN_TIMESTAMP))
            val amount = cursor.getDouble(cursor.getColumnIndexOrThrow(ReceiptEntry.COLUMN_AMOUNT))
            val currency =
                cursor.getString(cursor.getColumnIndexOrThrow(ReceiptEntry.COLUMN_CURRENCY))

            cursor.close()

            // Return the Receipt object
            Receipt(formattedReceipt, transactionResponse, orderId, timestamp, amount, currency)

        } else {
            null
        }
    }

    // Function to delete a receipt by its ID from the database
    fun deleteReceipt(receiptId: Long?): Boolean {

        val selection = "${ReceiptEntry._ID} = ?"
        val selectionArgs = arrayOf(receiptId.toString())

        val deletedRows = writableDatabase.delete(
            ReceiptEntry.TABLE_NAME,
            selection,
            selectionArgs
        )

        return deletedRows > 0
    }

    // Function to check if there are any receipts in the database
    fun hasReceipts(): Boolean {
        val cursor =
            readableDatabase.rawQuery("SELECT COUNT(*) FROM ${ReceiptEntry.TABLE_NAME}", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        return count > 0
    }

    // Function to retrieve all receipts from the database
    fun getAllReceipts(): List<Pair<Long, Receipt>> {
        val receiptPairs = mutableListOf<Pair<Long, Receipt>>()
        val cursor: Cursor = readableDatabase.query(
            ReceiptEntry.TABLE_NAME,
            arrayOf(
                ReceiptEntry._ID,
                ReceiptEntry.COLUMN_FORMATTED_RECEIPT,
                ReceiptEntry.COLUMN_TRANSACTION_RESPONSE,
                ReceiptEntry.COLUMN_ORDER_ID,
                ReceiptEntry.COLUMN_TIMESTAMP,
                ReceiptEntry.COLUMN_AMOUNT,
                ReceiptEntry.COLUMN_CURRENCY
            ),
            null,
            null,
            null,
            null,
            null
        )

        with(cursor) {
            while (moveToNext()) {
                // Extract data from the cursor and create Receipt objects
                val rowId = getLong(getColumnIndexOrThrow(ReceiptEntry._ID))
                val formattedReceipt =
                    getString(getColumnIndexOrThrow(ReceiptEntry.COLUMN_FORMATTED_RECEIPT))
                val transactionResponseJson =
                    getString(getColumnIndexOrThrow(ReceiptEntry.COLUMN_TRANSACTION_RESPONSE))
                val transactionResponse =
                    Gson().fromJson(transactionResponseJson, TransactionResponse::class.java)
                val orderId = getString(getColumnIndexOrThrow(ReceiptEntry.COLUMN_ORDER_ID))
                val timestamp = getString(getColumnIndexOrThrow(ReceiptEntry.COLUMN_TIMESTAMP))
                val amount = getDouble(getColumnIndexOrThrow(ReceiptEntry.COLUMN_AMOUNT))
                val currency = getString(getColumnIndexOrThrow(ReceiptEntry.COLUMN_CURRENCY))

                val receipt = Receipt(
                    formattedReceipt,
                    transactionResponse,
                    orderId,
                    timestamp,
                    amount,
                    currency
                )
                receiptPairs.add(Pair(rowId, receipt))
            }
        }

        cursor.close()
        return receiptPairs
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "TransactionReceipts.db"

        private const val SQL_CREATE_ENTRIES =
            "CREATE TABLE ${ReceiptEntry.TABLE_NAME} (" +
                    "${ReceiptEntry._ID} INTEGER PRIMARY KEY," +
                    "${ReceiptEntry.COLUMN_FORMATTED_RECEIPT} TEXT," +
                    "${ReceiptEntry.COLUMN_TRANSACTION_RESPONSE} TEXT," +
                    "${ReceiptEntry.COLUMN_ORDER_ID} TEXT," +
                    "${ReceiptEntry.COLUMN_TIMESTAMP} TEXT," +
                    "${ReceiptEntry.COLUMN_AMOUNT} TEXT," +
                    "${ReceiptEntry.COLUMN_CURRENCY} TEXT)"

        private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${ReceiptEntry.TABLE_NAME}"
    }
}

// Define an object with constants for the receipt table's columns
object ReceiptEntry {
    const val TABLE_NAME = "receipts"
    const val _ID = "_id"
    const val COLUMN_FORMATTED_RECEIPT = "formatted_receipt"
    const val COLUMN_TRANSACTION_RESPONSE = "transaction_response"
    const val COLUMN_ORDER_ID = "order_id"
    const val COLUMN_TIMESTAMP = "timestamp"
    const val COLUMN_AMOUNT = "amount"
    const val COLUMN_CURRENCY = "currency"
}