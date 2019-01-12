package tools

import javax.persistence.StoredProcedureQuery

import scala.collection.JavaConverters._

/**
  * Handles loading of query results from stored procedures.
  *
  * @author Josh Harkema
  */
object QueryHandler {
  def queryToString(query: StoredProcedureQuery): String = {
    var text: String = ""
    query.execute()
    for (result <- query.getResultList.asScala) {
      text += result
    }
    text
  }
}
