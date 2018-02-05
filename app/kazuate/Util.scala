package kazuate

import akka.actor._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

//object util extends util
object Util extends Util

class Util {
//class util extends Actor {
  val prefixWidth = 5

  var kazuateSystem: ActorSystem = null
  def setKazuateSystem(actorSystem: ActorSystem) {kazuateSystem = actorSystem}
  def serverPath = "akka://kazuate/user/server"
  def server: ActorSelection = kazuateSystem.actorSelection(serverPath)
  def roomListPath = "akka://kazuate/user/roomList"
  def roomList: ActorSelection = kazuateSystem.actorSelection(roomListPath)

  def parentName(actorRef: ActorRef): String = parentName(actorRef.path)
  def parentName(actorPath: ActorPath): String = {
    actorPath.elements.takeRight(2).head
  }

  def prefix(name: String): String = {
    name.padTo(prefixWidth, ' ') + ": "
  }

  def prefixFA(actorRef: ActorRef): String = {
    "fa" + parentName(actorRef).padTo(prefixWidth-2, ' ') + ": "
  }

  def doNothing {}
  def booleanToString(bool: Boolean): String = if(bool) "1" else "0"

  def secSchedule(system: ActorSystem = kazuateSystem,
                      interval: Int,
                      actorRef: ActorRef,
                      message: Message): Cancellable = {
    system.scheduler.schedule(0 seconds, interval seconds, actorRef, message)
  }

  def msecScheduleOnce(system: ActorSystem = kazuateSystem,
            interval: Int,
            actorRef: ActorRef,
            message: Message): Cancellable = {
    system.scheduler.scheduleOnce(interval milliseconds, actorRef, message)
  }


  def stringToMessage(text: String): Message = {
    val elements = text.split(";")
    val commandLength = text.indexOf(";")
    val command = text.slice(0, commandLength)
    val data = text.drop(commandLength+1)

    command match {
      //case "Peer" => PeerMessage(data)
      case "StartRandomMatch" => StartRandomMatch()
      case "StartPrivateMatch" => StartPrivateMatch()
      //case "CancelMatch" => CancelMatch()
      case "Terminate" => Terminate()
      case _ => PeerMessage(text)
    }
  }




}
