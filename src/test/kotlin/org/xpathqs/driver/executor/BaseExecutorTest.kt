package org.xpathqs.driver.executor

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import org.xpathqs.core.selector.selector.Selector
import org.xpathqs.driver.actions.WaitAction
import org.xpathqs.driver.actions.WaitForSelectorAction
import org.xpathqs.fake.FakeBaseExecutor

internal class BaseExecutorTest {

    @Test
    fun actionHandlerForWaitAction() {
        assertThat(
            FakeBaseExecutor()
                .hasActionHandler(WaitAction())
        ).isEqualTo(true)
    }

    @Test
    fun actionHandlerForWaitSelectorAction() {
        assertThat(
            FakeBaseExecutor()
                .hasActionHandler(
                    WaitForSelectorAction(Selector())
                )
        ).isEqualTo(false)
    }
}