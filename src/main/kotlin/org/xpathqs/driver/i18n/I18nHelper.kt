package org.xpathqs.driver.i18n

import org.xpathqs.driver.log.Log
import org.xpathqs.prop.PropParser
import java.lang.IllegalArgumentException

object I18nHelper {
    fun init(obj: Any, path: String
        = "lang/messages-${System.getProperty("i18n", "en")}.yml") {
        try {
            PropParser(obj, path).parse()
        } catch (e: IllegalArgumentException) {
            Log.error(e.toString())
        }
    }
}