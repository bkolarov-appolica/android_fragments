##
# ==================================================================================================
#                             Copyright (C) 2016 Universum Studios
# ==================================================================================================
#         Licensed under the Apache License, Version 2.0 or later (further "License" only).
# --------------------------------------------------------------------------------------------------
# You may use this file only in compliance with the License. More details and copy of this License
# you may obtain at
#
# 		http://www.apache.org/licenses/LICENSE-2.0
#
# You can redistribute, modify or publish any part of the code written within this file but as it
# is described in the License, the software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES or CONDITIONS OF ANY KIND.
#
# See the License for the specific language governing permissions and limitations under the License.
# ==================================================================================================
##
# Keep all fragment annotations.
-keep public @interface universum.studios.android.support.fragment.annotation.** { *; }
-keep @interface universum.studios.android.support.fragment.**$** { *; }
# Keep BaseFragment implementation details:
# - public empty constructor for proper working of instantiation process using reflection.
-keepclassmembers class * extends universum.studios.android.support.fragment.BaseFragment {
    public <init>();
}
# Keep fragment class annotations. We need to specify this rule in case of fragments that have multiple
# annotations presented above theirs class when in such case Proguard just keeps one of them.
-keep @universum.studios.android.support.fragment.annotation.** class *
# Keep members with @FactoryFragment annotation within fragment factories.
-keepclassmembers class * extends universum.studios.android.support.fragment.manage.BaseFragmentFactory {
    @universum.studios.android.support.fragment.annotation.FactoryFragment <fields>;
}
# Keep FragmentFactory implementation details:
# - public empty constructor for proper working of instantiation process using reflection when joining
#   factories via annotation.
-keepclassmembers class * implements universum.studios.android.support.fragment.manage.FragmentController$FragmentFactory {
    public <init>();
}
# Keep annotation handlers implementation details:
# - constructor taking Class parameter [always]
-keepclassmembers class * extends universum.studios.android.support.fragment.annotation.handler.BaseAnnotationHandler {
    public <init>(java.lang.Class);
}