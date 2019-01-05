package utils

import scala.collection.JavaConverters.seqAsJavaList
import scala.util.Random

/**
  * Object for easy generation of random string and ints.
  *
  * @author Josh Harkema
  */
object RandomDataGenerators {
  private val DEFAULT_STRING_LENGTH: Int = 10
  private val DEFAULT_INTEGER_LIMIT: Int = 20

  def randomString(): String = Random.alphanumeric.take(DEFAULT_STRING_LENGTH).mkString("")

  def randomInteger(limit: Int): Int = Random.nextInt(limit)

  def randomJavaStringList(): java.util.List[String] = {
    seqAsJavaList(
      Array(
        randomString(randomInteger()),
        randomString(randomInteger()),
        randomString(randomInteger()))
        .toSeq)
  }

  def randomString(length: Int): String = Random.alphanumeric.take(length).mkString("")

  def randomInteger(): Int = Random.nextInt(DEFAULT_INTEGER_LIMIT)
}
