package org.xpathqs.driver.navigation.impl

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.core.selector.base.hasAnnotation
import org.xpathqs.core.selector.block.Block
import org.xpathqs.core.selector.block.allInnerSelectors
import org.xpathqs.core.selector.block.findWithAnnotation
import org.xpathqs.core.selector.extensions.parents
import org.xpathqs.driver.exceptions.XPathQsException
import org.xpathqs.driver.extensions.click
import org.xpathqs.driver.extensions.isHidden
import org.xpathqs.driver.extensions.makeVisible
import org.xpathqs.driver.navigation.Edge
import org.xpathqs.driver.navigation.NavWrapper
import org.xpathqs.driver.navigation.Navigator
import org.xpathqs.driver.navigation.annotations.UI
import org.xpathqs.driver.navigation.annotations.UI.Visibility.Companion.UNDEF_STATE
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
                            CheckBoxNavigation(
                                CheckBoxLinkedNavigation(
                                    SelectableNavigation(
                                        ClickToBackNavigation(
                                            InternalPageStateNavigation(
                                                VisibilityMapInputNavigation(
                                                    LinkedVisibilityNavigation(
                                                        TriggerModelNavigation(
                                                            BlockSelectorNavigationImpl()
                                                        )
                                                    )
                                                )
                                            )
                                        )
                                    )
                                )
                            )
                   // )
                )
            )
        )
) : INavigable, IBlockSelectorNavigation {
    override fun addNavigation(to: INavigable, order: Int, selfState: Int, state: Int, action: (() -> Unit)?) {

        navigator.addEdge(
            Edge(
                from = NavWrapper.get(block, selfState),
                to = NavWrapper.get(to, state),
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
                        from = NavWrapper.get(to, state),
                        to = NavWrapper.get(block),
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
        (elem as? BaseSelector)?.parents?.let { parents ->
            parents.reversed().forEach { parent ->
                if(parent.name.isNotEmpty()
                    && parent.xpath.isNotEmpty()
                    && parent.isHidden
                ) {
                    selectorNavigator.navigate(parent, navigator)
                }
            }
        }
        if((elem as? BaseSelector)?.isHidden == true) {
            selectorNavigator.navigate(elem, navigator)
        }
    }

    override fun navigate(state: Int) {
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
                val currentPage = navigator.currentPage
                val currentState = if(currentPage is IPageState) {
                    currentPage.pageState
                } else {
                    UNDEF_STATE
                }
                val navState = if(currentPage === this.block && state == UNDEF_STATE) {
                    currentState
                } else state

                navigator.navigate(
                    NavWrapper.get(currentPage, currentState),
                    NavWrapper.get(this.block, navState)
                )
            } catch (e : XPathQsException.NoNavigation) {
                this.block.findWithAnnotation(UI.Nav.DeterminateBy::class)?.makeVisible()
            }
        }
    }
}

