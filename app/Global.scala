import play.api._

import models._
import anorm._

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    import console.Actors._
    InitialData.insert()
    console.Actors.Scraper.startItAll
  }


  override def onStop(app: Application) {
      console.Actors.Scraper.endItAll
    }
}

/**
 * Initial set of data to be imported
 * in the sample application.
 */
object InitialData {

  def date(str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(str)

  def insert() = {

    if(User.findAll.isEmpty) {

      Seq(
        User("mrspeaker@gmail.com", "qwerty")
      ).foreach(User.create)

      Seq(
        Story(NotAssigned, "Steve Jobs is dead!!", 1, 100, Some(date("2011-11-15")), None),
        Story(NotAssigned, "Play 2.0 released", 2, 30, Some(date("2011-11-15")), None)
      ).foreach(Story.create)

    }

  }

}