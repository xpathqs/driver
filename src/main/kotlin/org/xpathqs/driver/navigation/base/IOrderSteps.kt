package org.xpathqs.driver.navigation.base

import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.memberProperties


enum class InputType{
    INPUT, SUBMIT, CLICK, DYNAMIC
}

open class InputAction(
    val type: InputType = InputType.SUBMIT,
    val props: Collection<KProperty<*>> = emptyList()
) {

    companion object {
        fun submit(obj: Any?) = if(obj == null) submit() else submit(obj!!::class)
        fun submit(cls: KClass<*>) = submit(*cls.memberProperties.filterIsInstance<KMutableProperty<*>>().toTypedArray())
        fun submit(vararg props: KProperty<*>)
                = InputAction(InputType.SUBMIT, listOf(*props))

        fun input(obj: Any?) = input(if(obj == null) null else obj::class)
        fun input(cls: KClass<*>?) = input(*cls?.memberProperties?.filterIsInstance<KMutableProperty<*>>()?.toTypedArray() ?: emptyArray())
        fun input(vararg props: KProperty<*>)
                = InputAction(InputType.INPUT, listOf(*props))

        fun click(obj: Any?) = click(if(obj == null) null else obj::class)
        fun click(cls: KClass<*>?) = click(*cls?.memberProperties?.filterIsInstance<KMutableProperty<*>>()?.toTypedArray() ?: emptyArray())
        fun click(vararg props: KProperty<*>)
                = InputAction(InputType.CLICK, listOf(*props))

        fun switch(func: ()->Boolean, onTrue: InputAction, onFalse: InputAction) = SwitchInputAction(
            func, onTrue, onFalse
        )

        /*private fun propOrder(obj: Any): Collection<KProperty<*>> {
            val allProps = obj::class.memberProperties.filterIsInstance<KMutableProperty<*>>()
            if(obj is IValueDependency) {
                obj.valueDependency
            }
        }*/
    }

}

class SwitchInputAction(
    val func: ()->Boolean,
    val onTrue: InputAction,
    val onFalse: InputAction
): InputAction(type = InputType.DYNAMIC)

interface IOrderedSteps {
    val steps: Collection<InputAction>
}