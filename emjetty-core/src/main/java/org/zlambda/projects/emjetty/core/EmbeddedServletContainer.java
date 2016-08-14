package org.zlambda.projects.emjetty.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.FragmentConfiguration;
import org.eclipse.jetty.webapp.JettyWebXmlConfiguration;
import org.eclipse.jetty.webapp.MetaInfConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;

/**
 * Reference:
 *
 * @see <a href="https://github.com/jetty-project/embedded-servlet-3.1/blob/master/src/main/java/org/eclipse/jetty/demo/EmbedMe.java"/>
 * @see <a href="http://qiita.com/opengl-8080/items/673bfbfeebe6c6db8578/>
 * <p>
 * TODO: More configuration
 * 1. HTTPS
 * 2. JSP Support
 */
public class EmbeddedServletContainer {
    private static final Logger LOGGER = LogManager.getLogger(EmbeddedServletContainer.class);

    static {
        Log.setLog(new JettyLog4jBridge(LOGGER));
    }

    private final Server server;

    private EmbeddedServletContainer(Builder builder) {
        this.server = new Server(builder.port);
        String warPath = builder.mainClass.getProtectionDomain().getCodeSource().getLocation().toExternalForm();
        LOGGER.info("war path <{}>.", warPath);
        WebAppContext context = new WebAppContext();
        context.setWar(warPath);
        context.setContextPath(builder.rootContextPath);
        context.setConfigurations(new Configuration[] {
                new AnnotationConfiguration(),
                new WebInfConfiguration(),
                new WebXmlConfiguration(),
                new MetaInfConfiguration(),
                new FragmentConfiguration(),
                new EnvConfiguration(),
                new PlusConfiguration(),
                new JettyWebXmlConfiguration()
        });
        context.setParentLoaderPriority(true);
        server.setHandler(context);
    }

    public EmbeddedServletContainer start() throws Exception {
        server.start();
        return this;
    }

    public EmbeddedServletContainer join() throws Exception {
        server.join();
        return this;
    }

    public static class Builder {
        private int port = 8080;
        private String rootContextPath = "/";
        private final Class<?> mainClass;

        public Builder(Class<?> mainClass) {
            this.mainClass = mainClass;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder rootContextPath(String rootContextPath) {
            this.rootContextPath = rootContextPath;
            return this;
        }

        public EmbeddedServletContainer build() {
            return new EmbeddedServletContainer(this);
        }
    }
}
