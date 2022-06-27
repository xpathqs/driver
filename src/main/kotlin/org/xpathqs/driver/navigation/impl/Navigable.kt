package org.xpathqs.driver.navigation.impl

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.core.selector.base.hasAnnotation
import org.xpathqs.core.selector.block.Block
import org.xpathqs.core.selector.block.allInnerSelectors
import org.xpathqs.core.selector.block.findWithAnnotation
import org.xpathqs.driver.exceptions.XPathQsException
import org.xpathqs.driver.extensions.click
import org.xpathqs.driver.extensions.makeVisible
import org.xpathqs.driver.navigation.Edge
import org.xpathqs.driver.navigation.Navigator
import org.xpathqs.driver.navigation.annotations.UI
import org.xpathqs.driver.navigation.base.*
import org.xpathqs.driver.navigation.util.IBlockNavigation
import org.xpathqs.driver.page.Page
import java.time.Duration

open class Navigable(
    protected val navigator: Navigator,
    protected val block: INavigable,
    protected val selectorNavigator: IBlockSelectorNavigation
        = ModelStateSelectorNavigation(
            ModelStateParentNavigation(
                FormSelectorSelectOptionNavigation(
                    //FormSelectorValidationErrorNavigation(
                        TriggerModelNavigation(
                            CheckBoxNavigation(
                                SelectableNavigation(
                                    ClickToBackNavigation(
                                        BlockSelectorNavigationImpl()
                                    )
                                )
                            )
                        )
                   // )
                )
            )
        )
) : INavigable, IBlockSelectorNavigation {
    override fun addNavigation(to: INavigable, order: Int, action: (() -> Unit)?) {
        navigator.addEdge(
            Edge(
                from = block,
                to = to,
                _weight = order.toDouble(),
                action = action
            )
        )

        val targetPage = to as? Block
        targetPage?.let { page ->
            page.allInnerSelectors.firstOrNull {
                it.hasAnnotation(UI.Widgets.Back::class)
            }?.let { back ->
                navigator.addEdge(
                    Edge(
                        from = to,
                        to = block,
                        _weight = order.toDouble(),
                        action = {
                            back.click()
                        }
                    )
                )
            }
        }
    }

    override fun navigate(elem: ISelector, navigator: INavigator) {
        selectorNavigator.navigate(elem, navigator)
    }

    override fun navigate() {
        if(this.block is IBlockNavigation) {
            if((this.block as? INavigableDetermination)?.isVisible == false) {
                if(this.block.selfNavigation.byCheckbox is BaseSelector) {
                    (this.block.selfNavigation.byCheckbox as BaseSelector).click()
                    (this.block as ILoadable).waitForLoad(Duration.ofSeconds(30))
                    return
                }
            }
        }

        if(this.block is Page) {
            try {
                navigator.navigate(navigator.currentPage, this.block)
            } catch (e : XPathQsException.NoNavigation) {
                this.block.findWithAnnotation(UI.Nav.DeterminateBy::class)?.makeVisible()
            }
        }
    }
}

