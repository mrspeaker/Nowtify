package models

import java.util.{Date}

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class Story(id: Pk[Long], name: String, rank: Long, rate: Long, added: Option[Date], updated: Option[Date])

object Story {
  
  // -- Parsers
  
  /**
   * Parse a Story from a ResultSet
   */
  val simple = {
    get[Pk[Long]]("story.id") ~/
    get[String]("story.name") ~/
    get[Long]("story.rank") ~/
    get[Long]("story.rate") ~/
    get[Option[Date]]("story.added") ~/
    get[Option[Date]]("story.updated") ^^ {
      case id ~ name ~ rank ~ rate ~ added ~ updated => Story(
        id, name, rank, rate, added, updated
      )
    }
  }
  
  // -- Queries
    /**
     * Retrieve all stories
     */
    def findAll: Seq[Story] = {
      DB.withConnection { implicit connection =>
        SQL("select * from story").as(Story.simple *)
      }
    }
  
  /**
   * Retrieve a Story from the id.
   */
  def findById(id: Long): Option[Story] = {
    DB.withConnection { implicit connection =>
      SQL("select * from story where id = {id}").on(
        'id -> id
      ).as(Story.simple ?)
    }
  }

  /**
   * Delete a story
   */
  def delete(id: Long) {
    DB.withConnection { implicit connection =>
      SQL("delete from story where id = {id}").on(
        'id -> id
      ).executeUpdate()
    }
  }

  /**
   * Create a Story.
   */
  def create(story: Story): Story = {
    DB.withConnection { implicit connection =>
      
      // Get the story id
      val id: Long = story.id.getOrElse {
        SQL("select next value for story_seq").as(scalar[Long])
      }
      
      SQL(
        """
          insert into story values (
            {id}, {name}, {rank}, {rate}, {added}, {updated}
          )
        """
      ).on(
        'id -> id,
        'name -> story.name,
        'rank -> story.rank,
        'rate -> story.rate,
        'added -> story.added,
        'updated -> story.updated
      ).executeUpdate()
      
      story.copy(id = Id(id))
      
    }
  }
  
}