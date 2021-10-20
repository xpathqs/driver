package org.xpathqs.driver.navigation.annotations

enum class NavOrderType(
    val value: Int
) {
    NULL(-1),
    LOWEST(0),
    LOW(50),
    DEFAULT(100),
    HIGH(150),
    HIGHEST(200)
}