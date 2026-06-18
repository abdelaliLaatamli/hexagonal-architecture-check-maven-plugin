```cmd
mvn clean verify -DbasePackage=co.laatamli.custom
```


```xml
          <plugin>
            <groupId>co.alaatamli</groupId>
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

````xml
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
````