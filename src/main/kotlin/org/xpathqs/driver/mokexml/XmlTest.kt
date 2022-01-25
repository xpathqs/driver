package org.xpathqs.driver.mokexml

import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.driver.cache.Cache
import org.xpathqs.driver.cache.XmlCache
import org.xpathqs.driver.executor.IExecutor
import org.xpathqs.driver.log.Log
import org.xpathqs.driver.navigation.Navigator
import org.xpathqs.driver.page.Page
import org.xpathqs.log.BaseLogger
import org.xpathqs.log.Logger
import org.xpathqs.log.printers.StreamLogPrinter
import org.xpathqs.log.printers.args.NoArgsProcessor
import org.xpathqs.log.printers.args.StyleArgsProcessor
import org.xpathqs.log.printers.args.TimeArgsProcessor
import org.xpathqs.log.printers.body.BodyProcessorImpl
import org.xpathqs.log.printers.body.HierarchyBodyProcessor
import org.xpathqs.log.printers.body.StyledBodyProcessor
import org.xpathqs.log.style.Style
import org.xpathqs.log.style.StyledString

open class XmlTest(
    resource: String,
    private val cache: Cache = XmlCache(),
    val navigator: Navigator = Navigator()
) {
    protected lateinit var executor: IExecutor

    fun getResourceAsText(path: String): String {
        return this::class.java.classLoader.getResource(path).readText()
    }

    init {
        refreshCache(resource)
        initLog()
        Log.info("Finish init log")
    }

    fun refreshCache(path: String) {
        val xml = getResourceAsText(path)
        executor = MockCachedExecutor(xml, cache)
        navigator.init(executor)
        org.xpathqs.driver.constants.Global.localExecutor.set(executor)
    }

    open protected fun initLog() {
        val consoleLog = Logger(
            streamPrinter = StreamLogPrinter(
                argsProcessor =
                StyleArgsProcessor(
                    TimeArgsProcessor(
                        NoArgsProcessor()
                    ),
                    Style(textColor = 60)
                ),
                bodyProcessor =
                StyledBodyProcessor(
                    HierarchyBodyProcessor(
                        BodyProcessorImpl()
                    ),
                    level1 = Style(textColor = 48)
                ),
                writer = System.out
            )
        )
        Log.log = BaseLogger(
            arrayListOf(
                consoleLog
            ),
            StyledString.defaultStyles
        )
    }

    fun isPresent(sel: ISelector)
        = executor.isPresent(sel)

    fun count(sel: ISelector)
        = executor.getElementsCount(sel)

    val currentPage: Page
        get() = navigator.currentPage as Page
}