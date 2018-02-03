package kz.bako.facedetection.face

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.face.FaceDetector
import java.io.ByteArrayInputStream

fun getFace(context: Context, data: ByteArray): Bitmap? {
    try {
        val imageStrem = ByteArrayInputStream(data)
        var bitmap = BitmapFactory.decodeStream(imageStrem)
        if (bitmap.width > bitmap.height) {
            val matrix = Matrix()
            matrix.postRotate(270f)
            if (bitmap.width > 1500) matrix.postScale(0.5f, 0.5f)
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }
        val faceDetector = FaceDetector.Builder(context).setProminentFaceOnly(true).setTrackingEnabled(false).build()
        val frame = Frame.Builder().setBitmap(bitmap).build()
        val faces = faceDetector.detect(frame)
        var results: Bitmap? = null
        for (i in 0 until faces.size()) {
            val thisFace = faces.valueAt(i)
            val x = thisFace.position.x
            val y = thisFace.position.y
            val x2 = x / 4 + thisFace.width
            val y2 = y / 4 + thisFace.height
            results = Bitmap.createBitmap(bitmap, x.toInt(), y.toInt(), x2.toInt(), y2.toInt())
        }
        return results
    } catch (e: Exception) {
        Log.e("GET_FACE", e.message)
    }
    return null
}