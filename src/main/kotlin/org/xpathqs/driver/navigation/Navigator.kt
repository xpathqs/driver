package org.xpathqs.driver.navigation

import org.jgrapht.GraphPath
import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.graph.SimpleDirectedWeightedGraph
import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.core.selector.block.Block
import org.xpathqs.core.selector.extensions.rootParent
import org.xpathqs.driver.constants.Messages
import org.xpathqs.driver.exceptions.XPathQsException
import org.xpathqs.driver.executor.IExecutor
import org.xpathqs.driver.log.Log
import org.xpathqs.driver.navigation.annotations.UI
import org.xpathqs.driver.page.*
import org.xpathqs.driver.navigation.base.INavigable
import org.xpathqs.driver.navigation.base.INavigableDetermination
import org.xpathqs.driver.navigation.base.IPageCallback
import org.xpathqs.driver.navigation.util.NavigationParser
import org.xpathqs.log.style.StyleFactory.keyword
import org.xpathqs.log.style.StyleFactory.selectorName

open class Navigator {
    private lateinit var executor: IExecutor
    private val pages = ArrayList<INavigableDetermination>()
    private val blocks = ArrayList<INavigable>()
    private val edges = ArrayList<Edge>()
    private val graph = SimpleDirectedWeightedGraph<INavigable, Edge>(
        Edge::class.java
    )
    private val shortestPath = DijkstraShortestPath(graph)

    private val firstTimeDetection = HashSet<String>()

    fun register(page: INavigable) {
        page as ISelector
        if(!pages.contains(page)) {
            Log.info(selectorName(page.name) + " was added to the " + keyword(this::class.simpleName!!))
            if(page is Page && page is INavigableDetermination) {
                pages.add(page)
            } else if(page is Block) {
                if(page.rootParent !is Page) {
                    blocks.add(page)
                }
            }
            graph.addVertex(page)
        }
    }

    fun addEdge(edge: Edge) {
        Log.info("Call addEdge to the: " + this)
        if(!edges.contains(edge)) {
            val newEdge = graph.addEdge(edge.from, edge.to)
            newEdge.from = edge.from
            newEdge.to = edge.to
            newEdge.action = edge.action
            edges.add(newEdge)
            graph.setEdgeWeight(newEdge, edge._weight)
            Log.info("Adge was edded: $edge")
        }
    }

    fun init(executor: IExecutor) {
        this.executor = executor
    }

    fun initNavigations() {
        pages.sortByDescending { it.navOrder }
        pages.forEach {
            it.initNavigation()
            NavigationParser(it).parse()
        }
        blocks.forEach {
            it.initNavigation()
            NavigationParser(it).parse()
        }
    }

   private val sortedPages: Collection<INavigableDetermination> by lazy {
       pages.sortByDescending { p ->
           val ann = p.javaClass.annotations.find { it is UI.Nav.Order } as? UI.Nav.Order
           ann?.type?.value ?: UI.Nav.Order.DEFAULT
       }
       pages
   }

    open val currentPage: INavigableDetermination
        get() {
            return Log.action(Messages.Navigator.curPage) {

                val res = sortedPages.find {
                    Log.action(Messages.Navigator.checkPageIteration(it as Page)) {
                        executor.isAllPresent(it.determination.exist)
                                && (it.determination.notExist.isEmpty() ||
                                             !executor.isAllPresent(it.determination.notExist))
                    }
                }
                if(res != null) {
                    Log.result(Messages.Navigator.pageFound)
                    if(res is IPageCallback) {
                        if(res is Page) {
                            if(!firstTimeDetection.contains(res.name)) {
                                firstTimeDetection.add(res.name)
                                res.afterPageDetectedFirstTime()
                            } else {
                                res.afterPageDetected()
                            }
                        }

                    }
                    res
                } else {
                    Log.warning(Messages.Navigator.pageNotFound)
                    throw XPathQsException.CurrentPageNotFound()
                }
            }
        }

    fun findPath(from: INavigable, to: INavigable): GraphPath<INavigable, Edge>? {
        return shortestPath.getPath(from, to)
    }
}