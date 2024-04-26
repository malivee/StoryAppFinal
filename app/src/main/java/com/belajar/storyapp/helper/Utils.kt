package com.belajar.storyapp.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val DATE_FORMAT = "ddMMyyyy_HHmmss"
val date = SimpleDateFormat(DATE_FORMAT, Locale.US).format(Date())
val MAX_SIZE = 1000000


fun String.toDateFormat() : String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    val outputDate = SimpleDateFormat("HH:mm:ss, dd/MMM/yyyy", Locale.US)
    val dateFormatted = inputFormat.parse(this) ?: return ""
    return outputDate.format(dateFormatted)


//    val dateFormatted = date.parse(this) as Date
//    return DateFormat.getDateInstance(DateFormat.FULL).format(dateFormatted)
}





fun temptFile(context: Context) : File {
    val directory = context.externalCacheDir
    return File.createTempFile(date, ".jpg",directory)
}

fun uriToFile(imageUri: Uri, context: Context): File {
    val myFile = temptFile(context)
    val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
    val outputStream = FileOutputStream(myFile)
    val buffer = ByteArray(1024)
    var length: Int
    while (inputStream.read(buffer).also { length = it } > 0) outputStream.write(buffer, 0, length)
    return myFile
}

fun File.reduceFileImage(): File {
    val file = this
    val bitmap = BitmapFactory.decodeFile(file.path)
    var compressQuality = 100
    var streamLength: Int
    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > MAX_SIZE)
    bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))

    return file
}

fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}


