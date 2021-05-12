package org.xpathqs.driver.exceptions

import org.xpathqs.driver.actions.IAction
import org.xpathqs.driver.executor.IExecutor

open class XPathQsException(
    msg: String
) : Exception(msg) {
    class ActionNotFound(
        action: IAction,
        executor: IExecutor
    ) : XPathQsException(
        msg = "There is no handler for '${action.name}' in the ${executor::class.java.simpleName}"
    )
}