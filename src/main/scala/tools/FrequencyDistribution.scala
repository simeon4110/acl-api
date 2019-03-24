package tools

import java.util

import com.sonnets.sonnet.wordtools.NLPTools

import scala.collection.JavaConverters._
import scala.collection.immutable.ListMap

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
    val lemmas: Array[String] = pipeline.getListOfLemmatizedWords(FormatTools.removePunctuation(input))
    lemmas.map(_.toLowerCase)
    lemmas.map(_.trim)

    // Remove list items that consist of a "'" or ".".
    val clean: Array[String] = lemmas.filterNot(x => x.equals("'") || x.equals("."))
    val out: Map[String, Int] = clean.groupBy(identity).map(x => (x._1, x._2.length))
    mapAsJavaMap(ListMap(out.toSeq.sortWith(_._2 > _._2): _*).slice(0, numberOfTerms))
  }
}
