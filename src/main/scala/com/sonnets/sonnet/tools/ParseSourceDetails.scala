package com.sonnets.sonnet.tools

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
    obj.setDateOfPublication(convertDate(dto.getDateOfPublication))
    obj.setShortTitle(dto.getShortTitle)
    obj.setSourceTitle(dto.getSourceTitle)
    obj.setUrl(dto.getUrl)
    obj.setDateOfAccess(convertDate(dto.getDateOfAccess))
    obj.setJournalName(dto.getJournalName)
    obj.setDOI(dto.getDOI)
    obj.setJournalVolume(dto.getJournalVolume)
    obj.setJournalIssue(dto.getJournalIssue)
    obj.setJournalPageRange(dto.getJournalPageRange)
    obj.setJournalAbbr(dto.getJournalAbbr)

    // Set default language to English
    if (dto.getLanguage == null) obj.setLanguage("English")
    else obj.setLanguage(dto.getLanguage)

    // Set default public domain to false.
    if (dto.getPublicDomain == null) obj.setPublicDomain(false)
    else obj.setPublicDomain(dto.getPublicDomain)

    obj.setPageRange(dto.getPageRange)
    obj
  }

  /**
    * Converts a string date into a java Date obj.
    *
    * @param dateStr the string to parse.
    * @return a java Date obj.
    */
  def convertDate(dateStr: String): java.util.Date = {
    if (dateStr == null || dateStr == "") { // Prevent null strings from causing parse error.
      return null
    }

    var pattern: String = ""
    if (dateStr.length == 10) {
      pattern = "yyyy-MM-dd"
    }
    if (dateStr.length == 8) {
      pattern = "yyyyMMdd"
    } else {
      pattern = "yyyy"
    }

    new java.text.SimpleDateFormat(pattern).parse(dateStr)
  }
}
