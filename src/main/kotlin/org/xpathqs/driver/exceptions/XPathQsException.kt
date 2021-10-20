package org.xpathqs.driver.exceptions

import org.xpathqs.driver.actions.IAction
import org.xpathqs.driver.executor.IExecutor
import org.xpathqs.driver.page.Page

open class XPathQsException(
    msg: String
) : Exception(msg) {
    class ActionNotFound(
        action: IAction,
        executor: IExecutor
    ) : XPathQsException(
        msg = "There is no handler for '${action.name}' in the ${executor::class.java.simpleName}"
    )

    class CurrentPageNotFound : XPathQsException (
        msg = "Current Page can't be determinate"
    )

    class NoNavigation : XPathQsException (
        msg = "No Navigation"
    )

    class NoModelForThePage(page: Page) : XPathQsException (
        msg = "$page doesn't have a model"
    )
}