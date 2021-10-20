package org.xpathqs.driver.navigation.annotations

class Model {
    class Validation

    @Target(
        AnnotationTarget.CLASS,
        AnnotationTarget.FIELD
    )
    @Retention(AnnotationRetention.RUNTIME)
    annotation class Order(
        val order: Int = DEFAULT_ORDER
    ) {
        companion object {
            const val DEFAULT_ORDER = 1
        }
    }

    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.RUNTIME)
    annotation class ReflectionOrder
}