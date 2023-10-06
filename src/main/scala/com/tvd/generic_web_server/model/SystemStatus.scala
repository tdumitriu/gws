package com.tvd.generic_web_server.model

import com.fasterxml.jackson.annotation.{JsonCreator, JsonProperty}

@JsonCreator
case class SystemStatus(@JsonProperty("component") system: String,
                        @JsonProperty("status") status: String,
                        @JsonProperty("description") description: String)
