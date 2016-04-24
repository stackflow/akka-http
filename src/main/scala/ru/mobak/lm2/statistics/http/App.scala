package ru.mobak.lm2.statistics.http

import akka.actor.{Actor, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import org.joda.time.{LocalDate, LocalDateTime}
import ru.mobak.lm2.statistics.http.net.HttpAssembly
import ru.mobak.lm2.statistics.http.rating.Rating
import scalikejdbc.config._

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.Failure


class HelloActor extends Actor {
  def receive = {
    case "hello" => println("hello!")
    case _ => println("huh?")
  }
}

object App extends App with LazyLogging {
  val config = ConfigFactory.load()

  implicit val system = ActorSystem(config.getString("app.name"))
  implicit val materializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.dispatcher

  DBsWithEnv("app").setupAll()

  val host = "localhost"
  val port = 8089

  val route =
    path("hello") {
      get {
        //        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
        complete(StatusCodes.OK, "<h1>Say hello to akka-http</h1>")
      }
    }

  val bindingFuture = Http().bindAndHandle(HttpAssembly.route, host, port)
  bindingFuture.onComplete {
    case Failure(ex) =>
      logger.error(s"Failed to bind to $host:$port!", ex)
    case _ =>
      logger.info(s"Server online at http://$host:$port/")
  }

  val dateTime = LocalDate.now()
  val startOfWeek = dateTime.weekOfWeekyear().roundFloorCopy()
  val endOfWeek = dateTime.weekOfWeekyear().roundCeilingCopy()

  logger.info(s"DateTime: $dateTime")
  logger.info(s"StartOfWeek: $startOfWeek")
  logger.info(s"EndOfWeek: $endOfWeek")

//  Rating.membersByLevel(20) foreach { m =>
//    logger.info(s"${m.name}")
//  }

  Await.result(system.whenTerminated, Duration.Inf)
}
