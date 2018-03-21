package de.cap3.sangria

import play.api.Configuration

case class GraphQlConfig
(
  authTokenHeader: String,
  showSchema: Boolean
)

object GraphQlConfig {
  def apply(configuration: Configuration): GraphQlConfig = GraphQlConfig(
    authTokenHeader = configuration.get[String]("authTokenHeader"),
    showSchema = configuration.get[Boolean]("showSchema")
  )
}
