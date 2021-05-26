package org.xpathqs.driver.constants

import org.xpathqs.driver.i18n.I18nHelper

object Messages {
    fun init() {
        I18nHelper.init(this)
    }

    object Executor {
        val beforeAction = ""
    }

    object Actions {
        object Input {
            val name = ""
            val toString = ""
        }

        object Click {
            val name = ""
            val toString = ""
        }

        object WaitForSelector {
            val name = ""
            val toString = ""
        }
    }
}