package org.xpathqs.driver.navigation.annotations

class Model {
    class Validation

    class DataTypes {
        @Target(
            AnnotationTarget.PROPERTY,
        )
        @Retention(AnnotationRetention.RUNTIME)
        annotation class Date

        @Target(
            AnnotationTarget.PROPERTY,
        )
        @Retention(AnnotationRetention.RUNTIME)
        annotation class Number

        @Target(
            AnnotationTarget.PROPERTY,
        )
        @Retention(AnnotationRetention.RUNTIME)
        annotation class Currency

        @Target(
            AnnotationTarget.PROPERTY,
        )
        @Retention(AnnotationRetention.RUNTIME)
        annotation class Ignore

        @Target(
            AnnotationTarget.PROPERTY,
        )
        @Retention(AnnotationRetention.RUNTIME)
        annotation class Items(
            val items: Array<String>
        )
    }

    class ComparatorConfig {
        @Target(
            AnnotationTarget.PROPERTY,
        )
        @Retention(AnnotationRetention.RUNTIME)
        annotation class IgnoreDots

        @Target(
            AnnotationTarget.PROPERTY,
        )
        @Retention(AnnotationRetention.RUNTIME)
        annotation class IgnoreSpaces

        @Target(
            AnnotationTarget.PROPERTY,
        )
        @Retention(AnnotationRetention.RUNTIME)
        annotation class StartsWith
    }

    @Target(
        AnnotationTarget.CLASS,
        AnnotationTarget.PROPERTY
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