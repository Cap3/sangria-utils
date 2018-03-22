package de.cap3.sangria

import play.api.Configuration

case class SangriaConfig
(
  authTokenHeader: String,
  showSchema: Boolean
)

object SangriaConfig {
  def apply(configuration: Configuration): SangriaConfig = SangriaConfig(
    authTokenHeader = configuration.get[String]("authTokenHeader"),
    showSchema = configuration.get[Boolean]("showSchema")
  )
}
