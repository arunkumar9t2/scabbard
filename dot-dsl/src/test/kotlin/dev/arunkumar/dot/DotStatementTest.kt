package dev.arunkumar.dot

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DotStatementTest {

  private fun dotStatement() = DotStatement("graph")

  private fun node() = DotNode("dot")

  @Test
  fun `test base is rendered correctly when there are no attributes`() {
    val statement = dotStatement()
    assertThat(statement.toString()).contains("graph$NEWLN")
  }

  @Test
  fun `test attributes are rendered correctly after base`() {
    val statement = dotStatement().apply {
      "label" eq "dot"
      "color" eq "yellow"
      "compound" eq true
    }
    assertThat(statement.toString()).contains("graph [label=\"dot\", color=\"yellow\", compound=\"true\"]$NEWLN")
  }

  @Test
  fun `test attributes are rendered correctly`() {
    val statement = dotStatement().apply {
      addAttribute("label", "dot")
    }
    assertThat(statement.toString()).contains("graph [label=\"dot\"]$NEWLN")
  }

  @Test
  fun `test node name is wrapped with quotes`() {
    val node = node()
    assertThat(node.toString()).contains("\"dot\"$NEWLN")
  }

  @Test
  fun `test node attributes are rendered correctly`() {
    val node = node().apply {
      addAttribute("label", "dot")
    }
    assertThat(node.toString()).contains("\"dot\" [label=\"dot\"]$NEWLN")
  }
}
