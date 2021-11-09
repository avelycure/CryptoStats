package com.avelycure.cryptostats.domain.state

sealed class UIComponent {
    data class Dialog(
        val description: String,
    ) : UIComponent()
}
