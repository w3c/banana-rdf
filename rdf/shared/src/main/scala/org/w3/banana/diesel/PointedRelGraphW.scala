package org.w3.banana.diesel

import org.w3.banana.*
import org.w3.banana.RDF.*
//import org.w3.banana.binder._

import scala.util.*

/**
  * Functions needed to **construct** a rGraph.
  *
  * Note this may be better build as a typeClass, since exactly the same methods
  * make sense on an `rGraph`, as on a `Stream[rTriple]`, and perhaps even on a
  * Pointed DataSet (one may be adding triples to a graph in a dataset...).
  *
  * We remove the search capabilities we had in banana 0.8.x and should add those
  * to a different extension, typeclass as those pragmatically require the triples
  * to be indexed.
  * */
class PointedRelGraphW[Rdf <: RDF](val pointed: PointedRelGraph[Rdf]) extends AnyVal:

  import pointed.graph

  def a(clazz: rURI[Rdf])(using ops: Ops[Rdf]): PointedRelGraph[Rdf] =
    import ops.{given,*}
    val newGraph = graph + rTriple(pointed.pointer, rdfPfx.`type`, clazz)
    PointedRelGraph(pointed.pointer, newGraph)
  
  infix def --(p: rURI[Rdf]): PointedGraphPredicate[Rdf] =
    new PointedGraphPredicate[Rdf](pointed, p)

  infix def -<-(p: rURI[Rdf]): PredicatePointedRelGraph[Rdf] =
    new PredicatePointedRelGraph[Rdf](p, pointed)
