package ru.mobak.lm2.statistics.http.rating

import scalikejdbc._

case class Rating(userId: String,
                  heroId: String,
                  regionId: String,
                  raceId: String,
                  name: String,
                  level: Int,
                  pvpCount: Int,
                  pvpWinCount: Int)

object Rating extends SQLSyntaxSupport[Rating] {
  implicit val session = AutoSession

  val queryAll = "SELECT * FROM rating"
  val queryByLevelLimit = queryAll + " ORDER BY level DESC LIMIT ?"
  val queryByPvpLimit = queryAll + " ORDER BY pvp_count DESC, pvp_win_count DESC LIMIT ?"
  val queryByPvpWinLimit = queryAll + " ORDER BY pvp_win_count DESC, pvp_count DESC LIMIT ?"

  override val tableName = "rating"

  def apply(rs: WrappedResultSet): Rating = Rating(
    userId = rs.string("user_id"),
    heroId = rs.string("hero_id"),
    regionId = rs.string("region_id"),
    raceId = rs.string("race_id"),
    name = rs.string("title"),
    level = rs.int("level"),
    pvpCount = rs.int("pvp_count"),
    pvpWinCount = rs.int("pvp_win_count")
  )

  def queryLimit(queryString: String, limit: Int) =
    SQL(queryString).bind(limit).map(rs => Rating(rs)).list.apply()

  def membersByLevel(limit: Int) =
    queryLimit(queryByLevelLimit, limit)

  def membersByPvp(limit: Int) =
    queryLimit(queryByPvpLimit, limit)

  def membersByPvpWin(limit: Int) =
    queryLimit(queryByPvpWinLimit, limit)

}
