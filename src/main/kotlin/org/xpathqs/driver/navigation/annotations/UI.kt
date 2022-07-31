package org.xpathqs.driver.navigation.annotations

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.core.selector.block.Block
import kotlin.reflect.KClass

class UI {
    class Widgets {
        @Target(
            AnnotationTarget.CLASS,
            AnnotationTarget.FIELD
        )
        @Retention(AnnotationRetention.RUNTIME)
        annotation class Form(
            val cleanByDefault: Boolean = true
        )

        @Target(
            AnnotationTarget.CLASS,
            AnnotationTarget.FIELD
        )
        @Retention(AnnotationRetention.RUNTIME)
        annotation class Submit

        @Target(
            AnnotationTarget.CLASS,
            AnnotationTarget.FIELD
        )
        @Retention(AnnotationRetention.RUNTIME)
        annotation class Input(
            val type: String = "",
            val afterInputDelayMs: Long = 0
        )

        @Target(
            AnnotationTarget.CLASS,
            AnnotationTarget.FIELD
        )
        @Retention(AnnotationRetention.RUNTIME)
        annotation class Select

        @Target(
            AnnotationTarget.CLASS,
            AnnotationTarget.FIELD
        )
        @Retention(AnnotationRetention.RUNTIME)
        annotation class OptionItem

        @Target(
            AnnotationTarget.CLASS,
            AnnotationTarget.FIELD
        )
        @Retention(AnnotationRetention.RUNTIME)
        annotation class DropdownItem

        @Target(
            AnnotationTarget.CLASS,
            AnnotationTarget.FIELD
        )
        @Retention(AnnotationRetention.RUNTIME)
        annotation class ValidationError

        @Target(
            AnnotationTarget.CLASS,
            AnnotationTarget.FIELD
        )
        @Retention(AnnotationRetention.RUNTIME)
        annotation class ResetForm

        @Target(
            AnnotationTarget.CLASS,
            AnnotationTarget.FIELD
        )
        @Retention(AnnotationRetention.RUNTIME)
        annotation class ClickToClose

        @Target(
            AnnotationTarget.CLASS,
            AnnotationTarget.FIELD
        )
        @Retention(AnnotationRetention.RUNTIME)
        annotation class Back

        @Target(
            AnnotationTarget.CLASS,
            AnnotationTarget.FIELD
        )
        @Retention(AnnotationRetention.RUNTIME)
        annotation class Checkbox(
            val onChecked: KClass<out BaseSelector> = BaseSelector::class,
            val onUnchecked: KClass<out BaseSelector> = BaseSelector::class,
            val default: Boolean = true
        )

        @Target(
            AnnotationTarget.CLASS,
            AnnotationTarget.FIELD
        )
        @Retention(AnnotationRetention.RUNTIME)
        annotation class Selectable(
            val onSelected: Array<KClass<out BaseSelector>> = [],
        )
    }
    class Visibility {
        @Target(
            AnnotationTarget.CLASS,
            AnnotationTarget.FIELD
        )
        @Retention(AnnotationRetention.RUNTIME)
        annotation class Always

        @Target(
            AnnotationTarget.CLASS,
            AnnotationTarget.FIELD
        )
        @Retention(AnnotationRetention.RUNTIME)
        annotation class Dynamic(
            val modelState: Int = UNDEF_STATE,
            val modelDepends: Int = UNDEF_STATE,
            val overlapped: Boolean = false
        )

        @Target(
            AnnotationTarget.CLASS,
            AnnotationTarget.FIELD
        )
        @Retention(AnnotationRetention.RUNTIME)
        annotation class Backend

        companion object {
            const val UNDEF_STATE = -1
        }
    }
    class Nav {
        @Target(
            AnnotationTarget.CLASS,
            AnnotationTarget.FIELD,
            AnnotationTarget.FUNCTION
        )
        @Retention(AnnotationRetention.RUNTIME)
        annotation class PathTo(
            val byClick: KClass<out Block> = Block::class,
            val byInvoke: KClass<out Block> = Block::class,
            val bySubmit: KClass<out Block> = Block::class,
            val value: String = "",
            val weight: Int = UNDEF,
            vararg val contains: KClass<out Block> = [],
            val switchTab: Boolean = false
        ) {
            companion object {
                const val UNDEF = -1
                const val ALREADY_PRESENT_WEIGHT = 10
                const val DEFAULT_WEIGHT = 100
            }
        }

        @Target(
            AnnotationTarget.CLASS,
            AnnotationTarget.FIELD
        )
        @Retention(AnnotationRetention.RUNTIME)
        annotation class DeterminateBy(
            val determination: DeterminationType = DeterminationType.EXIST
        )

        @Target(
            AnnotationTarget.CLASS
        )
        @Retention(AnnotationRetention.RUNTIME)
        annotation class Order(
            val order: Int = DEFAULT,
            val type: NavOrderType = NavOrderType.DEFAULT
        ) {
            companion object {
                const val DEFAULT = 100
            }
        }

        @Target(
            AnnotationTarget.CLASS
        )
        @Retention(AnnotationRetention.RUNTIME)
        annotation class WaitFor

        @Target(
            AnnotationTarget.CLASS
        )
        @Retention(AnnotationRetention.RUNTIME)
        annotation class Autoclose
    }

    @Target(
        AnnotationTarget.CLASS,
        AnnotationTarget.FIELD
    )
    @Retention(AnnotationRetention.RUNTIME)
    annotation class Animated(
        val timeToCompleteMs: Int = 500,
        val autoCloseMs: Int = 0
    )
}
