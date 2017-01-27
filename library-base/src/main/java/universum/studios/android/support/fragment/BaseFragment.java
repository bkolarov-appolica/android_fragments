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
package universum.studios.android.support.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.transition.Transition;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import universum.studios.android.support.fragment.annotation.FragmentAnnotations;
import universum.studios.android.support.fragment.annotation.handler.BaseAnnotationHandlers;
import universum.studios.android.support.fragment.annotation.handler.FragmentAnnotationHandler;
import universum.studios.android.support.fragment.util.FragmentUtils;

/**
 * A {@link Fragment} implementation designed to provide extended API and logic that is useful almost
 * every time you need to implement your desired fragment.
 * <p>
 * BaseFragment class provides lifecycle state check methods that may be used to check whether a
 * particular instance of fragment is in a specific lifecycle state. All provided methods are listed
 * below:
 * <ul>
 * <li>{@link #isAttached()}</li>
 * <li>{@link #isCreated()}</li>
 * <li>{@link #isStarted()}</li>
 * <li>{@link #isPaused()}</li>
 * <li>{@link #isStopped()}</li>
 * <li>{@link #isDestroyed()}</li>
 * </ul>
 * as addition to the Android framework's lifecycle methods:
 * <ul>
 * <li>{@link #isResumed()}</li>
 * <li>{@link #isDetached()}</li>
 * </ul>
 * <p>
 * You can also easily dispatch view click events to your specific implementation of BaseFragment
 * via {@link #dispatchViewClick(View)} or back press events via {@link #dispatchBackPress()}
 * from activity's context in which such fragment presented.
 *
 * <h3>Accepted annotations</h3>
 * <ul>
 * <li>
 * {@link universum.studios.android.support.fragment.annotation.ContentView @ContentView} <b>[class - inherited]</b>
 * <p>
 * If this annotation is presented, the layout resource specified via this annotation will be used
 * to inflate root view for an instance of annotated BaseFragment sub-class when
 * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} is called.
 * </li>
 * </ul>
 *
 * @author Martin Albedinsky
 */
public abstract class BaseFragment extends Fragment implements BackPressWatcher, ViewClickWatcher {

	/**
	 * Interface ===================================================================================
	 */

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	private static final String TAG = "BaseFragment";

	/**
	 * Defines an annotation for determining set of available lifecycle flags.
	 */
	@IntDef(flag = true, value = {
			LIFECYCLE_ATTACHED,
			LIFECYCLE_CREATED,
			LIFECYCLE_STARTED,
			LIFECYCLE_RESUMED,
			LIFECYCLE_PAUSED,
			LIFECYCLE_STOPPED,
			LIFECYCLE_DESTROYED,
			LIFECYCLE_DETACHED
	})
	@Retention(RetentionPolicy.SOURCE)
	private @interface LifecycleFlag {
	}

	/**
	 * Lifecycle flag used to indicate that fragment is <b>attached</b> to the parent context.
	 */
	private static final int LIFECYCLE_ATTACHED = 0x00000001;

	/**
	 * Lifecycle flag used to indicate that fragment is <b>created</b>.
	 */
	private static final int LIFECYCLE_CREATED = 0x00000001 << 1;

	/**
	 * Lifecycle flag used to indicate that fragment is <b>started</b>.
	 */
	private static final int LIFECYCLE_STARTED = 0x00000001 << 2;

	/**
	 * Lifecycle flag used to indicate that fragment is <b>resumed</b>.
	 */
	private static final int LIFECYCLE_RESUMED = 0x00000001 << 3;

	/**
	 * Lifecycle flag used to indicate that fragment is <b>paused</b>.
	 */
	private static final int LIFECYCLE_PAUSED = 0x00000001 << 4;

	/**
	 * Lifecycle flag used to indicate that fragment is <b>stopped</b>.
	 */
	private static final int LIFECYCLE_STOPPED = 0x00000001 << 5;

	/**
	 * Lifecycle flag used to indicate that fragment is <b>destroyed</b>.
	 */
	private static final int LIFECYCLE_DESTROYED = 0x00000001 << 6;

	/**
	 * Lifecycle flag used to indicate that fragment is <b>detached</b>.
	 */
	private static final int LIFECYCLE_DETACHED = 0x00000001 << 7;

	/**
	 * Static members ==============================================================================
	 */

	/**
	 * Members =====================================================================================
	 */

	/**
	 * Handler responsible for processing of all annotations of this class and also for handling all
	 * annotations related operations for this class.
	 */
	final FragmentAnnotationHandler mAnnotationHandler;

	/**
	 * Delegate for activity to which is this instance of fragment currently attached. This delegate
	 * is available between calls to {@link #onAttach(Context)} and {@link #onDetach()}.
	 */
	ActivityDelegate mActivityDelegate;

	/**
	 * Stores all lifecycle related flags for this fragment.
	 */
	private int mLifecycleFlags;

	/**
	 * Constructors ================================================================================
	 */

	/**
	 * Creates a new instance of BaseFragment.
	 * <p>
	 * If annotations processing is enabled via {@link FragmentsConfig} all annotations supported by
	 * this class will be processed/obtained here so they can be later used.
	 */
	public BaseFragment() {
		this.mAnnotationHandler = onCreateAnnotationHandler();
	}

	/**
	 * Methods =====================================================================================
	 */

	/**
	 * Creates a new instance of the specified <var>classOfFragment</var> with the given <var>args</var>.
	 *
	 * @param classOfFragment Class of the desired fragment to instantiate.
	 * @param args            Arguments to set to new instance of fragment by {@link Fragment#setArguments(Bundle)}.
	 * @param <F>             Type of the desired fragment.
	 * @return New instance of fragment with the given arguments or {@code null} if some instantiation
	 * error occurs.
	 */
	@Nullable
	public static <F extends Fragment> F newInstanceWithArguments(@NonNull Class<F> classOfFragment, @Nullable Bundle args) {
		try {
			final F fragment = classOfFragment.newInstance();
			fragment.setArguments(args);
			return fragment;
		} catch (Exception e) {
			Log.e(TAG, "Failed to instantiate instance of " + classOfFragment + " with arguments!", e);
		}
		return null;
	}

	/**
	 * Invoked to create annotations handler for this instance.
	 *
	 * @return Annotations handler specific for this class.
	 */
	FragmentAnnotationHandler onCreateAnnotationHandler() {
		return BaseAnnotationHandlers.obtainFragmentHandler(getClass());
	}

	/**
	 * Returns handler that is responsible for annotations processing of this class and also for
	 * handling all annotations related operations for this class.
	 *
	 * @return Annotations handler specific for this class.
	 * @throws IllegalStateException If annotations processing is not enabled for the Fragments library.
	 */
	@NonNull
	protected FragmentAnnotationHandler getAnnotationHandler() {
		FragmentAnnotations.checkIfEnabledOrThrow();
		return mAnnotationHandler;
	}

	/**
	 * Updates the current private flags.
	 *
	 * @param flag Value of the desired flag to add/remove to/from the current private flags.
	 * @param add  Boolean flag indicating whether to add or remove the specified <var>flag</var>.
	 * @see #hasLifecycleFlag(int)
	 */
	private void updateLifecycleFlags(@LifecycleFlag int flag, boolean add) {
		if (add) this.mLifecycleFlags |= flag;
		else this.mLifecycleFlags &= ~flag;
	}

	/**
	 * Returns a boolean flag indicating whether the specified <var>flag</var> is contained within
	 * the current private flags or not.
	 *
	 * @param flag Value of the flag to check.
	 * @return {@code True} if the requested flag is contained, {@code false} otherwise.
	 * @see #updateLifecycleFlags(int, boolean)
	 */
	private boolean hasLifecycleFlag(@LifecycleFlag int flag) {
		return (mLifecycleFlags & flag) != 0;
	}

	/**
	 */
	@Override
	@SuppressWarnings("deprecation")
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mActivityDelegate = ActivityDelegate.create(activity);
		this.updateLifecycleFlags(LIFECYCLE_DETACHED, false);
		this.updateLifecycleFlags(LIFECYCLE_ATTACHED, true);
	}

	/**
	 * Checks whether this fragment is attached to its parent context. This is {@code true} for
	 * duration of {@link #onAttach(Context)} and {@link #onDetach()}.
	 * <p>
	 * When this method returns {@code true} the opposite lifecycle state method {@link #isDetached()}
	 * returns {@code false} and vise versa.
	 *
	 * @return {@code True} if fragment is attached, {@code false} otherwise.
	 * @see #isDetached()
	 */
	public final boolean isAttached() {
		return hasLifecycleFlag(LIFECYCLE_ATTACHED);
	}

	/**
	 * Returns the theme of the context to which is this fragment attached.
	 *
	 * @return Parent context's theme.
	 * @throws IllegalStateException If this fragment is not attached to any context.
	 */
	@NonNull
	protected Resources.Theme getContextTheme() {
		final Activity activity = getActivity();
		if (activity == null) throw new IllegalStateException("Fragment is not attached to parent context.");
		return activity.getTheme();
	}

	/**
	 * Delegate method for {@link Activity#runOnUiThread(Runnable)} of the parent activity.
	 *
	 * @return {@code True} if parent activity is available and action was posted, {@code false}
	 * otherwise.
	 */
	public final boolean runOnUiThread(@NonNull Runnable action) {
		final Activity activity = getActivity();
		if (activity != null) {
			activity.runOnUiThread(action);
			return true;
		}
		return false;
	}

	/**
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.updateLifecycleFlags(LIFECYCLE_DESTROYED, false);
		this.updateLifecycleFlags(LIFECYCLE_CREATED, true);
	}

	/**
	 * Checks whether this fragment is created. This is {@code true} for duration of {@link #onCreate(Bundle)}
	 * and {@link #onDestroy()}.
	 * <p>
	 * When this method returns {@code true} the opposite lifecycle state method {@link #isDestroyed()}
	 * returns {@code false} and vise versa.
	 *
	 * @return {@code True} if fragment is created, {@code false} otherwise.
	 * @see #isDestroyed()
	 */
	public final boolean isCreated() {
		return hasLifecycleFlag(LIFECYCLE_CREATED);
	}

	/**
	 */
	@Override
	public void onStart() {
		super.onStart();
		this.updateLifecycleFlags(LIFECYCLE_STOPPED, false);
		this.updateLifecycleFlags(LIFECYCLE_STARTED, true);
	}

	/**
	 * Checks whether this fragment is in the started lifecycle state. This is {@code true} for
	 * duration of {@link #onStart()} and {@link #onStop()}.
	 * <p>
	 * When this method returns {@code true} the opposite lifecycle state method {@link #isStopped()}
	 * returns {@code false} and vise versa.
	 *
	 * @return {@code True} if fragment has been started, {@code false} otherwise.
	 * @see #isStopped()
	 */
	public final boolean isStarted() {
		return hasLifecycleFlag(LIFECYCLE_STARTED);
	}

	/**
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mAnnotationHandler != null) {
			final int viewResource = mAnnotationHandler.getContentViewResource(-1);
			if (viewResource != -1) {
				if (mAnnotationHandler.shouldAttachContentViewToContainer()) {
					inflater.inflate(viewResource, container, true);
					return null;
				}
				return inflater.inflate(viewResource, container, false);
			}
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	/**
	 */
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (mAnnotationHandler != null) {
			final int backgroundResId = mAnnotationHandler.getContentViewBackgroundResId(-1);
			if (backgroundResId != -1) {
				view.setBackgroundResource(backgroundResId);
			}
		}
	}

	/**
	 * Returns a boolean flag indicating whether the view is already created or not.
	 *
	 * @return {@code True} if the view of this fragment is already created, {@code false} otherwise.
	 */
	public final boolean isViewCreated() {
		return getView() != null;
	}

	/**
	 */
	@Override
	public void setAllowEnterTransitionOverlap(boolean allow) {
		if (FragmentsConfig.TRANSITIONS_SUPPORTED) super.setAllowEnterTransitionOverlap(allow);
	}

	/**
	 */
	@Override
	public void setAllowReturnTransitionOverlap(boolean allow) {
		if (FragmentsConfig.TRANSITIONS_SUPPORTED) super.setAllowReturnTransitionOverlap(allow);
	}

	/**
	 * @see FragmentUtils#inflateTransition(Context, int)
	 * @see FragmentUtils#inflateTransitionManager(Context, int, ViewGroup)
	 */
	@Override
	public void setEnterTransition(Transition transition) {
		if (FragmentsConfig.TRANSITIONS_SUPPORTED) super.setEnterTransition(transition);
	}

	/**
	 * @see FragmentUtils#inflateTransition(Context, int)
	 * @see FragmentUtils#inflateTransitionManager(Context, int, ViewGroup)
	 */
	@Override
	public void setExitTransition(Transition transition) {
		if (FragmentsConfig.TRANSITIONS_SUPPORTED) super.setExitTransition(transition);
	}

	/**
	 * @see FragmentUtils#inflateTransition(Context, int)
	 * @see FragmentUtils#inflateTransitionManager(Context, int, ViewGroup)
	 */
	@Override
	public void setReenterTransition(Transition transition) {
		if (FragmentsConfig.TRANSITIONS_SUPPORTED) super.setReenterTransition(transition);
	}

	/**
	 * @see FragmentUtils#inflateTransition(Context, int)
	 * @see FragmentUtils#inflateTransitionManager(Context, int, ViewGroup)
	 */
	@Override
	public void setReturnTransition(Transition transition) {
		if (FragmentsConfig.TRANSITIONS_SUPPORTED) super.setReturnTransition(transition);
	}

	/**
	 * @see FragmentUtils#inflateTransition(Context, int)
	 * @see FragmentUtils#inflateTransitionManager(Context, int, ViewGroup)
	 */
	@Override
	public void setSharedElementEnterTransition(Transition transition) {
		if (FragmentsConfig.TRANSITIONS_SUPPORTED) super.setSharedElementEnterTransition(transition);
	}

	/**
	 * @see FragmentUtils#inflateTransition(Context, int)
	 * @see FragmentUtils#inflateTransitionManager(Context, int, ViewGroup)
	 */
	@Override
	public void setSharedElementReturnTransition(Transition transition) {
		if (FragmentsConfig.TRANSITIONS_SUPPORTED) super.setSharedElementReturnTransition(transition);
	}

	/**
	 */
	@Override
	public void onResume() {
		super.onResume();
		this.updateLifecycleFlags(LIFECYCLE_PAUSED, false);
		this.updateLifecycleFlags(LIFECYCLE_RESUMED, true);
	}

	/**
	 */
	/*@Override
	public final boolean isResumed() {
		return hasLifecycleFlag(LIFECYCLE_RESUMED);
	}*/

	/**
	 * Dispatches to {@link #onViewClick(View)}.
	 * <p>
	 * By default returns {@code false} for all passed views.
	 */
	@Override
	public boolean dispatchViewClick(@NonNull View view) {
		onViewClick(view);
		return false;
	}

	/**
	 * Invoked immediately after {@link #dispatchViewClick(View)} was called to process
	 * click event on the given <var>view</var>.
	 *
	 * @param view The clicked view dispatched to this fragment.
	 */
	protected void onViewClick(@NonNull View view) {
	}

	/**
	 * Starts a loader with the specified <var>id</var>. If there was already started loader with the
	 * same id before, such a loader will be <b>re-started</b>, otherwise new loader will be <b>initialized</b>.
	 *
	 * @param id        Id of the desired loader to start.
	 * @param params    Params for loader.
	 * @param callbacks Callbacks for loader.
	 * @return Initialized or re-started loader instance or {@code null} if the specified <var>callbacks</var>
	 * do not create loader for the specified <var>id</var>.
	 * @see #initLoader(int, Bundle, LoaderManager.LoaderCallbacks)
	 * @see #restartLoader(int, Bundle, LoaderManager.LoaderCallbacks)
	 * @see #destroyLoader(int)
	 */
	@Nullable
	public <D> Loader<D> startLoader(@IntRange(from = 0) int id, @Nullable Bundle params, @NonNull LoaderManager.LoaderCallbacks<D> callbacks) {
		final LoaderManager manager = getLoaderManager();
		if (manager.getLoader(id) == null) return initLoader(id, params, callbacks);
		else return restartLoader(id, params, callbacks);
	}

	/**
	 * Initializes a loader with the specified <var>id</var> for the given <var>callbacks</var>.
	 *
	 * @param id        Id of the desired loader to init.
	 * @param params    Params for loader.
	 * @param callbacks Callbacks for loader.
	 * @return Initialized loader instance or {@code null} if the specified <var>callbacks</var> do
	 * not create loader for the specified <var>id</var>.
	 * @see #startLoader(int, Bundle, LoaderManager.LoaderCallbacks)
	 * @see #restartLoader(int, Bundle, LoaderManager.LoaderCallbacks)
	 * @see #destroyLoader(int)
	 * @see LoaderManager#initLoader(int, Bundle, LoaderManager.LoaderCallbacks)
	 */
	@Nullable
	public <D> Loader<D> initLoader(@IntRange(from = 0) int id, @Nullable Bundle params, @NonNull LoaderManager.LoaderCallbacks<D> callbacks) {
		return getLoaderManager().initLoader(id, params, callbacks);
	}

	/**
	 * Re-starts a loader with the specified <var>id</var> for the given <var>callbacks</var>.
	 *
	 * @param id        Id of the desired loader to re-start.
	 * @param params    Params for loader.
	 * @param callbacks Callbacks for loader.
	 * @return Re-started loader instance or {@code null} if the specified <var>callbacks</var> do
	 * not create loader for the specified <var>id</var>.
	 * @see #startLoader(int, Bundle, LoaderManager.LoaderCallbacks)
	 * @see #initLoader(int, Bundle, LoaderManager.LoaderCallbacks)
	 * @see #destroyLoader(int)
	 * @see LoaderManager#restartLoader(int, Bundle, LoaderManager.LoaderCallbacks)
	 */
	@Nullable
	public <D> Loader<D> restartLoader(@IntRange(from = 0) int id, @Nullable Bundle params, @NonNull LoaderManager.LoaderCallbacks<D> callbacks) {
		return getLoaderManager().restartLoader(id, params, callbacks);
	}

	/**
	 * Destroys a loader with the specified <var>id</var>.
	 *
	 * @param id Id of the desired loader to destroy.
	 * @see #initLoader(int, Bundle, LoaderManager.LoaderCallbacks)
	 * @see #restartLoader(int, Bundle, LoaderManager.LoaderCallbacks)
	 * @see LoaderManager#destroyLoader(int)
	 */
	public void destroyLoader(@IntRange(from = 0) int id) {
		getLoaderManager().destroyLoader(id);
	}

	/**
	 */
	@Override
	public void onPause() {
		super.onPause();
		this.updateLifecycleFlags(LIFECYCLE_RESUMED, false);
		this.updateLifecycleFlags(LIFECYCLE_PAUSED, true);
	}

	/**
	 * Checks whether this fragment is in the paused lifecycle state. This is {@code true} for
	 * duration of {@link #onPause()} until {@link #onResume()} is called again.
	 * <p>
	 * When this method returns {@code true} the opposite lifecycle state method {@link #isResumed()}
	 * returns {@code false} and vise versa.
	 *
	 * @return {@code True} if fragment has been paused, {@code false} otherwise.
	 * @see #isResumed()
	 */
	public final boolean isPaused() {
		return hasLifecycleFlag(LIFECYCLE_PAUSED);
	}

	/**
	 */
	@Override
	public void onStop() {
		super.onStop();
		this.updateLifecycleFlags(LIFECYCLE_STARTED, false);
		this.updateLifecycleFlags(LIFECYCLE_STOPPED, true);
	}

	/**
	 * Checks whether this fragment is in the stopped lifecycle state. This is {@code true} for
	 * duration of {@link #onStop()} until {@link #onStart()} is called again.
	 * <p>
	 * When this method returns {@code true} the opposite lifecycle state method {@link #isStarted()}
	 * returns {@code false} and vise versa.
	 *
	 * @return {@code True} if fragment has been stopped, {@code false} otherwise.
	 * @see #isStarted()
	 */
	public final boolean isStopped() {
		return hasLifecycleFlag(LIFECYCLE_STOPPED);
	}

	/**
	 */
	@Override
	public boolean dispatchBackPress() {
		return onBackPress();
	}

	/**
	 * Invoked immediately after {@link #dispatchBackPress()} was called to process back press event.
	 *
	 * @return {@code True} if this instance of fragment has processed the back press event,
	 * {@code false} otherwise.
	 */
	protected boolean onBackPress() {
		return false;
	}

	/**
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		this.updateLifecycleFlags(LIFECYCLE_CREATED, false);
		this.updateLifecycleFlags(LIFECYCLE_DESTROYED, true);
	}

	/**
	 * Checks whether this fragment is destroyed. This is {@code true} whenever {@link #onDestroy()}
	 * has been called.
	 * <p>
	 * When this method returns {@code true} the opposite lifecycle state method {@link #isCreated()}
	 * returns {@code false} and vise versa.
	 *
	 * @return {@code True} if fragment is destroyed, {@code false} otherwise.
	 * @see #isCreated()
	 */
	public final boolean isDestroyed() {
		return hasLifecycleFlag(LIFECYCLE_DESTROYED);
	}

	/**
	 */
	@Override
	public void onDetach() {
		super.onDetach();
		this.updateLifecycleFlags(LIFECYCLE_ATTACHED, false);
		this.updateLifecycleFlags(LIFECYCLE_DETACHED, true);
		this.mActivityDelegate = null;
	}

	/**
	 */
	/*@Override
	public final boolean isDetached() {
		return hasLifecycleFlag(LIFECYCLE_DETACHED);
	}*/

	/**
	 * Inner classes ===============================================================================
	 */
}
