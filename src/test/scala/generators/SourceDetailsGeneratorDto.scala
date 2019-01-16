package generators

import com.sonnets.sonnet.persistence.dtos.base.SourceDetailsDto

class SourceDetailsGeneratorDto[T <: SourceDetailsDto] {
  def add(i: T): T = {
    i.setPlaceOfPublication("test place")
    i.setPublisher("test publisher")
    i.setDateOfPublication("2008")
    i
  }
}
