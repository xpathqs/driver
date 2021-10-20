package org.xpathqs.driver.navigator

import org.jgrapht.Graph
import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.graph.*
import org.junit.jupiter.api.Test
import org.xpathqs.driver.constants.Messages
import org.xpathqs.driver.navigation.Navigator


interface Base {
    var baseName: String
}

interface House : Base {
    var houseName: String
    var t: String

    fun print() {
        println(houseName)
    }
}

interface Car : Base {
    var carName: String
    var t: String

    fun print() {
        println(carName)
    }
}

interface CarHouse : House, Car {
    var test2: String

    override fun print() {
        super<House>.print()
    }
}

class Imlp : CarHouse {
    override var test2: String = ""
    override var houseName: String = ""
    override var t: String = ""
    override var baseName: String = ""
    override var carName: String = ""

}

class GraphTest {

    @Test
    fun test1() {
        val f = Navigator::initNavigations

        f
        val g = SimpleDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge::class.java)

        val v1 = "v1"
        val v2 = "v2"
        val v3 = "v3"
        val v4 = "v4"

        // add the vertices

        // add the vertices
        g.addVertex(v1)
        g.addVertex(v2)
        g.addVertex(v3)
        g.addVertex(v4)

        val e1 = g.addEdge(v1, v2)
        g.setEdgeWeight(e1, 1.0)

        val e2 = g.addEdge(v2, v3)
        g.setEdgeWeight(e2, 1.0)

        val e3 = g.addEdge(v3, v4)
        g.setEdgeWeight(e3, 1.0)

        val e4 = g.addEdge(v4, v1)
        g.setEdgeWeight(e4, 1.0)

        val e5 = g.addEdge(v1, v4)
        g.setEdgeWeight(e5, 10.0)

        val path = DijkstraShortestPath(g)
        val paths = path.getPath("v1", "v4")

        println(paths)
    }
}