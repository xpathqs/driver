package org.xpathqs.driver.i18n

import org.xpathqs.log.Log
import org.xpathqs.prop.PropParser
import org.xpathqs.prop.impl.YmlModelExtractor
import java.lang.IllegalArgumentException

object I18nHelper {
    fun init(obj: Any, path: String
        = "lang/messages-${System.getProperty("i18n", "en")}.yml") {
        try {
            val stream = this::class.java.classLoader.getResourceAsStream(path)
            PropParser(obj, YmlModelExtractor(stream)).parse()
        } catch (e: IllegalArgumentException) {
            Log.error(e.toString())
        }
    }
}