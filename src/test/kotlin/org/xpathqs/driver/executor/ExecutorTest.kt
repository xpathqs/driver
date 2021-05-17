package org.xpathqs.driver.executor

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import org.xpathqs.core.selector.selector.Selector
import org.xpathqs.driver.actions.WaitAction
import org.xpathqs.driver.actions.WaitForSelectorAction
import org.xpathqs.driver.moke.MkExecutor

internal class ExecutorTest {

    @Test
    fun actionHandlerForWaitAction() {
        assertThat(
            MkExecutor()
                .hasActionHandler(WaitAction())
        ).isEqualTo(true)
    }

    @Test
    fun actionHandlerForWaitSelectorAction() {
        assertThat(
            MkExecutor()
                .hasActionHandler(
                    WaitForSelectorAction(Selector())
                )
        ).isEqualTo(false)
    }
}