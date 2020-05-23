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