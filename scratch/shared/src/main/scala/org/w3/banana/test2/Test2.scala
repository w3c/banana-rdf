//two imaginary RDF frameworks very differently implemented
//a J-framework and an O-framework
package J: //an O-framework
	enum Node:
		case BNode(n: Int)
		case Uri(u: String)
		case Literal(s: String)
	case class Triple(subj: Node, rel: Node.Uri, obj: Node)
	type Graph = Set[Triple]

package O:
	type Node = Long | java.net.URI | String
	type Triple = (Node, java.net.URI, Node)
	type Graph = Set[Triple]

package banana:
	trait Ops[R<:RDF]:
		def empty: RDF.Graph[R]
		def mkTriple(s: RDF.Node[R], r: RDF.URI[R], o: RDF.Node[R]): RDF.Triple[R]
		def mkURI(u: String): RDF.URI[R]
		def mkLiteral(lit: String): RDF.Literal[R]
		import scala.language.implicitConversions
		implicit def lit2Node(lit: RDF.Literal[R]): RDF.Node[R] = lit.asInstanceOf[RDF.Node[R]]
		implicit def uri2Node(uri: RDF.URI[R]): RDF.Node[R] = uri.asInstanceOf[RDF.Node[R]]

	trait RDF:
		rdf =>
		type R = rdf.type
		type Node
		type BNode <: Node
		type Literal <: Node
		type URI <: Node
		type Triple
		type Graph
		given ops: Ops[R]

	object RDF:
		type Graph[R <: RDF] = R match
			case GetGraph[g] => g
		type Triple[R <: RDF] = R match
			case GetTriple[t] => t
		type Node[R <: RDF] = R match
			case GetNode[n] => n
		type URI[R <: RDF]  = R match
			case GetURI[u] => u
		type Literal[R <: RDF] = R match
			case GetLiteral[l] => l

		type GetURI[U]  = RDF { type URI = U }
		type GetNode[N] = RDF { type Node = N }
		type GetLiteral[L] = RDF { type Literal = L }
		type GetTriple[T] = RDF { type Triple = T }
		type GetGraph[T] = RDF { type Graph = T }

	case class PG[Rdf <: RDF](node: RDF.Node[Rdf], graph: RDF.Graph[Rdf])
	object PG:
		def apply[R<:RDF](node: RDF.Node[R])(using ops: Ops[R]): PG[R] = new PG(node, ops.empty)

package banana.JRDF:

	import banana.RDF

	object JRdf extends banana.RDF:
		opaque type Node = J.Node
		opaque type BNode <: Node = J.Node.BNode
		opaque type Literal <: Node = J.Node.Literal
		opaque type URI <: Node = J.Node.Uri
		opaque type Triple = J.Triple
		opaque type Graph = Set[Triple]

		given ops: banana.Ops[JRdf.type] with
			def empty: banana.RDF.Graph[JRdf.type] = Set()
			def mkTriple(s: RDF.Node[R], r: RDF.URI[R], o: RDF.Node[R]): RDF.Triple[R] =
				J.Triple(s,r,o)
			def mkURI(u: String): RDF.URI[R] = J.Node.Uri(u)
			def mkLiteral(lit: String): RDF.Literal[R] = J.Node.Literal(lit)

package banana.ORDF:

	import banana.JRDF.JRdf.R
	import banana.RDF

	object ORdf extends banana.RDF:
		opaque type Node = Long | String | java.net.URI
		opaque type BNode <: Node = Long
		opaque type Literal <: Node = String
		opaque type URI <: Node = java.net.URI
		opaque type Triple = O.Triple
		opaque type Graph = O.Graph

		given ops: banana.Ops[ORdf.type] with
			def empty: banana.RDF.Graph[ORdf.type] = Set()
			def mkTriple(s: RDF.Node[R], r: RDF.URI[R], o: RDF.Node[R]): RDF.Triple[R] =
				(s,r,o)
			def mkURI(u: String): RDF.URI[R] = java.net.URI(u)
			def mkLiteral(lit: String): RDF.Literal[R] = lit


def main(args: Array[String]): Unit =
	println("jrdf="+simpleTest(using banana.JRDF.JRdf.ops))
	println("ordf="+simpleTest(using banana.ORDF.ORdf.ops))

def simpleTest[Rdf<:banana.RDF](using ops: banana.Ops[Rdf]) =
	import ops.*
	val bbl = ops.mkURI("https://bblfish.net/#i")
	val name = ops.mkURI("https://xmlns.com/foaf/0.1/name")
	val henry = ops.mkLiteral("Henry")
	ops.mkTriple(bbl,name,henry)

