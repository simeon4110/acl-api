package org.acl.database.tools

import javax.persistence.StoredProcedureQuery

import scala.collection.JavaConverters._

/**
  * Handles loading of query results from stored procedures.
  *
  * @author Josh Harkema
  */
object QueryHandler {

  /**
    * This query parser handles the extra formatting of JSON strings.
    *
    * @param query      the query to run.
    * @param formatJSON true/false, it doesn't matter.
    * @return a JSON formatted string of the result.
    */
  def queryToString(query: StoredProcedureQuery, formatJSON: Boolean): String = {
    var result = queryToString(query)
    result = result.substring(0, result.length - 1)
    result = "[" + result + "]"
    result
  }

  /**
    * Collects a result list into a string.
    *
    * @param query the query to execute.
    * @return a string of the results.
    */
  def queryToString(query: StoredProcedureQuery): String = {
    var text: String = ""
    query.execute()
    for (result <- query.getResultList.asScala) {
      text += result
    }
    text
  }
}
