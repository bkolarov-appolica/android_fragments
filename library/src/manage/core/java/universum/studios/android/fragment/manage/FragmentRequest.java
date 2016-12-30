/*
 * =================================================================================================
 *                             Copyright (C) 2016 Universum Studios
 * =================================================================================================
 *         Licensed under the Apache License, Version 2.0 or later (further "License" only).
 * -------------------------------------------------------------------------------------------------
 * You may use this file only in compliance with the License. More details and copy of this License
 * you may obtain at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * You can redistribute, modify or publish any part of the code written within this file but as it
 * is described in the License, the software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES or CONDITIONS OF ANY KIND.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 * =================================================================================================
 */
package universum.studios.android.fragment.manage;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.transition.Transition;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import universum.studios.android.fragment.transition.BasicFragmentTransition;

/**
 * A FragmentTransactionOptions class holds options used when showing a specific fragment via
 * {@link FragmentController#showFragment(FragmentRequest)} and related methods.
 * <p>
 * These options contains for example id of view container in which should be a view hierarchy of
 * the showing fragment placed. Such an id can be specified via {@link #containerId(int)}. When
 * you want to show the desired fragment with some transition, you can specify it via
 * {@link #transition(BasicFragmentTransition)}.
 *
 * <h3>Default options</h3>
 * Below are listed default values of all arguments within the FragmentTransactionOptions:
 * <ul>
 * <li>tag: <b>unspecified</b></li>
 * <li>container id: <b>-1</b></li>
 * <li>back-stacking: <b>false</b></li>
 * <li>arguments: <b>unspecified</b></li>
 * <li>replacing same: <b>true</b></li>
 * <li>showing immediately: <b>false</b></li>
 * <li>transition: <b>unspecified</b></li>
 * <li>transition style: <b>-1</b></li>
 * <li>enter transition: <b>unspecified</b></li>
 * <li>exit transition: <b>unspecified</b></li>
 * <li>reenter transition: <b>unspecified</b></li>
 * <li>return transition: <b>unspecified</b></li>
 * <li>shared element enter transition: <b>unspecified</b></li>
 * <li>shared element return transition: <b>unspecified</b></li>
 * <li>enter transition overlapping: <b>unspecified</b></li>
 * <li>return transition overlapping: <b>unspecified</b></li>
 * </ul>
 *
 * @author Martin Albedinsky
 * @see FragmentController
 * @see FragmentFactory
 */
public final class FragmentRequest {

	/**
	 * Interface ===================================================================================
	 */

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "FragmentRequest";

	/**
	 * Constant used to determine that no fragment id has been specified for a particular instance
	 * of FragmentTransactionOptions (regardless <var>incoming</var> or <var>outgoing</var> fragment id).
	 *
	 * @see #FragmentRequest(int, int)
	 */
	public static final int NO_ID = -1;

	/**
	 * todo:
	 */
	public static final int NO_STYLE = -1;

	/**
	 * Flag indicating whether {@link #mEnterTransition} has been specified or not.
	 */
	static final int ENTER_TRANSITION = 0x00000001;

	/**
	 * Flag indicating whether {@link #mExitTransition} has been specified or not.
	 */
	static final int EXIT_TRANSITION = 0x00000002;

	/**
	 * Flag indicating whether {@link #mReenterTransition} has been specified or not.
	 */
	static final int REENTER_TRANSITION = 0x00000004;

	/**
	 * Flag indicating whether {@link #mReturnTransition} has been specified or not.
	 */
	static final int RETURN_TRANSITION = 0x00000008;

	/**
	 * Flag indicating whether {@link #mSharedElementEnterTransition} has been specified or not.
	 */
	static final int SHARED_ELEMENT_ENTER_TRANSITION = 0x00000010;

	/**
	 * Flag indicating whether {@link #mSharedElementReturnTransition} has been specified or not.
	 */
	static final int SHARED_ELEMENT_RETURN_TRANSITION = 0x00000020;

	/**
	 * Static members ==============================================================================
	 */

	/**
	 * Members =================================================================================
	 */

	/**
	 * todo:
	 */
	final Fragment mFragment;

	/**
	 * todo:
	 */
	private final FragmentController mController;

	/**
	 * Id of incoming fragment associated with these transaction options.
	 * <p>
	 * May be {@link #NO_ID} if these options are used outside of context of
	 * {@link FragmentFactory}.
	 *
	 * @see #FragmentRequest(int, int)
	 */
	public final int incomingFragmentId;

	/**
	 * Id of outgoing fragment associated with these transaction options.
	 * <p>
	 * May be {@link #NO_ID} if these options are used outside of context of
	 * {@link FragmentFactory} or for set up of these options is relevant only
	 * {@link #incomingFragmentId}.
	 *
	 * @see #FragmentRequest(int, int)
	 */
	public final int outgoingFragmentId;

	/**
	 * Tag for fragment.
	 */
	protected String mTag = null;

	/**
	 * Fragment container layout id. This is a view group into which should be a view hierarchy
	 * of the new fragment placed.
	 */
	protected int mContainerId = -1;

	/**
	 * Flag indicating, whether fragment should be added to back stack or not.
	 */
	protected boolean mAddToBackStack = false;

	/**
	 * Arguments for incoming fragment associated with these options.
	 */
	protected Bundle mArguments;

	/**
	 * Flag indicating, whether a same fragment (currently showing) can be replaced by a new one
	 * with this options containing same tag or not.
	 */
	protected boolean mReplaceSame = true;

	/**
	 * Flag indicating, whether a corresponding {@link FragmentTransaction} should be committed via
	 * {@link FragmentTransaction#commitAllowingStateLoss()} or via default {@link FragmentTransaction#commit()}
	 * method.
	 */
	protected boolean mCommitAllowingStateLoss = false;

	/**
	 * Flag indicating, whether a new fragment should be showed immediately or not.
	 */
	protected boolean mShowImmediate = false;

	/**
	 * Flag indicating, whether to add a new fragment or replace old one.
	 */
	protected boolean mAdd;

	/**
	 * Set of transitions used when showing new fragment. These transitions are also used when
	 * popping old fragment from the back stack.
	 */
	protected FragmentTransition mTransition = null;

	/**
	 * Resource id of the style containing transitions used to animate fragment.
	 */
	protected int mTransitionStyle = -1;

	/**
	 * Enter transition for a new incoming fragment.
	 * <p>
	 * See {@link android.support.v4.app.Fragment#setEnterTransition(Object)} for more information.
	 */
	protected Transition mEnterTransition;

	/**
	 * Exit transition for a new incoming fragment.
	 * <p>
	 * See {@link android.support.v4.app.Fragment#setExitTransition(Object)} for more information.
	 */
	protected Transition mExitTransition;

	/**
	 * Reenter transition for a new incoming fragment.
	 * <p>
	 * See {@link android.support.v4.app.Fragment#setReenterTransition(Object)} for more information.
	 */
	protected Transition mReenterTransition;

	/**
	 * Return transition for a new incoming fragment.
	 * <p>
	 * See {@link android.support.v4.app.Fragment#setReturnTransition(Object)} for more information.
	 */
	protected Transition mReturnTransition;

	/**
	 * Shared element's enter transition for a new incoming fragment.
	 * <p>
	 * See {@link android.support.v4.app.Fragment#setSharedElementEnterTransition(Object)} for more information.
	 */
	protected Transition mSharedElementEnterTransition;

	/**
	 * Shared element's return transition for a new incoming fragment.
	 * <p>
	 * See {@link android.support.v4.app.Fragment#setSharedElementReturnTransition(Object)} for more information.
	 */
	protected Transition mSharedElementReturnTransition;

	/**
	 * Set of transition flags determining which transitions has been requested.
	 */
	int mRequestedTransitions;

	/**
	 * Set of shared elements for the new incoming fragment.
	 * <p>
	 * This option is only temporary and it is always removed from these options after it is properly used.
	 */
	protected List<Pair<View, String>> mSharedElements;

	/**
	 * Flag indicating whether enter transitions within a new incoming fragment can overlap or not.
	 * <p>
	 * See {@link android.support.v4.app.Fragment#setAllowEnterTransitionOverlap(boolean)} for more information.
	 */
	protected Boolean mAllowEnterTransitionOverlap;

	/**
	 * Flag indicating whether return transitions within a new incoming fragment can overlap or not.
	 * <p>
	 * See {@link android.support.v4.app.Fragment#setAllowReturnTransitionOverlap(boolean)} for more information.
	 */
	protected Boolean mAllowReturnTransitionOverlap;

	/**
	 * Boolean flag indicating whether this request has been already executed via {@link #execute()}
	 * or not.
	 */
	private boolean mExecuted = false;

	/**
	 * Constructors ============================================================================
	 */

	/**
	 * todo:
	 *
	 * @param fragment
	 * @param controller
	 */
	FragmentRequest(Fragment fragment, FragmentController controller) {
		this.mFragment = fragment;
		this.mController = controller;
	}

	/**
	 * Same as {@link #FragmentRequest(int, int)} with both fragment ids set to {@link #NO_ID}.
	 * <p>
	 * This constructor can be used to instantiate transaction options to show instance of fragment
	 * outside of context of {@link FragmentFactory}.
	 */
	@SuppressWarnings("ResourceType")
	FragmentRequest() {
		this(NO_ID, NO_ID);
	}

	/**
	 * Same as {@link #FragmentRequest(int, int)} with <var>outgoingFragmentId</var> set
	 * to {@link #NO_ID}.
	 * <p>
	 * This constructor can be used to instantiate transaction options to show instance of fragment
	 * provided by {@link FragmentFactory} where only id of incoming fragment is
	 * relevant to properly set up its options.
	 *
	 * @param fragmentId Id of the incoming fragment associated with these options.
	 */
	@SuppressWarnings("ResourceType")
	FragmentRequest(@IntRange(from = 0) int fragmentId) {
		this(fragmentId, NO_ID);
	}

	/**
	 * Creates a new instance of default FragmentTransactionOptions for the specified <var>incomingFragmentId</var>
	 * and <var>outgoingFragmentId</var>.
	 * <p>
	 * This constructor can be used to instantiate transaction options to show instance of fragment
	 * provided by {@link FragmentFactory} where as id of incoming fragment so
	 * id of outgoing fragment are relevant to properly set up options of incoming fragment.
	 *
	 * @param incomingFragmentId Id of the incoming fragment associated with these options.
	 * @param outgoingFragmentId Id of the outgoing fragment associated with these options.
	 */
	FragmentRequest(@IntRange(from = 0) int incomingFragmentId, @IntRange(from = 0) int outgoingFragmentId) {
		this.incomingFragmentId = incomingFragmentId;
		this.outgoingFragmentId = outgoingFragmentId;
	}

	/**
	 * Methods =================================================================================
	 */

	/**
	 */
	@Override
	@SuppressWarnings("StringBufferReplaceableByString")
	public String toString() {
		final StringBuilder builder = new StringBuilder(128);
		builder.append("FragmentRequest{incomingFragmentId: ");
		builder.append(incomingFragmentId);
		builder.append(", outgoingFragmentId: ");
		builder.append(outgoingFragmentId);
		builder.append(", tag: ");
		builder.append(mTag);
		builder.append(", container: ");
		builder.append(mContainerId);
		builder.append(", arguments: ");
		builder.append(mArguments);
		builder.append(", transition: ");
		builder.append(mTransition != null ? mTransition.getName() : "null");
		builder.append(", transitionStyle: ");
		builder.append(mTransitionStyle);
		builder.append(", backStacked: ");
		builder.append(mAddToBackStack);
		builder.append("), commitAllowingStateLoss: ");
		builder.append(mCommitAllowingStateLoss);
		builder.append("), replace: ");
		builder.append(mReplaceSame);
		builder.append(", add: ");
		builder.append(mAdd);
		return builder.append("}").toString();
	}

	/**
	 * Sets a tag for associated fragment to be shown.
	 *
	 * @param fragmentTag The desired fragment tag.
	 * @return These options to allow methods chaining.
	 * @see FragmentTransaction#replace(int, android.support.v4.app.Fragment, String)
	 * @see #tag()
	 */
	public final FragmentRequest tag(@Nullable String fragmentTag) {
		this.mTag = fragmentTag;
		return this;
	}

	/**
	 * Returns the tag specified via {@link #tag(String)}.
	 *
	 * @return Tag for the associated fragment to be shown using these options.
	 */
	@Nullable
	public final String tag() {
		return mTag;
	}

	/**
	 * Sets a flag indicating, whether associated fragment should be added to the fragments back stack
	 * or not.
	 * <p>
	 * Default: <b>false</b>
	 *
	 * @param add {@code True} to add fragment to the back stack, {@code false} otherwise.
	 * @return These options to allow methods chaining.
	 * @see FragmentTransaction#addToBackStack(String)
	 * @see #addToBackStack()
	 */
	public final FragmentRequest addToBackStack(boolean add) {
		this.mAddToBackStack = add;
		return this;
	}

	/**
	 * Returns the boolean flag specified via {@link #addToBackStack(boolean)}.
	 *
	 * @return {@code True} if associated fragment to be shown using these options should be added
	 * into back stack, {@code false} otherwise.
	 */
	public final boolean addToBackStack() {
		return mAddToBackStack;
	}

	/**
	 * Sets an arguments for associated fragment.
	 *
	 * @param arguments The desired arguments for fragment.
	 * @return These options to allow methods chaining.
	 * @see Fragment#setArguments(Bundle)
	 * @see #arguments()
	 */
	public final FragmentRequest arguments(@Nullable Bundle arguments) {
		this.mArguments = arguments;
		return this;
	}

	/**
	 * Returns the arguments specified via {@link #arguments(Bundle)}.
	 *
	 * @return Arguments for associated fragment to be shown using these options.
	 */
	@Nullable
	public final Bundle arguments() {
		return mArguments;
	}

	/**
	 * Sets a boolean flag indicating, whether to add or replace associated fragment.
	 * <p>
	 * Default: <b>false</b>
	 *
	 * @return These options to allow methods chaining.
	 * @see FragmentTransaction#add(int, android.support.v4.app.Fragment, String)
	 * @see FragmentTransaction#replace(int, android.support.v4.app.Fragment, String)
	 * @see #add()
	 */
	public final FragmentRequest add(boolean add) {
		this.mAdd = add;
		return this;
	}

	/**
	 * Returns the boolean flag specified via {@link #add(boolean)}.
	 *
	 * @return {@code True} if view of associated fragment will be added into container's view hierarchy,
	 * {@code false} if it will replace the current container's view hierarchy.
	 */
	public final boolean add() {
		return mAdd;
	}

	/**
	 * Sets a transition used to animate views change of incoming and outgoing fragments.
	 *
	 * @param transition Transition with animations.
	 * @return These options to allow methods chaining.
	 * @see FragmentTransaction#setCustomAnimations(int, int)
	 * @see FragmentTransaction#setCustomAnimations(int, int, int, int)
	 * @see #transition()
	 */
	public final FragmentRequest transition(@Nullable FragmentTransition transition) {
		this.mTransition = transition;
		return this;
	}

	/**
	 * Returns the transition specified via {@link #transition(FragmentTransition)}.
	 *
	 * @return Transition used to animate views change of incoming and outgoing fragments associated
	 * with these options.
	 */
	@Nullable
	public final FragmentTransition transition() {
		return mTransition;
	}

	/**
	 * Sets a resource id of the style containing animations used to animate views change of incoming
	 * and outgoing fragments.
	 * <p>
	 * Default: <b>-1</b>
	 *
	 * @param transitionStyle Resource id of the desired style.
	 * @return These options to allow methods chaining.
	 * @see FragmentTransaction#setTransitionStyle(int)
	 * @see #transitionStyle()
	 */
	public final FragmentRequest transitionStyle(@StyleRes int transitionStyle) {
		this.mTransitionStyle = transitionStyle;
		return this;
	}

	/**
	 * Returns the transition style specified via {@link #transitionStyle(int)}.
	 *
	 * @return Style containing animations used to animate views change of incoming and outgoing
	 * fragments.
	 */
	@StyleRes
	public final int transitionStyle() {
		return mTransitionStyle;
	}

	/**
	 * Sets an id of the layout container into which should be an incoming fragment's view placed.
	 * <p>
	 * Default: <b>-1</b>
	 *
	 * @param layoutId An id of the desired layout container to be used as container for associated
	 *                 fragment's view.
	 * @return These options to allow methods chaining.
	 * @see FragmentTransaction#replace(int, android.support.v4.app.Fragment, String)
	 * @see FragmentController#setFragmentContainerId(int)
	 * @see #containerId()
	 */
	public final FragmentRequest containerId(@IdRes int layoutId) {
		this.mContainerId = layoutId;
		return this;
	}

	/**
	 * Returns the id of the layout container specified via {@link #containerId(int)}.
	 *
	 * @return Id of layout container into which should be an incoming fragment's view placed.
	 */
	@IdRes
	public final int containerId() {
		return mContainerId;
	}

	/**
	 * Sets a boolean flag indicating, whether the currently showing fragment with the same TAG can be
	 * replaced by a new one (using this options) or not.
	 * <p>
	 * Default: <b>true</b>
	 *
	 * @param replace {@code True} to replace an existing fragment with the same TAG as specified
	 *                via {@link #tag(String)} with associated one, {@code false} otherwise.
	 * @return These options to allow methods chaining.
	 * @see #replaceSame()
	 */
	public final FragmentRequest replaceSame(boolean replace) {
		this.mReplaceSame = replace;
		return this;
	}

	/**
	 * Return the boolean flag specified via {@link #replaceSame(boolean)}.
	 *
	 * @return {@code True} to replace an existing fragment with the same tag as specified via {@link #tag(String)},
	 * {@code false} otherwise.
	 */
	public final boolean replaceSame() {
		return mReplaceSame;
	}

	/**
	 * Sets a boolean flag indicating, whether a new fragment should be showed immediately or not.
	 * <p>
	 * Default: <b>false</b>
	 *
	 * @param immediate {@code True} to show immediately (synchronously), {@code false} otherwise.
	 * @return These options to allow methods chaining.
	 * @see FragmentManager#executePendingTransactions()
	 * @see #showImmediate()
	 */
	public final FragmentRequest showImmediate(boolean immediate) {
		this.mShowImmediate = immediate;
		return this;
	}

	/**
	 * Returns the boolean flag specified via {@link #showImmediate(boolean)}.
	 *
	 * @return {@code True} if associated fragment should be shown immediately (synchronously),
	 * {@code false} otherwise.
	 */
	public final boolean showImmediate() {
		return mShowImmediate;
	}

	/**
	 * Sets a boolean flag indicating, whether transaction for a new fragment can be committed
	 * allowing state loss or not.
	 * <p>
	 * Default: <b>false</b>
	 *
	 * @param allowing {@code True} to allow state loss when committing transaction, {@code false}
	 *                 otherwise.
	 * @return These options to allow methods chaining.
	 * @see FragmentTransaction#commitAllowingStateLoss()
	 */
	public final FragmentRequest commitAllowingStateLoss(boolean allowing) {
		this.mCommitAllowingStateLoss = allowing;
		return this;
	}

	/**
	 * Returns the boolean flag specified via {@link #commitAllowingStateLoss(boolean)}.
	 *
	 * @return {@code True} if transaction for associated fragment should be committed allowing state
	 * loss, {@code false} to be committed in a standard fashion.
	 */
	public final boolean commitAllowingStateLoss() {
		return mCommitAllowingStateLoss;
	}

	/**
	 * Specifies a shared element view and its name for a new incoming fragment.
	 * <p>
	 * Multiple calls to this method will just append already specified shared elements and theirs
	 * names.
	 * <p>
	 * <b>Note, that this option is only temporary in terms of storing within these options. All
	 * shared elements will be removed from these options immediately after they are passed to
	 * the FragmentTransaction object.</b>
	 *
	 * @param element The view to be shared with the new incoming fragment.
	 * @param name    The name of the shared element.
	 * @return These options to allow methods chaining.
	 * @see FragmentTransaction#addSharedElement(View, String)
	 */
	public final FragmentRequest sharedElement(@NonNull View element, @NonNull String name) {
		return sharedElements(new Pair<>(element, name));
	}

	/**
	 * Same as {@link #sharedElement(View, String)}, but this method allows to specify
	 * a set of shared elements at once.
	 *
	 * @param elements The desired shared elements pairs.
	 * @return These options to allow methods chaining.
	 * @see #sharedElements()
	 */
	@SafeVarargs
	public final FragmentRequest sharedElements(@NonNull Pair<View, String>... elements) {
		if (mSharedElements == null) mSharedElements = new ArrayList<>(1);
		mSharedElements.addAll(Arrays.asList(elements));
		return this;
	}

	/**
	 * Returns all shared elements specified via {@link #sharedElement(View, String)} or
	 * {@link #sharedElements(Pair[])} for these options.
	 *
	 * @return List of shared element pairs for associated fragment.
	 */
	@Nullable
	public final List<Pair<View, String>> sharedElements() {
		return mSharedElements;
	}

	/**
	 * Clears the shared elements attached to these options. This should be called whenever the
	 * shared elements has been already attached to a particular FragmentTransaction object.
	 */
	protected final void clearSharedElements() {
		this.mSharedElements = null;
	}

	/**
	 * Specifies the enter transition for a new incoming fragment.
	 * <p>
	 * Default: <b>none</b>
	 * <p>
	 * See {@link android.support.v4.app.Fragment#setEnterTransition(Object)} for more information.
	 *
	 * @param transition The desired enter transition. Can be {@code null} to clear the default
	 *                   one.
	 * @return These options to allow methods chaining.
	 * @see #enterTransition()
	 */
	public final FragmentRequest enterTransition(@Nullable Object transition) {
		this.setTransitionRequested(ENTER_TRANSITION);
		this.mEnterTransition = transition;
		return this;
	}

	/**
	 * Returns the transition specified via {@link #enterTransition(Object)}.
	 *
	 * @return Enter transition for the associated fragment to be shown.
	 */
	@Nullable
	public final Object enterTransition() {
		return mEnterTransition;
	}

	/**
	 * Specifies the exit transition for a new incoming fragment.
	 * <p>
	 * Default: <b>none</b>
	 * <p>
	 * See {@link android.support.v4.app.Fragment#setExitTransition(Object)} for more information.
	 *
	 * @param transition The desired exit transition. Can be {@code null} to clear the default
	 *                   one.
	 * @return These options to allow methods chaining.
	 * @see #exitTransition()
	 */
	public final FragmentRequest exitTransition(@Nullable Object transition) {
		this.setTransitionRequested(EXIT_TRANSITION);
		this.mExitTransition = transition;
		return this;
	}

	/**
	 * Returns the transition specified via {@link #exitTransition(Object)}.
	 *
	 * @return Exit transition for the associated fragment to be shown.
	 */
	@Nullable
	public final Object exitTransition() {
		return mExitTransition;
	}

	/**
	 * Specifies the reenter transition for a new incoming fragment.
	 * <p>
	 * Default: <b>none</b>
	 * <p>
	 * See {@link android.support.v4.app.Fragment#setReenterTransition(Object)} for more information.
	 *
	 * @param transition The desired reenter transition. Can be {@code null} to clear the default
	 *                   one.
	 * @return These options to allow methods chaining.
	 * @see #reenterTransition()
	 */
	public final FragmentRequest reenterTransition(@Nullable Object transition) {
		this.setTransitionRequested(REENTER_TRANSITION);
		this.mReenterTransition = transition;
		return this;
	}

	/**
	 * Returns the transition specified via {@link #reenterTransition(Object)}.
	 *
	 * @return Re-enter transition for the associated fragment to be shown.
	 */
	@Nullable
	public final Object reenterTransition() {
		return mReenterTransition;
	}

	/**
	 * Specifies the return transition for a new incoming fragment.
	 * <p>
	 * Default: <b>none</b>
	 * <p>
	 * See {@link android.support.v4.app.Fragment#setReturnTransition(Object)} for more information.
	 *
	 * @param transition The desired return transition. Can be {@code null} to clear the default
	 *                   one.
	 * @return These options to allow methods chaining.
	 * @see #returnTransition()
	 */
	public final FragmentRequest returnTransition(@Nullable Object transition) {
		this.setTransitionRequested(RETURN_TRANSITION);
		this.mReturnTransition = transition;
		return this;
	}

	/**
	 * Returns the transition specified via {@link #returnTransition(Object)}.
	 *
	 * @return Return transition for the associated fragment to be shown.
	 */
	@Nullable
	public final Object returnTransition() {
		return mEnterTransition;
	}

	/**
	 * Specifies the enter transition for a shared element of a new incoming fragment.
	 * <p>
	 * Default: <b>none</b>
	 * <p>
	 * See {@link android.support.v4.app.Fragment#setSharedElementEnterTransition(Object)} for more information.
	 *
	 * @param transition The desired shared element's enter transition. Can be {@code null} to
	 *                   clear the default one.
	 * @return These options to allow methods chaining.
	 * @see #sharedElementEnterTransition(Object)
	 */
	public final FragmentRequest sharedElementEnterTransition(@Nullable Object transition) {
		this.setTransitionRequested(SHARED_ELEMENT_ENTER_TRANSITION);
		this.mSharedElementEnterTransition = transition;
		return this;
	}

	/**
	 * Returns the transition specified via {@link #sharedElementEnterTransition(Object)}.
	 *
	 * @return Enter transition for shared element of the associated fragment to be shown.
	 */
	@Nullable
	public final Object sharedElementEnterTransition() {
		return mSharedElementEnterTransition;
	}

	/**
	 * Specifies the return transition for a shared element of a new incoming fragment.
	 * <p>
	 * Default: <b>none</b>
	 * <p>
	 * See {@link android.support.v4.app.Fragment#setSharedElementReturnTransition(Object)} for more information.
	 *
	 * @param transition The desired shared element's return transition. Can be {@code null} to
	 *                   clear the default one.
	 * @return These options to allow methods chaining.
	 * @see #sharedElementReturnTransition(Object)
	 */
	public final FragmentRequest sharedElementReturnTransition(@Nullable Object transition) {
		this.setTransitionRequested(SHARED_ELEMENT_RETURN_TRANSITION);
		this.mSharedElementReturnTransition = transition;
		return this;
	}

	/**
	 * Returns the transition specified via {@link #sharedElementReturnTransition(Object)}.
	 *
	 * @return Return transition for shared element of the associated fragment to be shown.
	 */
	@Nullable
	public final Object sharedElementReturnTransition() {
		return mSharedElementReturnTransition;
	}

	/**
	 * Specifies a boolean flag indicating whether enter transition of a new incoming fragment
	 * can overlap or not.
	 * <p>
	 * Default: <b>false</b>
	 * <p>
	 * See {@link android.support.v4.app.Fragment#setAllowEnterTransitionOverlap(boolean)} for more information.
	 *
	 * @param allowOverlap {@code True} to allow enter transition overlapping, {@code false}
	 *                     otherwise.
	 * @return This options to allow methods chaining.
	 */
	public final FragmentRequest allowEnterTransitionOverlap(boolean allowOverlap) {
		this.mAllowEnterTransitionOverlap = allowOverlap;
		return this;
	}

	/**
	 * Returns the boolean flag specified via {@link #allowEnterTransitionOverlap(boolean)}.
	 *
	 * @return {@code True} to allow enter transition overlapping, {@code false} otherwise.
	 */
	public final boolean allowEnterTransitionOverlap() {
		return mAllowEnterTransitionOverlap;
	}

	/**
	 * Specifies a boolean flag indicating whether return transition of a new incoming fragment
	 * can overlap or not.
	 * <p>
	 * Default: <b>false</b>
	 * <p>
	 * See {@link android.support.v4.app.Fragment#setAllowReturnTransitionOverlap(boolean)} for more information.
	 *
	 * @param allowOverlap {@code True} to allow return transition overlapping, {@code false}
	 *                     otherwise.
	 * @return This options to allow methods chaining.
	 */
	public final FragmentRequest allowReturnTransitionOverlap(boolean allowOverlap) {
		this.mAllowReturnTransitionOverlap = allowOverlap;
		return this;
	}

	/**
	 * Returns the boolean flag specified via {@link #allowReturnTransitionOverlap(boolean)}.
	 *
	 * @return {@code True} to allow return transition overlapping, {@code false} otherwise.
	 */
	public final boolean allowReturnTransitionOverlap() {
		return mAllowReturnTransitionOverlap;
	}

	/**
	 * Specifies that a scene transition with the specified <var>transition</var> flag has been
	 * requested.
	 *
	 * @param transition Flag of the requested transition.
	 */
	private void setTransitionRequested(int transition) {
		this.mRequestedTransitions |= transition;
	}

	/**
	 * todo:
	 *
	 * @return
	 */
	@Nullable
	public Fragment execute() {
		this.assertNotExecuted();
		return mController.executeRequest(this);
	}

	/**
	 * Asserts that this request has not been executed yet. If it has been executed, an exception is
	 * thrown.
	 */
	private void assertNotExecuted() {
		if (mExecuted) throw new IllegalArgumentException("Already executed!");
	}

	/**
	 * Inner classes ===============================================================================
	 */
}
