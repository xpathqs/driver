package org.xpathqs.driver.executor

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import org.xpathqs.core.selector.selector.Selector
import org.xpathqs.driver.actions.WaitAction
import org.xpathqs.driver.actions.WaitForSelectorAction
import org.xpathqs.driver.actions.WaitForSelectorDisappearAction
import org.xpathqs.driver.moke.MkCacheExecutor

internal class CacheExecutorTest {

    @Test
    fun actionHandlerForWaitAction() {
        assertThat(
            MkCacheExecutor()
                .hasActionHandler(WaitAction())
        ).isEqualTo(true)
    }

    @Test
    fun actionHandlerForWaitSelectorAction() {
        assertThat(
            MkCacheExecutor()
                .hasActionHandler(
                    WaitForSelectorAction(Selector())
                )
        ).isEqualTo(true)
    }

    @Test
    fun actionHandlerForWaitSelectorDissapearAction() {
        assertThat(
            MkCacheExecutor()
                .hasActionHandler(
                    WaitForSelectorDisappearAction(Selector())
                )
        ).isEqualTo(true)
    }
}