package console.Actors

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Actor._
import akka.actor.Scheduler
import java.util.concurrent.TimeUnit

import scala.util.parsing.combinator._

class HNActor(parser: ActorRef) extends Actor {
    def receive = {
        case "Fetch" => {
            play.api.WS.url("http://news.ycombinator.com/").get().map(response => {
                play.Logger.info(response.getStatusCode().toString())
                parser ! response.getResponseBody()
            })
        }
        case "lol" => play.Logger.info("oh my!")
    }
}

class HNParser extends Actor {
    def receive = {
        case m: String => {
            play.Logger.info(HNParser.parseItUp(m).toString())
            self reply "lol"
        }
    }
}

object HNParser {
	def parseItUp(html:String) = {
		val container = (((utils.HTML5Parser.loadString(html) \\ "table").head \ "tbody").head \ "tr").drop(2).head
        val story = ((container \\ "tbody").head \ "tr").grouped(3).toList.reverse.tail.reverse
        /*
        <tr>
            <td class="title" valign="top" align="right">1.</td>
            <td>
                <center>
                    <a href="vote?for=3276362&amp;dir=up&amp;whence=%6e%65%77%73" id="up_3276362">
                        <img hspace="2" vspace="3" border="0" src="http://ycombinator.com/images/grayarrow.gif"></img>
                    </a>
                    <span id="down_3276362"></span>
                </center>
            </td>
            <td class="title">
                <a href="http://www.technologyreview.com/blog/helloworld/27369/?ref=rss">
                    Microsoft Announces &quot;Kinect Accelerator&quot; to Turn Hacks into Businesses
                </a>
                <span class="comhead"> (technologyreview.com) </span>
            </td>
        </tr>
        <tr>
            <td colspan="2"></td>
            <td class="subtext">
                <span id="score_3276362">59 points</span> 
                by 
                <a href="user?id=wglb">wglb</a> 
                2 hours ago  | 
                <a href="item?id=3276362">10 comments</a>
            </td>
        </tr>
        <tr style="height:5px"></tr>
        */
        story.map { story =>
		    val score = ((story \ "td").drop(4) \ "span").head.text.trim.split(" ").head.toInt
		    val title = ((story \ "td").drop(2) \ "a").head.text.trim
		    val id = ((story \ "td").drop(4) \ "span").head.attribute("id").getOrElse("score_-1").toString.split("_").last.toInt
		    val time = ((story \ "td").drop(4) \ "_").drop(2).head.toString
            (id, title, score, time)
		}
	}
}

object Scraper {
    play.Logger.warn("go go gadgets")
    val parser = actorOf[HNParser].start()
    val hner = actorOf(new HNActor(parser)).start()
    //val future = hner ! "Fetch"
    
    Scheduler.schedule(hner, "Fetch", 1, 20, TimeUnit.SECONDS)
    
    def startItAll = "I'm alive!"
    def endItAll = {
        hner.stop()
        parser.stop()
    }
}