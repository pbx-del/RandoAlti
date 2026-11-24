package com.example.montee

import kotlinx.coroutines.flow.MutableStateFlow

/** Une ligne d'affichage : fenêtre (ou global), vitesse en m/h, dénivelé net en m. */
data class Row(val label: String, val rate: Double?, val delta: Double?)

data class Stats(
    val status: String = "Arrêté",
    val altitude: Double? = null,
    val rows: List<Row> = emptyList()
)

object ClimbState {
    val stats = MutableStateFlow(Stats())
    val running = MutableStateFlow(false)
}
