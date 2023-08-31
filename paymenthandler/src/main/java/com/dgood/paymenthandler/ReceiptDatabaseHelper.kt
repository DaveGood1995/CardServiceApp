package com.dgood.paymenthandler

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.dgood.paymenthandler.model.response.Receipt
import com.dgood.paymenthandler.model.response.TransactionResponse
import com.google.gson.Gson

class ReceiptDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    fun insertReceipt(receipt: Receipt): Long {
        val values = ContentValues().apply {
            put(ReceiptEntry.COLUMN_FORMATTED_RECEIPT, receipt.formattedReceipt)
            put(ReceiptEntry.COLUMN_TRANSACTION_RESPONSE, Gson().toJson(receipt.transactionResponse))
        }

        return writableDatabase.insert(ReceiptEntry.TABLE_NAME, null, values)
    }

    fun getAllReceipts(): List<Receipt> {
        val receipts = mutableListOf<Receipt>()
        val cursor: Cursor = readableDatabase.query(
            ReceiptEntry.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null
        )

        with(cursor) {
            while (moveToNext()) {
                val formattedReceipt = getString(getColumnIndexOrThrow(ReceiptEntry.COLUMN_FORMATTED_RECEIPT))
                val transactionResponseJson = getString(getColumnIndexOrThrow(ReceiptEntry.COLUMN_TRANSACTION_RESPONSE))
                val transactionResponse = Gson().fromJson(transactionResponseJson, TransactionResponse::class.java)

                receipts.add(Receipt(formattedReceipt, transactionResponse))
            }
        }

        cursor.close()
        return receipts
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "TransactionReceipts.db"

        private const val SQL_CREATE_ENTRIES =
            "CREATE TABLE ${ReceiptEntry.TABLE_NAME} (" +
                    "${ReceiptEntry._ID} INTEGER PRIMARY KEY," +
                    "${ReceiptEntry.COLUMN_FORMATTED_RECEIPT} TEXT," +
                    "${ReceiptEntry.COLUMN_TRANSACTION_RESPONSE} TEXT)"

        private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${ReceiptEntry.TABLE_NAME}"
    }
}

object ReceiptEntry {
    const val TABLE_NAME = "receipts"
    const val _ID = "_id"
    const val COLUMN_FORMATTED_RECEIPT = "formatted_receipt"
    const val COLUMN_TRANSACTION_RESPONSE = "transaction_response"
}