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

**[Core](https://github.com/universum-studios/android_fragments/tree/support-master/library/src/main)**

    compile 'universum.studios.android.support:support-fragments-core:1.0.1@aar'

**[Base](https://github.com/universum-studios/android_fragments/tree/support-master/library/src/base)**

    compile 'universum.studios.android.support:support-fragments-base:1.0.1@aar'

_depends on:_
[support-fragments-core](https://github.com/universum-studios/android_fragments/tree/support-master/library/src/main)

**[Common](https://github.com/universum-studios/android_fragments/tree/support-master/library/src/common)**

    compile 'universum.studios.android.support:support-fragments-common:1.0.1@aar'

_depends on:_
[support-fragments-core](https://github.com/universum-studios/android_fragments/tree/support-master/library/src/main),
[support-fragments-base](https://github.com/universum-studios/android_fragments/tree/support-master/library/src/base)

**[Web](https://github.com/universum-studios/android_fragments/tree/support-master/library/src/web)**

    compile 'universum.studios.android.support:support-fragments-web:1.0.1@aar'

_depends on:_
[support-fragments-core](https://github.com/universum-studios/android_fragments/tree/support-master/library/src/main),
[support-fragments-base](https://github.com/universum-studios/android_fragments/tree/support-master/library/src/base),
[support-fragments-common](https://github.com/universum-studios/android_fragments/tree/support-master/library/src/common)

**[Manage](https://github.com/universum-studios/android_fragments/tree/support-master/library/src/manage)**

    compile 'universum.studios.android.support:support-fragments-manage:1.0.1@aar'

_depends on:_
[support-fragments-core](https://github.com/universum-studios/android_fragments/tree/support-master/library/src/main)

**[Manage-Core](https://github.com/universum-studios/android_fragments/tree/support-master/library/src/manage/core)**

    compile 'universum.studios.android.support:support-fragments-manage-core:1.0.1@aar'

_depends on:_
[support-fragments-core](https://github.com/universum-studios/android_fragments/tree/support-master/library/src/main)

**[Manage-Base](https://github.com/universum-studios/android_fragments/tree/support-master/library/src/manage/base)**

    compile 'universum.studios.android.support:support-fragments-manage-base:1.0.1@aar'

_depends on:_
[support-fragments-core](https://github.com/universum-studios/android_fragments/tree/support-master/library/src/main),
[support-fragments-manage-core](https://github.com/universum-studios/android_fragments/tree/support-master/library/src/manage/core)

**[Transition](https://github.com/universum-studios/android_fragments/tree/support-master/library/src/transition)**

    compile 'universum.studios.android.support:support-fragments-transition:1.0.1@aar'

_depends on:_
[support-fragments-core](https://github.com/universum-studios/android_fragments/tree/support-master/library/src/main),
[support-fragments-manage-core](https://github.com/universum-studios/android_fragments/tree/support-master/library/src/manage/core)

**[Transition-Core](https://github.com/universum-studios/android_fragments/tree/support-master/library/src/transition/core)**

    compile 'universum.studios.android.support:support-fragments-transition-core:1.0.1@aar'

_depends on:_
[support-fragments-core](https://github.com/universum-studios/android_fragments/tree/support-master/library/src/main),
[support-fragments-manage-core](https://github.com/universum-studios/android_fragments/tree/support-master/library/src/manage/core)

**[Transition-Common](https://github.com/universum-studios/android_fragments/tree/support-master/library/src/transition/common)**

    compile 'universum.studios.android.support:support-fragments-transition-common:1.0.1@aar'

_depends on:_
[support-fragments-core](https://github.com/universum-studios/android_fragments/tree/support-master/library/src/main),
[support-fragments-manage-core](https://github.com/universum-studios/android_fragments/tree/support-master/library/src/manage/core),
[support-fragments-transition-core](https://github.com/universum-studios/android_fragments/tree/support-master/library/src/transition/core)

**[Transition-Extra](https://github.com/universum-studios/android_fragments/tree/support-master/library/src/transition/extra)**

    compile 'universum.studios.android.support:support-fragments-transition-extra:1.0.1@aar'

_depends on:_
[support-fragments-core](https://github.com/universum-studios/android_fragments/tree/support-master/library/src/main),
[support-fragments-manage-core](https://github.com/universum-studios/android_fragments/tree/support-master/library/src/manage/core),
[support-fragments-transition-core](https://github.com/universum-studios/android_fragments/tree/support-master/library/src/transition/core),
[support-fragments-transition-extra](https://github.com/universum-studios/android_fragments/tree/support-master/library/src/transition/extra)