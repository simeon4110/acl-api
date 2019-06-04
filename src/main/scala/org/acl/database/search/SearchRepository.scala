package org.acl.database.search

import java.nio.file.Paths

import org.acl.database.config.LuceneConfig
import org.acl.database.persistence.models.TypeConstants
import org.acl.database.persistence.models.base.{Item, Poem, Section, ShortStory}
import org.acl.database.services.search.SearchConstants
import org.apache.lucene.document.{Document, Field, StringField, TextField}
import org.apache.lucene.index._
import org.apache.lucene.search.{IndexSearcher, TermQuery}
import org.apache.lucene.store.FSDirectory

import scala.collection.JavaConverters._

/**
  * Repository for handling com.sonnets.sonnet.search documents.
  *
  * @author Josh Harkema
  */
object SearchRepository {
  val retryAttempts = 3

  /**
    * Attempts to run a function n times before rethrowing the exception and failing. This is 100% thread
    * blocking.
    *
    * @param n  number of times to retry.
    * @param fn the function to run.
    * @tparam T the fn return type.
    * @return the fn results.
    */
  def retry[T](n: Int)(fn: => T): T = {
    try {
      fn
    } catch {
      case e: Throwable =>
        if (n > 1) {
          Thread.sleep(1000) // hold for a second.
          retry(n - 1)(fn)
        }
        else throw e
    }
  }

  /**
    * Adds a new document to an existing Lucene index.
    *
    * @param document the document to add.
    * @param itemType the item type of the document.
    */
  def addDocument(document: Document, itemType: String): Unit = {
    retry(retryAttempts) {
      val writer = getWriter(itemType)
      writer.addDocument(document)
      writer.commit()
      writer.close()
    }
  }

  /**
    * THIS FUNCTION DELETES THE ENTIRE INDEX FOR itemType WHEN CALLED.
    *
    * @param documents the list of documents to add.
    * @param itemType  the type of item the documents are comprised of.
    */
  def addDocuments(documents: java.util.List[Document], itemType: String): Unit = {
    retry(retryAttempts) {
      val writer = getWriter(itemType)
      writer.deleteAll()
      documents.asScala.foreach(writer.addDocument(_))
      writer.commit()
      writer.close()
    }
  }

  /**
    * Removes a document from an existing Lucene index.
    *
    * @param docId    the id of the document to delete.
    * @param itemType the type of the item to delete.
    */
  def deleteDocument(docId: String, itemType: String): Unit = {
    retry(retryAttempts) {
      val writer = getWriter(itemType)
      writer.deleteDocuments(new Term(SearchConstants.ID, docId))
      writer.commit()
      writer.close()
    }
  }

  /**
    * Updates an existing poem.
    *
    * @param poem the new poem data.
    */
  def updatePoem(poem: Poem): Unit = {
    val toUpdate = parseCommonFields(getDocument(poem.getId.toString, TypeConstants.POEM), poem)
    toUpdate.add(new TextField(SearchConstants.POEM_FORM, poem.getForm, Field.Store.YES))
    toUpdate.add(new TextField(SearchConstants.TOPIC_MODEL, poem.getTopicModel.toString, Field.Store.YES))
    toUpdate.removeField(SearchConstants.TEXT)
    toUpdate.add(LuceneConfig.getTextField(String.join(SearchConstants.LINE_DELIMITER_POEM, poem.getText)))
    updateDocument(poem.getId.toString, toUpdate, TypeConstants.POEM)
  }

  /**
    * Updates and existing section.
    *
    * @param section the new section data.
    */
  def updateSection(section: Section): Unit = {
    val toUpdate = parseCommonFields(getDocument(section.getId.toString, TypeConstants.SECTION), section)
    toUpdate.add(new StringField(SearchConstants.PARENT_ID, section.getParentId.toString, Field.Store.YES))
    toUpdate.add(new TextField(SearchConstants.PARENT_TITLE, section.getParentTitle, Field.Store.YES))
    toUpdate.add(LuceneConfig.getTextField(section.getText))
    updateDocument(section.getId.toString, toUpdate, TypeConstants.SECTION)
  }

  private def getDocument(docId: String, docType: String): Document = {
    val reader = getReader(docType)
    val searcher = new IndexSearcher(reader)
    val docs = searcher.search(new TermQuery(new Term(SearchConstants.ID, docId)), 1)
    searcher.doc(docs.scoreDocs(0).doc)
  }

  /**
    * @param itemType the index type.
    * @return an open IndexReader.
    */
  def getReader(itemType: String): IndexReader = {
    DirectoryReader.open(FSDirectory.open(Paths.get(getPath(itemType))))
  }

  private def updateDocument(docId: String, document: Document, itemType: String): Unit = {
    retry(retryAttempts) {
      val writer = getWriter(itemType)
      writer.updateDocument(new Term(SearchConstants.ID, docId), document)
      writer.commit()
      writer.close()
    }
  }

  private def getWriter(itemType: String): IndexWriter = {
    new IndexWriter(
      FSDirectory.open(Paths.get(getPath(itemType))),
      new IndexWriterConfig(LuceneConfig.getAnalyzer)
    )
  }

  private def getPath(itemType: String): String = {
    "%s/%s".format(SearchConstants.DOCS_PATH, itemType)
  }

  /**
    * Handles fields that are common to all objects that extend Item.
    *
    * @param document the document to add the fields to.
    * @param item     the item with the data to add.
    * @return a document with the common fields parsed.
    */
  def parseCommonFields(document: Document, item: Item): Document = {
    document.add(new StringField(SearchConstants.ID, item.getId.toString, Field.Store.YES))
    document.add(new TextField(SearchConstants.TITLE, item.getTitle, Field.Store.YES))
    document.add(new TextField(SearchConstants.CATEGORY, item.getCategory, Field.Store.YES))
    document.add(new TextField(SearchConstants.AUTHOR_FIRST_NAME, item.getAuthor.getFirstName, Field.Store.YES))
    document.add(new TextField(SearchConstants.AUTHOR_LAST_NAME, item.getAuthor.getLastName, Field.Store.YES))
    if (item.getPeriod != null) { // Some items don't have periods.
      document.add(new StringField(SearchConstants.PERIOD, item.getPeriod, Field.Store.YES))
    }
    if (item.getPublicDomain != null) { // Some items do not have this toggled.
      document.add(new TextField(SearchConstants.IS_PUBLIC, item.getPublicDomain.toString, Field.Store.YES))
    }
    document
  }

  /**
    * Updates and existing short story.
    *
    * @param shortStory the new short story data.
    */
  def updateShortStory(shortStory: ShortStory): Unit = {
    val toUpdate = parseCommonFields(getDocument(shortStory.getId.toString, TypeConstants.SHORT_STORY), shortStory)
    toUpdate.removeField(SearchConstants.TEXT)
    toUpdate.add(LuceneConfig.getTextField(shortStory.getText))
    updateDocument(shortStory.getId.toString, toUpdate, TypeConstants.SHORT_STORY)
  }
}
