package org.xpathqs.driver.extensions

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.driver.actions.*
import org.xpathqs.driver.constants.Global
import org.xpathqs.driver.page.Page
import org.xpathqs.driver.selector.SecretInput
import java.time.Duration

val <T : BaseSelector> T.isVisible
    get() = Global.executor.isPresent(this)

val <T : BaseSelector> T.isHidden: Boolean
    get() = !isVisible

fun <T : BaseSelector> T.waitForVisible(duration: Duration = Global.WAIT_FOR_ELEMENT_TIMEOUT): T {
    Global.executor.execute(
        WaitForSelectorAction(this, duration)
    )
    return this
}

fun <T : BaseSelector> T.waitForDisappear(duration: Duration = Global.WAIT_FOR_ELEMENT_TIMEOUT): T {
    Global.executor.execute(
        WaitForSelectorDisappearAction(this, duration)
    )
    return this
}

fun <T : BaseSelector> T.click(moveMouse: Boolean = false): T {
    Global.executor.execute(
        ClickAction(this, moveMouse)
    )
    return this
}

fun <T : BaseSelector> T.input(value: String, clear: Boolean = true): T {
    Global.executor.execute(
        InputAction(value, this, clear)
    )
    return this
}

fun <T : BaseSelector> T.clear(): T {
    Global.executor.execute(
        ClearAction(this)
    )
    return this
}

val <T : BaseSelector> T.text: String
    get() = getAttr(Global.TEXT_ARG)

val <T : BaseSelector> T.value: String
    get() = getAttr("value")

fun <T : BaseSelector> T.getAttr(name: String) =
    Global.executor.getAttr(this, name)

fun <T : BaseSelector> T.isSecret(): Boolean {
    return this is SecretInput
}

fun <T : BaseSelector> T.makeVisible(): T {
    Global.executor.execute(
        MakeVisibleAction(this)
    )
    return this
}

fun <T : BaseSelector> T.screenshot(boundRect: Boolean=true): T {
    Global.executor.execute(
        ScreenShotAction(this, boundRect)
    )
    return this
}
