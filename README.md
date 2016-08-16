# Embedded Jetty
This project help quickly embed Jetty into application.

# Embedded Servlet Container
`EmbeddedServletContainer` and `Bootstrap` help build an executable war. Simply, you just create your servlet web application like the way you do with the traditional container.
Take `examples/exec-war-example` as an example, the following is project structure. The project structure is same as the traditional servelt web application structure except that you need to add one more file `assembly/bootstrap.xml`.

```
pom.xml
src
├── assembly
│   └── bootstrap.xml
└── main
    ├── java
    │   └── org
    │       └── zlambda
    │           └── projects
    │               └── emjetty
    │                   └── examples
    │                       ├── HelloworldServlet.java
    │                       └── Listener.java
    ├── resources
    │   └── log4j2.properties
    └── webapp
        └── WEB-INF
            └── emjetty
                └── config.properties
```

Then let's go through the steps of creating an executable war.

First we need to modify `pom.xml` file. You need to add `exec-war-boostrap` and `exec-war-container` to your dependencies

```xml
<dependency>
    <groupId>org.zlambda.projects.emjetty</groupId>
    <artifactId>exec-war-bootstrap</artifactId>
</dependency>
<dependency>
    <groupId>org.zlambda.projects.emjetty</groupId>
    <artifactId>exec-war-container</artifactId>
    <!-- emjetty-core is deployed with two artifacts -->
    <!-- one is the general jar and other one is the jar with all dependencies (jetty) -->
    <!-- Here we must use the one will the dependencies, so specify the classifier to select it -->
    <classifier>with-dependencies</classifier>
    <version>${emjetty.version}</version>
</dependency>
```

and then add the following plugin configuration

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-assembly-plugin</artifactId>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>single</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <descriptors>
            <descriptor>src/assembly/bootstrap.xml</descriptor>
        </descriptors>
        <archive>
            <manifest>
                <mainClass>org.zlambda.projects.emjetty.execwar.Bootstrap</mainClass>
            </manifest>
        </archive>
    </configuration>
</plugin>
```

Finally create your `assembly/bootstrap.xml` by copying the following one (don't need to change anything)

```xml
<assembly xmlns="http://maven.apache.org/plugins/maven-assembly-plugin/assembly/1.1.3"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/plugins/maven-assembly-plugin/assembly/1.1.3 http://maven.apache.org/xsd/assembly-1.1.3.xsd">
    <id>embedded-jetty</id>
    <formats>
        <format>war</format>
    </formats>
    <includeBaseDirectory>false</includeBaseDirectory>
    <dependencySets>
        <dependencySet>
            <excludes>
                <exclude>org.zlambda.projects.emjetty:exec-war-bootstrap</exclude>
                <exclude>org.zlambda.projects.emjetty:exec-war-container</exclude>
            </excludes>
            <outputDirectory>/WEB-INF/lib</outputDirectory>
            <useProjectArtifact>false</useProjectArtifact>
            <unpack>false</unpack>
            <scope>runtime</scope>
        </dependencySet>
        <dependencySet>
            <includes>
                <include>org.zlambda.projects.emjetty:exec-war-container</include>
            </includes>
            <outputDirectory>/WEB-INF/lib-provided</outputDirectory>
            <useProjectArtifact>false</useProjectArtifact>
            <unpack>false</unpack>
            <scope>runtime</scope>
        </dependencySet>
        <dependencySet>
            <includes>
                <include>org.zlambda.projects.emjetty:exec-war-bootstrap</include>
            </includes>
            <outputDirectory>/</outputDirectory>
            <useProjectArtifact>false</useProjectArtifact>
            <unpack>true</unpack>
            <scope>runtime</scope>
        </dependencySet>
    </dependencySets>
    <fileSets>
        <fileSet>
            <outputDirectory>/WEB-INF/classes</outputDirectory>
            <directory>${project.build.outputDirectory}</directory>
        </fileSet>
        <fileSet>
            <directory>src/main/webapp</directory>
            <outputDirectory>/</outputDirectory>
        </fileSet>
    </fileSets>
</assembly>
```

Finally, you just need to invoke the maven to build your project into an executable war and execute it.

```bash
mvn clean package # output target/exec-war-example-1.0.0-SNAPSHOT-embedded-jetty.war
java -jar target/exec-war-example-1.0.0-SNAPSHOT-embedded-jetty.war
```

## About Logging
`EmbeddedServletContainer` uses `log4j2` for logging, you need to provide log4j2 configuration by creating a file `log4j2.properties` under `resources` folder.

## About Server Configuration
Configuration of the server is through `config.properties` file stored under `webapp/WEB-INF/emjetty` (you need to create yourself)

Current implementation only supports configure the port and root context path

```
port=9999
rootPath=/
```

