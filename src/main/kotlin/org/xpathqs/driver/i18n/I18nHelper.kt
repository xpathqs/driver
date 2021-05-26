package org.xpathqs.driver.i18n

import org.xpathqs.prop.PropParser

object I18nHelper {
    fun init(obj: Any, path: String
        = "lang/messages-${System.getProperty("i18n", "en")}.yml") {
        PropParser(obj, path).parse()
    }
}