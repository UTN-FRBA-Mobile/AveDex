package com.sophiadiagrams.avedex.presentation.camera

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import io.fotoapparat.result.BitmapPhoto
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.Detection
import org.tensorflow.lite.task.vision.detector.ObjectDetector


class BirdDetector(val context: Context) {

    fun detect(bitmapPhoto: BitmapPhoto?): Bitmap {
        val image = TensorImage.fromBitmap(bitmapPhoto!!.bitmap)

        val options = ObjectDetector.ObjectDetectorOptions.builder()
            .setMaxResults(5)
            .setScoreThreshold(0.5f)
            .build()

        val detector = ObjectDetector.createFromFileAndOptions(
            this.context, // the application context
            "object-detector.tflite", // must be same as the filename in assets folder
            options
        )

        val results = detector.detect(image)

        debugPrint(results)

        val processedBitmaps = mutableListOf<Bitmap>()

        for (detectedObject in results) {
            if (detectedObject.categories.first().label == "bird") {
                val boundingBox = Rect()
                detectedObject.boundingBox.round(boundingBox)
                val bitmap = Bitmap.createBitmap(
                    bitmapPhoto.bitmap,
                    boundingBox.left,
                    boundingBox.top,
                    boundingBox.width(),
                    boundingBox.height()
                )
//                val processedImage =
//                    InputImage.fromBitmap(bitmap, bitmapPhoto.rotationDegrees).mediaImage
//                processedImages.add(processedImage!!)
                processedBitmaps.add(bitmap)
            }
        }

        return processedBitmaps.first()
    }

    private fun debugPrint(results: List<Detection>) {
        for ((i, obj) in results.withIndex()) {
            val box = obj.boundingBox

            Log.d(TAG, "Detected object: ${i} ")
            Log.d(TAG, "  boundingBox: (${box.left}, ${box.top}) - (${box.right},${box.bottom})")

            for ((j, category) in obj.categories.withIndex()) {
                Log.d(TAG, "    Label $j: ${category.label}")
                val confidence: Int = category.score.times(100).toInt()
                Log.d(TAG, "    Confidence: ${confidence}%")
            }
        }
    }
}