package com.tvd.generic_web_server.model

import com.fasterxml.jackson.annotation.{JsonCreator, JsonProperty}

@JsonCreator
case class LogLevel(@JsonProperty("path") path: String,
                    @JsonProperty("level") level: String)

@JsonCreator
case class LogLevelChange(@JsonProperty("status") status: String,
                          @JsonProperty("path") path: String,
                          @JsonProperty("from") from: String,
                          @JsonProperty("to") to: String)
