package com.xesnet.runui;

import org.teknux.jettybootstrap.JettyBootstrap;
import org.teknux.jettybootstrap.JettyBootstrapException;


public class Main {
    public static void main(String[] args) {
		JettyBootstrap bootstrap = new JettyBootstrap();
        try {
            bootstrap.startServer();
        } catch (JettyBootstrapException e) {
            e.printStackTrace();
        }
    }
}
