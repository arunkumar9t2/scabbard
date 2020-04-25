# Configuration

## Gradle plugin

The `scabbard` plugin can be configured in following ways and is the entry point for customizing scabbard processor behavior.

!!! example "Configuration Examples"
    Example gradle build script configurations.

    * [Android Kotlin project](https://github.com/arunkumar9t2/scabbard/blob/master/samples/android-kotlin/build.gradle)
    * [Java library project with KTS build script](https://github.com/arunkumar9t2/scabbard/blob/master/samples/java-library-kts-script/build.gradle.kts)

### Enable scabbard processor

```Groovy tab=
scabbard {
  enabled true // default false
}
```

```Kotlin tab=
scabbard {
  enabled = true // default false
}
```

### Fail build on any error in Scabbard processor

```Groovy tab=
scabbard {
  failOnError true // default false
}
```

```Kotlin tab=
scabbard {
  failOnError = true // default false
}
```

By default, Scabbard processor does not fail the build should any error occur. This flag could be used to change that behavior.

### Enable full binding graph validation

```Groovy tab=
scabbard {
  fullBindingGraphValidation true // default false
}
```

```Kotlin tab=
scabbard {
  fullBindingGraphValidation = true // default false
}
```

Enables Dagger's [full binding graph validation](https://dagger.dev/compiler-options.html#full-binding-graph-validation) which validates the entire graph including all bindings in every `@Component`, `@Subcomponent` and `@Module`. This enables highlighting missing bindings which can be used to understand errors. Additionally since `@Module` itself is seen a graph, graphs will be generated for bindings in a `@Module`.

### Qualified Names

Since `0.2.0`, Scabbard uses simple names in graph images to keep the graph short. The qualified names behaviour could be restored as follows.

```Groovy tab=
scabbard {
  qualifiedNames true // default false
}
```

```Kotlin tab=
scabbard {
  qualifiedNames = true // default false
}
```

### Output format

Supported output formats for generated images are `png` and `svg`.

```Groovy tab=
scabbard {
  outputFormat "png" // default png
}
```

```Kotlin tab=
scabbard {
  outputFormat = "svg" // default png
}
```

### Multi-module projects

For multi-module projects, it is sufficient to apply Scabbard plugin in root project's `build.gradle` file. Doing so will let the plugin configure all subprojects for graph generation. 

In root `build.gradle` file:

```Groovy tab=
apply plugin: "scabbard.gradle"

scabbard {
  enabled true
  failOnError true
  fullBindingGraphValidation true
  outputFormat "svg"
}
```

```Kotlin tab=
apply(plugin = "scabbard.gradle")

scabbard {
  enabled = true
  failOnError = false
  fullBindingGraphValidation = false
  outputFormat = "svg"
}
```