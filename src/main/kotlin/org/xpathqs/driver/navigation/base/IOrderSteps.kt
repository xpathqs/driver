package org.xpathqs.driver.navigation.base

import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
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
        fun submit(cls: KClass<*>) = submit(*(sortMembers(cls).toTypedArray()))
        fun submit(vararg props: KProperty<*>)
                = InputAction(InputType.SUBMIT, listOf(*props))

        fun input(obj: Any?) = input(if(obj == null) null else obj::class)
        fun input(cls: KClass<*>?) = input(*(sortMembers(cls).toTypedArray()))
        fun input(vararg props: KProperty<*>)
                = InputAction(InputType.INPUT, listOf(*props))

        fun click(obj: Any?) = click(if(obj == null) null else obj::class)
        fun click(cls: KClass<*>?) = click(*(sortMembers(cls).toTypedArray()))
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

        fun sortMembers(cls: KClass<*>?): List<KMutableProperty<*>> {
            if(cls == null) return emptyList()
            val fields = cls.java.declaredFields
            val orderById = fields.withIndex().associate { it.value.name.substringBefore("$") to it.index }
            return cls.memberProperties.sortedBy { orderById[it.name] }.filterIsInstance<KMutableProperty<*>>()
        }
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