package com.sophiadiagrams.avedex.lib.services.image_analyzer

import android.graphics.Bitmap
import android.graphics.Matrix

object Utils {
    fun rotateBitmap(original: Bitmap, degrees: Int): Bitmap {
        val matrix = Matrix()
        matrix.preRotate((360 - degrees).toFloat())
        return Bitmap.createBitmap(original, 0, 0, original.width, original.height, matrix, true)
    }
}