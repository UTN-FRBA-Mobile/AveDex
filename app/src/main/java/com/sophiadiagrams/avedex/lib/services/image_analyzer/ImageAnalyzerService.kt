package com.sophiadiagrams.avedex.lib.services.image_analyzer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
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
                return Bitmap.createBitmap(
                    bitmap,
                    boundingBox.left - 60,
                    boundingBox.top - 60,
                    boundingBox.width() + 120,
                    boundingBox.height() + 120
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
        return if (result.label != "None" && result.score >= 0.85) result
        else null
    }
}


