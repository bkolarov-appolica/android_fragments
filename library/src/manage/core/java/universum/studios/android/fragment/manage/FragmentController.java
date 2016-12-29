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

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import universum.studios.android.fragment.FragmentsConfig;
import universum.studios.android.fragment.transition.BasicFragmentTransition;

/**
 * FragmentController class is designed primarily to simplify management of showing and hiding
 * of {@link Fragment Fragments} within an Android application. For proper working, it is necessary
 * that a specific instance of FragmentController has specified an id of the layout container via
 * {@link #setFragmentContainerId(int)} into which will be placed view hierarchies of the new fragments.
 * <p>
 * The desired fragment can be simply shown via {@link #showFragment(android.support.v4.app.Fragment)}.
 * Such a fragment will be shown without any transition and {@link #FRAGMENT_TAG} will be used as
 * tag for that fragment. If you want to specify more options determining how should be your desired
 * fragment shown, use {@link #showFragment(android.support.v4.app.Fragment, FragmentTransactionOptions)}
 * and specify your required options via {@link FragmentTransactionOptions} object.
 *
 * <h3>Fragment factory</h3>
 * The best advantage of the FragmentController and globally of this library can be accomplished by
 * using of {@link FragmentFactory} attached to fragment controller. Basically in your application
 * you will use directly instances of the FragmentController to show/hide/find your application's
 * fragments and for each of your screens (Activities) you can define one FragmentFactory that will
 * provide fragment instances for a specific part of that screen. For example you can specify one
 * fragment factory for your home activity with <b>navigation drawer</b> where that factory will
 * provide fragments for each of the items within the navigation menu. Than you can specify another
 * fragment factory for profile activity and that factory will provide all fragments used within that
 * activity.
 * <p>
 * The factory for FragmentController can be specified via {@link #setFragmentFactory(FragmentController.FragmentFactory)}.
 * Fragments provided by attached factory can be than shown via {@link #showFragment(FragmentTransactionOptions)}.
 * <b>Note, that it is required that factory is attached to the controller before calling one of these
 * methods, otherwise exception will be thrown.</b>
 * <p>
 * You can use {@link BaseFragmentFactory} that specifies base implementation of FragmentFactory so
 * you do not need to implement all those required methods, basically you will need to implement
 * only {@link BaseFragmentFactory#onConfigureTransactionOptions(FragmentTransactionOptions)} method
 * if using fragment annotations.
 *
 * <h3>Callbacks</h3>
 * If you want to listen for changes in fragments when they are being shown, you can attach
 * {@link OnChangeListener} to the FragmentController via {@link #addOnChangeListener(FragmentController.OnChangeListener)}
 * that will receive callback whenever a new fragment is shown via this controller.
 * <p>
 * If you want to listen for changes in the fragments back stack whenever a new fragment is added to
 * the stack or an old one removed from the stack, you can attach {@link OnBackStackChangeListener}
 * to the FragmentController via {@link #addOnBackStackChangeListener(FragmentController.OnBackStackChangeListener)}
 * that will receive callback whenever such a change occurs.
 *
 * @author Martin Albedinsky
 * @see universum.studios.android.fragment.manage.FragmentController.FragmentFactory
 * @see FragmentTransactionOptions
 */
public class FragmentController {

	/**
	 * Interface ===================================================================================
	 */

	/**
	 * Required interface for fragment factory that can be used to supply fragment instances for
	 * {@link universum.studios.android.fragment.manage.FragmentController}.
	 *
	 * @author Martin Albedinsky
	 */
	public interface FragmentFactory {

		/**
		 * Returns an instance of the fragment associated with the specified <var>options</var>
		 * within this fragment factory.
		 *
		 * @param options Options used to show the requested fragment via {@link FragmentTransaction}.
		 * @return Instance of the fragment associated with the <var>options</var> or {@code null} if
		 * this fragment factory doesn't provide requested fragment.
		 * @see #isFragmentProvided(int)
		 */
		@Nullable
		Fragment createFragmentInstance(@NonNull FragmentTransactionOptions options);

		/**
		 * Configures the specified <var>options</var> for the fragment associated with them
		 * within this fragment factory.
		 *
		 * @param options Transaction options to be configured and to be used by {@link FragmentController}
		 *                to show the associated fragment.
		 * @return Configured options for the associated fragment or unchanged options if this fragment
		 * factory does not provide the associated fragment.
		 */
		@NonNull
		FragmentTransactionOptions configureTransactionOptions(@NonNull FragmentTransactionOptions options);

		/**
		 * Returns a tag for the fragment associated with the specified <var>fragmentId</var> within
		 * this fragment factory.
		 *
		 * @param fragmentId An id of the fragment for which is its TAG requested.
		 * @return Tag for fragment associated with the specified <var>fragmentId</var> or {@code null}
		 * if this fragment factory does not provide TAG for fragment with the specified id.
		 */
		@Nullable
		String getFragmentTag(@IntRange(from = 0) int fragmentId);

		/**
		 * Returns flag indicating whether there is provided a fragment for the specified <var>fragmentId</var>
		 * by this factory or not.
		 *
		 * @param fragmentId An id of the desired fragment to check.
		 * @return {@code True} if fragment is provided, so {@link #createFragmentInstance(FragmentTransactionOptions)}
		 * will return an instance of such a fragment, {@code false} otherwise.
		 */
		boolean isFragmentProvided(@IntRange(from = 0) int fragmentId);
	}

	/**
	 * Listener that can receive a callback about changed fragment whenever one of methods of
	 * {@link FragmentController} is called to show a specific fragment instance, like {@link #showFragment(Fragment)}.
	 *
	 * @author Martin Albedinsky
	 * @see #setOnChangeListener(FragmentController.OnChangeListener)
	 */
	public interface OnChangeListener {

		/**
		 * Invoked whenever an old fragment is replaced by a new one or simply a new fragment is first
		 * time showed by an instance of FragmentController.
		 *
		 * @param id          An id of the currently changed (showed) fragment.
		 * @param tag         A tag of the currently changed (showed) fragment.
		 * @param fromFactory {@code True} if the changed fragment was obtained from a factory,
		 *                    {@code false} otherwise.
		 */
		void onFragmentChanged(int id, @Nullable String tag, boolean fromFactory);
	}

	/**
	 * Listener that can receive a callback about changed fragments back stack whenever a new or old
	 * fragment is added/removed to/from the stack.
	 *
	 * @author Martin Albedinsky
	 * @see #addOnBackStackChangeListener(FragmentController.OnBackStackChangeListener)
	 */
	public interface OnBackStackChangeListener {

		/**
		 * Invoked whenever fragments back stack change occur.
		 *
		 * @param added {@code True} if there was added new back stack entry, {@code false}
		 *              if old one was removed.
		 * @param id    An id of the back stack entry of which status was changed. This is actually
		 *              a position of the added/removed entry in the fragments back stack. This is
		 *              default behaviour of the fragments back stack managed by {@link android.support.v4.app.FragmentManager}.
		 * @param tag   A tag of the back stack entry of which status was changed.
		 */
		void onFragmentsBackStackChanged(boolean added, int id, @Nullable String tag);
	}

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	private static final String TAG = "FragmentController";

	/**
	 * Default tag used when showing fragments.
	 */
	public static final String FRAGMENT_TAG = "universum.studios.android.fragment.TAG.Fragment";

	/**
	 * Flag indicating whether we can attach transitions to a fragment instance at the current Android
	 * API level or not.
	 */
	private static final boolean CAN_ATTACH_TRANSITIONS = Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT;

	/**
	 * Static members ==============================================================================
	 */

	/**
	 * Members =====================================================================================
	 */

	/**
	 * Fragment manager to handle showing, obtaining and hiding fragments.
	 */
	final FragmentManager mFragmentManager;

	/**
	 * Fragment factory which provides fragment instances to show (manage) by this controller.
	 */
	private FragmentFactory mFactory;

	/**
	 * Id of a layout container within the current window view hierarchy, into which will be view of
	 * all managed fragments placed.
	 */
	private int mFragmentContainerId = -1;

	/**
	 * The entry at the top of the fragments back stack.
	 */
	private FragmentManager.BackStackEntry mTopBackStackEntry;

	/**
	 * List of listener callbacks for back stack changes.
	 */
	private List<OnBackStackChangeListener> mBackStackChangeListeners;

	/**
	 * List of listener callbacks for fragment changes.
	 */
	private List<OnChangeListener> mChangeListeners;

	/**
	 * Tag of the currently showing fragment showed by this controller
	 */
	private String mCurrentFragmentTag;

	/**
	 * Constructors ================================================================================
	 */

	/**
	 * Creates a new instance of FragmentController within the given <var>parentFragment</var>'s context.
	 * <p>
	 * Passed fragment will be used to obtain an instance of {@link android.support.v4.app.FragmentManager}
	 * via {@link android.support.v4.app.Fragment#getFragmentManager()} that will be used to mange
	 * fragments by this controller.
	 * <p>
	 * If the given <var>parentFragment</var> implements {@link FragmentController.OnBackStackChangeListener}
	 * or {@link universum.studios.android.fragment.manage.FragmentController.OnChangeListener}
	 * it will be automatically attached to the new controller as such listener.
	 *
	 * @param parentFragment The fragment in which will be this controller used.
	 * @see #FragmentController(android.support.v4.app.FragmentActivity)
	 */
	public FragmentController(@NonNull Fragment parentFragment) {
		this(parentFragment.getFragmentManager());
		if (parentFragment instanceof OnBackStackChangeListener) {
			addOnBackStackChangeListener((OnBackStackChangeListener) parentFragment);
		}
		if (parentFragment instanceof OnChangeListener) {
			setOnChangeListener((OnChangeListener) parentFragment);
		}
	}

	/**
	 * Creates a new instance of FragmentController within the given <var>parentActivity</var>'s context.
	 * <p>
	 * Passed activity will be used to obtain an instance of {@link android.support.v4.app.FragmentManager}
	 * via {@link android.support.v4.app.FragmentActivity#getFragmentManager()} that will be used to
	 * mange fragments by this controller.
	 * <p>
	 * If the given <var>parentActivity</var> implements {@link FragmentController.OnBackStackChangeListener}
	 * or {@link universum.studios.android.fragment.manage.FragmentController.OnChangeListener}
	 * it will be automatically attached to the new controller as such listener.
	 *
	 * @param parentActivity The activity in which will be this controller used.
	 * @see #FragmentController(android.support.v4.app.Fragment)
	 */
	public FragmentController(@NonNull FragmentActivity parentActivity) {
		this(parentActivity.getSupportFragmentManager());
		if (parentActivity instanceof OnBackStackChangeListener) {
			addOnBackStackChangeListener((OnBackStackChangeListener) parentActivity);
		}
		if (parentActivity instanceof OnChangeListener) {
			addOnChangeListener((OnChangeListener) parentActivity);
		}
	}

	/**
	 * Creates a new instance of FragmentController with the given <var>fragmentManager</var>.
	 *
	 * @param fragmentManager Fragment manager that will be used to manage fragments.
	 * @see #FragmentController(android.support.v4.app.FragmentActivity)
	 * @see #FragmentController(android.support.v4.app.Fragment)
	 */
	public FragmentController(@NonNull FragmentManager fragmentManager) {
		this.mFragmentManager = fragmentManager;
		mFragmentManager.addOnBackStackChangedListener(new BackStackListener());
		// Check for back stacked fragments.
		final int n = mFragmentManager.getBackStackEntryCount();
		if (n > 0) {
			this.mTopBackStackEntry = mFragmentManager.getBackStackEntryAt(n - 1);
		}
	}

	/**
	 * Methods =====================================================================================
	 */

	/**
	 * Returns the fragment manager passed to this controller during its initialization.
	 *
	 * @return Instance of FragmentManager.
	 */
	@NonNull
	public FragmentManager getFragmentManager() {
		return mFragmentManager;
	}

	/**
	 * Sets an id of the layout container used to host root views of all fragments manager by this
	 * controller.
	 *
	 * @param layoutId The desired id of layout container within the current window view hierarchy,
	 *                 into which should be views of all managed fragments placed.
	 * @see #getFragmentContainerId()
	 */
	public void setFragmentContainerId(@IdRes int layoutId) {
		this.mFragmentContainerId = layoutId;
	}

	/**
	 * Returns the id of the layout container for fragment views.
	 *
	 * @return The desired id of layout container within the current window view hierarchy, into which
	 * should be views of all managed fragments placed or {@code -1} as default.
	 * @see #setFragmentContainerId(int)
	 */
	@IdRes
	public int getFragmentContainerId() {
		return mFragmentContainerId;
	}

	/**
	 * Returns a boolean flag indicating whether this controller has factory attached or not.
	 *
	 * @return {@code True} if factory is attached, {@code false} otherwise.
	 */
	public boolean hasFragmentFactory() {
		return mFactory != null;
	}

	/**
	 * Sets the factory for this fragment controller.
	 *
	 * @param factory The desired factory. May {@code null} to clear the current one.
	 * @see #getFragmentFactory()
	 * @see #hasFragmentFactory()
	 */
	public void setFragmentFactory(@Nullable FragmentFactory factory) {
		this.mFactory = factory;
	}

	/**
	 * Returns the current fragment factory of this controller instance.
	 *
	 * @return Instance of fragment factory or {@code null} if there was no factory attached yet.
	 * @see #setFragmentFactory(universum.studios.android.fragment.manage.FragmentController.FragmentFactory)
	 * @see #hasFragmentFactory()
	 */
	@Nullable
	public FragmentFactory getFragmentFactory() {
		return mFactory;
	}

	/**
	 * Adds a callback to be invoked when fragments are being changed.
	 *
	 * @param listener The desired listener callback.
	 * @deprecated Use {@link #addOnChangeListener(OnChangeListener)} instead.
	 */
	@Deprecated
	public void setOnChangeListener(@NonNull OnChangeListener listener) {
		if (mChangeListeners == null) mChangeListeners = new ArrayList<>(1);
		if (!mChangeListeners.contains(listener)) mChangeListeners.add(listener);
	}

	/**
	 * Adds a callback to be invoked when fragments are being changed.
	 * <p>
	 * <b>Note, that in case of fragments provided by {@link FragmentFactory} this callback is properly
	 * invoked only if fragments are showing immediately. See {@link FragmentTransactionOptions#showImmediate()}
	 * for more information.</b>
	 *
	 * @param listener The desired listener callback to be added.
	 * @see #removeOnChangeListener(OnChangeListener)
	 */
	public void addOnChangeListener(@NonNull OnChangeListener listener) {
		if (mChangeListeners == null) mChangeListeners = new ArrayList<>(1);
		if (!mChangeListeners.contains(listener)) mChangeListeners.add(listener);
	}

	/**
	 * Removes the specified callback from the current change listeners.
	 *
	 * @param listener The listener callback to be removed.
	 * @see #addOnChangeListener(OnChangeListener)
	 */
	public void removeOnChangeListener(@NonNull OnChangeListener listener) {
		if (mChangeListeners != null) mChangeListeners.remove(listener);
	}

	/**
	 * Adds a callback to be invoked when some change occur in the fragments back stack.
	 *
	 * @param listener The desired listener callback to be added.
	 * @see #removeOnBackStackChangeListener(OnBackStackChangeListener)
	 */
	public void addOnBackStackChangeListener(@NonNull OnBackStackChangeListener listener) {
		if (mBackStackChangeListeners == null) mBackStackChangeListeners = new ArrayList<>(1);
		if (!mBackStackChangeListeners.contains(listener)) mBackStackChangeListeners.add(listener);
	}

	/**
	 * Removes the specified callback from the current back stack change listener.
	 *
	 * @param listener The listener callback to be removed.
	 * @see #addOnBackStackChangeListener(OnBackStackChangeListener)
	 */
	public void removeOnBackStackChangeListener(@NonNull OnBackStackChangeListener listener) {
		if (mBackStackChangeListeners != null) mBackStackChangeListeners.remove(listener);
	}

	/**
	 * Same as {@link #showFragment(FragmentTransactionOptions)} with instance of transaction options
	 * instantiated with the specified <var>fragmentId</var>.
	 *
	 * @param fragmentId Factory id of the desired fragment to be shown.
	 * @see FragmentTransactionOptions#FragmentTransactionOptions(int)
	 */
	public boolean showFragment(@IntRange(from = 0) int fragmentId) {
		return showFragment(new FragmentTransactionOptions(fragmentId));
	}

	/**
	 * Same as {@link #showFragment(FragmentTransactionOptions)} with instance of transaction options
	 * instantiated with the specified <var>fragmentId</var> and attached <var>arguments</var>
	 *
	 * @param fragmentId Factory id of the desired fragment to be shown.
	 * @param arguments  Arguments to be attached to the fragment.
	 * @see #showFragment(int)
	 * @see FragmentTransactionOptions#FragmentTransactionOptions(int)
	 * @see FragmentTransactionOptions#arguments(Bundle)
	 */
	public boolean showFragment(@IntRange(from = 0) int fragmentId, @Nullable Bundle arguments) {
		return showFragment(new FragmentTransactionOptions(fragmentId).arguments(arguments));
	}

	/**
	 * Shows a fragment provided by the current factory associated with the specified <var>transactionOptions</var>.
	 *
	 * @param transactionOptions Options used to show the requested fragment via {@link FragmentTransaction}.
	 *                           These options will be delivered to the current factory for configuration
	 *                           via {@link FragmentFactory#configureTransactionOptions(FragmentTransactionOptions)}
	 *                           before this controller properly set ups transaction for the fragment.
	 * @return {@code True} if the requested fragment has been successfully shown, {@code false}
	 * if the current factory does not provide fragment for the specified options or there is already
	 * fragment with the same TAG already showing and should not be replaced.
	 * @throws IllegalStateException If this controller does not have factory attached.
	 * @see #showFragment(int)
	 * @see #showFragment(int, Bundle)
	 */
	public boolean showFragment(@NonNull FragmentTransactionOptions transactionOptions) {
		// Check if we have fragment factory and fragment is provided.
		final int fragmentId = transactionOptions.incomingFragmentId;
		if (!providesFactoryFragmentWithId(fragmentId)) {
			Log.e(
					TAG, "Current factory(" + mFactory.getClass().getSimpleName() + ") does not " +
							"provide fragment for the requested id(" + fragmentId + ")."
			);
			return false;
		}
		return this.performShowFactoryFragment(transactionOptions);
	}

	/**
	 * Checks whether this controller instance has attached fragment factory that provides an instance
	 * of fragment with the specified <var>dialogId</var>.
	 *
	 * @param fragmentId Id of the desired fragment to check.
	 * @return {@code True} if the current factory provides fragment with requested id, {@code false}
	 * otherwise.
	 * @throws IllegalStateException If this controller does not have factory attached.
	 */
	private boolean providesFactoryFragmentWithId(int fragmentId) {
		this.checkAvailableFactoryOrThrowException();
		return mFactory.isFragmentProvided(fragmentId);
	}

	/**
	 * Checks whether this controller has attached fragment factory or not. If not this method
	 * will throw {@link IllegalStateException}.
	 */
	private void checkAvailableFactoryOrThrowException() {
		if (mFactory == null) throw new IllegalStateException(
				"There is no FragmentFactory attached to FragmentController(" + this + ")."
		);
	}

	/**
	 * Performs showing of a fragment obtained from the current fragment factory.
	 *
	 * @param transactionOptions Options used when showing the requested fragment.
	 * @return {@code True} if showing was successful, {@code false} otherwise.
	 */
	private boolean performShowFactoryFragment(FragmentTransactionOptions transactionOptions) {
		// First obtain fragment instance then fragment tag.
		Fragment fragment = mFactory.createFragmentInstance(transactionOptions);
		if (fragment == null) {
			// Invalid fragment instance.
			Log.e(TAG, "No fragment instance has been provided by the factory(" + mFactory.getClass().getSimpleName() + ") " +
					"for the requested incoming fragment id(" + transactionOptions.incomingFragmentId + ").");
			return false;
		}
		final boolean success = onShowFragment(fragment, mFactory.configureTransactionOptions(transactionOptions));
		return success && notifyFragmentChanged(transactionOptions.incomingFragmentId, fragment.getTag(), true);
	}

	/**
	 * Same as {@link #showFragment(Fragment, String)} with {@link #FRAGMENT_TAG}
	 * as tag for fragment.
	 */
	public boolean showFragment(@NonNull Fragment fragment) {
		return showFragment(fragment, FRAGMENT_TAG);
	}

	/**
	 * Same as {@link #showFragment(android.support.v4.app.Fragment, FragmentTransactionOptions)}
	 * with default transaction options with the specified <var>fragmentTag</var> attached.
	 *
	 * @see #showFragment(Fragment)
	 * @see FragmentTransactionOptions#tag(String)
	 */
	public boolean showFragment(@NonNull Fragment fragment, @Nullable String fragmentTag) {
		return showFragment(
				fragment,
				new FragmentTransactionOptions().tag(fragmentTag)
		);
	}

	/**
	 * Shows the given <var>fragment</var> using the specified options.
	 *
	 * @param fragment The fragment instance to show.
	 * @param options  Options used by this controller to properly set up fragment transaction.
	 * @return {@code True} if the fragment has been successfully shown, {@code false}
	 * if there is already fragment with the same TAG already showing and should not be replaced.
	 * @see #showFragment(Fragment)
	 * @see #showFragment(Fragment, String)
	 */
	public boolean showFragment(@NonNull Fragment fragment, @Nullable FragmentTransactionOptions options) {
		final boolean success = onShowFragment(fragment, options);
		return success && notifyFragmentChanged(fragment.getId(), fragment.getTag(), false);
	}

	/**
	 * Invoked to show the given fragment instance using the given options.
	 *
	 * @param fragment Fragment to show.
	 * @param options  Transaction options for the given fragment. If the given <var>options</var>
	 *                 are invalid, the default options will be used.
	 * @return {@code True} if showing has been successful, {@code false} if there is already
	 * fragment showing with the same tag as specified by the options.
	 * @throws IllegalStateException If the current id for layout container is invalid.
	 */
	@SuppressWarnings("NewApi")
	protected boolean onShowFragment(@NonNull Fragment fragment, @Nullable FragmentTransactionOptions options) {
		if (options == null) {
			options = new FragmentTransactionOptions();
		}
		if (!options.mReplaceSame) {
			// Do not replace same fragment.
			Fragment currentFragment = mFragmentManager.findFragmentByTag(options.mTag);
			if (currentFragment != null) {
				if (FragmentsConfig.LOG_ENABLED) {
					Log.v(TAG, "Fragment with tag(" + options.mTag + ") is already showing or within the back-stack.");
				}
				return false;
			}
		}
		// Check if we have place where the fragment should be placed.
		if (options.mContainerId <= 0) {
			if (mFragmentContainerId <= 0) {
				// No id provided for the layout where should be fragment placed.
				throw new IllegalStateException(
						"No id specified for the layout container into which should be " +
								"requested fragment's view placed."
				);
			} else {
				options.mContainerId = mFragmentContainerId;
			}
		}
		final FragmentTransaction transaction = this.buildTransaction(fragment, options);
		if (options.mAddToBackStack) {
			if (FragmentsConfig.DEBUG_LOG_ENABLED) {
				Log.d(TAG, "Fragment(" + fragment + ") added to back stack under the tag(" + fragment.getTag() + ").");
			}
		}
		this.attachTransitionsToFragment(fragment, options);
		// Add shared elements if any.
		if (options.mSharedElements != null && CAN_ATTACH_TRANSITIONS) {
			final List<Pair<View, String>> elements = options.mSharedElements;
			for (Pair<View, String> pair : elements) {
				transaction.addSharedElement(pair.first, pair.second);
			}
			options.clearSharedElements();
		}
		return onCommitTransaction(transaction, options);
	}

	/**
	 * Builds a new FragmentTransaction using the specified transaction <var>options</var>.
	 *
	 * @param fragment A fragment for which to build the new transaction.
	 * @param options  The options from which to build the new transaction.
	 * @return New instance of FragmentTransaction.
	 */
	private FragmentTransaction buildTransaction(Fragment fragment, FragmentTransactionOptions options) {
		final FragmentTransaction transaction = beginTransaction();
		// Apply animations to the transaction from the FragmentTransition parameter.
		if (options.mTransition != null && options.mTransition != BasicFragmentTransition.NONE) {
			final BasicFragmentTransition trans = options.mTransition;

			/**
			 * <pre>
			 * There are provided 4 animations:
			 * First two for currently incoming and outgoing fragment.
			 * Second two for incoming fragment from back stack and
			 * currently outgoing fragment.
			 * </pre>
			 */
			transaction.setCustomAnimations(
					trans.mInAnimRes, trans.mOutAnimRes,
					trans.mInBackAnimRes, trans.mOutBackAnimRes
			);
		} else if (options.mTransitionStyle != -1) {
			transaction.setTransitionStyle(options.mTransitionStyle);
		}
		if (options.mAdd) {
			transaction.add(options.mContainerId, fragment, options.mTag);
		} else {
			transaction.replace(options.mContainerId, fragment, options.mTag);
		}
		// Add fragment to back stack if requested.
		if (options.mAddToBackStack) {
			transaction.addToBackStack(fragment.getTag());
		}
		return transaction;
	}

	/**
	 * Delegate method for {@link android.support.v4.app.FragmentManager#beginTransaction()}.
	 * <p>
	 * <b>Do not forget to commit here created transaction.</b>
	 */
	@NonNull
	@SuppressLint("CommitTransaction")
	public FragmentTransaction beginTransaction() {
		return mFragmentManager.beginTransaction();
	}

	/**
	 * Attaches all transitions contained within the specified <var>options</var> to the given <var>fragment</var>.
	 *
	 * @param fragment The fragment instance to which to attach the transitions.
	 * @param options  Options holding the desired transitions for the fragment.
	 */
	@SuppressWarnings("NewApi")
	private void attachTransitionsToFragment(Fragment fragment, FragmentTransactionOptions options) {
		if (!CAN_ATTACH_TRANSITIONS) return;
		final int requested = options.mRequestedTransitions;
		if ((requested & FragmentTransactionOptions.ENTER_TRANSITION) != 0) {
			fragment.setEnterTransition(options.mEnterTransition);
		}
		if ((requested & FragmentTransactionOptions.EXIT_TRANSITION) != 0) {
			fragment.setExitTransition(options.mExitTransition);
		}
		if ((requested & FragmentTransactionOptions.REENTER_TRANSITION) != 0) {
			fragment.setReenterTransition(options.mReenterTransition);
		}
		if ((requested & FragmentTransactionOptions.RETURN_TRANSITION) != 0) {
			fragment.setReturnTransition(options.mReturnTransition);
		}
		if ((requested & FragmentTransactionOptions.SHARED_ELEMENT_ENTER_TRANSITION) != 0) {
			fragment.setSharedElementEnterTransition(options.mSharedElementEnterTransition);
		}
		if ((requested & FragmentTransactionOptions.SHARED_ELEMENT_RETURN_TRANSITION) != 0) {
			fragment.setSharedElementReturnTransition(options.mSharedElementReturnTransition);
		}
		if (options.mAllowEnterTransitionOverlap != null) {
			fragment.setAllowEnterTransitionOverlap(options.mAllowEnterTransitionOverlap);
		}
		if (options.mAllowReturnTransitionOverlap != null) {
			fragment.setAllowReturnTransitionOverlap(options.mAllowReturnTransitionOverlap);
		}
		options.clearTransitions();
	}

	/**
	 * Invoked to finally commit created fragment transaction. Here passed transaction is already
	 * configured according to the specified <var>options</var>.
	 * <p>
	 * This implementation commits the passed <var>transaction</var> and in case that the <var>options</var>
	 * has set flag {@link FragmentTransactionOptions#mShowImmediate} to {@code true},
	 * {@link FragmentManager#executePendingTransactions()} will be invoked too on the attached FragmentManager.
	 *
	 * @param transaction Final fragment transaction to commit.
	 * @param options     Already processed transaction options.
	 * @return Always returns {@code true}.
	 */
	protected boolean onCommitTransaction(@NonNull FragmentTransaction transaction, @NonNull FragmentTransactionOptions options) {
		if (options.mCommitAllowingStateLoss) {
			transaction.commitAllowingStateLoss();
		} else {
			transaction.commit();
		}
		if (options.mShowImmediate) {
			mFragmentManager.executePendingTransactions();
		}
		return true;
	}

	/**
	 * Same as {@link #createTransaction(Fragment, FragmentTransactionOptions)} with default transaction
	 * options.
	 */
	@NonNull
	public FragmentTransaction createTransaction(@NonNull Fragment fragment) {
		return createTransaction(fragment, new FragmentTransactionOptions(fragment.getId()));
	}

	/**
	 * Creates a new FragmentTransaction for the specified <var>fragment</var> using the specified
	 * <var>options</var> that are used to properly set up new FragmentTransaction.
	 * <p>
	 * <b>Note, that do not forget to commit the obtained transaction using {@link FragmentTransaction#commit()}.</b>
	 *
	 * @param fragment The fragment for which to create the requested FragmentTransaction.
	 * @param options  The options from which to create the new FragmentTransaction.
	 * @return New FragmentTransaction that can be immediately committed via {@link FragmentTransaction#commit()}.
	 */
	@NonNull
	public FragmentTransaction createTransaction(@NonNull Fragment fragment, @NonNull FragmentTransactionOptions options) {
		return buildTransaction(fragment, options);
	}

	/**
	 * Same as {@link #createTransaction(FragmentTransactionOptions)} with instance of transaction
	 * options instantiated with the specified <var>fragmentId</var>.
	 *
	 * @see FragmentTransactionOptions#FragmentTransactionOptions(int)
	 */
	@Nullable
	public FragmentTransaction createTransaction(@IntRange(from = 0) int fragmentId) {
		return createTransaction(new FragmentTransactionOptions(fragmentId));
	}

	/**
	 * Creates a new FragmentTransaction for a fragment provided by the current factory associated
	 * with the specified <var>transactionOptions</var>.
	 * <p>
	 * {@link FragmentTransactionOptions} configured by the current factory for the associated fragment
	 * via {@link FragmentFactory#configureTransactionOptions(FragmentTransactionOptions)}
	 * will be used to properly set up the new FragmentTransaction.
	 * <p>
	 * <b>Note, that do not forget to commit the obtained transaction using {@link FragmentTransaction#commit()}.</b>
	 *
	 * @param transactionOptions Set of options used to properly set up the requested transaction.
	 * @return New FragmentTransaction that can be immediately committed using {@link FragmentTransaction#commit()}
	 * or {@code null} if the current factory does not provide fragment associated with the specified
	 * options.
	 * @throws IllegalStateException If this controller does not have factory attached.
	 * @see #createTransaction(int)
	 * @see #createTransaction(FragmentTransactionOptions)
	 */
	@Nullable
	public FragmentTransaction createTransaction(@NonNull FragmentTransactionOptions transactionOptions) {
		if (!providesFactoryFragmentWithId(transactionOptions.incomingFragmentId)) {
			Log.e(
					TAG, "Current factory(" + mFactory.getClass().getSimpleName() + ") does not " +
							"provide transaction options nor fragment for the requested id(" + transactionOptions.incomingFragmentId + ")."
			);
			return null;
		}
		return buildTransaction(
				mFactory.createFragmentInstance(transactionOptions),
				mFactory.configureTransactionOptions(transactionOptions)
		);
	}

	/**
	 * Same as {@link #findFragmentByTag(String)}, where fragment tag will be requested from the
	 * current factory.
	 *
	 * @param fragmentId Id of the desired factory fragment to find.
	 * @throws IllegalStateException If this controller does not have factory attached.
	 */
	@Nullable
	public Fragment findFactoryFragmentById(@IntRange(from = 0) int fragmentId) {
		return this.providesFactoryFragmentWithId(fragmentId) ? findFragmentByTag(mFactory.getFragmentTag(fragmentId)) : null;
	}

	/**
	 * Returns an instance of Fragment obtained form the FragmentManager attached to this controller
	 * by the specified <var>fragmentTag</var>.
	 *
	 * @param fragmentTag TAG of the desired fragment to find.
	 * @return Fragment instance or {@code null} if there is no such a fragment within the
	 * FragmentManager with the specified tag.
	 * @see #findFragmentById(int)
	 * @see #findFactoryFragmentById(int)
	 */
	@Nullable
	public Fragment findFragmentByTag(@Nullable String fragmentTag) {
		return mFragmentManager.findFragmentByTag(fragmentTag);
	}

	/**
	 * Same as {@link #findFragmentByTag(String)} for fragment id.
	 */
	@Nullable
	public Fragment findFragmentById(@IdRes int fragmentId) {
		return mFragmentManager.findFragmentById(fragmentId);
	}

	/**
	 * Delegate method for {@link android.support.v4.app.FragmentManager#popBackStack()}.
	 */
	public void hideVisibleFragment() {
		mFragmentManager.popBackStack();
	}

	/**
	 * Delegate method for {@link android.support.v4.app.FragmentManager#popBackStackImmediate()}.
	 */
	public boolean hideVisibleFragmentImmediate() {
		return mFragmentManager.popBackStackImmediate();
	}

	/**
	 * Same as {@link #setFragmentOptionsMenuVisible(String, boolean)}, where fragment tag will be
	 * requested form the current factory by the specified <var>fragmentId</var>.
	 *
	 * @param fragmentId Id of the desired fragment from the current factory, of which options menu
	 *                   to show/hide.
	 */
	public boolean setFactoryFragmentOptionsMenuVisible(@IntRange(from = 0) int fragmentId, boolean visible) {
		return this.providesFactoryFragmentWithId(fragmentId) && setFragmentOptionsMenuVisible(mFactory.getFragmentTag(fragmentId), visible);
	}

	/**
	 * Same as {@link #setFragmentOptionsMenuVisible(String, boolean)} but with fragment id.
	 */
	public boolean setFragmentOptionsMenuVisible(@IdRes int fragmentId, boolean visible) {
		final Fragment fragment = mFragmentManager.findFragmentById(fragmentId);
		if (fragment != null) {
			fragment.setHasOptionsMenu(visible);
			return true;
		}
		return false;
	}

	/**
	 * Shows/hides options menu of the requested fragment by calling {@link android.support.v4.app.Fragment#setHasOptionsMenu(boolean)}.
	 *
	 * @param fragmentTag A tag of the desired fragment of which options to show/hide.
	 * @param visible     {@code True} to show options menu, {@code false} to hide options menu.
	 * @return {@code True} if fragment was found and request to show/hide its options menu was
	 * performed, {@code false} otherwise.
	 * @see #setFragmentOptionsMenuVisible(int, boolean)
	 */
	public boolean setFragmentOptionsMenuVisible(@Nullable String fragmentTag, boolean visible) {
		final Fragment fragment = mFragmentManager.findFragmentByTag(fragmentTag);
		if (fragment != null) {
			fragment.setHasOptionsMenu(visible);
			return true;
		}
		return false;
	}

	/**
	 * Returns a boolean flag indicating whether there are some fragments within the fragment manager's
	 * back stack or not.
	 *
	 * @return {@code True} if fragment manager's back stack holds some entries, {@code false}
	 * otherwise.
	 * @see android.support.v4.app.FragmentManager#getBackStackEntryCount()
	 */
	public boolean hasBackStackEntries() {
		return mFragmentManager.getBackStackEntryCount() > 0;
	}

	/**
	 * Clears fragments back stack by calling {@link android.support.v4.app.FragmentManager#popBackStack()}
	 * in loop of size obtained by {@link android.support.v4.app.FragmentManager#getBackStackEntryCount()}.
	 * <p>
	 * <b>Note</b>, that {@link android.support.v4.app.FragmentManager#popBackStack()} is an asynchronous
	 * call, so the fragments back stack can be cleared in the feature not immediately.
	 *
	 * @see #clearBackStackImmediate()
	 */
	public void clearBackStack() {
		final int n = mFragmentManager.getBackStackEntryCount();
		if (n > 0) {
			for (int i = 0; i < n; i++) {
				mFragmentManager.popBackStack();
			}
		}
	}

	/**
	 * Like {@link #clearBackStack()}, but this will call {@link android.support.v4.app.FragmentManager#popBackStackImmediate()}.
	 * <p>
	 * <b>Note</b>, that {@link android.support.v4.app.FragmentManager#popBackStackImmediate()} is a
	 * synchronous call, so the fragments back stack will be popped immediately within this call. If
	 * there is too many fragments, this can take some time.
	 *
	 * @return {@code True} if there was at least one fragment popped, {@code false} otherwise.
	 */
	public boolean clearBackStackImmediate() {
		final int n = mFragmentManager.getBackStackEntryCount();
		if (n > 0) {
			boolean popped = false;
			for (int i = 0; i < n; i++) {
				if (mFragmentManager.popBackStackImmediate() && !popped) {
					popped = true;
				}
			}
			return popped;
		}
		return false;
	}

	/**
	 * Returns a tag of the currently showing fragment. <b>Note</b>, that this is only accurate, as
	 * it depends on how are fragments changing, and if all fragments are managed by this controller.
	 *
	 * @return Tag of the currently showing fragment or {@code null} if no fragment was shown
	 * by this controller yet.
	 */
	@Nullable
	public String getCurrentFragmentTag() {
		if (mFragmentContainerId > 0) {
			final Fragment fragment = mFragmentManager.findFragmentById(mFragmentContainerId);
			return fragment != null ? fragment.getTag() : null;
		}
		return null;
	}

	/**
	 * Returns the top entry of the fragments back stack.
	 *
	 * @return The top back stack entry or {@code null} if there are no back stack entries.
	 */
	@Nullable
	public FragmentManager.BackStackEntry getTopBackStackEntry() {
		return mTopBackStackEntry;
	}

	/**
	 * Called to notify, that the given <var>changedEntry</var> was added or removed from the back stack.
	 *
	 * @param changedEntry The back stack entry which was changed.
	 * @param added        {@code True} if the specified entry was added to the back stack,
	 *                     {@code false} if was removed.
	 */
	private void notifyBackStackEntryChange(FragmentManager.BackStackEntry changedEntry, boolean added) {
		this.mCurrentFragmentTag = changedEntry.getName();
		if (mBackStackChangeListeners == null || mBackStackChangeListeners.isEmpty()) return;
		for (OnBackStackChangeListener listener : mBackStackChangeListeners) {
			listener.onFragmentsBackStackChanged(added, changedEntry.getId(), mCurrentFragmentTag);
		}
	}

	/**
	 * Called to notify, that there was fragment with the given id and tag currently changed, so replaces
	 * the old one.
	 *
	 * @param id      The id of the currently changed (showed) fragment.
	 * @param tag     The tag of the currently changed (showed) fragment.
	 * @param factory {@code True} if the changed fragment was obtained from a factory,
	 *                {@code false} otherwise.
	 * @return Always {@code true}.
	 */
	private boolean notifyFragmentChanged(int id, String tag, boolean factory) {
		this.mCurrentFragmentTag = tag;
		if (mChangeListeners == null || mChangeListeners.isEmpty()) return true;
		for (OnChangeListener listener : mChangeListeners) {
			listener.onFragmentChanged(id, mCurrentFragmentTag, factory);
		}
		return true;
	}

	/**
	 * Called to dispatch the back stack change.
	 *
	 * @param entriesCount The count of the fragment back stack entries.
	 * @param action       The back stack change action identifier.
	 */
	final void dispatchBackStackChanged(int entriesCount, int action) {
		final boolean added = action == BackStackListener.ADDED;
		if (entriesCount > 0) {
			final FragmentManager.BackStackEntry entry = mFragmentManager.getBackStackEntryAt(entriesCount - 1);
			if (entry != null) {
				notifyBackStackEntryChange(mTopBackStackEntry = entry, added);
			}
		} else if (mTopBackStackEntry != null) {
			notifyBackStackEntryChange(mTopBackStackEntry, false);
			this.mTopBackStackEntry = null;
		}
	}

	/**
	 * Inner classes ===============================================================================
	 */

	/**
	 * Fragments back stack listener inner implementation.
	 */
	private final class BackStackListener implements FragmentManager.OnBackStackChangedListener {

		/**
		 * Flag to indicate, that fragment was added to the back stack.
		 */
		static final int ADDED = 0x00;

		/**
		 * Flag to indicate, that fragment was removed from the back stack.
		 */
		static final int REMOVED = 0x01;

		/**
		 * Current size of the fragments back stack.
		 */
		int currentCount = 0;

		/**
		 */
		@Override
		public void onBackStackChanged() {
			final int n = mFragmentManager.getBackStackEntryCount();
			if (n >= 0 && n != currentCount) {
				dispatchBackStackChanged(n, n > currentCount ? ADDED : REMOVED);
				this.currentCount = n;
			}
		}
	}
}
