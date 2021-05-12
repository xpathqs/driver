package org.xpathqs.driver.extensions

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.driver.actions.*
import org.xpathqs.driver.const.Global
import java.time.Duration

val <T : BaseSelector> T.isVisible: Boolean
    get() = Global.executor.isPresent(this)

val <T : BaseSelector> T.isHidden: Boolean
    get() = !isVisible

fun <T : BaseSelector> T.waitForVisible(duration: Duration = Global.WAIT_FOR_ELEMENT_TIMEOUT) {
    Global.executor.execute(WaitForSelectorAction(this, duration))
}

fun <T : BaseSelector> T.waitForDisappear(duration: Duration = Global.WAIT_FOR_ELEMENT_TIMEOUT) {
    Global.executor.execute(WaitForSelectorDisappearAction(this, duration))
}

fun <T : BaseSelector> T.click(): T {
    Global.executor.execute(ClickAction(this))
    return this
}

fun <T : BaseSelector> T.input(value: String): T {
    Global.executor.execute(InputAction(value, this))
    return this
}

fun <T : BaseSelector> T.clear(): T {
    Global.executor.execute(ClearAction(this))
    return this
}

val <T : BaseSelector> T.text: String
    get() = getAttr(Global.TEXT_ARG)

fun <T : BaseSelector> T.getAttr(name: String) =
    Global.executor.getAttr(this, name)
