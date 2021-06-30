package com.guyboldon.appmonitor

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.ui.SimpleToolWindowPanel
import io.ktor.client.HttpClient
import io.ktor.client.features.HttpRequestTimeoutException
import io.ktor.client.features.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.HttpStatusCode
import io.ktor.network.sockets.ConnectTimeoutException
import io.ktor.network.sockets.SocketTimeoutException
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.util.Vector
import java.util.concurrent.CompletableFuture
import javax.swing.table.DefaultTableModel

class ToolWindowPanel : SimpleToolWindowPanel(false, true) {

    companion object {
        const val TOOL_WINDOW_ID: String = "App Monitor"
        private const val timeoutMillis: Long = 3 * 1000
    }

    private val log = Logger.getInstance(ToolWindowPanel::class.java)

    private val appWindow: AppMonitorToolWindow = AppMonitorToolWindow()
    private val tableModel: DefaultTableModel = DefaultTableModel(0, 0)
    private val serverConfigs: List<ServerConfig> = listOf(
        ServerConfig(0, "XKCD Test", "https://xkcd.com/info.0.json"),
    )

    init {
        initToolbar()
        initTable()
        setContent(appWindow.content)
        CompletableFuture.runAsync { queryServersAddResponses() }
    }

    private fun initToolbar() {
        val actionManager = ActionManager.getInstance()
        DefaultActionGroup()
            .apply {
                add(actionManager.getAction("AppMonitor.Reload"))
            }.let {
                actionManager.createActionToolbar(ActionPlaces.EDITOR_TOOLBAR, it, false)
            }.also {
                this.toolbar = it.component
            }
    }

    private fun initTable() {
        appWindow.serverList.model = tableModel

        tableModel.apply {
            addColumn("Name")
            addColumn("Status")
            addColumn("Answer")
        }

        appWindow.serverList.columnModel.getColumn(0).minWidth = 100
        appWindow.serverList.columnModel.getColumn(0).preferredWidth = 100
        appWindow.serverList.columnModel.getColumn(1).minWidth = 50
        appWindow.serverList.columnModel.getColumn(1).preferredWidth = 50
        appWindow.serverList.columnModel.getColumn(2).minWidth = 150
    }

    fun queryServersAddResponses() {
        for (i in (tableModel.rowCount - 1) downTo 0) {
            tableModel.removeRow(i)
        }
        runBlocking {
            val startTime = System.currentTimeMillis()
            val client = HttpClient {
                expectSuccess = false
                install(HttpTimeout) {
                    requestTimeoutMillis = timeoutMillis
                    connectTimeoutMillis = timeoutMillis
                    socketTimeoutMillis = timeoutMillis
                }
            }

            serverConfigs.forEach { serverConfig ->
                try {
                    val request = async { client.get<HttpResponse>(serverConfig.url) }
                    val response = request.await()
                    val responseStatus = HttpStatusCode.fromValue(response.status.value)
                    if (responseStatus == HttpStatusCode.OK) {
                        addResponseToTable(
                            serverConfig,
                            status = responseStatus.description,
                            answer = response.readText()
                        )
                    } else {
                        addResponseToTable(serverConfig, responseStatus.description)
                    }
                    log.debug("${serverConfig.name} Response: ${responseStatus.description}")
                } catch (ex: Exception) {
                    when (ex) {
                        is HttpRequestTimeoutException,
                        is ConnectTimeoutException,
                        is SocketTimeoutException -> {
                            log.warn(ex)
                            addResponseToTable(serverConfig, "Timeout")
                        }
                        else -> throw ex
                    }
                }
            }
            client.close()
            log.info("Total time taken: ${(System.currentTimeMillis() - startTime) / 1000.0}s")
        }
    }

    private fun addResponseToTable(serverConfig: ServerConfig, status: String, answer: String = "") {
        tableModel
            .insertRow(serverConfig.orderId, Vector<String>()
                .apply {
                    add(serverConfig.name)
                    add(status)
                    add(answer)
                })
    }
}