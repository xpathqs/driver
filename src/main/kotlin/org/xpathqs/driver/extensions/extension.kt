package org.xpathqs.driver.extensions

import java.time.Duration

val Int.seconds: Duration
    get() = Duration.ofSeconds(this.toLong())

val Long.seconds: Duration
    get() = Duration.ofSeconds(this)

val Int.ms: Duration
    get() = Duration.ofMillis(this.toLong())

val Long.ms: Duration
    get() = Duration.ofMillis(this)