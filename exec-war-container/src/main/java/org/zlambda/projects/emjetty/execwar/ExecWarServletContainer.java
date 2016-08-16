package org.zlambda.projects.emjetty.execwar;

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

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * Reference:
 *
 * TODO: More configuration
 * 1. HTTPS
 * 2. JSP Support
 * 3. Other server configuration
 */
public class ExecWarServletContainer {
    private static final Logger LOGGER = LogManager.getLogger(ExecWarServletContainer.class);
    static {
        Log.setLog(new JettyLog4jBridge(LOGGER));
    }

    private final Server server;

    private ExecWarServletContainer(Builder builder) {
        this.server = new Server(builder.port);
        this.server.setStopAtShutdown(true);
        String warPath = builder.warPath;
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
        /**
         * Consistent to the Servlet spec, first load from web application loader
         */
        context.setParentLoaderPriority(false);
        server.setHandler(context);
    }

    public ExecWarServletContainer start() throws Exception {
        server.start();
        return this;
    }

    public ExecWarServletContainer join() throws Exception {
        server.join();
        return this;
    }

    public ExecWarServletContainer stop() throws Exception {
        server.stop();
        return this;
    }

    public static class Builder {
        private int port = 8080;
        private String rootContextPath = "/";
        private String warPath;
        private String[] args;

        public Builder() {
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder rootContextPath(String rootContextPath) {
            this.rootContextPath = rootContextPath;
            return this;
        }

        public Builder warPath(String warPath) {
            this.warPath = warPath;
            return this;
        }

        public Builder args(String[] args) {
            this.args = args;
            return this;
        }

        public ExecWarServletContainer build() {
            return new ExecWarServletContainer(this);
        }
    }

    public static ExecWarServletContainer startContainer(String[] args) throws Exception {
        Builder builder = new Builder().warPath(System.getProperty("__EXEC_WAR_PATH__"));
        /**
         * Really simple configuration
         */
        URL simpleConfig = ExecWarServletContainer.class.getClassLoader().getResource("WEB-INF/emjetty/config.properties");
        if (null != simpleConfig) {
            Properties properties = new Properties();
            properties.load((InputStream)simpleConfig.getContent());
            builder.port(Integer.parseInt((String)properties.getOrDefault("port", "8080")))
                    .rootContextPath((String)properties.getOrDefault("rootPath", "/"));
        }
        return builder.build().start();
    }
}
