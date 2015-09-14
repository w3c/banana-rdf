package org.w3.banana.examples

import org.w3.banana._
import org.w3.banana.diesel._
import java.net.URL



/* declare your dependencies as a trait with all the modules you need
 */
trait SPARQLHttpEndPointDependencies
  extends RDFModule
  with RDFOpsModule
  with SparqlOpsModule
  with SparqlHttpModule

/* Here is an example doing some IO. Read below to see what's
 * happening.
 * 
 * As you can see, we never use Jena nor Sesame directly. The binding
 * is done later by providing the module implementation you
 * want. Hopefully, you'll have the same results :-)
 * 
 * To run this example from sbt:
 *   project examples
 *   run-main org.w3.banana.examples.SPARQLExampleWithJena
 */
trait SPARQLConsumer extends SPARQLHttpEndPointDependencies {

  import ops._
  import sparqlOps._
  import sparqlHttp.sparqlEngineSyntax._

  def sparqlOperation(): Unit = {

    /* gets a SparqlEngine out of a Sparql endpoint */

    val endpoint = new URL("http://dbpedia.org/sparql/")

    /* creates a Sparql Select query */

    val query = parseSelect("""
                                PREFIX ont: <http://dbpedia.org/ontology/>
                                SELECT DISTINCT ?language WHERE {
                                 ?language a ont:ProgrammingLanguage .
                                 ?language ont:influencedBy ?other .
                                 ?other ont:influencedBy ?language .
                                } LIMIT 100
                            """
    ).get

    /* executes the query */

    val answers: Rdf#Solutions = endpoint.executeSelect(query).get

    /* iterate through the solutions */

    val languages: Iterator[Rdf#URI] = answers.iterator map { row =>
      /* row is an Rdf#Solution, we can get an Rdf#Node from the variable name */
      /* both the #Rdf#Node projection and the transformation to Rdf#URI can fail in the Try type, hense the flatMap */
      row("language").get.as[Rdf#URI].get
    }

    println(languages.to[List])
  }

}

/* Here is how you instantiate your modules. Note that this is using
 * the default module implementations in banana-rdf but nothing is
 * preventing you from implementing your own or re-using part of
 * them. */

import org.w3.banana.jena.JenaModule
import org.w3.banana.sesame.SesameModule


case class SPARQLConsumerWithJena() extends SPARQLConsumer with JenaModule
case class SPARQLConsumerWithSesame() extends SPARQLConsumer with SesameModule

object SPARQLConsumerApp extends App {

  SPARQLConsumerWithJena().sparqlOperation()
  SPARQLConsumerWithSesame().sparqlOperation()

}