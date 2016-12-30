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

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import universum.studios.android.fragment.FragmentsConfig;

/**
 * FragmentController class is designed primarily to simplify management of showing and hiding
 * of {@link Fragment Fragments} within an Android application. For proper working, it is necessary
 * that a specific instance of FragmentController has specified an id of the layout container via
 * {@link #setFragmentContainerId(int)} into which will be placed view hierarchies of the new fragments.
 * <p>
 * The desired fragment can be simply shown via {@link #showFragment(android.support.v4.app.Fragment)}.
 * Such a fragment will be shown without any transition and {@link #FRAGMENT_TAG} will be used as
 * tag for that fragment. If you want to specify more options determining how should be your desired
 * fragment shown, use {@link #showFragment(android.support.v4.app.Fragment, FragmentRequest)}
 * and specify your required options via {@link FragmentRequest} object.
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
 * The factory for FragmentController can be specified via {@link #setFactory(FragmentFactory)}.
 * Fragments provided by attached factory can be than shown via {@link #showFragment(FragmentRequest)}.
 * <b>Note, that it is required that factory is attached to the controller before calling one of these
 * methods, otherwise exception will be thrown.</b>
 * <p>
 * You can use {@link BaseFragmentFactory} that specifies base implementation of FragmentFactory so
 * you do not need to implement all those required methods, basically you will need to implement
 * only {@link BaseFragmentFactory#onConfigureTransactionOptions(FragmentRequest)} method
 * if using fragment annotations.
 *
 * <h3>Callbacks</h3>
 * If you want to listen for changes in fragments when they are being shown, you can attach
 * {@link OnChangeListener} to the FragmentController via {@link #registerOnChangeListener(FragmentController.OnChangeListener)}
 * that will receive callback whenever a new fragment is shown via this controller.
 * <p>
 * If you want to listen for changes in the fragments back stack whenever a new fragment is added to
 * the stack or an old one removed from the stack, you can attach {@link OnBackStackChangeListener}
 * to the FragmentController via {@link #registerOnBackStackChangeListener(FragmentController.OnBackStackChangeListener)}
 * that will receive callback whenever such a change occurs.
 *
 * @author Martin Albedinsky
 * @see FragmentFactory
 * @see FragmentRequest
 */
public class FragmentController {

	/**
	 * Interface ===================================================================================
	 */

	/**
	 * Listener that receives a callback about changed fragment whenever one of methods of
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
	 * Listener that may be used to receive a callback about changes in the fragments back stack.
	 * The callback is fired whenever a new fragment is added into the back stack or an old fragment
	 * is removed from the back stack.
	 *
	 * @author Martin Albedinsky
	 * @see #registerOnBackStackChangeListener(FragmentController.OnBackStackChangeListener)
	 */
	public interface OnBackStackChangeListener {

		/**
		 * Invoked whenever fragments back stack change occur.
		 *
		 * @param backStackEntry The back stack entry that was added into back stack or removed from it.
		 * @param added          {@code True} if the entry has been added, {@code false} if removed.
		 */
		void onFragmentsBackStackChanged(@NonNull FragmentManager.BackStackEntry backStackEntry, boolean added);
	}

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	private static final String TAG = "FragmentController";

	/**
	 * Default TAG used for fragments.
	 */
	public static final String FRAGMENT_TAG = "universum.studios.android.fragment.TAG.Fragment";

	/**
	 * Constant used to identify that no container id is specified.
	 */
	public static final int NO_CONTAINER_ID = -1;

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
	 * Listener used to listen for changes in fragments back stack.
	 */
	private final FragmentManager.OnBackStackChangedListener mBackStackChangeListener = new BackStackListener();

	/**
	 * Fragment manager used to perform fragments related operations.
	 */
	private final FragmentManager mFragmentManager;

	/**
	 * Id of a layout container where to show the desired fragments.
	 */
	private int mFragmentContainerId = NO_CONTAINER_ID;

	/**
	 * Fragment factory that provides fragment instances for this controller.
	 */
	private FragmentFactory mFactory;

	/**
	 * todo:
	 */
	private FragmentRequestInterceptor mRequestInterceptor;

	/**
	 * List of listener callbacks for fragment changes.
	 */
	private List<OnChangeListener> mChangeListeners;

	/**
	 * List of listener callbacks for back stack changes.
	 */
	private List<OnBackStackChangeListener> mBackStackChangeListeners;

	/**
	 * Entry that is at the top of the fragments back stack.
	 */
	private FragmentManager.BackStackEntry mTopBackStackEntry;

	/**
	 * Boolean flag indicating whether this controller has been destroyed or not.
	 */
	private boolean mDestroyed;

	/**
	 * Constructors ================================================================================
	 */

	/**
	 * Creates a new instance of FragmentController for the given <var>parentActivity</var>.
	 * <p>
	 * Passed activity will be used to obtain an instance of {@link FragmentManager} for the new
	 * controller.
	 * <p>
	 * If the given <var>parentActivity</var> implements {@link FragmentController.OnBackStackChangeListener}
	 * or {@link universum.studios.android.fragment.manage.FragmentController.OnChangeListener}
	 * it will be automatically attached to the new controller as such listener.
	 *
	 * @param parentActivity The activity that wants to use the new fragment controller.
	 * @see #FragmentController(Fragment)
	 */
	public FragmentController(@NonNull Activity parentActivity) {
		this(parentActivity.getFragmentManager());
		if (parentActivity instanceof FragmentRequestInterceptor) {
			setRequestInterceptor((FragmentRequestInterceptor) parentActivity);
		}
		if (parentActivity instanceof OnChangeListener) {
			registerOnChangeListener((OnChangeListener) parentActivity);
		}
		if (parentActivity instanceof OnBackStackChangeListener) {
			registerOnBackStackChangeListener((OnBackStackChangeListener) parentActivity);
		}
	}

	/**
	 * Creates a new instance of FragmentController for the given <var>parentFragment</var>.
	 * <p>
	 * Passed fragment will be used to obtain an instance of {@link FragmentManager} for the new
	 * controller.
	 * <p>
	 * This constructor attaches the given fragment to the new controller as one of interfaces
	 * listed below if the fragment implements listed interfaces respectively:
	 * <ul>
	 * <li>{@link FragmentRequestInterceptor} -> {@link #setRequestInterceptor(FragmentRequestInterceptor)}</li>
	 * <li>{@link OnChangeListener} -> {@link #registerOnChangeListener(OnChangeListener)}</li>
	 * <li>{@link OnBackStackChangeListener} -> {@link #registerOnBackStackChangeListener(OnBackStackChangeListener)}</li>
	 * </ul>
	 * <p>
	 * <b>Do not forget to destroy the new controller via {@link #destroy()} when the fragment is
	 * also destroyed.</b>
	 *
	 * @param parentFragment The fragment that wants to use the new fragment controller.
	 * @see #FragmentController(Activity)
	 */
	public FragmentController(@NonNull Fragment parentFragment) {
		this(parentFragment.getFragmentManager());
		if (parentFragment instanceof FragmentRequestInterceptor) {
			setRequestInterceptor((FragmentRequestInterceptor) parentFragment);
		}
		if (parentFragment instanceof OnChangeListener) {
			registerOnChangeListener((OnChangeListener) parentFragment);
		}
		if (parentFragment instanceof OnBackStackChangeListener) {
			registerOnBackStackChangeListener((OnBackStackChangeListener) parentFragment);
		}
	}

	/**
	 * Creates a new instance of FragmentController with the given <var>fragmentManager</var>.
	 *
	 * @param fragmentManager Fragment manager that will be used to perform fragments related operations.
	 * @see #FragmentController(Activity)
	 * @see #FragmentController(Fragment)
	 */
	public FragmentController(@NonNull FragmentManager fragmentManager) {
		this.mFragmentManager = fragmentManager;
		this.mFragmentManager.addOnBackStackChangedListener(mBackStackChangeListener);
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
	 * @return FragmentManager instance.
	 * @see #FragmentController(FragmentManager)
	 */
	@NonNull
	public final FragmentManager getFragmentManager() {
		return mFragmentManager;
	}

	/**
	 * Sets an id of the layout container used to host root views of all fragments shown via this
	 * controller.
	 * <p>
	 * <b>Note</b>, that this container id is used to specify initial/default container id for all
	 * {@link FragmentRequest FragmentRequests} created via {@link #newRequest(Fragment)}
	 *
	 * @param layoutId The desired id of layout container within the current window view hierarchy,
	 *                 into which should be views of all managed fragments placed.
	 * @see #getFragmentContainerId()
	 */
	public final void setFragmentContainerId(@IdRes int layoutId) {
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
	public final int getFragmentContainerId() {
		return mFragmentContainerId;
	}

	/**
	 * Sets the factory for this fragment controller.
	 *
	 * @param factory The desired factory. May {@code null} to clear the current one.
	 * @see #getactory()
	 * @see #hasFactory()
	 */
	public void setFactory(@Nullable FragmentFactory factory) {
		this.mFactory = factory;
	}

	/**
	 * Returns the current fragment factory of this controller instance.
	 *
	 * @return Instance of fragment factory or {@code null} if there was no factory attached yet.
	 * @see #setFactory(FragmentFactory)
	 * @see #hasFactory()
	 */
	@Nullable
	public FragmentFactory getactory() {
		return mFactory;
	}

	/**
	 * Returns a boolean flag indicating whether this controller has factory attached or not.
	 *
	 * @return {@code True} if factory is attached, {@code false} otherwise.
	 */
	public boolean hasFactory() {
		return mFactory != null;
	}

	/**
	 * Asserts that the factory has been attached to this controller. If no factory is attached,
	 * an exception is thrown.
	 */
	private void assertHasFactory() {
		if (mFactory == null) throw new NullPointerException("No factory attached!");
	}

	/**
	 * todo:
	 *
	 * @param interceptor
	 */
	public void setRequestInterceptor(@Nullable FragmentRequestInterceptor interceptor) {
		this.mRequestInterceptor = interceptor;
	}

	/**
	 * Adds a callback to be invoked when fragments are being changed.
	 * <p>
	 * <b>Note, that in case of fragments provided by {@link FragmentFactory} this callback is properly
	 * invoked only if fragments are showing immediately. See {@link FragmentRequest#showImmediate()}
	 * for more information.</b>
	 *
	 * @param listener The desired listener callback to be added.
	 * @see #unregisterOnChangeListener(OnChangeListener)
	 */
	public void registerOnChangeListener(@NonNull OnChangeListener listener) {
		if (mChangeListeners == null) this.mChangeListeners = new ArrayList<>(1);
		if (!mChangeListeners.contains(listener)) mChangeListeners.add(listener);
	}

	/**
	 * Removes the specified callback from the current change listeners.
	 *
	 * @param listener The listener callback to be removed.
	 * @see #registerOnChangeListener(OnChangeListener)
	 */
	public void unregisterOnChangeListener(@NonNull OnChangeListener listener) {
		if (mChangeListeners != null) mChangeListeners.remove(listener);
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
		if (mChangeListeners == null || mChangeListeners.isEmpty()) return true;
		for (OnChangeListener listener : mChangeListeners) {
			listener.onFragmentChanged(id, tag, factory);
		}
		return true;
	}

	/**
	 * Registers a callback to be invoked when some change occurs in the fragments back stack.
	 *
	 * @param listener The desired listener callback to be registered.
	 * @see #unregisterOnBackStackChangeListener(OnBackStackChangeListener)
	 * @see FragmentManager#addOnBackStackChangedListener(FragmentManager.OnBackStackChangedListener)
	 */
	public void registerOnBackStackChangeListener(@NonNull OnBackStackChangeListener listener) {
		if (mBackStackChangeListeners == null) this.mBackStackChangeListeners = new ArrayList<>(1);
		if (!mBackStackChangeListeners.contains(listener)) mBackStackChangeListeners.add(listener);
	}

	/**
	 * Un-registers the given callback from the registered back stack change listeners.
	 *
	 * @param listener The desired listener callback to be un-registered.
	 * @see #registerOnBackStackChangeListener(OnBackStackChangeListener)
	 */
	public void unregisterOnBackStackChangeListener(@NonNull OnBackStackChangeListener listener) {
		if (mBackStackChangeListeners != null) mBackStackChangeListeners.remove(listener);
	}

	/**
	 * Notifies all registered OnBackStackChangeListener that the given <var>changedEntry</var> was
	 * added or removed from the back stack.
	 *
	 * @param changedEntry The back stack entry that was changed.
	 * @param added        {@code True} if the specified entry was added to the back stack,
	 *                     {@code false} if it was removed.
	 */
	private void notifyBackStackEntryChange(FragmentManager.BackStackEntry changedEntry, boolean added) {
		if (mBackStackChangeListeners != null && !mBackStackChangeListeners.isEmpty()) {
			for (OnBackStackChangeListener listener : mBackStackChangeListeners) {
				listener.onFragmentsBackStackChanged(changedEntry, added);
			}
		}
	}

	/**
	 * Same as {@link #newRequest(Fragment)} where instance of the desired fragment will be requested
	 * from the attached factory.
	 * <p>
	 * <b>Note</b>, that this method assumes that there is factory attached and that factory provides
	 * fragment that is associated with the specified <var>factoryFragmentId</var> otherwise an
	 * exception is thrown.
	 *
	 * @param factoryFragmentId Id of the desired factory fragment for which to crate new request.
	 * @return New fragment request with tag provided by the factory via {@link FragmentFactory#createFragmentTag(int)}
	 * for the specified fragment id.
	 * @throws NullPointerException     If there is no factory attached.
	 * @throws IllegalArgumentException If the attached factory does not provide fragment for the
	 *                                  specified id.
	 */
	@NonNull
	public final FragmentRequest newRequest(int factoryFragmentId) {
		this.assertNotDestroyed("NEW REQUEST");
		this.assertHasFactory();
		if (!mFactory.isFragmentProvided(factoryFragmentId)) {
			throw new IllegalArgumentException(
					"Cannot create new request. Current factory(" + mFactory.getClass() + ") " +
							"does not provide fragment for the requested id(" + factoryFragmentId + ")!");
		}
		final Fragment fragment = mFactory.createFragment(factoryFragmentId);
		if (fragment == null) {
			throw new NullPointerException(
					"Cannot create new request. Current factory(" + mFactory.getClass() + ") is cheating. " +
							"FragmentFactory.isFragmentProvided() returned true, but FragmentFactory.createFragment(...) returned null!"
			);
		}
		return newRequest(fragment).tag(mFactory.createFragmentTag(factoryFragmentId));
	}

	/**
	 * todo:
	 *
	 * @param fragment The fragment for which to create new request.
	 * @return New fragment request with default {@link #FRAGMENT_TAG} and container id specified for
	 * this controller via {@link #setFragmentContainerId(int)}.
	 * @see FragmentRequest#tag(String)
	 * @see FragmentRequest#containerId(int)
	 */
	@NonNull
	public final FragmentRequest newRequest(@NonNull Fragment fragment) {
		this.assertNotDestroyed("NEW REQUEST");
		return new FragmentRequest(fragment, this).tag(FRAGMENT_TAG).containerId(mFragmentContainerId);
	}

	/**
	 * todo:
	 *
	 * @param request
	 * @return
	 */
	final Fragment executeRequest(FragmentRequest request) {
		this.assertNotDestroyed("EXECUTE REQUEST");
		return mRequestInterceptor != null && mRequestInterceptor.interceptFragmentRequest(request) ?
				null :
				onExecuteRequest(request);
	}

	/**
	 * todo:
	 *
	 * @param request
	 * @return
	 */
	@NonNull
	@SuppressWarnings("NewApi")
	protected Fragment onExecuteRequest(@NonNull FragmentRequest request) {
		if (!request.mReplaceSame) {
			// Do not replace same fragment if there is already showing fragment with the same tag.
			final Fragment showingFragment = mFragmentManager.findFragmentByTag(request.mTag);
			if (showingFragment != null) {
				if (FragmentsConfig.LOG_ENABLED) {
					Log.v(TAG, "Fragment with tag(" + request.mTag + ") is already showing or it is in the back-stack.");
				}
				return showingFragment;
			}
		}
		// Check if we have container where the fragment should be placed.
		if (request.mContainerId <= 0) {
			if (mFragmentContainerId != NO_CONTAINER_ID) {
				request.mContainerId = mFragmentContainerId;
			} else {
				throw new IllegalStateException(
						"No id specified for the layout container where should be the requested fragment showed."
				);
			}
		}
		// Crate transaction for the fragment request.
		final Fragment fragment = request.mFragment;
		final FragmentTransaction transaction = createTransaction(request);
		if (request.mAddToBackStack) {
			if (FragmentsConfig.DEBUG_LOG_ENABLED) {
				Log.d(TAG, "Fragment(" + fragment + ") will be added to back-stack under the tag(" + fragment.getTag() + ").");
			}
		}
		// Attach transitions with shared elements, if specified.
		this.attachTransitionsToFragment(request, fragment);
		if (CAN_ATTACH_TRANSITIONS && request.mSharedElements != null) {
			final List<Pair<View, String>> elements = request.mSharedElements;
			for (Pair<View, String> pair : elements) {
				transaction.addSharedElement(pair.first, pair.second);
			}
		}
		// Finally, commit the transaction.
		if (request.mCommitAllowingStateLoss) {
			transaction.commitAllowingStateLoss();
		} else {
			transaction.commit();
		}
		if (request.mShowImmediate) {
			mFragmentManager.executePendingTransactions();
		}
		// todo: ??? notifyFragmentChanged(...)
		return fragment;
	}

	/**
	 * todo:
	 *
	 * @param request
	 * @return
	 */
	@NonNull
	public FragmentTransaction createTransaction(@NonNull FragmentRequest request) {
		this.assertNotDestroyed("CREATE TRANSACTION");
		final FragmentTransaction transaction = mFragmentManager.beginTransaction();
		// Attach animations to the transaction from the FragmentTransition parameter.
		if (request.mTransition != null) {
			transaction.setCustomAnimations(
					request.mTransition.getIncomingAnimation(),
					request.mTransition.getOutgoingAnimation(),
					request.mTransition.getIncomingBackStackAnimation(),
					request.mTransition.getOutgoingBackStackAnimation()
			);
		} else if (request.mTransitionStyle != FragmentRequest.NO_STYLE) {
			transaction.setTransitionStyle(request.mTransitionStyle);
		}
		final Fragment fragment = request.mFragment;
		// todo: add support also for attach(...), detach(...)
		/*if (options.mAdd) {
			transaction.add(options.mContainerId, fragment, options.mTag);
		} else {
			transaction.replace(options.mContainerId, fragment, options.mTag);
		}*/
		// Add fragment to back stack if requested.
		if (request.mAddToBackStack) {
			transaction.addToBackStack(fragment.getTag());
		}
		return transaction;
	}

	/**
	 * Attaches all transitions specified via the given <var>request</var> to the given <var>fragment</var>.
	 *
	 * @param request  Request caring the specified transitions for the fragment.
	 * @param fragment The fragment instance to which to attach the transitions.
	 */
	@SuppressWarnings("NewApi")
	private void attachTransitionsToFragment(FragmentRequest request, Fragment fragment) {
		if (CAN_ATTACH_TRANSITIONS) {
			final int requested = request.mRequestedTransitions;
			if ((requested & FragmentRequest.ENTER_TRANSITION) != 0) {
				fragment.setEnterTransition(request.mEnterTransition);
			}
			if ((requested & FragmentRequest.EXIT_TRANSITION) != 0) {
				fragment.setExitTransition(request.mExitTransition);
			}
			if ((requested & FragmentRequest.REENTER_TRANSITION) != 0) {
				fragment.setReenterTransition(request.mReenterTransition);
			}
			if ((requested & FragmentRequest.RETURN_TRANSITION) != 0) {
				fragment.setReturnTransition(request.mReturnTransition);
			}
			if ((requested & FragmentRequest.SHARED_ELEMENT_ENTER_TRANSITION) != 0) {
				fragment.setSharedElementEnterTransition(request.mSharedElementEnterTransition);
			}
			if ((requested & FragmentRequest.SHARED_ELEMENT_RETURN_TRANSITION) != 0) {
				fragment.setSharedElementReturnTransition(request.mSharedElementReturnTransition);
			}
			if (request.mAllowEnterTransitionOverlap != null) {
				fragment.setAllowEnterTransitionOverlap(request.mAllowEnterTransitionOverlap);
			}
			if (request.mAllowReturnTransitionOverlap != null) {
				fragment.setAllowReturnTransitionOverlap(request.mAllowReturnTransitionOverlap);
			}
		}
	}

	/**
	 * todo:
	 *
	 * @return
	 * @throws UnsupportedOperationException If there is no fragment container id specified.
	 * @see #setFragmentContainerId(int)
	 */
	@Nullable
	public Fragment findCurrentFragment() {
		this.assertNotDestroyed("FIND CURRENT FRAGMENT");
		if (mFragmentContainerId == NO_CONTAINER_ID) {
			throw new UnsupportedOperationException("Cannot find current fragment. No fragment container id specified!");
		}
		return mFragmentManager.findFragmentById(mFragmentContainerId);
	}

	/**
	 * Delegates to {@link FragmentManager#findFragmentByTag(String)} with the TAG obtained via
	 * {@link FragmentFactory#createFragmentTag(int)} from the current factory.
	 * <p>
	 * <b>Note</b>, that this method assumes that there is factory attached and that factory provides
	 * fragment that is associated with the specified <var>factoryFragmentId</var> otherwise an
	 * exception is thrown.
	 *
	 * @param factoryFragmentId Id of the desired factory fragment to find.
	 * @throws NullPointerException     If there is no factory attached.
	 * @throws IllegalArgumentException If the attached factory does not provide fragment for the
	 *                                  specified id.
	 */
	@Nullable
	public Fragment findFragmentByFactoryId(int factoryFragmentId) {
		this.assertNotDestroyed("FIND FRAGMENT BY FACTORY ID");
		this.assertHasFactory();
		if (!mFactory.isFragmentProvided(factoryFragmentId)) {
			throw new IllegalArgumentException(
					"Cannot find fragment by factory id. Current factory(" + mFactory.getClass() + ") " +
							"does not provide fragment for the requested id(" + factoryFragmentId + ")!");
		}
		return mFragmentManager.findFragmentByTag(mFactory.createFragmentTag(factoryFragmentId));
	}

	/**
	 * Checks whether there are some fragments within the back stack or not.
	 *
	 * @return {@code True} if fragments back stack contains at least one entry, {@code false} otherwise.
	 * @see FragmentManager#getBackStackEntryCount()
	 */
	public final boolean hasBackStackEntries() {
		return mFragmentManager.getBackStackEntryCount() > 0;
	}

	/**
	 * Returns the top entry from the fragments back stack.
	 *
	 * @return The top back stack entry or {@code null} if there are no back stack entries.
	 * @see #hasBackStackEntries()
	 */
	@Nullable
	public final FragmentManager.BackStackEntry getTopBackStackEntry() {
		return mTopBackStackEntry;
	}

	/**
	 * Clears fragments back stack by calling {@link FragmentManager#popBackStack()} in loop for current
	 * back stack size obtained via {@link FragmentManager#getBackStackEntryCount()}.
	 * <p>
	 * <b>Note</b>, that {@link FragmentManager#popBackStack()} is an asynchronous call, so the
	 * fragments back stack may be cleared in a feature, not immediately.
	 *
	 * @see #clearBackStackImmediate()
	 */
	public void clearBackStack() {
		this.assertNotDestroyed("CLEAR BACK STACK");
		final int n = mFragmentManager.getBackStackEntryCount();
		if (n > 0) {
			for (int i = 0; i < n; i++) {
				mFragmentManager.popBackStack();
			}
		}
	}

	/**
	 * Like {@link #clearBackStack()} but this will call {@link FragmentManager#popBackStackImmediate()}
	 * instead of {@link FragmentManager#popBackStack()}.
	 * <p>
	 * <b>Note</b>, that {@link FragmentManager#popBackStackImmediate()} is a synchronous call, so
	 * the fragments back stack will be popped immediately within this call. If there are too many
	 * fragments, this may take some time.
	 *
	 * @return {@code True} if there was at least one fragment popped, {@code false} otherwise.
	 */
	public boolean clearBackStackImmediate() {
		this.assertNotDestroyed("CLEAR BACK STACK IMMEDIATE");
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
	 * Destroys this fragment controller instance, mainly un-registering its internal <b>back-stack</b>
	 * listener from the attached {@link FragmentManager}.
	 * <p>
	 * Fragment controller should be destroyed whenever it is used in application component that has
	 * 'shorter' lifecycle (fragment) as its parent application component (activity).
	 * <p>
	 * <b>Note</b>, that already destroyed controller should not be used further as such usage will
	 * result in an exception to be thrown.
	 */
	public final void destroy() {
		if (!mDestroyed) {
			this.mDestroyed = true;
			this.mFragmentManager.removeOnBackStackChangedListener(mBackStackChangeListener);
			this.mBackStackChangeListeners = null;
			this.mChangeListeners = null;
		}
	}

	/**
	 * Asserts that this controller is not destroyed yet. If it is already destroyed, an exception
	 * is thrown.
	 *
	 * @param forAction Action for which the check should be performed. The action will be placed
	 *                  into exception if it will be thrown.
	 */
	private void assertNotDestroyed(String forAction) {
		if (mDestroyed) throw new IllegalStateException("Cannot perform " + forAction + " action. Controller is already destroyed!");
	}

	/**
	 * Called to dispatch change in the fragments back stack.
	 *
	 * @param backStackSize Current size of the fragments back stack.
	 * @param change        Identifier determining the occurred change. One of {@link BackStackListener#ADDED}
	 *                      or {@link BackStackListener#REMOVED}.
	 */
	@SuppressWarnings("WeakerAccess")
	final void handleBackStackChange(int backStackSize, int change) {
		if (backStackSize > 0) {
			final FragmentManager.BackStackEntry entry = mFragmentManager.getBackStackEntryAt(backStackSize - 1);
			if (entry != null) {
				notifyBackStackEntryChange(mTopBackStackEntry = entry, change == BackStackListener.ADDED);
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
	 * A {@link FragmentManager.OnBackStackChangedListener} implementation used to listen for changes
	 * in fragments back stack.
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
		int backStackSize;

		/**
		 */
		@Override
		public void onBackStackChanged() {
			final int n = mFragmentManager.getBackStackEntryCount();
			if (n >= 0 && n != backStackSize) {
				handleBackStackChange(n, n > backStackSize ? ADDED : REMOVED);
				this.backStackSize = n;
			}
		}
	}
}
