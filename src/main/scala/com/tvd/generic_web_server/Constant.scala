package com.tvd.generic_web_server

object Constant {

  object ActorSystem {
    final val Name = "generic_web_server_actor_system"
    final val ServiceRegistry = "ServiceRegistryActor"
  }

  object Basic {
    final val PathPrefix = "generic_web_server"
    final val Ok = "ok"
    final val Low = "low"
    final val Unresponsive = "unresponsive"
    final val Unknown = "unknown"
    final val NA = "N/A"
  }

  object Http {
    final val Scheme = "http.scheme"
    final val Interface = "http.interface"
    final val Port = "http.port"
    final val Timeout = "http.timeout"
  }

  object RoutePath {
    final val Ping = "ping"
    final val Healthcheck = "healthcheck"
    final val Log = "log"
    final val Level = "level"
    final val Grade = "grade"
    final val Database = "db"
    final val Memory = "mem"
    final val User = "user"
  }

  object Logger {
    final val PackagePath = "com.tvd"

    object Level {
      final val Unknown = "UNKNOWN"
      final val Trace = "trace"
      final val Debug = "debug"
      final val Info = "info"
      final val Warning = "warn"
      final val Error = "error"
      final val All = "all"
      final val Off = "off"
    }
  }
}
