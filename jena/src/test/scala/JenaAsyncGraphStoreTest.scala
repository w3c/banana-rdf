package org.w3.banana.jena

import org.w3.banana._
import com.hp.hpl.jena.sparql.core._

class JenaAsyncGraphStoreTest() extends AsyncGraphStoreTest[Jena](
  JenaDiesel,
  JenaStore(DatasetGraphFactory.createMem()),
  JenaRDFXMLReader,
  JenaGraphIsomorphism)