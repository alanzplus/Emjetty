package org.zlambda.projects.emjetty.examples;

import org.zlambda.projects.emjetty.core.EmbeddedServletContainer;

public class App {
    public static void main(String[] args) throws Exception {
        EmbeddedServletContainer container = new EmbeddedServletContainer.Builder(App.class).build().start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                container.stop();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));
    }
}
