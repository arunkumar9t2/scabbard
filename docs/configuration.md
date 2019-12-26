# Configuration

## Gradle plugin

The `scabbard` plugin can be configured in following ways.

### Enable scabbard processor

```groovy
scabbard {
    enabled = true // default true
}
```

### Fail build on any error in Scabbard processor

```groovy
scabbard {
    failOnError = true // default false
}
```

By default, Scabbard processor does not fail the build should any error occur. This flag could be used to change that behavior.

### Enable full binding graph validation

```groovy
scabbard {
    fullBindingGraphValidation = true // default false
}
```

Enables Dagger's [full binding graph validation](https://dagger.dev/compiler-options.html#full-binding-graph-validation) which validates the entire graph including all bindings in every `@Component`, `@Subcomponent` and `@Module`. This enables highlighting missing bindings which can be used to understand errors. Additionally since `@Module` itself is seen a graph, graphs will be generated for bindings in a `@Module`.

### Mult-module projects

While in most cases, applying on `app` module is sufficient, depending on your project structure, you might need to apply Scabbard gradle plugin wherever Dagger compiler is applied.