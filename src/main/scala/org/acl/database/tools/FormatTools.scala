package org.acl.database.tools

import java.net.URL

import scala.io.Source

/**
  * Scala object for parsing text, arrays of text, and removing stop words.
  *
  * @author Josh Harkema
  */
object FormatTools {
  val filename: URL = getClass.getResource("/STOPWORDS.txt")
  var STOP_WORDS: Array[String] = _

  {
    val fileSource = Source.fromFile(filename.getPath)
    STOP_WORDS = fileSource.getLines.toArray
    STOP_WORDS.map(_.toLowerCase)
    STOP_WORDS.map(_.trim)
    fileSource.close()
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
    output.trim
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
    if (stripPunctuation) text = removePunctuation(input)
    if (lowerCase) text = text.toLowerCase
    val tokens: Array[String] = text.split(" ")
    if (stripStopWords) tokens.filter(containsStopWords)
    tokens
  }

  /**
    * @param input the text to strip all punctuation from.
    * @return a string with all punctuation removed.
    */
  def removePunctuation(input: String): String = {
    input
      .replaceAll("""[\p{Punct}&&[^.]]""", "")
      .replaceAll("'", "")
      .replaceAll("""[0-9]""", "")
      .replace("\n", " ")
      .replace("\\n", " ")
  }

  def containsStopWords(s: String): Boolean = {
    STOP_WORDS contains s
  }

  def getStopWords: Array[String] = {
    STOP_WORDS
  }

  /**
    * Replace _'s with spaces.
    *
    * @param s the string to replace _ in.
    * @return a string without _'s.
    */
  def parseParam(s: String): String = {
    s.replace("_", " ")
  }
}
