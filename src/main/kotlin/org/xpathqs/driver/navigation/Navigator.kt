package org.xpathqs.driver.navigation

import org.jgrapht.GraphPath
import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.graph.AbstractBaseGraph
import org.jgrapht.graph.SimpleDirectedWeightedGraph
import org.jgrapht.graph.specifics.Specifics
import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.core.selector.base.hasAnnotation
import org.xpathqs.core.selector.block.Block
import org.xpathqs.core.selector.extensions.rootParent
import org.xpathqs.driver.constants.Messages
import org.xpathqs.driver.exceptions.XPathQsException
import org.xpathqs.driver.executor.CachedExecutor
import org.xpathqs.driver.executor.IExecutor
import org.xpathqs.driver.log.Log
import org.xpathqs.driver.navigation.annotations.UI
import org.xpathqs.driver.navigation.annotations.UI.Visibility.Companion.UNDEF_STATE
import org.xpathqs.driver.navigation.base.*
import org.xpathqs.driver.page.*
import org.xpathqs.driver.navigation.util.NavigationParser
import org.xpathqs.log.style.StyleFactory.keyword
import org.xpathqs.log.style.StyleFactory.selectorName
import java.time.Duration
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

open class Navigator : INavigator {
    private lateinit var executor: IExecutor
    private val pages = ArrayList<INavigableDetermination>()
    private val blocks = ArrayList<INavigable>()
    private val edges = ArrayList<Edge>()
    private val graph = SimpleDirectedWeightedGraph<NavWrapper, Edge>(
        Edge::class.java
    )
    private val shortestPath = DijkstraShortestPath(graph)

    private val firstTimeDetection = HashSet<String>()

    @OptIn(ExperimentalStdlibApi::class)
    fun register(page: INavigable) {
        page as ISelector
        if(!pages.contains(page)) {
            //Log.info(selectorName(page.name) + " was added to the " + keyword(this::class.simpleName!!))
            if(page is Page && page is INavigableDetermination) {
                pages.add(page)

                val ann = page::class.findAnnotations<UI.Nav.PathTo>().filter {
                    it.bySubmit != Block::class
                }
                if(ann.isNotEmpty()) {
                    ann.forEach {
                        val state = it.pageState
                        graph.addVertex(
                            NavWrapper.get(it.bySubmit.objectInstance as INavigableDetermination, state)
                        )
                    }
                }
            } else if(page is Block) {
                if(page.rootParent !is Page) {
                    blocks.add(page)
                }
            }

            graph.addVertex(
                NavWrapper.get(page)
            )
        }
    }

    fun getByName(pageName: String): Page? {
        return pages.firstOrNull {
            (it as? Page)?.name == pageName
        } as? Page
    }

    fun addEdge(edge: Edge) {
        //Log.info("Call addEdge to the: " + this)
        if(!edges.contains(edge)) {
            graph.addVertex(edge.from)
            graph.addVertex(edge.to)

            if(edge.from.state == UNDEF_STATE) {
                graph.vertexSet().filter {
                    it.nav == edge.from.nav
                }.forEach {
                    val newEdge = graph.addEdge(it, edge.to)
                    if(newEdge != null) {
                        newEdge.from =it
                        newEdge.to = edge.to
                        newEdge.action = edge.action

                        edges.add(newEdge)
                        graph.setEdgeWeight(newEdge, edge._weight)
                    } else {
                        println("newEdge is null")
                    }

                }
            } else {
                val newEdge = graph.addEdge(edge.from, edge.to)
                if(newEdge != null) {
                    newEdge.from = edge.from
                    newEdge.to = edge.to
                    newEdge.action = edge.action

                    edges.add(newEdge)
                    graph.setEdgeWeight(newEdge, edge._weight)
                } else {
                    println("New edge is null 2")
                }
            }
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
           val ann = (p as Block).annotations.find { it is UI.Nav.Order } as? UI.Nav.Order
           ann?.type?.value ?: UI.Nav.Order.DEFAULT
       }
       pages
   }

    override val currentPage: INavigableDetermination
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

    fun findPath(from: NavWrapper?, to: NavWrapper?): GraphPath<NavWrapper, Edge>? {
        if(to == null) return null
        return shortestPath.getPath(from, to)
    }

    override fun navigate(from: NavWrapper, to: NavWrapper) {
        if(from === to) return

        val navigations = findPath(from, to)
            ?: throw XPathQsException.NoNavigation()

        navigations.edgeList.forEach {
            if(it.action != null) {
                it.action!!()
                Thread.sleep(500)
                (executor as? NavExecutor)?.refreshCache()
            }
            (it.to.nav as? ILoadable)?.waitForLoad(Duration.ofSeconds(30))
            val cp = currentPage
            if((it.to.nav is INavigableDetermination && it.to.nav is Page) && it.to.nav != cp) {
                if((cp as? Block)?.hasAnnotation(UI.Nav.Autoclose::class) == true) {
                    //println("close page!")
                }
                throw Exception("Wrong page")
            }
        }
    }

}