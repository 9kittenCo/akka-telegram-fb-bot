package service

import org.flywaydb.core.Flyway

object MigrationConfig extends Config {

  private val flyway = new Flyway()
  flyway.setDataSource(databaseUrl, databaseUser, databasePassword)

  def migrate(): Unit = flyway.migrate()

  def reloadSchema(): Unit = {
    flyway.clean()
    flyway.migrate()
  }
}
