package com.visioncameratextrecognition

import android.util.Log
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateRemoteModel

private const val TAG = "RemoveLanguageModel"

class RemoveLanguageModel(reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {

    private val modelManager = RemoteModelManager.getInstance()

    @ReactMethod
    fun remove(code: String, promise: Promise) {
        try {
            val modelName = translateLanguage(code)?.let { TranslateRemoteModel.Builder(it).build() }
            if (modelName != null) {
                modelManager.deleteDownloadedModel(modelName)
                    .addOnSuccessListener {
                        Log.d(TAG, "Successfully removed language model for: $code")
                        promise.resolve(true)
                    }
                    .addOnFailureListener { exception ->
                        Log.e(TAG, "Failed to remove language model for: $code", exception)
                        promise.reject("MODEL_REMOVAL_ERROR", "Failed to remove language model: ${exception.message}", exception)
                    }
            } else {
                Log.w(TAG, "Invalid language code provided: $code")
                promise.reject("INVALID_LANGUAGE_CODE", "Invalid language code: $code")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in remove method", e)
            promise.reject("REMOVE_ERROR", "Error removing language model: ${e.message}", e)
        }
    }

    override fun getName(): String {
        return NAME
    }

    companion object {
        const val NAME = "RemoveLanguageModel"
    }
}
