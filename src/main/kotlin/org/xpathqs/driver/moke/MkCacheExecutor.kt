package org.xpathqs.driver.moke

import org.xpathqs.driver.executor.CachedExecutor

open class MkCacheExecutor : CachedExecutor(MkExecutor(), MkCache(), MkNavigator())