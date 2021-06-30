package com.guyboldon.appmonitor;

import javax.swing.*;

public class AppMonitorToolWindow {

    private JPanel appMonitorContent;
    private JTable serverList;
    private JScrollPane scrollPane;

    public JTable getServerList() {
        return serverList;
    }

    public JPanel getContent() {
        return appMonitorContent;
    }

}
