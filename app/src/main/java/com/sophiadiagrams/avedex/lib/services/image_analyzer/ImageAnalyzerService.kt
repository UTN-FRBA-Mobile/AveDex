package com.sophiadiagrams.avedex.lib.services.image_analyzer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import com.sophiadiagrams.avedex.ml.BirdsClassifier
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.label.Category
import org.tensorflow.lite.task.vision.detector.ObjectDetector

class ImageAnalyzerService(val context: Context) {
    var utils = Utils

    fun detect(bitmap: Bitmap): Bitmap? {
        val image = TensorImage.fromBitmap(bitmap)

        val options = ObjectDetector.ObjectDetectorOptions.builder()
            .setMaxResults(5)
            .setScoreThreshold(0.3f)
            .build()

        val detector = ObjectDetector.createFromFileAndOptions(
            this.context,
            "object-detector.tflite",
            options
        )

        val results = detector.detect(image)
        for (detectedObject in results) {
            if (detectedObject.categories.map { it.label }.any { it == "bird" }) {
                val boundingBox = Rect()
                detectedObject.boundingBox.round(boundingBox)
                val padding = 60
                return Bitmap.createBitmap(
                    bitmap,
                    boundingBox.left - padding,
                    boundingBox.top - padding,
                    boundingBox.width() + 2 * padding, // Es dos veces el padding porque tiene que compensar de ambos lados
                    boundingBox.height() + 2 * padding
                )
            }
        }
        return null
    }

    // TODO: usar nuestro modelo
    fun classify(bitmap: Bitmap): Category? {
        val model = BirdsClassifier.newInstance(context)
        val image = TensorImage.fromBitmap(bitmap)
        val outputs = model.process(image)
        val result = outputs.probabilityAsCategoryList.maxBy { it.score }
        model.close()
        return if (result.label != "None" && result.score >= 0.5) result
        else null
    }
}


