package org.xpathqs.driver.navigation.impl

import org.xpathqs.core.selector.block.Block
import org.xpathqs.driver.extensions.isHidden
import org.xpathqs.driver.extensions.waitForVisible
import org.xpathqs.driver.log.Log
import org.xpathqs.driver.navigation.base.ILoadable
import org.xpathqs.driver.navigation.util.Loading
import org.xpathqs.driver.navigation.util.LoadingParser
import java.time.Duration

open class Loadable(private val block: Block) : ILoadable {
    private var isLoading = false

    override val loading: Loading by lazy {
        if(!isLoading) {
            isLoading = true
            (block as? ILoadable)?.loading
        } else {
            LoadingParser(block).parse()
        } ?: LoadingParser(block).parse()
    }

    override fun waitForLoad(duration: Duration) {
        Log.action("Ожидаем Загрузки страницы") {
            if(loading.loadSelector != null) {
                loading.loadSelector!!.waitForVisible(duration)
                if(loading.loadSelector!!.isHidden) {
                    Log.error("Страница не была загружена")
                }
            } else if(loading.loadSelectors.isNotEmpty()) {
                loading.loadSelectors.forEach {
                    if(it.isHidden) {
                        it.waitForVisible(duration)
                    }
                }
            } else {
                Log.warning("No Load Selector defined")
            }

            if(!loading.isLoaded) {
                Log.error("Страница не была загружена")
            }
        }
    }
}