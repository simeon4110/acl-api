package tools

import com.sonnets.sonnet.persistence.dtos.base.SourceDetailsDto
import com.sonnets.sonnet.persistence.models.base.Item

/**
  * Handler for parsing source details from a dto to an object.
  *
  * @tparam T the type of item to parse the details ONTO.
  * @tparam D the type of dto to get the details FROM.
  * @author Josh Harkema
  */
class ParseSourceDetails[T <: Item, D <: SourceDetailsDto] {
  def parse(obj: T, dto: D): T = {
    obj.setEdition(dto.getEdition)
    obj.setPlaceOfPublication(dto.getPlaceOfPublication)
    obj.setPublisher(dto.getPublisher)
    obj.setDateOfPublication(dto.getDateOfPublication)
    obj.setShortTitle(dto.getShortTitle)
    obj.setUrl(dto.getUrl)
    obj.setDateOfAccess(dto.getDateOfAccess)
    obj.setJournalName(dto.getJournalName)
    obj.setDOI(dto.getDOI)
    obj.setJournalVolume(dto.getJournalVolume)
    obj.setJournalIssue(dto.getJournalIssue)
    obj.setJournalPageRange(dto.getJournalPageRange)
    obj.setJournalAbbr(dto.getJournalAbbr)
    obj.setLanguage(dto.getLanguage)
    obj
  }
}
