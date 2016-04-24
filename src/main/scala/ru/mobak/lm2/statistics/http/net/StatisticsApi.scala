package ru.mobak.lm2.statistics.http.net

import akka.http.scaladsl.server.Directives._

trait StatisticsApi extends RatingApi with CounterApi {
  lazy val statisticsRoute = {
    pathPrefix("statistics") {
      ratingRoute ~ counterRoute
    }
  }
}
