package com.guyboldon.appmonitor

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.wm.ToolWindowType
import com.intellij.ui.content.ContentManager
import javax.swing.JComponent

class AppMonitorToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        toolWindow.setType(ToolWindowType.DOCKED, null)
        val contentManager: ContentManager = toolWindow.contentManager
        val content = contentManager.factory.createContent(
            ToolWindowPanel(), "", false
        )
        contentManager.addContent(content);
    }
}