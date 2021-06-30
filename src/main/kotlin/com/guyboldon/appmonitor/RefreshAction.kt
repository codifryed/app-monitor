package com.guyboldon.appmonitor

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.wm.ToolWindowManager
import java.util.concurrent.CompletableFuture

class RefreshAction : AnAction() {

    private val log = Logger.getInstance(RefreshAction::class.java)

    override fun actionPerformed(actionEvent: AnActionEvent) {
        log.info("refreshing....")
        actionEvent.project
            ?.also {
                val toolWindow =
                    ToolWindowManager.getInstance(it).getToolWindow(ToolWindowPanel.TOOL_WINDOW_ID)
                val toolWindowPanel =
                    toolWindow?.contentManager?.getContent(0)?.component as? ToolWindowPanel

                toolWindowPanel?.apply {
                    CompletableFuture.runAsync { queryServersAddResponses() }
                }

                if (toolWindowPanel == null) {
                    log.error("Can not find ToolWindowPanel to do refresh")
                }
            }
    }
}