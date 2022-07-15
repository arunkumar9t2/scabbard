/*
 * Copyright 2022 Arunkumar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.arunkumar.dot

import dev.arunkumar.dot.EdgeOp.DIRECTED
import dev.arunkumar.dot.EdgeOp.UNDIRECTED

enum class EdgeOp(val op: String) {
  UNDIRECTED(" -- "),
  DIRECTED(" -> ")
}

class DirectedDotEdge(
  override val leftNode: Any,
  override val rightNode: Any
) : DotEdge(leftNode, rightNode, DIRECTED)

open class DotEdge(
  open val leftNode: Any,
  open val rightNode: Any,
  private val edgeOp: EdgeOp = UNDIRECTED
) : DotStatement(leftNode.quote() + edgeOp.op + rightNode.quote())
