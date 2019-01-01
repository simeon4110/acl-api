package tools

import java.net.URL

import scala.io.Source

/**
  * Scala object for parsing text, arrays of text, and removing stop words.
  *
  * @author Josh Harkema
  */
object FormatTools {
  val filename: URL = getClass.getResource("/STOPWORDS.txt")
  var STOPWORDS: Array[String] = _

  {
    STOPWORDS = Source.fromFile(filename.getPath).getLines.toArray
  }

  /**
    * Converts an array of strings to a single string.
    *
    * @param input the array of strings to convert.
    */
  def arrayToString(input: Array[String]): String = {
    var output: String = ""
    for (line <- input) {
      output += line + " "
    }
    output
  }

  /**
    * @param input            the string to tokenize.
    * @param stripPunctuation should all punctuation be removed?
    * @param lowerCase        should all text be converted to lower case?
    * @param stripStopWords   should stop words be removed?
    * @return a String[] of word tokens.
    */
  def tokenizeWords(input: String, stripPunctuation: Boolean, lowerCase: Boolean,
                    stripStopWords: Boolean): Array[String] = {
    var text: String = ""
    if (stripPunctuation) {
      text = removePunctuation(input)
    }
    if (lowerCase) {
      text = text.toLowerCase
    }
    val tokens: Array[String] = text.split(" ")
    if (stripStopWords) {
      tokens.filter(containsStopWords)
    }
    tokens
  }

  /**
    * @param input the text to strip all punctuation from.
    * @return a string with all punctuation removed.
    */
  def removePunctuation(input: String): String = {
    input
      .replace("-", " ")
      .replaceAll("""[\p{Punct}&&[^.]]""", "")
      .replaceAll("""[0-9]""", "")
      .replace("\n", " ")
      .replace("\\n", " ")
  }

  def containsStopWords(s: String): Boolean = {
    STOPWORDS contains s
  }
}
