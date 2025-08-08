package com.visioncameratextrecognition

import android.net.Uri
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.WritableNativeMap
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class PhotoRecognizerModule(reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {

    private val latinOptions = TextRecognizerOptions.DEFAULT_OPTIONS
    private val chineseOptions = ChineseTextRecognizerOptions.Builder().build()
    private val devanagariOptions = DevanagariTextRecognizerOptions.Builder().build()
    private val japaneseOptions = JapaneseTextRecognizerOptions.Builder().build()
    private val koreanOptions = KoreanTextRecognizerOptions.Builder().build()

    @ReactMethod
    fun process(uri: String, language: String, promise: Promise) {
        try {
            val parsedUri = Uri.parse(uri)
            val data = WritableNativeMap()
            val image = InputImage.fromFilePath(this.reactApplicationContext, parsedUri)
            
            // Select the appropriate recognizer based on language
            val recognizer = when (language) {
                "latin" -> TextRecognition.getClient(latinOptions)
                "chinese" -> TextRecognition.getClient(chineseOptions)
                "devanagari" -> TextRecognition.getClient(devanagariOptions)
                "japanese" -> TextRecognition.getClient(japaneseOptions)
                "korean" -> TextRecognition.getClient(koreanOptions)
                else -> TextRecognition.getClient(latinOptions)
            }
            
            val task: Task<Text> = recognizer.process(image)
            
            val text: Text = Tasks.await(task)
            if (text.text.isEmpty()) {
                promise.resolve(WritableNativeMap())
                return
            }
            
            data.putString("resultText", text.text)
            data.putArray("blocks", VisionCameraTextRecognitionPlugin.getBlocks(text.textBlocks))
            promise.resolve(data)
        } catch (e: Exception) {
            e.printStackTrace()
            promise.reject("TEXT_RECOGNITION_ERROR", "Error processing image: ${e.message}", e)
        }
    }
    override fun getName(): String {
        return NAME
    }
    companion object {
        const val NAME = "PhotoRecognizerModule"
    }
}