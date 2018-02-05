package kazuate

import akka.actor._

import akka.pattern.ask
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{ Try, Success, Failure }
import akka.util.Timeout

trait RandomMatch {

  val self: ActorRef
  //println("RMM start ("+Util.parentName(self)+")")
  var isOffer: Boolean = false
  var resetTimer: Cancellable = null

  def rmSetRandomMatch {
    Util.server ! Entry(self)
  }

  def rmReset {
    println(Util.prefixFA(self) + "rmm reset")
    Util.server ! EntryCancellation(self)
    isOffer = false
    Thread.sleep(500)
    rmSetRandomMatch
  }

  def rmStop {
    Util.server ! EntryCancellation(self)
  }

  private def sendOffer(actorRef: ActorRef) {
    resetTimer = Util.msecScheduleOnce(interval=300, actorRef=self, message=RandomMatchReset())
    actorRef ! Offer(self)
  }

  def rmReceive(msg: RandomMatchMessage) {
    msg match {
      case msg: EntryReply => onEntryReply(msg)
      case msg: Offer => onOffer(msg)
      case msg: OfferReply => onOfferReply(msg)
      case msg: RandomMatchReset => rmReset
      case _ => println(Util.prefixFA(self) + "rm receive _")
    }
  }


  private def onEntryReply(er: EntryReply) {
    println(Util.prefixFA(self) + "rEntryReply("+Util.parentName(er.actorRef)+")")
    if(isOffer) return
    er.actorRef match {
      case `self` => self ! WaitingOpponent()
      case _      => {
        isOffer = true
        sendOffer(er.actorRef)
      }
    }
  }

  private def onOffer(o: Offer) {
    println(Util.prefixFA(self)+"rOffer("+Util.parentName(o.actorRef)+")")
    isOffer match {
      case true  => o.actorRef ! OfferReply(self, false)
      case false => {
        isOffer = true
        o.actorRef ! OfferReply(self, true)
        self ! FoundOpponent(o.actorRef, true)
      }
    }
  }

  private def onOfferReply(or: OfferReply) {
    println(Util.prefixFA(self)+"rOfferReply("+Util.parentName(or.actorRef)+")")
    if(isOffer == false) return
    if(resetTimer != null) resetTimer.cancel()
    or.result match {
      case true  => self ! FoundOpponent(or.actorRef, false)
      case false => rmReset
    }
  }


}
