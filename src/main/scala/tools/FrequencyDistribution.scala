package tools

import java.util

import com.sonnets.sonnet.wordtools.NLPTools

import scala.collection.JavaConverters._

/**
  * Functional frequency distribution.
  *
  * @author Josh Harkema
  */
object FrequencyDistribution {
  val pipeline: NLPTools = NLPTools.getInstance()

  /**
    * Runs a frequency distribution on an input string of words. Lemmatizes the words before running
    * the frequency distribution.
    *
    * @param input a string of words.
    * @return a java Map of the words.
    */
  def getFrequencyDistribution(input: String, numberOfTerms: Int): util.Map[String, Int] = {
    val lemmas: Array[String] = pipeline.getListOfLemmatizedWords(input)
    lemmas.map(_.toLowerCase)
    lemmas.map(_.trim)

    // Remove all list items that are only punctuation.
    val clean: Array[String] = lemmas.filterNot(x => x.contains("""[\p{Punct}&&[^.]]"""))
    val out: Map[String, Int] = clean.groupBy(identity).map(x => (x._1, x._2.length))
    mapAsJavaMap(out.slice(0, numberOfTerms))
  }
}
