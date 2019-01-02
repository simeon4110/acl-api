package generators

import com.sonnets.sonnet.constants.TestConstants
import com.sonnets.sonnet.persistence.dtos.poetry.PoemDto
import com.sonnets.sonnet.persistence.models.base.{Author, Item}
import com.sonnets.sonnet.persistence.models.poetry.Poem
import utils.RandomDataGenerators

object PoemGenerator {
  def generate(): Poem = {
    val a: Author = AuthorGenerator.generate()
    val p: Poem = new Poem
    p.setAuthor(a)
    p.setTitle(RandomDataGenerators.randomString())
    p.setPublicationStmt(RandomDataGenerators.randomString())
    p.setSourceDesc(RandomDataGenerators.randomString())
    p.setPeriod(TestConstants.DEFAULT_PERIOD.getStringValue)
    p.setText(RandomDataGenerators.randomJavaStringList())
    p.setCategory(Item.Type.POEM.getStringValue)
    p
  }

  def generateDto(dto: PoemDto): PoemDto = {
    val a: Author = AuthorGenerator.generateWithId(RandomDataGenerators.randomInteger(100))
    dto.setAuthorId(a.getId.toString)
    dto.setTitle(RandomDataGenerators.randomString())
    dto.setPublicationYear(RandomDataGenerators.randomInteger(2000))
    dto.setPublicationStmt(RandomDataGenerators.randomString())
    dto.setSourceDesc(RandomDataGenerators.randomString())
    dto.setPeriod(TestConstants.DEFAULT_PERIOD.getStringValue)
    dto.setForm(RandomDataGenerators.randomString())
    dto.setText("line one\nline two\nline three\nline four")
    dto
  }

  def generateWithId(pId: Long, aId: Long): Poem = {
    val a = AuthorGenerator.generateWithId(aId)
    val p = generate(a)
    p.setId(pId)
    p
  }

  def generate(a: Author): Poem = {
    val p: Poem = new Poem
    p.setAuthor(a)
    p.setTitle(RandomDataGenerators.randomString())
    p.setPublicationStmt(RandomDataGenerators.randomString())
    p.setSourceDesc(RandomDataGenerators.randomString())
    p.setPeriod(TestConstants.DEFAULT_PERIOD.getStringValue)
    p.setText(RandomDataGenerators.randomJavaStringList())
    p.setCategory(Item.Type.POEM.getStringValue)
    p
  }
}
