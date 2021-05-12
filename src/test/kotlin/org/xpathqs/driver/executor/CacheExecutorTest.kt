package org.xpathqs.driver.executor

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import org.xpathqs.core.selector.selector.Selector
import org.xpathqs.driver.actions.WaitAction
import org.xpathqs.driver.actions.WaitForSelectorAction
import org.xpathqs.driver.actions.WaitForSelectorDisappearAction
import org.xpathqs.fake.FakeCacheExecutor

internal class CacheExecutorTest {

    @Test
    fun actionHandlerForWaitAction() {
        assertThat(
            FakeCacheExecutor()
                .hasActionHandler(WaitAction())
        ).isEqualTo(true)
    }

    @Test
    fun actionHandlerForWaitSelectorAction() {
        assertThat(
            FakeCacheExecutor()
                .hasActionHandler(
                    WaitForSelectorAction(Selector())
                )
        ).isEqualTo(true)
    }

    @Test
    fun actionHandlerForWaitSelectorDissapearAction() {
        assertThat(
            FakeCacheExecutor()
                .hasActionHandler(
                    WaitForSelectorDisappearAction(Selector())
                )
        ).isEqualTo(true)
    }
}