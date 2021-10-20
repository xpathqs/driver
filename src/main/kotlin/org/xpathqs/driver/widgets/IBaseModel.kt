package org.xpathqs.driver.widgets

import org.apache.commons.lang3.ClassUtils
import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.core.selector.base.findAnnotation
import org.xpathqs.core.selector.base.findParentWithAnnotation
import org.xpathqs.core.selector.base.hasAnnotation
import org.xpathqs.core.selector.block.Block
import org.xpathqs.core.selector.block.allInnerSelectors
import org.xpathqs.core.selector.block.findWithAnnotation
import org.xpathqs.core.selector.extensions.parents
import org.xpathqs.core.selector.extensions.rootParent
import org.xpathqs.core.selector.extensions.simpleName
import org.xpathqs.core.selector.extensions.text
import org.xpathqs.driver.extensions.click
import org.xpathqs.driver.extensions.input
import org.xpathqs.driver.extensions.isHidden
import org.xpathqs.driver.extensions.waitForVisible
import org.xpathqs.driver.log.Log
import org.xpathqs.driver.navigation.annotations.UI
import org.xpathqs.driver.navigation.base.*
import kotlin.properties.Delegates
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaType
import kotlin.reflect.jvm.jvmName

open class IBaseModel(
    private val view: Block? = null
) {
    open val mappings: Map<KProperty<*>, BaseSelector>
        by lazy {
            reflectionMappings + propArgsMappings
        }

    open val reflectionMappings: Map<KProperty<*>, BaseSelector>
        by lazy {
            val res = HashMap<KProperty<*>, BaseSelector>()
            this::class.declaredMemberProperties.forEach { p ->
                view?.allInnerSelectors?.find { it.simpleName == p.name }?.let { s ->
                    res[p] = s
                }
            }
            res
        }

    open val propArgsMappings: Map<KProperty<*>, BaseSelector>
        by lazy {
            val before = isInit.get()
            isInit.set(false)

            allProperties().forEach { p ->
                val parent = findParent(this, p)
                val v = p.getter.call(parent)
                (p as? KMutableProperty<*>)?.setter?.call(parent, v)
            }

            isInit.set(before)
            lazyPropMap
        }

    private val lazyPropMap = HashMap<KProperty<*>, BaseSelector>()

    open val states: Map<Int, IBaseModel>
        get() = emptyMap()

    val containers: Collection<BaseSelector>
        get() = mappings.values
            .distinctBy { it.rootParent }
            .mapNotNull { it.rootParent as? BaseSelector }

    fun findWidget(ann: KClass<*>) =
        containers.map {
            (it as? Block)?.findWithAnnotation(ann) ?: it.findParentWithAnnotation(ann)
        }?.first()

    fun submit() {
      //  if(!propTrig) {
            fill()
      //  }
        findWidget(UI.Widgets.Submit::class)?.click()
    }

    fun reset() {
        mappings.keys.forEach {
            (it as KMutableProperty<*>).setter.call(this, "")
        }
    }

    fun invalidate(sel: BaseSelector) {
        val prop = findPropBySel(sel)!!
        val ann = prop.findAnnotation<Validation>()
        if (ann == null) {
            if (prop is KMutableProperty<*>) {
                val targetSel = findSelByProp(prop) as? Block
                val hasChildFile = targetSel?.allInnerSelectors?.firstOrNull {
                    it.findAnnotation<UI.Widgets.Input>()?.type != "file"
                } != null
                val hasFile = targetSel?.findAnnotation<UI.Widgets.Input>()?.type != "file"
                if(!hasChildFile && !hasFile) {
                    val parent = findParent(this, prop)
                    try {
                        prop.setter.call(parent, "")
                    } catch (e: Exception) {
                        println(e)
                        //     throw e
                    }catch (e: Error) {
                        println(e)
                        //    throw e
                    }
                }

                if (sel.isHidden) {
                    mappings.values.find { it.name != sel.name }?.click()
                }
                if (sel.isHidden) {
                    submit()
                }
            }
        }
    }

    fun fill(prop: KMutableProperty<*>) {
        val parent = findParent(this, prop)
        val v = prop.getter.call(parent)
        prop.setter.call(parent, v)
    }

    fun fill() {
        if(this is IOrderedSteps) {
            evalActions(steps)
        } else {
            this.mappings.keys.forEach {
                fill(it as KMutableProperty<*>)
            }
        }

        this.propTrig = true
    }

    fun evalActions(steps: Collection<InputAction>) {
        steps.forEach { action ->
            action.props.forEach {
                if(it.getter.parameters.isEmpty()) {
                    val v = it.getter.call()
                    (it as KMutableProperty<*>).setter.call(v)
                } else {
                    val parent = findParent(this, it)
                    try {
                        val v = it.getter.call(parent)
                        (it as KMutableProperty<*>).setter.call(parent, v)
                    } catch (e: Exception) {
                        it.getter.call(parent)
                        throw e
                    }
                }
            }

            if(action.type == InputType.SUBMIT) {
                (getSelector(action).rootParent as Block).findWithAnnotation(
                    UI.Widgets.Submit::class
                )!!.waitForVisible().click()
            }
        }
    }

    fun getSelector(action: InputAction): BaseSelector {
        return this.mappings.filterKeys {
            it.name == (action.props as List).first().name
        }.values.first()
    }

    fun submit(state: Int) {
        if(state == DEFAULT) {
            submit()
        } else {
            states[state]?.let {
                //it.fill()
                it.submit()
            }
        }
    }

    fun submit(page: INavigable) {
        if(this is IOrderedSteps) {
            val stepsToSubmit = ArrayList<InputAction>()
            steps.forEach { action ->
                val sel = getSelector(action)
                page as BaseSelector
                if(sel.parents.find { it.name == page.name} != null) {
                    stepsToSubmit.add(action)
                }
            }
            evalActions(stepsToSubmit)
        } else {
            if(states.containsKey(CORRECT)) {
                submit(CORRECT)
            } else {
                submit()
            }
        }
    }

    fun findSelByProp(prop: KProperty<*>) =
        mappings.filterKeys { it.name == prop.name }.values.first()


    fun findPropBySel(sel: BaseSelector): KProperty<*>? {
        val res = mappings.filterValues {
            it.name == sel.name
        }
        if (res.isNotEmpty()) {
            return res.keys.first()
        }

        val input = (sel.base as? Block)?.findWithAnnotation(UI.Widgets.Input::class)
        if (input != null) {
            return mappings.filterValues {
                it.name == input.name || it == input.base
            }.keys.first()
        }

        return null
    }

    private var propTrig = false

    fun findParent(source: Any, prop: KProperty<*>): Any? {
       // return Log.action("Finding parent for ${prop.name}") {
            try {
                properties(source).forEach {
                    if(it.name == prop.name) {
                        return source
                    }
                    if(!it.isPrimitive) {
                        try {
                            it.isAccessible = true
                        } catch (e:  Error) {

                        }catch (e:  Exception) {

                        }
                        var res: Any? = null
                        try {
                            res = findParent(it.getter.call(source)!!, prop)
                        } catch (e:  Exception) {

                        } catch (e: Error) {

                        }
                        if(res != null) {
                            return res
                        }
                    }
                }
            } catch (e: Exception) {

            }

            return null
      //  }

    }

    fun properties(obj: Any = this) = obj::class.memberProperties.filter {
        it is KMutableProperty<*>
                || it.returnType.javaType.typeName.startsWith(obj::class.jvmName)
    }

    fun allProperties(obj: Any = this): Collection<KProperty<*>> {
        val res = ArrayList<KProperty<*>>()

        res.addAll(
            obj::class.memberProperties.filterIsInstance<KMutableProperty<*>>()
        )

        obj::class.memberProperties.filter {
            it.returnType.javaType.typeName.startsWith(obj::class.jvmName)
        }.forEach {
            val v = it.getter.call(this)!!
            res.addAll(
                allProperties(v)
            )
        }

        return res
    }

    inner class FieldsCls {
        fun input(mapping: BaseSelector? = null, default: String = "") =
            Delegates.observable(default) { prop, old, new ->
                // if(old != new) {

                    val ts1 = System.currentTimeMillis()
                    propTrig = true
                    mapping?.let {
                        lazyPropMap[prop] = it
                    }

                    if(isInit.get()) {
                        val p = findParent(this@IBaseModel, prop)

                        val sel = mappings.filterKeys { it.name == prop.name }.values.first()

                        if(sel.isHidden) {
                            Log.action("Selector $sel is hidden") {
                                if(p is IValueDependency) {
                                    val vd = p.valueDependency.find { vd ->
                                        vd.source.find { it.name == prop.name } != null
                                    }

                                    val prop = vd?.dependsOn as? KMutableProperty<*>
                                    if(prop != null) {
                                        Log.action("Dependency was found of: ${prop.name}") {
                                            if(prop.getter.parameters.size == 1) {
                                                val thiz = findParent(this@IBaseModel, prop)
                                                if(vd!!.value is DefaultValue) {
                                                    val v = prop.getter.call(thiz)
                                                    prop.setter.call(thiz, v)
                                                } else {
                                                    prop.setter.call(thiz, vd!!.value)
                                                }
                                           //     Thread.sleep(500)
                                                vd.source.forEach {
                                                    findSelByProp(it).waitForVisible()
                                                }
                                            } else {
                                                if(vd!!.value is DefaultValue) {
                                                    val v = prop.getter.call()
                                                    prop.setter.call(v)
                                                } else {
                                                    prop.setter.call(vd!!.value)
                                                }
                                    //            Thread.sleep(500)
                                                vd.source.forEach {
                                                    findSelByProp(it).waitForVisible()
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Log.action("Selector ${sel.name} was found, calling input") {
                            if (sel is IFormInput) {
                                if(new.isEmpty()) {
                                    sel.clear()
                                } else {
                                    sel.input(new)
                                }
                            } else {
                                sel.input(new)
                            }
                        }
                    }

                //}
            }


        fun click(mapping: BaseSelector? = null, default: String = "") =
            Delegates.observable(default) { prop, old, new ->
                propTrig = true
                mapping?.let {
                    lazyPropMap[prop] = it
                }
                if(isInit.get()) {
                    val sel = mappings.filterKeys { it.name == prop.name }.values.first()

                    if (sel is IFormInput) {
                        sel.input(new)
                    } else {
                        sel.text(new).click()
                    }
                }
            }

        fun switch(onTrue: BaseSelector? = null, onFalse: BaseSelector? = null, default: Boolean) =
            Delegates.observable(default) { prop, old, new ->
                propTrig = true
                if(isInit.get()) {
                    if(new) {
                        onTrue?.click()
                    } else {
                        onFalse?.click()
                    }
                }
            }

    } val Fields = FieldsCls()

    companion object {
        const val DEFAULT = 0
        const val CORRECT = 1
        const val INCORRECT = 2
        const val EMPTY = 3

        val isInit = ThreadLocal<Boolean>().apply {set(true)}
    }
}

val KProperty<*>.isPrimitive: Boolean
    get() {
        return this.name.endsWith("String") || ClassUtils.isPrimitiveOrWrapper(this.javaClass)
    }

fun <T : Any> T.clone() :T {
    val before = IBaseModel.isInit.get()
    IBaseModel.isInit.set(false)

    val res = this::class.constructors.find { it.parameters.isEmpty() }!!.call()
    res.copyProps(this)

    val props = this::class.memberProperties.filter {
        it.returnType.toString().startsWith(
            this.javaClass.name.replace("$",".")
        )
    }

    props.forEach {
        if(it is KMutableProperty<*>) {
            val cloned = it.getter.call(this)!!.clone()
            it.setter.call(res, cloned)
        } else {
            val to = it.getter.call(res)!!
            val from = it.getter.call(this)!!
            to.copyProps(from)
        }
    }

    IBaseModel.isInit.set(before)
    return res
  //  return props.keys.associateWith { props[it]?.get(this) }
}

fun Any.copyProps(from: Any) {
    this::class.memberProperties
        .filterIsInstance<KMutableProperty<*>>()
        .forEach {
            val v = it.getter.call(from)!!
            val cls = v.javaClass
            if(cls.name.endsWith("String") || ClassUtils.isPrimitiveOrWrapper(cls)) {
                it.setter.call(this, v)
            }
        }
}