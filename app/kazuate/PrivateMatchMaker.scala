package kazuate


import akka.actor._
trait PrivateMatch {

  //val out: ActorRef
  val self: ActorRef

  def pmSetPrivateMatch {
    //Util.server ! Entry(self)
  }

  def pmStop {

  }

  //def getRoomList {}
  def pmReceive(msg: PrivateMatchMessage) {
  }

}
