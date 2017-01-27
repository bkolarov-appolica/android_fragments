Modules
===============

Library is also distributed via **separate modules** which may be downloaded as standalone parts of
the library in order to decrease dependencies count in Android projects, so only dependencies really
needed in an Android project are included. **However** some modules may depend on another modules
from this library or on modules from other libraries.

Below are listed modules that are available for download also with theirs dependencies.

## Download ##

### Gradle ###

For **successful resolving** of artifacts for separate modules via **Gradle** add the following snippet
into **build.gradle** script of your desired Android project and use `compile '...'` declaration
as usually.

    repositories {
        maven {
            url  "http://dl.bintray.com/universum-studios/android"
        }
    }

## Available modules ##
> Following modules are available in the [latest](https://github.com/universum-studios/android_fragments/releases "Latest Releases page") release.

- **[Core](https://github.com/universum-studios/android_fragments/tree/master/library-core)**
- **[Base](https://github.com/universum-studios/android_fragments/tree/master/library-base)**
- **[Common](https://github.com/universum-studios/android_fragments/tree/master/library-common)**
- **[Web](https://github.com/universum-studios/android_fragments/tree/master/library-web)**
- **[@Manage](https://github.com/universum-studios/android_fragments/tree/master/library-manage_group)**
- **[Manage-Core](https://github.com/universum-studios/android_fragments/tree/master/library-manage-core)**
- **[Manage-Base](https://github.com/universum-studios/android_fragments/tree/master/library-manage-base)**
- **[@Transition](https://github.com/universum-studios/android_fragments/tree/master/library-transition_group)**
- **[Transition-Core](https://github.com/universum-studios/android_fragments/tree/master/library-transition-core)**
- **[Transition-Common](https://github.com/universum-studios/android_fragments/tree/master/library-transition-common)**
- **[Transition-Extra](https://github.com/universum-studios/android_fragments/tree/master/library-transition-extra)**
