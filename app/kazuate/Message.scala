package kazuate

import akka.actor._

// Base Message
class Message {
  val name: String = this.getClass().getSimpleName()
  def rawData: String = name + ";"
}

class ClientMessage extends Message
case class WaitingOpponent() extends ClientMessage
case class GameStart(isFirst: Boolean) extends ClientMessage {
  override def rawData: String = name + ";0:0:" + Util.booleanToString(isFirst)
}
case class OpponentClosed() extends ClientMessage
case class OpponentDisconnected() extends ClientMessage


case class PeerMessage(data: String) extends Message


// Message addressed to ClientActor
class ClientActorMessage extends Message
case class StartRandomMatch() extends ClientActorMessage
case class StartPrivateMatch() extends ClientActorMessage
case class FoundOpponent(actorRef: ActorRef, isHost: Boolean) extends ClientActorMessage
case class PeerRef(actorRef: ActorRef) extends ClientActorMessage
case class Terminate() extends ClientActorMessage

// for Random Match Making
class RandomMatchMessage extends Message
case class Entry(actorRef: ActorRef) extends RandomMatchMessage
case class EntryReply(actorRef: ActorRef) extends RandomMatchMessage
case class EntryCancellation(actorRef: ActorRef) extends RandomMatchMessage
case class Offer(actorRef: ActorRef) extends RandomMatchMessage
case class OfferReply(actorRef: ActorRef, result: Boolean) extends RandomMatchMessage
case class RandomMatchReset() extends RandomMatchMessage

// for Private Match Making
class PrivateMatchMessage extends Message
case class RoomListRequest(actorRef: ActorRef) extends PrivateMatchMessage
case class Invitation(actorRef: ActorRef) extends PrivateMatchMessage


class SystemMessage extends Message
case class UpdateRoomList() extends SystemMessage
case class CleanupDB() extends SystemMessage
case class LogMessage(text: String) extends SystemMessage{
  override def rawData: String = name + ";" + text
}




/*
class RMMMessage[T](val t: T) {
  def get: T = this.t
}
*/

//case class Message[T]
// 型パラメータ


// out : Actor[akka://kazuate/user/StreamSupervisor-5/flow-0-0-actorRefSource#1834226761]
