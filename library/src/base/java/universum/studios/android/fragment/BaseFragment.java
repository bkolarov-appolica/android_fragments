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
package universum.studios.android.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import universum.studios.android.fragment.annotation.FragmentAnnotations;
import universum.studios.android.fragment.annotation.handler.BaseAnnotationHandlers;
import universum.studios.android.fragment.annotation.handler.FragmentAnnotationHandler;

/**
 * A {@link Fragment} implementation designed to provide extended API and logic that is useful almost
 * every time you need to implement your desired fragment.
 * <p>
 * The BaseFragment class specifies check methods related to its view's life cycle like {@link #isViewCreated()},
 * {@link #isViewRestored()} that can be useful whenever you need to access UI from outside of
 * {@link #onViewCreated(View, Bundle)} method or just to check whether
 * the fragment's view has been restored or not.
 * <p>
 * You can also easily dispatch view click events to your specific implementation of BaseFragment
 * via {@link #dispatchViewClick(View)} or back press events via {@link #dispatchBackPressed()}
 * from activity's context in which is your fragment presented.
 *
 * <h3>Accepted annotations</h3>
 * <ul>
 * <li>
 * {@link universum.studios.android.fragment.annotation.ContentView @ContentView} <b>[class - inherited]</b>
 * <p>
 * If this annotation is presented, the resource presented within this annotation will be used to
 * inflate the root view for an instance of annotated BaseFragment sub-class in
 * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
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
	// private static final String TAG = "BaseFragment";

	/**
	 * Private flag indicating whether this instance of fragment is restored (like after orientation change)
	 * or not.
	 */
	static final int PFLAG_CREATED = 0x00000001;

	/**
	 * Private flag indicating whether this instance of fragment is restored (like after orientation change)
	 * or not.
	 */
	static final int PFLAG_RESTORED = 0x00000001 << 1;

	/**
	 * Private flag indicating whether the view of this instance of fragment is restored or not.
	 */
	static final int PFLAG_VIEW_RESTORED = 0x00000001 << 2;

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
	 * Wrapper for activity to which is this instance of fragment currently attached, {@code null}
	 * if this fragment is not attached to any activity.
	 */
	ActivityWrapper mActivityWrapper;

	/**
	 * Stores all private flags for this object.
	 */
	int mPrivateFlags;

	/**
	 * Inflater that can be used to inflate transitions for this fragment.
	 */
	private TransitionInflater mTransitionInflater;

	/**
	 * Constructors ================================================================================
	 */

	/**
	 * Creates a new instance of BaseFragment. If annotations processing is enabled via {@link FragmentsConfig}
	 * all annotations supported by this fragment class will be processed/obtained here so they can
	 * be later used.
	 */
	public BaseFragment() {
		this.mAnnotationHandler = onCreateAnnotationHandler();
	}

	/**
	 * Methods =====================================================================================
	 */

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
	 * Creates a new instance of the given <var>classOfFragment</var> with the given <var>args</var>.
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
			e.printStackTrace();
		}
		return null;
	}

	/**
	 */
	@Override
	@SuppressWarnings("deprecation")
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mActivityWrapper = ActivityWrapper.wrapActivity(activity);
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
		if (activity == null) throw new IllegalStateException("Fragment is not attached to context.");
		return activity.getTheme();
	}

	/**
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.updatePrivateFlags(PFLAG_CREATED, true);
		this.updatePrivateFlags(PFLAG_VIEW_RESTORED, false);
		this.updatePrivateFlags(PFLAG_RESTORED, savedInstanceState != null);
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
	 */
	@Override
	public void setAllowEnterTransitionOverlap(boolean allow) {
		super.setAllowEnterTransitionOverlap(allow);
	}

	/**
	 */
	@Override
	public void setAllowReturnTransitionOverlap(boolean allow) {
		super.setAllowReturnTransitionOverlap(allow);
	}

	/**
	 * Sets an enter transition via {@link #setEnterTransition(Object)} for this fragment inflated
	 * via {@link #inflateTransition(int)}.
	 *
	 * @param resource Resource id of the desired transition. Can be {@code 0} to clear the current one.
	 */
	public void setEnterTransition(int resource) {
		setEnterTransition(resource != 0 ? inflateTransition(resource) : null);
	}

	/**
	 * @see #setEnterTransition(int)
	 */
	@Override
	public void setEnterTransition(Object transition) {
		super.setEnterTransition(transition);
	}

	/**
	 * Sets an exit transition via {@link #setExitTransition(Object)} for this fragment inflated
	 * via {@link #inflateTransition(int)}.
	 *
	 * @param resource Resource id of the desired transition. Can be {@code 0} to clear the current one.
	 */
	public void setExitTransition(int resource) {
		setExitTransition(resource != 0 ? inflateTransition(resource) : null);
	}

	/**
	 * @see #setExitTransition(int)
	 */
	@Override
	public void setExitTransition(Object transition) {
		super.setExitTransition(transition);
	}

	/**
	 * Sets a reenter transition via {@link #setReenterTransition(Object)} for this fragment inflated
	 * via {@link #inflateTransition(int)}.
	 *
	 * @param resource Resource id of the desired transition. Can be {@code 0} to clear the current one.
	 */
	public void setReenterTransition(int resource) {
		setReenterTransition(resource != 0 ? inflateTransition(resource) : null);
	}

	/**
	 * @see #setReenterTransition(int)
	 */
	@Override
	public void setReenterTransition(Object transition) {
		super.setReenterTransition(transition);
	}

	/**
	 * Sets a return transition via {@link #setReturnTransition(Object)} for this fragment inflated
	 * via {@link #inflateTransition(int)}.
	 *
	 * @param resource Resource id of the desired transition. Can be {@code 0} to clear the current one.
	 */
	public void setReturnTransition(int resource) {
		setReturnTransition(resource != 0 ? inflateTransition(resource) : null);
	}

	/**
	 * @see #setReturnTransition(int)
	 */
	@Override
	public void setReturnTransition(Object transition) {
		super.setReturnTransition(transition);
	}

	/**
	 * Sets an enter transition for shared element via {@link #setSharedElementEnterTransition(Object)}
	 * for this fragment inflated via {@link #inflateTransition(int)}.
	 *
	 * @param resource Resource id of the desired transition. Can be {@code 0} to clear the current one.
	 */
	public void setSharedElementEnterTransition(int resource) {
		setSharedElementEnterTransition(resource != 0 ? inflateTransition(resource) : null);
	}

	/**
	 * @see #setSharedElementEnterTransition(int)
	 */
	@Override
	public void setSharedElementEnterTransition(Object transition) {
		super.setSharedElementEnterTransition(transition);
	}

	/**
	 * Sets a return transition for shared element via {@link #setSharedElementReturnTransition(Object)}
	 * for this fragment inflated via {@link #inflateTransition(int)}.
	 *
	 * @param resource Resource id of the desired transition. Can be {@code 0} to clear the current one.
	 */
	public void setSharedElementReturnTransition(int resource) {
		setSharedElementReturnTransition(resource != 0 ? inflateTransition(resource) : null);
	}

	/**
	 */
	@Override
	public void setSharedElementReturnTransition(Object transition) {
		super.setSharedElementReturnTransition(transition);
	}

	/**
	 * Inflates a new transition from the specified Xml <var>resource</var>.
	 *
	 * @param resource Resource id of the desired transition to be inflated.
	 * @return Inflated transition or {@code null} if the current Android version does not support
	 * inflating of transitions from the Xml.
	 */
	@Nullable
	@SuppressLint("NewApi")
	protected Transition inflateTransition(int resource) {
		if (!FragmentsConfig.TRANSITIONS_SUPPORTED) return null;
		this.ensureTransitionInflater();
		return mTransitionInflater.inflateTransition(resource);
	}

	/**
	 * Inflates a new transition manager from the specified Xml <var>resource</var>.
	 *
	 * @param resource  Resource id of the desired transition manager to be inflated.
	 * @param sceneRoot Scene root with which to create the requested manager.
	 * @return Inflated transition manager or {@code null} if the current Android version does not
	 * support inflating of transitions from the Xml.
	 */
	@Nullable
	@SuppressLint("NewApi")
	protected TransitionManager inflateTransitionManager(int resource, @NonNull ViewGroup sceneRoot) {
		if (!FragmentsConfig.TRANSITIONS_SUPPORTED) return null;
		this.ensureTransitionInflater();
		return mTransitionInflater.inflateTransitionManager(resource, sceneRoot);
	}

	/**
	 * Ensures that the transition inflater is initialized.
	 */
	@SuppressLint("NewApi")
	private void ensureTransitionInflater() {
		if (mTransitionInflater == null)
			mTransitionInflater = TransitionInflater.from(getActivity());
	}

	/**
	 * Dispatches to {@link #onViewClick(View)}.
	 * <p>
	 * By default returns {@code false} for all passed views.
	 */
	@Override
	@SuppressWarnings("deprecation")
	public boolean dispatchViewClick(@NonNull View view) {
		onViewClick(view, view.getId());
		return false;
	}

	/**
	 * <b>Note, that this method will be removed in the feature update.</b>
	 *
	 * @deprecated Use {@link #onViewClick(View)} instead.
	 */
	@Deprecated
	protected boolean onViewClick(@NonNull View view, int id) {
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
	 * Returns a boolean flag indicating whether this fragment instance was restored or not.
	 *
	 * @return {@code True} if this fragment was restored (<i>like, after orientation change</i>),
	 * {@code false} otherwise.
	 */
	public boolean isRestored() {
		return (mPrivateFlags & PFLAG_RESTORED) != 0;
	}

	/**
	 * Returns a boolean flag indicating whether the view was restored or not.
	 *
	 * @return {@code True} if the view of this fragment was restored (<i>like, when the fragment
	 * was showed from the back stack</i>), {@code false} otherwise.
	 */
	public boolean isViewRestored() {
		return (mPrivateFlags & PFLAG_VIEW_RESTORED) != 0;
	}

	/**
	 * Returns a boolean flag indicating whether the view is already created or not.
	 *
	 * @return {@code True} if the view of this fragment is already created, {@code false} otherwise.
	 */
	public boolean isViewCreated() {
		return getView() != null;
	}

	/**
	 * Same as {@link #getString(int)}, but first is performed check if the parent activity of this
	 * fragment instance is available to prevent illegal state exceptions.
	 */
	@NonNull
	public String obtainString(@StringRes int resId) {
		return isActivityAvailable() ? getString(resId) : "";
	}

	/**
	 * Same as  {@link #getString(int, Object...)}, but first is performed check if the parent activity
	 * of this fragment instance is available to prevent illegal state exceptions.
	 */
	@NonNull
	public String obtainString(@StringRes int resId, @Nullable Object... args) {
		return isActivityAvailable() ? getString(resId, args) : "";
	}

	/**
	 * Same as {@link #getText(int)}, but first is performed check if the parent activity of this
	 * fragment instance is available to prevent illegal state exceptions.
	 */
	@NonNull
	public CharSequence obtainText(@StringRes int resId) {
		return isActivityAvailable() ? getText(resId) : "";
	}

	/**
	 * Delegate method for {@link Activity#runOnUiThread(Runnable)} of the parent activity.
	 *
	 * @return {@code True} if parent activity is available and action was posted, {@code false}
	 * otherwise.
	 */
	public final boolean runOnUiThread(@NonNull Runnable action) {
		if (isActivityAvailable()) {
			getActivity().runOnUiThread(action);
			return true;
		}
		return false;
	}

	/**
	 * Returns a boolean flag indicating whether the parent Activity of this fragment instance is
	 * available or not.
	 * <p>
	 * Parent activity is always available between {@link #onAttach(Activity)} and
	 * {@link #onDetach()} life cycle calls.
	 *
	 * @return {@code True} if activity is available, {@code false} otherwise.
	 */
	protected boolean isActivityAvailable() {
		return mActivityWrapper != null;
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
	 * @see #initLoader(int, Bundle, android.support.v4.app.LoaderManager.LoaderCallbacks)
	 * @see #restartLoader(int, Bundle, android.support.v4.app.LoaderManager.LoaderCallbacks)
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
	 * @see #startLoader(int, Bundle, android.support.v4.app.LoaderManager.LoaderCallbacks)
	 * @see #restartLoader(int, Bundle, android.support.v4.app.LoaderManager.LoaderCallbacks)
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
	 * @see #startLoader(int, Bundle, android.support.v4.app.LoaderManager.LoaderCallbacks)
	 * @see #initLoader(int, Bundle, android.support.v4.app.LoaderManager.LoaderCallbacks)
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
	public boolean dispatchBackPressed() {
		return onBackPressed();
	}

	/**
	 * Invoked immediately after {@link #dispatchBackPressed()} was called to process back press event.
	 *
	 * @return {@code True} if this instance of fragment processes dispatched back press event,
	 * {@code false} otherwise.
	 */
	protected boolean onBackPressed() {
		return false;
	}

	/**
	 */
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		this.updatePrivateFlags(PFLAG_VIEW_RESTORED, true);
	}

	/**
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		this.updatePrivateFlags(PFLAG_CREATED, false);
		this.updatePrivateFlags(PFLAG_VIEW_RESTORED, false);
	}

	/**
	 */
	@Override
	public void onDetach() {
		super.onDetach();
		this.mActivityWrapper = null;
	}

	/**
	 * Updates the current private flags.
	 *
	 * @param flag Value of the desired flag to add/remove to/from the current private flags.
	 * @param add  Boolean flag indicating whether to add or remove the specified <var>flag</var>.
	 */
	@SuppressWarnings("unused")
	void updatePrivateFlags(int flag, boolean add) {
		if (add) this.mPrivateFlags |= flag;
		else this.mPrivateFlags &= ~flag;
	}

	/**
	 * Returns a boolean flag indicating whether the specified <var>flag</var> is contained within
	 * the current private flags or not.
	 *
	 * @param flag Value of the flag to check.
	 * @return {@code True} if the requested flag is contained, {@code false} otherwise.
	 */
	@SuppressWarnings("unused")
	boolean hasPrivateFlag(int flag) {
		return (mPrivateFlags & flag) != 0;
	}

	/**
	 * Inner classes ===============================================================================
	 */
}
