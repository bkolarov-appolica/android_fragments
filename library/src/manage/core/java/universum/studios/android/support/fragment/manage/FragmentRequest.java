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
package universum.studios.android.support.fragment.manage;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.util.Pair;
import android.transition.Transition;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A request that may be used to replace|add|remove|show|hide|attach|detach a desired {@link Fragment}
 * in a view hierarchy. New instance of request may be created via {@link FragmentController#newRequest(Fragment)}
 * or via {@link FragmentController#newRequest(int)} for fragments provided by {@link FragmentFactory}.
 * When request is created it may be configured via methods listed below and then executed via
 * {@link #execute()}.
 *
 * <h3>Configuration</h3>
 * <ul>
 * <li>{@link #fragmentId(int)}</li>
 * <li>{@link #outgoingFragmentId(int)}</li>
 * <li>{@link #arguments(Bundle)}</li>
 * <li>{@link #transaction(int)}</li>
 * <li>{@link #tag(String)}</li>
 * <li>{@link #viewContainerId(int)}</li>
 * <li>{@link #transition(FragmentTransition)}</li>
 * <li>{@link #transitionStyle(int)}</li>
 * <li>{@link #enterTransition(Transition)}</li>
 * <li>{@link #exitTransition(Transition)}</li>
 * <li>{@link #reenterTransition(Transition)}</li>
 * <li>{@link #returnTransition(Transition)}</li>
 * <li>{@link #allowEnterTransitionOverlap(boolean)}</li>
 * <li>{@link #allowReturnTransitionOverlap(boolean)}</li>
 * <li>{@link #sharedElementEnterTransition(Transition)}</li>
 * <li>{@link #sharedElementReturnTransition(Transition)}</li>
 * <li>{@link #sharedElement(View, String)}</li>
 * <li>{@link #sharedElements(Pair[])}</li>
 * <li>{@link #replaceSame(boolean)}</li>
 * <li>{@link #addToBackStack(boolean)}</li>
 * <li>{@link #executeAllowingStateLoss(boolean)}</li>
 * <li>{@link #executeImmediate(boolean)}</li>
 * </ul>
 * <p>
 * <b>Note, that each fragment request may be executed only once.</b>
 *
 * @author Martin Albedinsky
 * @see FragmentController
 * @see FragmentRequestInterceptor
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
	 * Constant used to determine that no fragment id has been specified.
	 */
	public static final int NO_ID = -1;

	/**
	 * Constant used to determine that no style resource has been specified.
	 */
	public static final int NO_STYLE = -1;

	/**
	 * Defines an annotation for determining available transaction types for {@link #transaction(int)}
	 * method.
	 *
	 * <h3>Available types:</h3>
	 * <ul>
	 * <li>{@link #REPLACE}</li>
	 * <li>{@link #ADD}</li>
	 * <li>{@link #REMOVE}</li>
	 * <li>{@link #SHOW}</li>
	 * <li>{@link #HIDE}</li>
	 * <li>{@link #ATTACH}</li>
	 * <li>{@link #DETACH}</li>
	 * </ul>
	 *
	 * @see #transaction(int)
	 */
	@IntDef({
			REPLACE,
			ADD, REMOVE,
			SHOW, HIDE,
			ATTACH, DETACH
	})
	@Retention(RetentionPolicy.SOURCE)
	public @interface Transaction {
	}

	/**
	 * Fragment transaction type used to indicate that the associated fragment should be committed
	 * using <b>replace</b> operation.
	 *
	 * @see FragmentTransaction#replace(int, Fragment, String)
	 */
	public static final int REPLACE = 0x00;

	/**
	 * Fragment transaction type used to indicate that the associated fragment should be committed
	 * using <b>add</b> operation.
	 *
	 * @see FragmentTransaction#add(int, Fragment, String)
	 */
	public static final int ADD = 0x01;

	/**
	 * Fragment transaction type used to indicate that the associated fragment should be committed
	 * using <b>remove</b> operation.
	 *
	 * @see FragmentTransaction#remove(Fragment)
	 */
	public static final int REMOVE = 0x02;

	/**
	 * Fragment transaction type used to indicate that the associated fragment should be committed
	 * using <b>show</b> operation.
	 *
	 * @see FragmentTransaction#show(Fragment)
	 */
	public static final int SHOW = 0x03;

	/**
	 * Fragment transaction type used to indicate that the associated fragment should be committed
	 * using <b>hide</b> operation.
	 *
	 * @see FragmentTransaction#hide(Fragment)
	 */
	public static final int HIDE = 0x04;

	/**
	 * Fragment transaction type used to indicate that the associated fragment should be committed
	 * using <b>attach</b> operation.
	 *
	 * @see FragmentTransaction#attach(Fragment)
	 */
	public static final int ATTACH = 0x05;

	/**
	 * Fragment transaction type used to indicate that the associated fragment should be committed
	 * using <b>detach</b> operation.
	 *
	 * @see FragmentTransaction#detach(Fragment)
	 */
	public static final int DETACH = 0x06;

	/**
	 * Defines an annotation for determining available boolean flags for FragmentRequest.
	 */
	@IntDef(flag = true, value = {
			REPLACE_SAME,
			ADD_TO_BACK_STACK,
			EXECUTE_ALLOWING_STATE_LOSS,
			EXECUTE_IMMEDIATE
	})
	@Retention(RetentionPolicy.SOURCE)
	private @interface Flag {
	}

	/**
	 * Flag indicating that a same fragment (currently showing) can be replaced by the associated fragment.
	 *
	 * @see FragmentTransaction#replace(int, Fragment, String)
	 */
	static final int REPLACE_SAME = 0x00000001;

	/**
	 * Flag indicating that the associated fragment should be added into back stack.
	 *
	 * @see FragmentTransaction#addToBackStack(String)
	 */
	static final int ADD_TO_BACK_STACK = 0x00000001 << 1;

	/**
	 * Flag indicating that the associated {@link FragmentTransaction} should be committed allowing
	 * state loss.
	 *
	 * @see FragmentTransaction#commitAllowingStateLoss()
	 */
	static final int EXECUTE_ALLOWING_STATE_LOSS = 0x00000001 << 4;

	/**
	 * Flag indicating that the associated {@link FragmentTransaction} should be executed immediately.
	 *
	 * @see FragmentManager#executePendingTransactions()
	 */
	static final int EXECUTE_IMMEDIATE = 0x00000001 << 5;

	/**
	 * Defines an annotation for determining available transition flags for FragmentRequest.
	 */
	@IntDef(flag = true, value = {
			TRANSITION_ENTER,
			TRANSITION_EXIT,
			TRANSITION_REENTER,
			TRANSITION_RETURN,
			TRANSITION_SHARED_ELEMENT_ENTER,
			TRANSITION_SHARED_ELEMENT_RETURN
	})
	@Retention(RetentionPolicy.SOURCE)
	private @interface TransitionFlag {
	}

	/**
	 * Flag indicating whether {@link #mEnterTransition} has been specified or not.
	 */
	static final int TRANSITION_ENTER = 0x00000001;

	/**
	 * Flag indicating whether {@link #mExitTransition} has been specified or not.
	 */
	static final int TRANSITION_EXIT = 0x00000001 << 1;

	/**
	 * Flag indicating whether {@link #mReenterTransition} has been specified or not.
	 */
	static final int TRANSITION_REENTER = 0x00000001 << 2;

	/**
	 * Flag indicating whether {@link #mReturnTransition} has been specified or not.
	 */
	static final int TRANSITION_RETURN = 0x00000001 << 3;

	/**
	 * Flag indicating whether {@link #mSharedElementEnterTransition} has been specified or not.
	 */
	static final int TRANSITION_SHARED_ELEMENT_ENTER = 0x00000001 << 4;

	/**
	 * Flag indicating whether {@link #mSharedElementReturnTransition} has been specified or not.
	 */
	static final int TRANSITION_SHARED_ELEMENT_RETURN = 0x00000001 << 5;

	/**
	 * Static members ==============================================================================
	 */

	/**
	 * Members =================================================================================
	 */

	/**
	 * Fragment instance associated with this request.
	 */
	final Fragment mFragment;

	/**
	 * Controller that has been used to create this request and also is responsible for execution
	 * of this request.
	 *
	 * @see #execute()
	 */
	private final FragmentController mController;

	/**
	 * Id of the associated fragment.
	 */
	private int mFragmentId = NO_ID;

	/**
	 * Id of the outgoing fragment that will be replaced by the associated fragment.
	 */
	private int mOutgoingFragmentId = NO_ID;

	/**
	 * Arguments for the associated fragment.
	 *
	 * @see Fragment#setArguments(Bundle)
	 */
	Bundle mArguments;

	/**
	 * Type determining what {@link FragmentTransaction} to perform for the associated fragment.
	 *
	 * @see Transaction @Transaction
	 */
	int mTransaction = REPLACE;

	/**
	 * Tag for the associated fragment.
	 */
	String mTag;

	/**
	 * Id of a view container where to place view hierarchy of the associated fragment.
	 */
	int mViewContainerId = FragmentController.NO_CONTAINER_ID;

	/**
	 * Transition object specifying transition resources for the associated {@link FragmentTransaction}.
	 *
	 * @see FragmentTransaction#setCustomAnimations(int, int, int, int)
	 */
	FragmentTransition mTransition;

	/**
	 * Resource id of the style containing transitions used to animate fragment.
	 *
	 * @see FragmentTransaction#setTransitionStyle(int)
	 */
	int mTransitionStyle = -1;

	/**
	 * Enter transition the associated fragment.
	 *
	 * @see Fragment#setEnterTransition(Transition)
	 */
	Transition mEnterTransition;

	/**
	 * Exit transition the associated fragment.
	 *
	 * @see Fragment#setExitTransition(Transition)
	 */
	Transition mExitTransition;

	/**
	 * Reenter transition the associated fragment.
	 *
	 * @see Fragment#setReenterTransition(Transition)
	 */
	Transition mReenterTransition;

	/**
	 * Return transition the associated fragment.
	 *
	 * @see Fragment#setReturnTransition(Transition)
	 */
	Transition mReturnTransition;

	/**
	 * Shared element's enter transition the associated fragment.
	 *
	 * @see Fragment#setSharedElementEnterTransition(Transition)
	 */
	Transition mSharedElementEnterTransition;

	/**
	 * Shared element's return transition the associated fragment.
	 *
	 * @see Fragment#setSharedElementReturnTransition(Transition)
	 */
	Transition mSharedElementReturnTransition;

	/**
	 * Transition flags determining which transitions has been specified for this request.
	 *
	 * @see TransitionFlag @TransitionFlag
	 */
	private int mSpecifiedTransitions;

	/**
	 * Flag indicating whether enter transition for the associated fragment can overlap or not.
	 *
	 * @see Fragment#setAllowReturnTransitionOverlap(boolean)
	 */
	Boolean mAllowEnterTransitionOverlap;

	/**
	 * Flag indicating whether return transition for the associated fragment can overlap or not.
	 *
	 * @see Fragment#setAllowEnterTransitionOverlap(boolean)
	 */
	Boolean mAllowReturnTransitionOverlap;

	/**
	 * Set of shared elements for the associated fragment.
	 *
	 * @see FragmentTransaction#addSharedElement(View, String)
	 */
	List<Pair<View, String>> mSharedElements;

	/**
	 * Flags specified for this request.
	 *
	 * @see Flag @Flag
	 */
	private int mFlags;

	/**
	 * Boolean flag indicating whether this request has been already executed via {@link #execute()}
	 * or not.
	 */
	private boolean mExecuted = false;

	/**
	 * Constructors ============================================================================
	 */

	/**
	 * Creates a new instance of FragmentRequest for the given <var>fragment</var>.
	 *
	 * @param fragment   The fragment to associate with the new request.
	 * @param controller Fragment controller that creates the new request and is also responsible
	 *                   for its execution.
	 */
	FragmentRequest(Fragment fragment, FragmentController controller) {
		this.mFragment = fragment;
		this.mController = controller;
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
		builder.append("FragmentRequest{fragmentId: ");
		builder.append(mFragmentId);
		builder.append(", outgoingFragmentId: ");
		builder.append(mOutgoingFragmentId);
		builder.append(", arguments: ");
		builder.append(mArguments);
		builder.append(", transactionType: ");
		builder.append(mTransition);
		builder.append(", tag: ");
		builder.append(mTag);
		builder.append(", viewContainerId: ");
		builder.append(mViewContainerId);
		builder.append(", transition: ");
		builder.append(mTransition != null ? mTransition.getName() : "null");
		builder.append(", transitionStyle: ");
		builder.append(mTransitionStyle);
		builder.append(", replaceSame: ");
		builder.append(hasFlag(REPLACE_SAME));
		builder.append(", addToBackStack: ");
		builder.append(hasFlag(ADD_TO_BACK_STACK));
		builder.append(", executeAllowingStateLoss: ");
		builder.append(hasFlag(EXECUTE_ALLOWING_STATE_LOSS));
		builder.append(", executeImmediate: ");
		builder.append(hasFlag(EXECUTE_IMMEDIATE));
		builder.append(", executed: ");
		builder.append(mExecuted);
		return builder.append("}").toString();
	}

	/**
	 * Returns the fragment instance associated with this request.
	 *
	 * @return This request's fragment.
	 * @see FragmentController#newRequest(Fragment)
	 */
	@NonNull
	public Fragment fragment() {
		return mFragment;
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

	/**
	 * Sets an id of the fragment associated with this request.
	 * <p>
	 * This id along with {@link #outgoingFragmentId()} may be used to determine exact change between
	 * two fragments and configure this request accordingly when using {@link FragmentRequestInterceptor}.
	 *
	 * @param fragmentId The desired fragment id.
	 * @return This request to allow methods chaining.
	 * @see #fragmentId()
	 * @see #outgoingFragmentId(int)
	 */
	public FragmentRequest fragmentId(int fragmentId) {
		this.mFragmentId = fragmentId;
		return this;
	}

	/**
	 * Returns the id of the associated fragment.
	 * <p>
	 * Default value: <b>{@link #NO_ID}</b>
	 *
	 * @return Fragment id or {@link #NO_ID} if no id has been specified.
	 * @see #fragmentId(int)
	 */
	public int fragmentId() {
		return mFragmentId;
	}

	/**
	 * Sets an id of the outgoing fragment that is to be replaced by the fragment associated with
	 * this request.
	 * <p>
	 * This id along with {@link #fragmentId()} may be used to determine exact change between two
	 * fragments and configure this request accordingly when using {@link FragmentRequestInterceptor}.
	 *
	 * @param fragmentId The desired fragment id.
	 * @return This request to allow methods chaining.
	 * @see #outgoingFragmentId()
	 * @see #fragmentId(int)
	 */
	public FragmentRequest outgoingFragmentId(int fragmentId) {
		this.mOutgoingFragmentId = fragmentId;
		return this;
	}

	/**
	 * Returns the id of the outgoing fragment.
	 * <p>
	 * Default value: <b>{@link #NO_ID}</b>
	 *
	 * @return Fragment id or {@link #NO_ID} if no id has been specified.
	 * @see #outgoingFragmentId(int)
	 */
	public int outgoingFragmentId() {
		return mOutgoingFragmentId;
	}

	/**
	 * Sets an arguments for the associated fragment.
	 *
	 * @param arguments The desired arguments for fragment. May be {@code null}.
	 * @return This request to allow methods chaining.
	 * @see Fragment#setArguments(Bundle)
	 * @see #arguments()
	 */
	public FragmentRequest arguments(@Nullable Bundle arguments) {
		this.mArguments = arguments;
		return this;
	}

	/**
	 * Returns the arguments that should be attached to the associated fragment.
	 * <p>
	 * Default value: <b>{@code null}</b>
	 *
	 * @return Arguments for fragment or {@code null} if no arguments have been specified yet.
	 * @see #arguments(Bundle)
	 */
	@Nullable
	public final Bundle arguments() {
		return mArguments;
	}

	/**
	 * Sets a transaction type determining what {@link FragmentTransaction} to perform for the
	 * associated fragment.
	 *
	 * @param transaction The desired transaction type. One of types defined by {@link Transaction @Transaction}
	 *                    annotation.
	 * @return This request to allow methods chaining.
	 * @see #transaction()
	 */
	public FragmentRequest transaction(@Transaction int transaction) {
		this.mTransaction = transaction;
		return this;
	}

	/**
	 * Returns the transaction type determining what {@link FragmentTransaction} to perform.
	 * <p>
	 * Default value: <b>{@link #REPLACE}</b>
	 *
	 * @return One of transaction types defined by {@link Transaction @Transaction} annotation.
	 * @see #transaction(int)
	 */
	@Transaction
	public int transaction() {
		return mTransaction;
	}

	/**
	 * Sets a tag for the associated fragment.
	 *
	 * @param fragmentTag The desired fragment tag. May be {@code null}.
	 * @return This request to allow methods chaining.
	 * @see #tag()
	 * @see Fragment#getTag()
	 */
	public final FragmentRequest tag(@Nullable String fragmentTag) {
		this.mTag = fragmentTag;
		return this;
	}

	/**
	 * Returns the tag by which should be the associated fragment identified.
	 * <p>
	 * Default value: <b>{@code null}</b>
	 *
	 * @return Tag for the associated fragment to be shown using these options.
	 * @see #tag(String)
	 */
	@Nullable
	public final String tag() {
		return mTag;
	}

	/**
	 * Sets an id of a view container where to place view hierarchy of the associated fragment.
	 *
	 * @param containerId The desired view container id.
	 * @return This request to allow methods chaining.
	 * @see #viewContainerId()
	 */
	public FragmentRequest viewContainerId(@IdRes int containerId) {
		this.mViewContainerId = containerId;
		return this;
	}

	/**
	 * Returns the id of view container where a view hierarchy of the associated fragment should be
	 * placed.
	 * <p>
	 * Default value: <b>{@link FragmentController#NO_CONTAINER_ID}</b>
	 *
	 * @return View container id or {@link FragmentController#NO_CONTAINER_ID NO_CONTAINER_ID} if no
	 * id has been specified.
	 * @see #viewContainerId(int)
	 */
	@IdRes
	public int viewContainerId() {
		return mViewContainerId;
	}

	/**
	 * Sets a transition that should be used to provide animation resources for the associated
	 * {@link FragmentTransaction}.
	 *
	 * @param transition Transition providing animation resources.
	 * @return This request to allow methods chaining.
	 * @see FragmentTransaction#setCustomAnimations(int, int, int, int)
	 * @see #transition()
	 */
	public final FragmentRequest transition(@Nullable FragmentTransition transition) {
		this.mTransition = transition;
		return this;
	}

	/**
	 * Returns the transition providing animation resources for {@link FragmentTransaction}.
	 * <p>
	 * Default value: <b>{@code null}</b>
	 *
	 * @return Transition with animation resources used to animate change of view between incoming
	 * and outgoing fragment.
	 */
	@Nullable
	public final FragmentTransition transition() {
		return mTransition;
	}

	/**
	 * Sets a resource id of the style containing transitions used to animate change between incoming
	 * and outgoing fragment.
	 *
	 * @param transitionStyle Resource id of the desired style.
	 * @return This request to allow methods chaining.
	 * @see FragmentTransaction#setTransitionStyle(int)
	 * @see #transitionStyle()
	 */
	public final FragmentRequest transitionStyle(@StyleRes int transitionStyle) {
		this.mTransitionStyle = transitionStyle;
		return this;
	}

	/**
	 * Returns the transition style providing transitions for fragment view changes.
	 * <p>
	 * Default value: <b>{@link #NO_STYLE}</b>
	 *
	 * @return Transition style resource or {@link #NO_STYLE} if no style has been specified.
	 */
	@StyleRes
	public final int transitionStyle() {
		return mTransitionStyle;
	}

	/**
	 * Sets an enter transition for the associated fragment.
	 *
	 * @param transition The desired enter transition. May be {@code null}.
	 * @return This request to allow methods chaining.
	 * @see #enterTransition()
	 * @see Fragment#setEnterTransition(Transition)
	 */
	public FragmentRequest enterTransition(@Nullable Transition transition) {
		this.mSpecifiedTransitions |= TRANSITION_ENTER;
		this.mEnterTransition = transition;
		return this;
	}

	/**
	 * Returns the enter transition to be played for the associated fragment.
	 * <p>
	 * Default value: <b>{@code null}</b>
	 *
	 * @return Transition or {@code null} if no enter transition has been specified yet.
	 * @see #enterTransition(Transition)
	 */
	@Nullable
	public Transition enterTransition() {
		return mEnterTransition;
	}

	/**
	 * Sets an exit transition for the associated fragment.
	 *
	 * @param transition The desired exit transition. May be {@code null}.
	 * @return This request to allow methods chaining.
	 * @see #exitTransition()
	 * @see Fragment#setExitTransition(Transition)
	 */
	public FragmentRequest exitTransition(@Nullable Transition transition) {
		this.mSpecifiedTransitions |= TRANSITION_EXIT;
		this.mExitTransition = transition;
		return this;
	}

	/**
	 * Returns the exit transition to be played for the associated fragment.
	 * <p>
	 * Default value: <b>{@code null}</b>
	 *
	 * @return Transition or {@code null} if no exit transition has been specified yet.
	 * @see #exitTransition(Transition)
	 */
	@Nullable
	public Transition exitTransition() {
		return mExitTransition;
	}

	/**
	 * Sets a reenter transition for the associated fragment.
	 *
	 * @param transition The desired reenter transition. May be {@code null}.
	 * @return This request to allow methods chaining.
	 * @see #reenterTransition()
	 * @see Fragment#setReenterTransition(Transition)
	 */
	public FragmentRequest reenterTransition(@Nullable Transition transition) {
		this.mSpecifiedTransitions |= TRANSITION_REENTER;
		this.mReenterTransition = transition;
		return this;
	}

	/**
	 * Returns the reenter transition to be played for the associated fragment.
	 * <p>
	 * Default value: <b>{@code null}</b>
	 *
	 * @return Transition or {@code null} if no reenter transition has been specified yet.
	 * @see #reenterTransition(Transition)
	 */
	@Nullable
	public Transition reenterTransition() {
		return mReenterTransition;
	}

	/**
	 * Sets a return transition for the associated fragment.
	 *
	 * @param transition The desired return transition. May be {@code null}.
	 * @return This request to allow methods chaining.
	 * @see #exitTransition()
	 * @see Fragment#setReturnTransition(Transition)
	 */
	public FragmentRequest returnTransition(@Nullable Transition transition) {
		this.mSpecifiedTransitions |= TRANSITION_RETURN;
		this.mReturnTransition = transition;
		return this;
	}

	/**
	 * Returns the return transition to be played for the associated fragment.
	 * <p>
	 * Default value: <b>{@code null}</b>
	 *
	 * @return Transition or {@code null} if no return transition has been specified yet.
	 * @see #returnTransition(Transition)
	 */
	@Nullable
	public Transition returnTransition() {
		return mReturnTransition;
	}

	/**
	 * Sets a boolean flag indicating whether enter transition for the associated fragment may overlap
	 * or not.
	 *
	 * @param allowOverlap {@code True} to allow enter transition overlapping, {@code false} otherwise.
	 * @return This request to allow methods chaining.
	 * @see Fragment#setAllowEnterTransitionOverlap(boolean)
	 * @see #allowEnterTransitionOverlap()
	 */
	public final FragmentRequest allowEnterTransitionOverlap(boolean allowOverlap) {
		this.mAllowEnterTransitionOverlap = allowOverlap;
		return this;
	}

	/**
	 * Returns boolean flag indicating whether overlapping for enter transition is allowed.
	 * <p>
	 * Default value: <b>{@code null}</b>
	 *
	 * @return {@code True} if overlapping for enter transition is allowed, {@code false} otherwise
	 * or {@code null} if this option has not been specified yet.
	 * @see #allowEnterTransitionOverlap(boolean)
	 */
	public final Boolean allowEnterTransitionOverlap() {
		return mAllowEnterTransitionOverlap;
	}

	/**
	 * Sets a boolean flag indicating whether return transition for the associated fragment may overlap
	 * or not.
	 *
	 * @param allowOverlap {@code True} to allow return transition overlapping, {@code false} otherwise.
	 * @return This request to allow methods chaining.
	 * @see Fragment#setAllowReturnTransitionOverlap(boolean)
	 * @see #allowReturnTransitionOverlap()
	 */
	public final FragmentRequest allowReturnTransitionOverlap(boolean allowOverlap) {
		this.mAllowReturnTransitionOverlap = allowOverlap;
		return this;
	}

	/**
	 * Returns boolean flag indicating whether overlapping for return transition is allowed.
	 * <p>
	 * Default value: <b>{@code null}</b>
	 *
	 * @return {@code True} if overlapping for return transition is allowed, {@code false} otherwise
	 * or {@code null} if this option has not been specified yet.
	 * @see #allowReturnTransitionOverlap(boolean)
	 */
	public final boolean allowReturnTransitionOverlap() {
		return mAllowReturnTransitionOverlap != null && mAllowReturnTransitionOverlap;
	}

	/**
	 * Bulk method for adding shared element pairs into this request.
	 *
	 * @param elements The desired shared elements pairs.
	 * @return This request to allow methods chaining.
	 * @see #sharedElement(View, String)
	 */
	@SafeVarargs
	public final FragmentRequest sharedElements(@NonNull Pair<View, String>... elements) {
		if (mSharedElements == null) {
			this.mSharedElements = new ArrayList<>(elements.length);
		}
		this.mSharedElements.addAll(Arrays.asList(elements));
		return this;
	}

	/**
	 * Adds a shared element view and its name for the associated fragment.
	 * <p>
	 * Multiple calls to this method will append list of already specified shared element pairs.
	 *
	 * @param element The view to be shared via transition.
	 * @param name    The name of the shared element.
	 * @return This request to allow methods chaining.
	 * @see FragmentTransaction#addSharedElement(View, String)
	 */
	public final FragmentRequest sharedElement(@NonNull View element, @NonNull String name) {
		if (mSharedElements == null) {
			this.mSharedElements = new ArrayList<>(1);
		}
		this.mSharedElements.add(new Pair<>(element, name));
		return this;
	}

	/**
	 * Returns all shared elements specified for this request.
	 *
	 * @return List with shared element pairs for the associated fragment or {@code null} if no
	 * pairs has been specified yet.
	 * @see #sharedElement(View, String)
	 * @see #sharedElements(Pair[])
	 */
	@Nullable
	public final List<Pair<View, String>> sharedElements() {
		return mSharedElements;
	}

	/**
	 * Sets an enter transition for shared elements of the associated fragment.
	 *
	 * @param transition The desired shared elements's enter transition. May be {@code null}.
	 * @return This request to allow methods chaining.
	 * @see #sharedElementEnterTransition()
	 * @see Fragment#setSharedElementEnterTransition(Transition)
	 */
	public FragmentRequest sharedElementEnterTransition(@Nullable Transition transition) {
		this.mSpecifiedTransitions |= TRANSITION_SHARED_ELEMENT_ENTER;
		this.mSharedElementEnterTransition = transition;
		return this;
	}

	/**
	 * Returns the enter transition to be played for shared elements of the associated fragment.
	 * <p>
	 * Default value: <b>{@code null}</b>
	 *
	 * @return Transition or {@code null} if no shared element enter transition has been specified yet.
	 * @see #sharedElementEnterTransition(Transition)
	 */
	@Nullable
	public Transition sharedElementEnterTransition() {
		return mSharedElementEnterTransition;
	}

	/**
	 * Sets an return transition for shared elements of the associated fragment.
	 *
	 * @param transition The desired shared elements's return transition. May be {@code null}.
	 * @return This request to allow methods chaining.
	 * @see #sharedElementEnterTransition()
	 * @see Fragment#setSharedElementReturnTransition(Transition)
	 */
	public FragmentRequest sharedElementReturnTransition(@Nullable Transition transition) {
		this.mSpecifiedTransitions |= TRANSITION_SHARED_ELEMENT_RETURN;
		this.mSharedElementReturnTransition = transition;
		return this;
	}

	/**
	 * Returns the return transition to be played for shared elements of the associated fragment.
	 * <p>
	 * Default value: <b>{@code null}</b>
	 *
	 * @return Transition or {@code null} if no shared element return transition has been specified yet.
	 * @see #sharedElementReturnTransition(Transition)
	 */
	@Nullable
	public Transition sharedElementReturnTransition() {
		return mSharedElementReturnTransition;
	}

	/**
	 * Checks whether a transition with the specified <var>transitionFlag</var> has been specified
	 * for this request or not.
	 * <p>
	 * <b>Note</b>, that also {@code null} transitions may be specified.
	 *
	 * @param transitionFlag One of transition flags defined by {@link TransitionFlag @TransitionFlag}
	 *                       annotation.
	 * @return {@code True} if transition has been specified, {@code false} otherwise.
	 */
	boolean hasTransition(@TransitionFlag int transitionFlag) {
		return (mSpecifiedTransitions & transitionFlag) != 0;
	}

	/**
	 * Sets a boolean flag indicating whether the already showing fragment with the same TAG as
	 * specified for this request may be replaced by the associated fragment or not.
	 *
	 * @param replace {@code True} to replace an existing fragment with the same TAG as specified
	 *                via {@link #tag(String)} with associated one, {@code false} otherwise.
	 * @return This request to allow methods chaining.
	 * @see #replaceSame()
	 */
	public final FragmentRequest replaceSame(boolean replace) {
		return setHasFlag(REPLACE_SAME, replace);
	}

	/**
	 * Return boolean flag indicating whether already showing fragment with the same TAG may be replaced
	 * by a new one.
	 * <p>
	 * Default value: <b>{@code false}</b>
	 *
	 * @return {@code True} if already showing fragment with the same tag may be replaced, {@code false}
	 * otherwise.
	 * @see #replaceSame(boolean)
	 */
	public final boolean replaceSame() {
		return hasFlag(REPLACE_SAME);
	}

	/**
	 * Sets a boolean flag indicating whether the associated fragment should be added into fragments
	 * back stack under its tag or not.
	 *
	 * @param add {@code True} to add fragment into back stack, {@code false} otherwise.
	 * @return This request to allow methods chaining.
	 * @see FragmentTransaction#addToBackStack(String)
	 * @see #addToBackStack()
	 */
	public final FragmentRequest addToBackStack(boolean add) {
		return setHasFlag(ADD_TO_BACK_STACK, add);
	}

	/**
	 * Returns boolean indicating whether to add fragment into back stack.
	 * <p>
	 * Default value: <b>{@code false}</b>
	 *
	 * @return {@code True} if to add the associated fragment into back stack, {@code false} otherwise.
	 * @see #addToBackStack(boolean)
	 */
	public final boolean addToBackStack() {
		return hasFlag(ADD_TO_BACK_STACK);
	}

	/**
	 * Sets a boolean flag indicating whether {@link FragmentTransaction} for the associated fragment
	 * may be committed allowing state loss or not.
	 *
	 * @param allowing {@code True} to allow state loss when committing transaction, {@code false}
	 *                 otherwise.
	 * @return This request to allow methods chaining.
	 * @see FragmentTransaction#commitAllowingStateLoss()
	 */
	public final FragmentRequest executeAllowingStateLoss(boolean allowing) {
		return setHasFlag(EXECUTE_ALLOWING_STATE_LOSS, allowing);
	}

	/**
	 * Returns boolean flag indicating whether to commit fragment transaction allowing state loss.
	 * <p>
	 * Default value: <b>{@code false}</b>
	 *
	 * @return {@code True} if transaction may be committed allowing state loss, {@code false} otherwise.
	 * @see #executeAllowingStateLoss(boolean)
	 */
	public final boolean executeAllowingStateLoss() {
		return hasFlag(EXECUTE_ALLOWING_STATE_LOSS);
	}

	/**
	 * Sets a boolean flag indicating whether {@link FragmentTransaction} for the associated fragment
	 * should be executed immediately or not.
	 *
	 * @param immediate {@code True} to execute immediately (synchronously), {@code false} otherwise
	 *                  (asynchronously).
	 * @return This request to allow methods chaining.
	 * @see FragmentManager#executePendingTransactions()
	 * @see #executeImmediate()
	 */
	public FragmentRequest executeImmediate(boolean immediate) {
		return setHasFlag(EXECUTE_IMMEDIATE, immediate);
	}

	/**
	 * Returns boolean indicating whether to execute fragment transaction immediately.
	 * <p>
	 * Default value: <b>{@code false}</b>
	 *
	 * @return {@code True} if fragment transaction should be executed immediately (synchronously),
	 * {@code false} otherwise (asynchronously).
	 * @see #executeImmediate(boolean)
	 */
	public boolean executeImmediate() {
		return hasFlag(EXECUTE_IMMEDIATE);
	}

	/**
	 * Sets whether this request has the specified <var>flag</var> registered or not.
	 *
	 * @param flag One of flags defined by {@link Flag @Flag} annotation.
	 * @param has  {@code True} to determine that this request has this flag, {@code false} that it
	 *             has not.
	 * @return This request to allow methods chaining.
	 */
	private FragmentRequest setHasFlag(@Flag int flag, boolean has) {
		if (has) this.mFlags |= flag;
		else this.mFlags &= ~flag;
		return this;
	}

	/**
	 * Checks whether this request has the specified <var>flag</var> registered or not.
	 *
	 * @param flag One of flags defined by {@link Flag @Flag} annotation.
	 * @return {@code True} if flag is registered, {@code false} otherwise.
	 */
	boolean hasFlag(@Flag int flag) {
		return (mFlags & flag) != 0;
	}

	/**
	 * Executes this request via the associated {@link FragmentController} that was used to create
	 * this request instance.
	 * <p>
	 * <b>Note</b>, that each request may be executed only once and any subsequent calls to this
	 * method will throw an exception.
	 *
	 * @return The fragment associated with this request. This may be either fragment already associated
	 * with this request or fragment that is already showing with the same TAG and should not be
	 * replaced by a new one.
	 * @throws IllegalStateException    If this request has been already executed.
	 * @throws IllegalArgumentException If current configuration of this request does not meet the
	 *                                  requirements. For example, request with transaction type of
	 *                                  {@link #REPLACE} or {@link #ADD} cannot be executed without
	 *                                  view container id specified.
	 */
	@NonNull
	public Fragment execute() {
		this.assertNotExecuted();
		switch (mTransaction) {
			case REPLACE:
			case ADD:
				if (mViewContainerId == FragmentController.NO_CONTAINER_ID) {
					throw new IllegalArgumentException("Cannot execute request for REPLACE|ADD transaction. No view container id specified!");
				}
				break;
		}
		return mController.executeRequest(this);
	}

	/**
	 * Asserts that this request has not been executed yet. If it has been executed, an exception is
	 * thrown.
	 */
	private void assertNotExecuted() {
		if (mExecuted) throw new IllegalStateException("Already executed!");
	}

	/**
	 * Returns boolean flag indicating whether this request has been executed.
	 *
	 * @return {@code True} if {@link #execute()} has been called for this request, {@code false}
	 * otherwise.
	 */
	public boolean executed() {
		return mExecuted;
	}

	/**
	 * Inner classes ===============================================================================
	 */
}
