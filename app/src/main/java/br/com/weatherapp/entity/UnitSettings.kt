package br.com.weatherapp.entity

enum class UnitSettings(var unit : String) {
    /** FAHRENHEIT é retorno default da API*/
    FAHRENHEIT(""),
    CELSIUS("metric")
}