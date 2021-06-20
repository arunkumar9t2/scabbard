# Scabbard

<p align="center">
<img src="images/scabbard-icon.png" 
width="190" hspace="10" vspace="10">
</p>
<p align="center">
A tool to visualize and understand your Dagger 2 dependency graph.
</p>
<br>
<video width="100%" controls>
  <source src="video/scabbard-demo.mp4" type="video/mp4">
  Your browser does not support the video tag.
</video>
More advanced [examples](examples.md).

## Features

* **Visualize** entry points, dependency graph, dagger errors, component relationships and scopes in your [Dagger 2](https://github.com/google/dagger) setup.

* **Minimal setup** - Scabbard's Gradle plugin prepares your project for graph generation and provides ability to customize graph generation behavior.

* **IDE integration** - Easily view a `@Component` or a `@Subcomponnet` graph directly from source code via gutter icons (IntelliJ/Android Studio).

* **Supports** both Kotlin and Java.

* Supports **Dagger Hilt** and **Anvil**

!!! tip "What's new"
    Recent additions include support for Dagger Hilt, Anvil, Open `SVG`s in browser and visualizing missing binding errors from Dagger.
    For more details, see [Releases](https://github.com/arunkumar9t2/scabbard/releases).

## How it works?

Scabbard gradle plugin registers a [Dagger SPI](https://dagger.dev/dev-guide/spi.html) plugin to your project and during build it generates graph images based on your dagger setup.

An IntelliJ/Android Studio plugin is also provided that links the generated file to editor for fast access.

## Getting Started

### Requirements

Scabbard uses [GraphViz](https://www.graphviz.org/) to generate graphs and hence requires `dot` command to be available for it to work.

##### Installation instructions

=== "Mac"
    Install via [homebrew](https://brew.sh/). 
    ```bash
    brew install graphviz
    ```
=== "Linux"
    Install via apt. 
    ```bash 
    sudo apt-get install graphviz
    ```
=== "Windows"
    Install via [GraphViz installer](https://graphviz.gitlab.io/_pages/Download/Download_windows.html) or with [Chocolatey](https://chocolatey.org/packages/Graphviz) by
    ```bash
    choco install graphviz
    ```

After installation, verify installation by executing `dot -V`, example:

```code
dot - graphviz version 2.38.0 (20140413.2041)
```

## Installation

Scabbard has two major components:

* **Scabbard Gradle Plugin** - Configures project to generate Dagger images during build.
* **Scabbard IntelliJ Plugin** (Optional) - Helps to link the generated images back to editor via gutter icons/error logs.

### [Gradle Plugin](https://plugins.gradle.org/plugin/scabbard.gradle)

#### Repositories

Scabbard artifacts are served via `jcenter()`. Please ensure `jcenter()` is added to your root `build.gradle`.

<a href="https://plugins.gradle.org/plugin/scabbard.gradle"><img src="https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/scabbard/gradle/scabbard.gradle.gradle.plugin/maven-metadata.xml.svg?style=flat-square&label=Gradle&logo=gradle&colorB=fb7b21&logoColor=06A0CE"/></a>

Using the plugins DSL:

=== "Groovy"
    ```groovy
    plugins {
        id "scabbard.gradle" version "0.5.0"
    }
    ```
=== "Kotlin"
    ```kotlin
    plugins {
        id("scabbard.gradle") version "0.5.0"
    }
    ```

or if you are using older versions of Gradle:

=== "Groovy"
    ```groovy
    buildscript {
      repositories {
        maven {
          url "https://plugins.gradle.org/m2/"
        }
      }
      dependencies {
        classpath "gradle.plugin.dev.arunkumar:scabbard-gradle-plugin:0.5.0"
      }
    }
    
    apply plugin: "scabbard.gradle"
    ```

=== "Kotlin"
    ```kotlin
    buildscript {
      repositories {
        maven {
          url = uri("https://plugins.gradle.org/m2/")
        }
      }
      dependencies {
        classpath("gradle.plugin.dev.arunkumar:scabbard-gradle-plugin:0.5.0")
      }
    }
    
    apply(plugin = "scabbard.gradle")
    ```

After applying the plugin, configure the plugin by adding a `scabbard` block:

=== "Groovy"
    ```groovy
    scabbard {
        enabled true
    }
    ```
=== "Kotlin"
    ```kotlin
    scabbard {
        enabled = true
    }
    ```
    
??? info "Configuring Scabbard for multi-module projects"
    For multi-module projects, apply Scabbard plugin in root project's `build.gradle` file. Doing so will let the plugin configure all subprojects for graph generation. 
    
    In root `build.gradle` file:
    
    === "Groovy"
        ```groovy
        apply plugin: "scabbard.gradle"
        
        scabbard {
          enabled true
          outputFormat "svg"
        }
        ```
    === "Kotlin"
        ```kotlin
        apply(plugin = "scabbard.gradle")
        
        scabbard {
          enabled = true
          outputFormat = "svg"
        }
        ```

!!! success
    That's it. Now after **building** the project, Scabbard would have generated `dot` and (`png` or `svg`) files for your Dagger components in your `build` folder. The default output directory is location defined by `StandardLocation.SOURCE_OUTPUT`.

    * **Java** : `build/generated/sources/annotationProcessors/java/$sourceSet/scabbard`
    * **Kotlin** : `build/generated/source/kapt/$sourceSet/scabbard/`
    
    In IDE, the generated images can be found as part of `java (generated)` source set.
    <p align="center">
    <img src="images/generated-images-ide.png" width="350" hspace="10" vspace="10">
    </p>


### [Android Studio/Idea Plugin](https://plugins.jetbrains.com/plugin/13548-scabbard--dagger-2-visualizer/)

<a href="https://plugins.jetbrains.com/plugin/13548-scabbard--dagger-2-visualizer"><img src="https://img.shields.io/jetbrains/plugin/v/13548-scabbard--dagger-2-visualizer?style=flat-square&label=IntelliJ&logo=intellij-idea&colorB=fb7b21&logoColor=18d68c"/></a>

Scabbard also ships an IDE plugin to open generated images directly from your source code via gutter icons. Please install plugins from `File > Preferences/Settings > Plugins > Market Place > Search for "Scabbard" > Install` and Restart.

Alternatively you could download the plugin `zip` file directly from [releases](https://github.com/arunkumar9t2/scabbard/releases) and install via `File > Preferences/Settings > Plugins > Gear ⚙ > Install from Disk` and point to zip file.

!!! success
    The plugin should automatically add an icon <img src="images/scabbard-gutter.png" width="16" height="16"> next to `@Component`, `@Subcomponent`, `@Module` or `@ContributesAndroidInjector` as soon as project is indexed.

### Other build systems

<img alt="Maven Central" src="https://img.shields.io/maven-central/v/dev.arunkumar/scabbard-processor?logo=apache-maven&logoColor=%23C71A36&style=flat-square&colorB=fb7b21">

Scabbard at its core is just an annotation processor and as long as it is available alongside Dagger in the classpath, it should generate images. For configuring other build systems, please see [setup guide](configuration.md#other-build-systems).

## Resources

* [Configuration](configuration.md)
* [FAQ](faq.md)
* [Cheat Sheet](cheat-sheet.md)
* [Examples](examples.md)
* [Contributing](contributing.md)

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