Change-Log
===============

### Release 1.1.1 ###
> 03.03.2017

- Deprecated some not properly named methods of `FragmentRequest` and replaced with better named ones.
- Updated implementation of `FragmentController.newRequest(int)` and of `FragmentController.executeRequest(...)`.
  See **[#3 Issue](https://github.com/universum-studios/android_fragments/issues/3)** for more info.
- Code quality improvements.

### Release 1.1.0 ###
> 19.01.2017

- `FragmentTransition` interface now extends `Parcelable`, this extension relation has been before
  declared for `BasicFragmentTransition` which implements `FragmentTransition`. This is not a concern
  for applications that use only predefined transitions from `FragmentTransitions` or `ExtraFragmentTransitions`
  factory or use custom transitions that extend `BasicFragmentTransition`. All other fragment transitions
  that implement `FragmentTransition` interface directly are now required to meet `Parcelable`
  implementation requirements.

### Release 1.0.1 ###
> 17.01.2017

- Removed interpolator from **alpha** animations/transitions.
- Updated **JavaDoc** for annotations.

### Release 1.0.0 ###
> 02.01.2017