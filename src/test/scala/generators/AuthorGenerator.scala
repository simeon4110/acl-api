package generators

import com.sonnets.sonnet.persistence.models.base.Author
import utils.RandomDataGenerators

object AuthorGenerator {
  def generateWithId(id: Long): Author = {
    val a: Author = generate()
    a.setId(id)
    a
  }

  def generate(): Author = {
    val a: Author = new Author
    a.setFirstName(RandomDataGenerators.randomString())
    a.setLastName(RandomDataGenerators.randomString())
    a
  }
}
