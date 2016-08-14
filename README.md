# Embedded Jetty
This project help quickly embed Jetty into application.

# Embedded Servlet Container
Class `EmbeddedServletContainer` helps build an executable war. Simply, you just create your servlet web application like the way you do with the traditional container. 

Take `emjetty-examples/emjetty-examples-servlet-container` as an example, the following is project structure. The project structure is same as the traditional servelt web application structure except that you need to create two more files, `assembly/bootstrap.xml` and `App.java` (`App.java` is the bootstrap class)

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
    │                       ├── App.java
    │                       └── HelloWorldServlet.java
    ├── resources
    └── webapp
        └── WEB-INF
            └── web.xml
```

Then let's go through the steps of creating an executable war.

First we need to modify `pom.xml` file. You need to add `emjetty-core` dependency to your project with following setting

```xml
<dependency>
    <groupId>org.zlambda.projects.emjetty</groupId>
    <artifactId>emjetty-core</artifactId>
    <!-- emjetty-core is deployed with two artifacts -->
    <!-- one is the general jar and other one is the jar with all dependencies (jetty) -->
    <!-- Here we must use the one will the dependencies, so specify the classifier to select it -->
    <classifier>with-dependencies</classifier>
    <version>${emjetty.version}</version>
</dependency>
```

and configure the maven-assembly-plugin like the following one

```xml
<build>
    <plugins>
        <plugin>
            <artifactId>maven-compiler-plugin</artifactId>
        </plugin>
        <plugin>
            <artifactId>maven-assembly-plugin</artifactId>
            <executions>
                <execution>
                    <id>make-executable-war</id>
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
                        <!-- bootstrap class entrypoint -->
                        <!-- what you need to do is just modify here -->
                        <!-- change the name to your bootstrap class -->
                        <mainClass>org.zlambda.projects.emjetty.examples.App</mainClass>
                    </manifest>
                </archive>
            </configuration>
        </plugin>
    </plugins>
</build>
```

Here is the `bootstrap.xml`
```xml
<assembly xmlns="http://maven.apache.org/plugins/maven-assembly-plugin/assembly/1.1.3"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/plugins/maven-assembly-plugin/assembly/1.1.3 http://maven.apache.org/xsd/assembly-1.1.3.xsd">
    <id>embedded-jetty-war</id>
    <formats>
        <format>war</format>
    </formats>
    <includeBaseDirectory>false</includeBaseDirectory>
    <dependencySets>
        <dependencySet>
            <outputDirectory>/WEB-INF/lib</outputDirectory>
            <useProjectArtifact>false</useProjectArtifact>
            <unpack>false</unpack>
            <scope>runtime</scope>
        </dependencySet>
        <dependencySet>
            <includes>
                <include>org.zlambda.projects.emjetty:emjetty-core</include>
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
            <directory>
                ${project.build.outputDirectory}
            </directory>
            <excludes>
                <!-- what you need to do is just modify here -->
                <!-- change the name to your bootstrap class -->
                <exclude>**/App.class</exclude>
            </excludes>
        </fileSet>
        <fileSet>
            <outputDirectory>/</outputDirectory>
            <directory>
                ${project.build.outputDirectory}
            </directory>
            <includes>
                <!-- what you need to do is just modify here -->
                <!-- change the name to your bootstrap class -->
                <include>**/App.class</include>
            </includes>
        </fileSet>
    </fileSets>
</assembly>
```

Then, look at the boostrap class `App.class`. It is really simple. And of course, you can configure server settings like port, etc.

```java
package org.zlambda.projects.emjetty.examples;
import org.zlambda.projects.emjetty.core.EmbeddedServletContainer;
public class App {
    public static void main(String[] args) throws Exception {
        new EmbeddedServletContainer.Builder(App.class).build().start().join();
    }
}
```

Finally, you just need to invoke the maven to build your project into an executable war and excute it.

```bash
mvn clean package # output it target/emjetty-examples-servlet-container-1.0.0-SNAPSHOT-embedded-jetty-war.war
java -jar target/emjetty-examples-servlet-container-1.0.0-SNAPSHOT-embedded-jetty-war.war
```

