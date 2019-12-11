package dev.arunkumar.dot

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.io.PrintWriter
import java.io.StringWriter

class DotGraphTest {
    private fun dotGraph() = DotGraph("graph")

    @Test
    fun `test empty dot graph is rendered correctly`() {
        val dotGraph = dotGraph()
        assertThat(dotGraph.toString()).contains("graph {\r\n}\r\n")
    }

    @Test
    fun `test compound statements are rendered correctly`() {
        val dotGraph = dotGraph().apply {
            DotStatement("graph").let(::add)
            DotNode("some node").let(::add)
            DotEdge("some node", "another node").let(::add)
        }
        assertThat(dotGraph.toString()).contains(
            "graph {\r\n" +
                    "  graph\r\n" +
                    "  \"some node\"\r\n" +
                    "  \"some node\" -- \"another node\"\r\n" +
                    "}\r\n"
        )
    }

    @Test
    fun `test statement indentation is applied and propogated correctly`() {
        val dotGraph = dotGraph().apply {
            DotGraph("subgraph ${"some graph".quote()}").let(::add)
        }
        val output = StringWriter().also { dotGraph.write(0, PrintWriter(it)) }.toString()
        assertThat(output).contains(
            "graph {\r\n" +
                    "  subgraph \"some graph\" {\r\n" +
                    "  }\r\n" +
                    "}\r\n"
        )
    }
}