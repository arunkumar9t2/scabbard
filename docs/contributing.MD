# Contributing to Scabbard

If you would like to contribute to Scabbard you can do so through GitHub by forking the repository and sending a pull request. Contributions of any kind from features, documentation, typo fixes and tests are welcome.

## Running Scabbard locally

After the cloning the repo, the project could be built by `.gradlew assemble` command. Scabbard also ships with IDE configurations for common tasks.

* `runScabbardProcessor` - Runs the annotation processor on `scabbard-sample` project. After build, graphs should have been generated.
* `debugScabbardProcessor` - Starts a gradle build for debugging and waits for debugger to be attached. Please run `Attach Scabbard Debugger` config to attach the debugger. Breakpoints and step through should work normally.
* `Run Scabbard IntelliJ Plugin` - Builds the plugin and opens an IntelliJ instance that has the plugin applied.

## Workflow

### Code style and language

Unless otherwise mentioned new modules are preferred to be written in Kotlin. Project uses default AS/IntelliJ code style with 2 spaces for indents.

### New issues

Please search for any related issues that might already answer your concern before creating new issues. Keeping the discussion focused help us avoid getting side tracked.

### Pull requests

For major changes like new features, please open an issue so that we can agree on the direction before raising pull requests. Small pull requests for trivial stuff are always welcome.

Tests for new code are greatly appreciated. Please ensure `./gradlew check` passes before submitting PR.
