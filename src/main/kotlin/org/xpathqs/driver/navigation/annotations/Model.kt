package org.xpathqs.driver.navigation.annotations

class Model {
    class Validation

    class DataTypes {
        @Target(
            AnnotationTarget.FIELD,
            AnnotationTarget.PROPERTY,
        )
        @Retention(AnnotationRetention.RUNTIME)
        annotation class Date

        @Target(
            AnnotationTarget.FIELD,
            AnnotationTarget.PROPERTY,
        )
        @Retention(AnnotationRetention.RUNTIME)
        annotation class Number

        @Target(
            AnnotationTarget.FIELD,
            AnnotationTarget.PROPERTY,
        )
        @Retention(AnnotationRetention.RUNTIME)
        annotation class Currency

        @Target(
            AnnotationTarget.FIELD,
            AnnotationTarget.PROPERTY,
        )
        @Retention(AnnotationRetention.RUNTIME)
        annotation class Ignore

        @Target(
            AnnotationTarget.FIELD,
            AnnotationTarget.PROPERTY,
        )
        @Retention(AnnotationRetention.RUNTIME)
        annotation class Items(
            val items: Array<String>
        )
    }

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