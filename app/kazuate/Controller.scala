package kazuate

import javax.inject.Inject
import akka.actor._
import akka.stream.Materializer
import akka.stream.ActorMaterializer
import play.api.mvc._
import play.api.libs.streams._
import play.inject.ApplicationLifecycle
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

//class Kazuate @Inject()(lifecycle: ApplicationLifecycle) extends Controller {
class Kazuate extends Controller {

  println("      @@@@@@@@@@ Kazuate start @@@@@@@@@@")
  implicit val system = ActorSystem("kazuate")
  implicit val materializer = ActorMaterializer()
  val server = system.actorOf(Props[ServerActor], "server")
  //val timer = system.actorOf(Props[TimerActor], "timer")
  val roomList = system.actorOf(Props[RoomList], "roomList")
  var clientCounter: Int = 0
  //RoomList.setActorSystem(system)
  Util.setKazuateSystem(system)

  //val app: ApplicationLifecycle = new Application
  //val timer = new Timer()

  private def actorName: String = {
    clientCounter = clientCounter + 1
    if(clientCounter > 1000) clientCounter = 0
    clientCounter.toString()
  }

  //def reception = WebSocket.accept[String, String] { request =>
  ////  server.flowActor(out)
//  }
/*
val interval = 3
system.scheduler.schedule(
    0 milliseconds,
    interval seconds,
    roomList,
    "update")*/

  //Util.secondsSchedule(system, 3, roomList, UpdateRoomList())
  //var test = Util.msecScheduleOnce(system, 5000, roomList, UpdateRoomList())
  //println(test)
  //test.cancel()
  //test = null

  def reception = WebSocket.accept[String, String] { request =>
    KazuateActorFlow.actorRef(out =>
      Props(classOf[ClientActor], out), actorName)
  }

  //override def destroy() {
  //  system.shutdown()
  //}

}
