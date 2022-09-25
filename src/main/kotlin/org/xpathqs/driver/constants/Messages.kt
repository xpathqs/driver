package org.xpathqs.driver.constants

import org.xpathqs.driver.actions.IAction
import org.xpathqs.driver.actions.SelectorInteractionAction
import org.xpathqs.driver.i18n.I18nHelper
import org.xpathqs.driver.page.Page
import org.xpathqs.log.style.StyledString
import org.xpathqs.log.style.StyledString.Companion.fromDefaultFormatString

object Messages {
    fun init() {
        I18nHelper.init(this)
    }

    object Cache {
        val isPresent = ""
    }

    object Executor {
        val beforeAction = ""
        val afterAction = ""
    }

    object NavExecutor {
        val beforeAction = ""

        fun beforeAction(action: SelectorInteractionAction) = fromDefaultFormatString(
            beforeAction,
            action.on.name
        )
    }

    object Navigator {
        val curPage = ""
        val checkPageIteration = ""
        fun checkPageIteration(page: Page) = fromDefaultFormatString(
            checkPageIteration,
            page.name
        )
        val pageNotFound = ""
        val pageFound = ""
    }

    object Actions {
        object Input {
            val name = ""
            val toString = ""
        }

        object InputFile {
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