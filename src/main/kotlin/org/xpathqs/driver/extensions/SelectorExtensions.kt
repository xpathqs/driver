package org.xpathqs.driver.extensions

import org.xpathqs.core.reflection.freeze
import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.core.selector.extensions.core.clone
import org.xpathqs.core.selector.selector.Selector
import org.xpathqs.driver.actions.*
import org.xpathqs.driver.constants.Global
import org.xpathqs.driver.executor.CachedExecutor
import org.xpathqs.driver.page.Page
import org.xpathqs.driver.selector.NearSelector
import org.xpathqs.driver.selector.SecretInput
import java.lang.Exception
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

fun <T : BaseSelector> Collection<T>.waitForFirstVisibleOf(duration: Duration = Global.WAIT_FOR_ELEMENT_TIMEOUT) {
    Global.executor.execute(
        WaitForFirstSelectorAction(this, duration)
    )
}

fun <T : BaseSelector> T.waitForElementsCount(
    count: Int = -1,
    moreThen: Int = -1,
    lessThen: Int = -1,
    duration: Duration = Global.WAIT_FOR_ELEMENT_TIMEOUT
): T {
    Global.executor.execute(
        WaitForSelectorCountAction(
            this,
            expected = count,
            moreThen = moreThen,
            lessThen = lessThen,
            timeout = duration
        )
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

fun <T : BaseSelector> T.file(value: String): T {
    Global.executor.execute(
        InputFileAction(value, this)
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

val <T : BaseSelector> T.cls: String
    get() = getAttr("class")

val <T : BaseSelector> T.isChecked: Boolean
    get() {
    return return try {
        getAttr("checked")
        true
    } catch (e: Exception) {
        false
    }
}


fun <T : BaseSelector> T.getAttr(name: String) =
    Global.executor.getAttr(this, name)

val <T : BaseSelector> T.count: Int
    get() = Global.executor.getElementsCount(this)

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

infix fun <T : BaseSelector> T.near(sel: Selector): NearSelector {
    //if(Global.executor is CachedExecutor) {
        return NearSelector((Global.executor as CachedExecutor).cache, this.clone().freeze(), sel.clone().freeze())
    //}
   // throw Exception("Near works only for CachedExecutors")
}