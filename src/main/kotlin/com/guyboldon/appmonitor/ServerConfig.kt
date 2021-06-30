package com.guyboldon.appmonitor

data class ServerConfig(
    val orderId: Int,
    val name: String,
    val url: String
) : Comparable<ServerConfig> {
    override fun compareTo(other: ServerConfig): Int {
        return orderId.compareTo(other.orderId)
    }
}
