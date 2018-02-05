package kazuate

import akka.actor._
import akka.stream.scaladsl.{Keep, Sink, Source, Flow}
import akka.stream.{Materializer, OverflowStrategy}

object KazuateActorFlow {
  def actorRef[In, Out](props: ActorRef => Props,
    actorName: String = "client",
    bufferSize: Int = 16,
    overflowStrategy: OverflowStrategy = OverflowStrategy.dropNew)(
      implicit factory: ActorRefFactory,
      mat: Materializer): Flow[In, Out, _] = {

    val (outActor, publisher) = Source.actorRef[Out](bufferSize, overflowStrategy)
      .toMat(Sink.asPublisher(false))(Keep.both).run()

    Flow.fromSinkAndSource(
      Sink.actorRef(factory.actorOf(Props(new Actor {
        val flowActor = context.watch(context.actorOf(props(outActor), "flowActor"))

        def receive = {
          case Status.Success(_) | Status.Failure(_) => flowActor ! PoisonPill
          case Terminated(_) => context.stop(self)
          case other => flowActor ! other
        }

        override def supervisorStrategy = OneForOneStrategy() {
          case _ => SupervisorStrategy.Stop
        }
    }), actorName), Status.Success(())),
      Source.fromPublisher(publisher)
    )
  }
}
