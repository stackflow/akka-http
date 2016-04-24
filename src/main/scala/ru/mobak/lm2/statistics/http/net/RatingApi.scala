package ru.mobak.lm2.statistics.http.net

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._

trait RatingApi {
  lazy val ratingRoute =
    pathPrefix("rating") {
      path("level") {
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
        }
      }
    }
}
