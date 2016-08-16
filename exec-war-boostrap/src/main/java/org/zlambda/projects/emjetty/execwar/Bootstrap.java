package org.zlambda.projects.emjetty.execwar;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

public class Bootstrap {
    private static final Pattern CONTAINER_JAR_PATTERN = Pattern.compile("^WEB-INF\\/lib-provided\\/exec-war-container.*jar$");
    private static final Pattern LOG4J_PATTERN = Pattern.compile("^WEB-INF\\/classes\\/log4j[^/]*$");
    private static final Pattern PATH_SPLIT_PATTERN = Pattern.compile("\\/");

    public static void main(String[] args) throws Exception {
        URL warLocation = Bootstrap.class.getProtectionDomain().getCodeSource().getLocation();
        if (null == warLocation) {
            throw new IllegalStateException("Bootstrap failure: cannot find war location");
        }
        JarFile war = new JarFile(warLocation.getFile());
        Enumeration<JarEntry> warEntries = war.entries();
        JarEntry containerLibEntry = null;
        JarEntry log4jEntry = null;
        while (warEntries.hasMoreElements()) {
            JarEntry entry = warEntries.nextElement();
            if (CONTAINER_JAR_PATTERN.matcher(entry.getName()).matches()) {
                containerLibEntry = entry;
            } else if (LOG4J_PATTERN.matcher(entry.getName()).matches()) {
                log4jEntry = entry;
            }
            if (null != log4jEntry && null != containerLibEntry) {
                break;
            }
        }

        if (null == containerLibEntry) {
            throw new IllegalStateException(
                    "Bootstrap failure: cannot find container jar using pattern <" +
                    CONTAINER_JAR_PATTERN.toString() + ">"
            );
        }

        Path tempDir = Files.createTempDirectory("emjetty-");
        File containerJar = new File(tempDir.toFile(), "emjetty-container.jar");
        readJarEntryIntoFile(war.getInputStream(containerLibEntry), containerJar.toPath());
        URLClassLoader bootstrapCL = new URLClassLoader(new URL[] { containerJar.toURI().toURL() });

        if (null != log4jEntry) {
            String[] names = PATH_SPLIT_PATTERN.split(log4jEntry.getName());
            File log4jConfigFile = new File(tempDir.toFile(), names[names.length - 1]);
            readJarEntryIntoFile(war.getInputStream(log4jEntry), log4jConfigFile.toPath());
            System.setProperty("log4j.configurationFile", log4jConfigFile.toPath().toAbsolutePath().toString());
        }

        /**
         * Use 3rd-party is trivial, but we don't want any dependency.
         */
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Files.walkFileTree(tempDir, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }
                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException("Fail to cleanup " + tempDir.toString());
            }
        }));

        /**
         * Jetty need to use the thread context loader
         */
        Thread.currentThread().setContextClassLoader(bootstrapCL);
        Class<?> serverClass = Class.forName("org.zlambda.projects.emjetty.execwar.ExecWarServletContainer", false, bootstrapCL);
        System.setProperty("__EXEC_WAR_PATH__", warLocation.toExternalForm());
        Object server = serverClass.getMethod("startContainer", args.getClass()).invoke(serverClass, new Object[] {args});
        serverClass.getMethod("join").invoke(server);
    }

    private static void readJarEntryIntoFile(InputStream jarInputStream, Path target) throws IOException {
        try (BufferedOutputStream os = new BufferedOutputStream(Files.newOutputStream(target))) {
            try (BufferedInputStream is = new BufferedInputStream(jarInputStream)) {
                int read = -1;
                while (-1 != (read = is.read())) {
                    os.write(read);
                }
            }
        }
    }
}
