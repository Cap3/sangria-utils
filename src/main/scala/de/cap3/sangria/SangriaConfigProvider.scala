package de.cap3.sangria

import javax.inject.{Inject, Provider}

import play.api.Configuration

class SangriaConfigProvider @Inject()(configuration: Configuration) extends Provider[SangriaConfig] {
  override def get(): SangriaConfig = SangriaConfig(configuration.get[Configuration]("graphql"))
}
