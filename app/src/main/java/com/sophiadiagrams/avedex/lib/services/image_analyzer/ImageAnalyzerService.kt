package com.sophiadiagrams.avedex.lib.services.image_analyzer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import com.sophiadiagrams.avedex.lib.services.retrofit.BirdsResponse
import com.sophiadiagrams.avedex.lib.services.retrofit.RetrofitService
import com.sophiadiagrams.avedex.ml.BirdsClassifier
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.ObjectDetector

class ImageAnalyzerService(val context: Context) {
    var utils = Utils
    private var retrofit = RetrofitService()
    private val options = ObjectDetector.ObjectDetectorOptions.builder()
        .setMaxResults(5)
        .setScoreThreshold(0.3f)
        .build()

    private val detector = ObjectDetector.createFromFileAndOptions(
        this.context,
        "object-detector.tflite",
        options
    )

    fun detect(bitmap: Bitmap): Bitmap? {
        val image = TensorImage.fromBitmap(bitmap)
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

    suspend fun classify(bitmap: Bitmap): BirdsResponse? {
        return retrofit.postBirdsData("ABBOTTS BABBLER")
//        return if (true) {
//            retrofit.postBirdsData("ABBOTTS BABBLER")
//        } else {
//            val model = BirdsClassifier.newInstance(context)
//            val image = TensorImage.fromBitmap(bitmap)
//            val outputs = model.process(image)
//            val result = outputs.probabilityAsCategoryList.maxBy { it.score }
//            model.close()
//            if (result.label != "None" && result.score >= 0.5) BirdsResponse(
//                result.displayName,
//                null,
//                null,
//                null
//            )
//            else null
//        }
    }
}


