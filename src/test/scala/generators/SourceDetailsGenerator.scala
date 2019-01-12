package generators

import java.util.Date

import com.sonnets.sonnet.persistence.models.base.Item

class SourceDetailsGenerator[T <: Item] {
  def addToItem(i: T): T = {
    i.setPlaceOfPublication("test place")
    i.setPublisher("test publisher")
    i.setDateOfPublication(new Date())
    i
  }
}
