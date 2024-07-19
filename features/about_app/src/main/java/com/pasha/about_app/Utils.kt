package com.pasha.about_app

import androidx.fragment.app.Fragment
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div2.DivData
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset


fun JSONObject.asDiv2DataWithTemplates(): DivData {
    val templates = getJSONObject("templates")
    val card = getJSONObject("card")
    val environment = DivParsingEnvironment(ParsingErrorLogger.LOG)
    environment.parseTemplates(templates)

    return DivData(environment, card)
}

fun Fragment.readJSONFromAssets(fileName: String): JSONObject? {
    var jsonContent: String? = null
    var jsonObject: JSONObject? = null

    try {
        requireActivity().assets.open(fileName).use { stream ->
            jsonContent = String(stream.readBytes(), Charset.defaultCharset())
        }

        jsonObject = jsonContent?.let { JSONObject(it) }
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return jsonObject
}

object Constants {
    const val FileName = "about_app_screen.json"
}