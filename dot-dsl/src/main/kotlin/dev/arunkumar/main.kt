package dev.arunkumar

import dev.arunkumar.dot.dsl.directedGraph

fun main(ars: Array<String>) {
  val dotGraph = directedGraph("Arun") {

    graphAttributes {
      "rankdir" eq "LR"
      "labeljust" eq "l"
      "compound" eq true
      "label" eq "Hello"
    }

    cluster("Hello") {

      graphAttributes {
        "rankdir" eq "LR"
        "color" eq "blue"
        "label" eq "Cluster One"
      }

      nodeAttributes {
        "fillcolor" eq "black"
      }

      nodes("A", "B", "C")
    }

    cluster("World") {

      nodes("D", "E", "F")
    }

    ("A" link "B") {
      "style" eq "dashed"
      "taillabel" eq "subcomponent"
    }
    "E" {
      "shape" eq "component"
    } link "C" {
      "shape" eq "oval"
    }
    "A" link "C" {
      "color" eq "yellow"
    }
  }

  println(dotGraph.toString())
}