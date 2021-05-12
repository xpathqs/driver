package org.xpathqs.fake

import org.xpathqs.driver.executor.CacheExecutor

open class FakeCacheExecutor : CacheExecutor(FakeDriver(), FakeCache())