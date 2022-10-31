package com.sophiadiagrams.avedex.presentation.camera

import android.content.Context
import android.graphics.Bitmap
import com.sophiadiagrams.avedex.ml.BirdsClassifier
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.label.Category

class BirdClassifier(val context: Context) {

    fun classify(bitmap: Bitmap): Category? {
        val model = BirdsClassifier.newInstance(context)
        // Creates inputs for reference.
        val image = TensorImage.fromBitmap(bitmap)

        // Runs model inference and gets result.
        val outputs = model.process(image)
        val result = outputs.probabilityAsCategoryList.maxBy { it.score }
        model.close()
        return if (result.score >= 0.9) result
        else null
    }
}