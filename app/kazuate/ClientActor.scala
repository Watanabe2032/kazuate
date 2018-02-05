package kazuate

import akka.actor._

object ClientActorState extends Enumeration {
  type ClientActorState = Value
  val plane, rm, pm, playing, error = Value
}

class ClientActor(out: ActorRef) extends Actor with RandomMatch with PrivateMatch {

  import ClientActorState._

  var state: ClientActorState = ClientActorState.plane
  var peerRef: ActorRef = null
  var peerActor: ActorRef = null

  override def preStart {
    println(Util.prefixFA(self) + "new")
    //println(Util.prefixFA(self) + "new  "+self.path)
    //println(Util.prefixFA(self) + "out  "+out.path)
  }

  override def postStop {
    println(Util.prefixFA(self) + "stop")
    terminate
  }

  def terminate {
    if(peerActor != null) peerActor ! OpponentClosed()
    state match {
      case rm => rmStop
      case pm => pmStop
      case _ => Util.doNothing
    }
  }

  def receive = {
    case s: String => {
      Util.stringToMessage(s) match {
        case msg: PeerMessage => peerActor ! msg
        case msg: Message => self ! msg
        case _ => Util.doNothing
      }
    }
    case msg: PeerMessage => out ! msg.data
    case msg: RandomMatchMessage => rmReceive(msg)
    case msg: PrivateMatchMessage => pmReceive(msg)
    case msg: ClientMessage => out ! msg.rawData
    case msg: ClientActorMessage => onClientMessage(msg)
    case _ => println(Util.prefixFA(self) + "receive other!")
  }


  private def onClientMessage(msg: ClientActorMessage) {
    msg match {
      case msg: StartRandomMatch => rmSetRandomMatch
      case msg: StartPrivateMatch => pmSetPrivateMatch
      case msg: Terminate => terminate
      case msg: FoundOpponent => onFoundOpponent(msg)
      case msg: PeerRef => onPeerRef(msg)
      case msg: OpponentClosed => out ! msg.rawData
      case msg: OpponentDisconnected => out ! msg.rawData
      case _ => println(Util.prefixFA(self) + "_")
    }
  }

  private def onFoundOpponent(fo: FoundOpponent) {
    println(Util.prefixFA(self) + "FoundOpponent("+Util.parentName(fo.actorRef)+")")
    if(peerActor != null) return
    peerActor = fo.actorRef
    out ! "FoundOpponent;"
    val resetTimer: Cancellable = Util.msecScheduleOnce(interval=1000, actorRef=self, message=GameStart(fo.isHost))
  }

  private def onPeerRef(pr: PeerRef) {
    if(peerRef != null) return
    peerRef = pr.actorRef
    println(Util.prefixFA(self) + "PeerRef("+Util.parentName(pr.actorRef)+")")
  }



}
