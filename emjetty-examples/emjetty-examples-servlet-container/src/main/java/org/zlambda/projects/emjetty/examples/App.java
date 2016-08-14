package org.zlambda.projects.emjetty.examples;

import org.zlambda.projects.emjetty.core.EmbeddedServletContainer;

public class App {
    public static void main(String[] args) throws Exception {
        new EmbeddedServletContainer.Builder(App.class).build().start().join();
    }
}
