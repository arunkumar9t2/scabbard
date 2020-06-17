# Configuration

## Gradle plugin

The `scabbard` plugin can be configured in following ways and is the entry point for customizing scabbard processor behavior.

!!! example "Configuration Examples"
    Example gradle build script configurations.

    * [Android Kotlin project](https://github.com/arunkumar9t2/scabbard/blob/master/samples/android-kotlin/build.gradle)
    * [Java library project with KTS build script](https://github.com/arunkumar9t2/scabbard/blob/master/samples/java-library-kts-script/build.gradle.kts)

### Enable Scabbard processor

=== "Groovy"
    ```groovy
    scabbard {
      enabled true // default false
    }
    ```
=== "Kotlin"
    ```kotlin
    scabbard {
      enabled = true // default false
    }
    ```

### Fail build on any error in Scabbard processor

=== "Groovy"
    ```groovy
    scabbard {
      failOnError true // default false
    }
    ```

=== "Kotlin"
    ```kotlin
    scabbard {
      failOnError = true // default false
    }
    ```

By default, Scabbard processor does not fail the build should any error occur. This flag could be used to change that behavior.

### Enable full binding graph validation

=== "Groovy"
    ```groovy
    scabbard {
      fullBindingGraphValidation true // default false
    }
    ```
=== "Kotlin"
    ```kotlin
    scabbard {
      fullBindingGraphValidation = true // default false
    }
    ```

Enables Dagger's [full binding graph validation](https://dagger.dev/compiler-options.html#full-binding-graph-validation) which validates the entire graph including all bindings in every `@Component`, `@Subcomponent` and `@Module`. This enables highlighting missing bindings which can be used to understand errors. Additionally since `@Module` itself is seen a graph, graphs will be generated for bindings in a `@Module`.

### Qualified Names

Since `0.2.0`, Scabbard uses simple names in graph images to keep the graph short. The qualified names behaviour could be restored as follows.


=== "Groovy"
    ```groovy
    scabbard {
      qualifiedNames true // default false
    }
    ```
=== "Kotlin"
    ```Kotlin
    scabbard {
      qualifiedNames = true // default false
    }
    ```

### Output format

Supported output formats for generated images are `png` and `svg`.

=== "Groovy"
    ```groovy
    scabbard {
      outputFormat "png" // default png
    }
    ```
=== "Kotlin"
    ```kotlin
    scabbard {
      outputFormat = "svg" // default png
    }
    ```
    
## Other build systems

Although Scabbard integrates well with Gradle, other build systems are supported in limited capacity. The majority of the work is done by Scabbard's annotation processor available on `jcenter` with the following maven coordinates: `dev.arunkumar:scabbard-processor:0.3.0`. 

The annotation processor behavior can be customized by passing the following Java compiler properties.

Java Compiler Property       | Values          | Behavior
-------------------------    | ----------------| ----------
 -Ascabbard.failOnError    | `true`, `false` | [Link](#fail-build-on-any-error-in-scabbard-processor)
 -Ascabbard.qualifiedNames | `true`, `false` | [Link](#qualified-names)
 -Ascabbard.outputFormat   | `png`, `svg`    | [Link](#output-format)
 
!!! info
    When using the Gradle plugin, all these options are abstracted into type safe plugin configuration DSL, hence most of them this is of less concern for Gradle users.

### Maven

For Maven projects, add Scabbard's annotation processor dependency alongside Dagger as shown below. [Full working sample](https://github.com/arunkumar9t2/scabbard/blob/master/samples/simple-maven/pom.xml).

```xml hl_lines="1 2 3 4 5 6 22 23 24 25 26 28 29 30"
<repositories>
    <repository>
        <id>jcenter</id>
        <url>https://jcenter.bintray.com</url>
    </repository>
</repositories>
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.6.1</version>
            <configuration>
                <source>1.8</source>
                <target>1.8</target>
                <annotationProcessorPaths>
                    <path>
                        <groupId>com.google.dagger</groupId>
                        <artifactId>dagger-compiler</artifactId>
                        <version>2.28</version>
                    </path>
                    <path>
                        <groupId>dev.arunkumar</groupId>
                        <artifactId>scabbard-processor</artifactId>
                        <version>0.3.0</version>
                    </path>
                </annotationProcessorPaths>
                <compilerArgs>
                    <arg>-Ascabbard.outputFormat=svg</arg>
                </compilerArgs>
            </configuration>
        </plugin>
    </plugins>
</build>
```

After running a build, the images should be available in `target/classes/scabbard`.