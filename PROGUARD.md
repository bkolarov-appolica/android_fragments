Proguard
===============

This file describes which proguard rules **should** be used to preserve *proper working* of the
source code provided by this library when the **Proguard's obfuscation** process is applied to a
project that depends on this library.

> **Note, that the proguard rules listed below are not guarantied to ensure obfuscation that will
not affect the proper working of your Android application. Each Android application has its specific
structure, so it is hard to find rules that will fit needs of all of them. New general rules may be
added in the future.**

### Proguard-Rules ###

> Use below rules to keep **"sensitive"** source code of the library.

    # Keep all fragment annotations.
    -keep public @interface universum.studios.android.fragment.annotation.** { *; }
    -keep @interface universum.studios.android.fragment.**$** { *; }
    # Keep BaseFragment implementation details:
    # - public empty constructor for proper working of instantiation process using reflection.
    -keepclassmembers class * extends universum.studios.android.fragment.BaseFragment {
        public <init>();
    }
    # Keep fragment class annotations. We need to specify this rule in case of fragments that have multiple
    # annotations presented above theirs class when in such case Proguard just keeps one of them.
    -keep @universum.studios.android.fragment.annotation.** class *
    # Keep members with @FactoryFragment annotation within fragment factories.
    -keepclassmembers class * extends universum.studios.android.fragment.manage.BaseFragmentFactory {
        @universum.studios.android.fragment.annotation.FactoryFragment <fields>;
    }
    # Keep annotation handlers implementation details:
    # - constructor taking Class parameter [always]
    -keepclassmembers class * extends universum.studios.android.fragment.annotation.handler.BaseAnnotationHandler {
        public <init>(java.lang.Class);
    }

> Use below rules to keep **entire** source code of the library.

    # Keep all classes within library package.
    -keep class universum.studios.android.fragment.** { *; }
    