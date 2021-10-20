package org.xpathqs.driver.page

import org.xpathqs.core.selector.NullSelector
import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.driver.navigation.Navigator
import org.xpathqs.driver.navigation.base.ILoadable
import org.xpathqs.driver.navigation.base.ILoadableDelegate
import org.xpathqs.driver.navigation.base.INavigableDetermination
import org.xpathqs.driver.navigation.base.INavigableDeterminationDelegate
import org.xpathqs.driver.navigation.impl.Loadable
import org.xpathqs.driver.navigation.impl.NavigableDetermination
import org.xpathqs.driver.navigation.util.Determination
import org.xpathqs.driver.navigation.util.NavOrderGetter

open class NavDetermination(
    title: String = "",
    base: ISelector = NullSelector(),
    protected val navigator: Navigator
): Page(base, title),
    INavigableDeterminationDelegate, ILoadableDelegate {

    private val navOrderGetter = NavOrderGetter(this)

    internal var reflectionDeterminations = Determination()

    override val navOrder: Int
        get() = navOrderGetter.navOrder

    override val nav: INavigableDetermination by lazy {
        NavigableDetermination(navigator, this)
    }

    override val loadable: ILoadable by lazy {
        Loadable(this)
    }

    override fun afterReflectionParse() {
        navigator.register(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NavDetermination) return false

        return this.name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

}

open class MockNavDetermination : NavDetermination(
    navigator = Nav
)

object Nav: Navigator()