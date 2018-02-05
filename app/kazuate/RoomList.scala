package kazuate

import akka.actor._
import play.api.db._
import java.sql._

//object RoomList extends RoomList

class RoomList extends Actor {
  val parentRoot = "akka://kazuate/user/"
  val ownName = "flowActor"
  //var system: ActorSystem = null
  var connection: Connection = null
  var readyMadeList = 1

  try {
    val driver = "com.mysql.jdbc.Driver"
    val url = "jdbc:mysql://localhost/KAZUATE?"
    val username = "user"
    val password = "password"
    Class.forName(driver)
    connection = DriverManager.getConnection(url, username, password)
  } catch {
    case e: Throwable => e.printStackTrace
  }

  def updateReadyMadeList {
    println("updateReadyMadeList")
  }

  def cleanupDB {}

  def receive = {
    case RoomListRequest(actorRef) => actorRef ! readyMadeList
    case msg: String => println(Util.prefix("RL")+"receive : "+msg)
    case msg: UpdateRoomList => updateReadyMadeList
    case msg: CleanupDB => cleanupDB
    case _ => println("RM receive _")
  }

}
