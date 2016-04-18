package ru.mobak.lm2.statistics.http

import akka.actor.{Actor, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging

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
  implicit val system = ActorSystem("lm2-statistics-http")
  implicit val materializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.dispatcher

  val host = "localhost"
  val port = 8080

  val route =
    path("hello") {
      get {
        //        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
        complete(StatusCodes.OK, "<h1>Say hello to akka-http</h1>")
      }
    }

  val bindingFuture = Http().bindAndHandle(route, host, port)
  bindingFuture.onComplete {
    case Failure(ex) =>
      logger.error(s"Failed to bind to $host:$port!", ex)
    case _ =>
      logger.info(s"Server online at http://$host:$port/")
  }

  Await.result(system.whenTerminated, Duration.Inf)
}
