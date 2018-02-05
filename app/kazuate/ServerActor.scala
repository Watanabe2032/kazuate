package kazuate

import akka.actor._


class ServerActor extends Actor {

  var clientCounter: Int = 0
  var formerActor: ActorRef = null
  //val roomList = context.actorOf(Props[RoomList], "roomList")
  //val testActor = context.actorOf(Props[TestActor], "test")
  override def preStart {
    //println("new server : " + self)
    println(Util.prefix("SV") + "new")
  }
  override def postStop {
    println(Util.prefix("SV") + "stop")
  }

  def actorName: String = {
    clientCounter = clientCounter + 1
    if(clientCounter > 1000) clientCounter = 0
    clientCounter.toString()
  }

  /*def flowActor(out: ActorRef) = {
    KazuateActorFlow.actorRef(out =>
      Props(classOf[ClientActor], out), actorName)
  }*/


  def receive = {
    case Entry(actorRef: ActorRef) => receiveEntry(actorRef)
    case EntryCancellation(actorRef: ActorRef) => receiveEntryCancellation(actorRef)
    case _ => println(Util.prefix("SV") + "receive _")
  }

  private def receiveEntry(actorRef: ActorRef) {
    val str = Util.prefix("SV") + "rEntry("+Util.parentName(actorRef)+") match "
    formerActor match {
      case `actorRef` => {
        println(str + "same")
        actorRef ! EntryReply(actorRef)
      }
      case null => {
        println(str + "null")
        actorRef ! EntryReply(actorRef)
        formerActor = actorRef
      }
      case _ => {
        println(str + "other")
        actorRef ! EntryReply(formerActor)
        formerActor = null
      }
    }
  }

  def receiveEntryCancellation(actorRef: ActorRef) {
    println(Util.prefix("SV") + "rEntryCancellation(" + Util.parentName(actorRef) + ")")
    if(formerActor == actorRef) formerActor = null
  }



}
