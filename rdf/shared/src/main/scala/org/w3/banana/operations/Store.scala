package org.w3.banana.operations

import org.w3.banana.RDF
import org.w3.banana.RDF.Statement as St

/**
 * trait for Factories to create store
 *
 * There is too much diversity in preferences for store creation to hope to
 * be able to create a uniform interface for all of them at this time.
 * Perhaps experience working with them will reveal some commonality.
 * So for the moment it is better to have a very simple trait that programmers
 * can pass as givens in their context.
 */
trait StoreFactory[Rdf <: RDF]:
	def makeStore(): RDF.Store[Rdf]
end StoreFactory

trait Store[Rdf<:RDF]:
	def apply()(using sf: StoreFactory[Rdf]): RDF.Store[Rdf] = sf.makeStore()

	extension (store: RDF.Store[Rdf])
		def add(qs: RDF.Quad[Rdf]*): store.type
		def remove(qs: RDF.Quad[Rdf]*): store.type

		def remove(
			s: St.Subject[Rdf] | RDF.NodeAny[Rdf],
			p: St.Relation[Rdf] | RDF.NodeAny[Rdf],
			o: St.Object[Rdf] | RDF.NodeAny[Rdf],
			g: St.Graph[Rdf] | RDF.NodeAny[Rdf]
		): store.type

		def find(
			s: St.Subject[Rdf] | RDF.NodeAny[Rdf],
			p: St.Relation[Rdf] | RDF.NodeAny[Rdf],
			o: St.Object[Rdf] | RDF.NodeAny[Rdf],
			g: St.Graph[Rdf] | RDF.NodeAny[Rdf]
		): Iterator[RDF.Quad[Rdf]]
end Store