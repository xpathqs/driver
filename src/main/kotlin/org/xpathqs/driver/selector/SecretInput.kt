package org.xpathqs.driver.selector

import org.xpathqs.core.selector.NullSelector
import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.core.selector.base.SelectorState
import org.xpathqs.core.selector.selector.Selector
import org.xpathqs.core.selector.selector.SelectorProps

open class SecretInput(
    state: SelectorState = SelectorState.INIT,
    base: ISelector = NullSelector(),
    name: String = "",
    fullName: String = "",
    props: SelectorProps = SelectorProps()
) : Selector(
    state,
    base,
    name,
    fullName,
    props
)