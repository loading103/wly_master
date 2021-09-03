package com.daqsoft.provider.bean

data class WeatherBean(
    val aqi: Int,
    val max: String,
    val min: String,
    val pic: String,
    val qlty: String,
    val suggestion: Suggestion,
    val txt: String,
    val unicode: String
)

data class Suggestion(
    val comf: Comf,
    val cw: Cw,
    val drsg: Drsg,
    val flu: Flu,
    val sport: Sport,
    val trav: Trav,
    val uv: Uv
)

data class Comf(
    val brf: String,
    val txt: String
)

data class Cw(
    val brf: String,
    val txt: String
)

data class Drsg(
    val brf: String,
    val txt: String
)

data class Flu(
    val brf: String,
    val txt: String
)

data class Sport(
    val brf: String,
    val txt: String
)

data class Trav(
    val brf: String,
    val txt: String
)

data class Uv(
    val brf: String,
    val txt: String
)