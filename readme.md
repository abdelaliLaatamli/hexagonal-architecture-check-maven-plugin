# Hexagonal-architecture-check-maven-plugin




```cmd
mvn clean verify -DbasePackage=co.laatamli.custom
```
## Base Package Configuration

The plugin requires a **base package** to identify the root package of the application.  
This package is used by the plugin to scan classes and verify that the project respects the defined hexagonal architecture rules.

### Configure Base Package

You can explicitly define the base package using the `basePackage` configuration property:

```xml
<plugin>
    <groupId>co.laatamli</groupId>
    <artifactId>hexagonal-architecture-check-maven-plugin</artifactId>
    <version>1.0.0-SNAPSHOT</version>

    <executions>
        <execution>
            <goals>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>

    <configuration>
        <basePackage>co.laatamli</basePackage>
    </configuration>
</plugin>
```

In this example, the plugin will use:

```
co.laatamli
```

as the root package and will analyze packages such as:

```
co.laatamli.domain
co.laatamli.application
co.laatamli.infrastructure
```

---

## Default Behavior

If the `basePackage` configuration is not provided, the plugin automatically uses the **Maven project's `groupId`** as the base package.

Example:

```xml
<plugin>
    <groupId>co.laatamli</groupId>
    <artifactId>hexagonal-architecture-check-maven-plugin</artifactId>
    <version>1.0.0-SNAPSHOT</version>

    <executions>
        <execution>
            <goals>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

For a project configured with:

```xml
<groupId>com.example</groupId>
```

the plugin will automatically use:

```
com.example
```

as the base package.

---

## Recommendation

It is recommended to explicitly configure `basePackage` when:

- The Java package structure does not match the Maven `groupId`.
- The project contains multiple root packages.
- You want to make the validation scope explicit.

Otherwise, the plugin will automatically use the Maven `groupId` as the base package.``

## run tests 
```mvn 
mvn test -DargLine="-XX:+EnableDynamicAgentLoading"
```