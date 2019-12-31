# Scabbard

A tool to visualize and understand your Dagger 2 dependency graph.
<video width="100%" controls>
  <source src="video/ViewComponent.mp4" type="video/mp4">
  Your browser does not support the video tag.
</video>
More advanced [examples](examples.md).

## Features

* **Visualize** entry points, dependency graph, component relationships and scopes in your [Dagger 2](https://github.com/google/dagger) setup.

* **Minimal setup** - Scabbard's Gradle plugin prepares your project for graph generation and provides ability to customize graph generation behavior.

* **IDE integration** - Easily view a `@Component` or a `@Subcomponnet` graph directly from source code via gutter icons (IntelliJ/Android Studio).

* **Supports** both Kotlin and Java.

## Getting Started

### Requirements

#### GraphViz

Scabbard uses [GraphViz](https://www.graphviz.org/) to generate graphs and hence requires `dot` command to be available for it to work.

##### Installation instructions

* **Mac** - Install via homebrew. `brew install graphviz`.

* **Linux** - Install via apt. `sudo apt-get install graphviz`.

* **Windows** - Install via [GraphViz installer](https://graphviz.gitlab.io/_pages/Download/Download_windows.html)

After installation, verify installation by executing `dot -V`, example:

```code
dot - graphviz version 2.38.0 (20140413.2041)
```

## Installation

### Repositories

Scabbard artifacts are served via `jcenter()`. Please ensure `jcenter()` is added to your root `build.gradle`.

### [Gradle Plugin](https://plugins.gradle.org/plugin/scabbard.gradle)

Using the plugins DSL:

```Groovy tab=
plugins {
  // After Java, Kotlin or Android plugins  
  id "scabbard.gradle" version "0.1.0"
}
```

```Kotlin tab=
plugins {
  // After Java, Kotlin or Android plugins  
  id("scabbard.gradle") version "0.1.0"
}
```

or if you are using older versions of Gradle:

```Groovy tab=
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "gradle.plugin.dev.arunkumar:scabbard-gradle-plugin:0.1.0"
  }
}

// After Java, Kotlin or Android plugins
apply plugin: "scabbard.gradle"
```

```Kotlin tab=
buildscript {
  repositories {
    maven {
      url = uri("https://plugins.gradle.org/m2/")
    }
  }
  dependencies {
    classpath("gradle.plugin.dev.arunkumar:scabbard-gradle-plugin:0.1.0")
  }
}

// After Java, Kotlin or Android plugins
apply(plugin = "scabbard.gradle")
```

After applying the plugin, configure the plugin by adding a `scabbard` block:

```Groovy tab=
scabbard {
    enabled true
}
```

```Kotlin tab=
scabbard.configure(closureOf<ScabbardSpec> {
    enabled(true)
})
```

!!! success
    That's it. Now after building the project, Scabbard would have generated `dot` and `png` files for your Dagger components in your `build` folder.

!!! todo
    There are some improvements planned for improving plugin configuration syntax from **Kotlin**. Please see [#15](https://github.com/arunkumar9t2/scabbard/issues/15) for more details.

### [Android Studio/Idea Plugin](https://plugins.jetbrains.com/plugin/13548-scabbard--dagger-2-visualizer/)

Scabbard also ships an IDE plugin to open generated `png`'s directly from your source code via gutter icons. Please install plugins from `File > Preferences/Settings > Plugins > Market Place > Search for "Scabbard" > Install` and Restart.

Alternatively you could download the plugin `zip` file directly from [releases](https://github.com/arunkumar9t2/scabbard/releases) and install via `File > Preferences/Settings > Plugins > Gear Icon > Install from Disk and point to jar file`.

!!! success
    That's it. The plugin will automatically add an icon 🗡 next to `@Component`, `@Subcomponent` or `@Module` as soon as project is indexed.

### Other build systems

Scabbard at its core is just an annotation processor. You could add `dev.arunkumar:scabbard-processor:0.1.0` to your build system's annotation processor config to get it working.

## Resources

* [Configuration](configuration.md)
* [FAQ](faq.md)

## License

    Copyright 2020 Arunkumar

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

## Thanks

Scabbard's icon 🗡 is from Square's [Dagger 1 Intellij plugin](https://github.com/square/dagger-intellij-plugin).
