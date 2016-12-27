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

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import universum.studios.android.fragment.annotation.ActionBarOptions;
import universum.studios.android.fragment.annotation.FragmentAnnotations;
import universum.studios.android.fragment.annotation.MenuOptions;
import universum.studios.android.fragment.annotation.handler.ActionBarFragmentAnnotationHandler;
import universum.studios.android.fragment.annotation.handler.AnnotationHandlers;

/**
 * A {@link BaseFragment} implementation that provides API allowing to access {@link ActionBar} directly
 * from an implementation of this class. ActionBar is accessed through parent activity to which
 * is the ActionBarFragment implementation attached. ActionBar can be accessed via {@link #getActionBar()}
 * after the fragment has been created, so its {@link #onCreate(Bundle)} has been called.
 * <p>
 * ActionBarFragment class provides also some delegate methods to set up ActionBar without accessing
 * it. All delegate methods and methods related to the ActionBar API are listed below:
 * <ul>
 * <li>{@link #setActionBarTitle(int)}</li>
 * <li>{@link #setActionBarTitle(CharSequence)}</li>
 * <li>{@link #setActionBarIcon(int)}</li>
 * <li>{@link #setActionBarIcon(Drawable)}</li>
 * <li>{@link #setHomeAsUpIndicator(int)}</li>
 * <li>{@link #setHomeAsUpIndicator(Drawable)}</li>
 * <li>{@link #invalidateOptionsMenu()}</li>
 * <li>{@link #requestWindowFeature(int)}</li>
 * </ul>
 *
 * <h4>Action mode</h4>
 * This fragment implementation also provides logic allowing to start an action mode via {@link #startActionMode()}
 * or {@link #startActionMode(android.support.v7.view.ActionMode.Callback)}. Whenever the new action
 * mode is started {@link #onActionModeStarted(android.support.v7.view.ActionMode)} is invoked. To
 * check whether the fragment is currently in action mode, call {@link #isInActionMode()}. The action
 * mode can be than accessed via {@link #getActionMode()}. Finishing of the started action mode can
 * be done via {@link #finishActionMode()} and {@link #onActionModeFinished()} will be invoked immediately.
 *
 * <h4>Accepted annotations</h4>
 * <ul>
 * <li>
 * {@link universum.studios.android.fragment.annotation.ActionBarOptions @ActionBarOptions} <b>[class - inherited]</b>
 * <p>
 * If this annotation is presented, all options presented within this annotation will be used to set
 * up an instance of ActionBar accessible from within context of a sub-class of ActionBarFragment.
 * Such a set up is accomplished in {@link #onViewCreated(android.view.View, Bundle)}.
 * </li>
 * <li>
 * {@link universum.studios.android.fragment.annotation.MenuOptions @MenuOptions} <b>[class - inherited]</b>
 * <p>
 * If this annotation is presented, options menu will be requested in {@link #onCreate(Bundle)}
 * by {@link #setHasOptionsMenu(boolean)} and menu will be created in {@link #onCreateOptionsMenu(Menu, MenuInflater)}
 * according to the options presented within this annotation.
 * </li>
 * <li>
 * {@link universum.studios.android.fragment.annotation.ActionModeOptions @ActionModeOptions} <b>[class - inherited]</b>
 * <p>
 * If this annotation is presented, the {@link android.support.v7.view.ActionMode} started via
 * {@link #startActionMode()} will be configured with options menu specified by this annotation
 * using an instance of {@link ActionModeCallback}.
 * </li>
 * </ul>
 *
 * @author Martin Albedinsky
 */
public class ActionBarFragment extends BaseFragment {

	/**
	 * Interface ===================================================================================
	 */

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "ActionBarFragment";

	/**
	 * Static members ==============================================================================
	 */

	/**
	 * Members =====================================================================================
	 */

	/**
	 * Current action mode started via {@link #startActionMode(ActionMode.Callback)}. May be {@code null}
	 * if no action mode has been started yet or has been finished.
	 */
	ActionMode mActionMode;

	/**
	 * Wrapper for ActionBar obtained from the parent activity of this fragment. Can be accessed
	 * immediately from {@link #onCreate(Bundle)}.
	 */
	private ActionBarWrapper mActionBarWrapper;

	/**
	 * Constructors ================================================================================
	 */

	/**
	 * Methods =====================================================================================
	 */

	/**
	 */
	@Override
	ActionBarFragmentAnnotationHandler onCreateAnnotationHandler() {
		return AnnotationHandlers.obtainActionBarFragmentHandler(getClass());
	}

	/**
	 */
	@NonNull
	@Override
	protected ActionBarFragmentAnnotationHandler getAnnotationHandler() {
		FragmentAnnotations.checkIfEnabledOrThrow();
		return (ActionBarFragmentAnnotationHandler) mAnnotationHandler;
	}

	/**
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (mAnnotationHandler != null) {
			final ActionBarFragmentAnnotationHandler annotationHandler = (ActionBarFragmentAnnotationHandler) mAnnotationHandler;
			if (annotationHandler.hasOptionsMenu()) {
				setHasOptionsMenu(true);
			}
		}
	}

	/**
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (mAnnotationHandler == null) {
			super.onCreateOptionsMenu(menu, inflater);
			return;
		}
		final ActionBarFragmentAnnotationHandler annotationHandler = (ActionBarFragmentAnnotationHandler) mAnnotationHandler;
		if (!annotationHandler.hasOptionsMenu()) {
			super.onCreateOptionsMenu(menu, inflater);
			return;
		}
		final int menuResource = annotationHandler.getOptionsMenuResource(-1);
		if (menuResource != -1) {
			if (annotationHandler.shouldClearOptionsMenu()) {
				menu.clear();
			}
			if (menuResource == 0) {
				super.onCreateOptionsMenu(menu, inflater);
				return;
			}
			switch (annotationHandler.getOptionsMenuFlags(0)) {
				case MenuOptions.IGNORE_SUPER:
					inflater.inflate(menuResource, menu);
					break;
				case MenuOptions.BEFORE_SUPER:
					inflater.inflate(menuResource, menu);
					super.onCreateOptionsMenu(menu, inflater);
					break;
				case MenuOptions.DEFAULT:
					super.onCreateOptionsMenu(menu, inflater);
					inflater.inflate(menuResource, menu);
					break;
			}
		}
	}

	/**
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.configureActionBar();
	}

	/**
	 * Called to configure ActionBar according to the {@link ActionBarOptions @ActionBarOptions}
	 * annotation (if presented).
	 */
	private void configureActionBar() {
		this.mActionBarWrapper = ActionBarWrapper.wrapActionBarOfActivity(getActivity());
		this.invalidateActionBar();
	}

	/**
	 * Called to invalidate {@link ActionBar} of the parent Activity according to the configuration
	 * of this fragment.
	 * <p>
	 * Default implementation configures the ActionBar with options specified via {@link ActionBarOptions @ActionBarOptions}
	 * annotation (if presented).
	 * <p>
	 * Inheritance hierarchies of ActionBarFragment may override this method to perform custom or
	 * additional ActionBar's invalidation.
	 */
	public void invalidateActionBar() {
		if (mActionBarWrapper != null && mAnnotationHandler != null) {
			final ActionBarFragmentAnnotationHandler annotationHandler = (ActionBarFragmentAnnotationHandler) mAnnotationHandler;
			annotationHandler.configureActionBar(mActionBarWrapper);
			if (annotationHandler.hasOptionsMenu()) {
				setHasOptionsMenu(true);
			}
		}
	}

	/**
	 * Delegate method for {@link android.app.Activity#invalidateOptionsMenu()}.
	 *
	 * @see #isActivityAvailable()
	 */
	public void invalidateOptionsMenu() {
		if (mActivityWrapper != null) mActivityWrapper.invalidateOptionsMenu();
	}

	/**
	 * Delegate method for {@link android.app.Activity#requestWindowFeature(int)}.
	 *
	 * @see #isActivityAvailable()
	 */
	public boolean requestWindowFeature(int featureId) {
		return mActivityWrapper != null && mActivityWrapper.requestWindowFeature(featureId);
	}

	/**
	 * Delegate method for {@link android.app.ActionBar#setTitle(int)}.
	 *
	 * @see #setActionBarTitle(CharSequence)
	 * @see #isActionBarAvailable()
	 */
	public void setActionBarTitle(@StringRes int resId) {
		if (mActionBarWrapper != null) mActionBarWrapper.setTitle(resId);
	}

	/**
	 * Delegate method for {@link android.app.ActionBar#setTitle(CharSequence)}.
	 *
	 * @see #setActionBarTitle(int)
	 * @see #isActionBarAvailable()
	 */
	public void setActionBarTitle(@Nullable CharSequence title) {
		if (mActionBarWrapper != null) mActionBarWrapper.setTitle(title);
	}

	/**
	 * Delegate method for {@link android.app.ActionBar#setIcon(int)}.
	 *
	 * @see #setActionBarIcon(Drawable)
	 * @see #isActionBarAvailable()
	 */
	public void setActionBarIcon(@DrawableRes int resId) {
		if (mActionBarWrapper != null) mActionBarWrapper.setIcon(resId);
	}

	/**
	 * Delegate method for {@link android.app.ActionBar#setIcon(Drawable)}.
	 *
	 * @see #setActionBarIcon(int)
	 * @see #isActionBarAvailable()
	 */
	public void setActionBarIcon(@Nullable Drawable icon) {
		if (mActionBarWrapper != null) mActionBarWrapper.setIcon(icon);
	}

	/**
	 * Delegate method for {@link android.app.ActionBar#setDisplayHomeAsUpEnabled(boolean)}.
	 *
	 * @see #setHomeAsUpIndicator(int)
	 * @see #setHomeAsUpIndicator(Drawable)
	 */
	public void setDisplayHomeAsUpEnabled(boolean enabled) {
		if (mActionBarWrapper != null) mActionBarWrapper.setDisplayHomeAsUpEnabled(enabled);
	}

	/**
	 * Delegate method for {@link android.app.ActionBar#setHomeAsUpIndicator(int)}.
	 *
	 * @see #setDisplayHomeAsUpEnabled(boolean)
	 * @see #setHomeAsUpIndicator(Drawable)
	 */
	public void setHomeAsUpIndicator(@DrawableRes int resId) {
		if (mActionBarWrapper != null) mActionBarWrapper.setHomeAsUpIndicator(resId);
	}

	/**
	 * Delegate method for {@link android.app.ActionBar#setHomeAsUpIndicator(int)} for vector drawable
	 * indicator.
	 *
	 * @see #setDisplayHomeAsUpEnabled(boolean)
	 * @see #setHomeAsUpIndicator(Drawable)
	 */
	public void setHomeAsUpVectorIndicator(@DrawableRes int resId) {
		if (mActionBarWrapper != null) mActionBarWrapper.setHomeAsUpVectorIndicator(resId);
	}

	/**
	 * Delegate method for {@link android.app.ActionBar#setHomeAsUpIndicator(Drawable)}.
	 *
	 * @see #setDisplayHomeAsUpEnabled(boolean)
	 * @see #setHomeAsUpIndicator(int)
	 */
	public void setHomeAsUpIndicator(@Nullable Drawable indicator) {
		if (mActionBarWrapper != null) mActionBarWrapper.setHomeAsUpIndicator(indicator);
	}

	/**
	 * Same as {@link #startActionMode(android.support.v7.view.ActionMode.Callback)} with a new instance
	 * of {@link ActionBarFragment.ActionModeCallback} for this fragment.
	 */
	protected boolean startActionMode() {
		return startActionMode(new ActionModeCallback(this));
	}

	/**
	 * Starts a new action mode for this fragment.
	 *
	 * @param callback The callback used to manage action mode.
	 * @return {@code True} if action mode has been started, {@code false} if this fragment
	 * is already in the action mode or the parent activity of this fragment is not available or some
	 * error occurs.
	 * @see #isInActionMode()
	 * @see #isActivityAvailable()
	 */
	protected boolean startActionMode(@NonNull ActionMode.Callback callback) {
		if (!isInActionMode() && mActivityWrapper != null) {
			final ActionMode actionMode = mActivityWrapper.startActionMode(callback);
			if (actionMode != null) {
				onActionModeStarted(actionMode);
				return true;
			}
		}
		return false;
	}

	/**
	 * Invoked immediately after {@link #startActionMode(android.support.v7.view.ActionMode.Callback)}
	 * was called and this fragment was not in the action mode yet.
	 * <p>
	 * <em>Derived classes should call through to the super class's implementation of this method.
	 * If not, proper working of action mode cannot be ensured.</em>
	 *
	 * @param actionMode Currently started action mode.
	 */
	@CallSuper
	protected void onActionModeStarted(@NonNull ActionMode actionMode) {
		this.mActionMode = actionMode;
	}

	/**
	 * Returns a boolean flag indicating whether this fragment is in action mode or not.
	 *
	 * @return {@code True} if this fragment is in the action mode, {@code false} otherwise.
	 * @see #getActionMode()
	 * @see #startActionMode(ActionMode.Callback)
	 */
	protected boolean isInActionMode() {
		return mActionMode != null;
	}

	/**
	 * Returns the current action mode (if started).
	 *
	 * @return The current action mode, or {@code null} if this fragment is not in action mode.
	 * @see #isInActionMode()
	 * @see #startActionMode(ActionMode.Callback)
	 */
	@Nullable
	protected ActionMode getActionMode() {
		return mActionMode;
	}

	/**
	 * Finishes the current action mode (if started).
	 *
	 * @return {@code True} if action mode has been finished, {@code false} if this fragment is not
	 * in action mode.
	 * @see #isInActionMode()
	 */
	protected boolean finishActionMode() {
		if (mActionMode != null) {
			mActionMode.finish();
			return true;
		}
		return false;
	}

	/**
	 * Invoked whenever {@link ActionModeCallback#onDestroyActionMode(android.support.v7.view.ActionMode)}
	 * is called on the current action mode callback (if instance of {@link ActionBarFragment.ActionModeCallback}).
	 * <p>
	 * <em>Derived classes should call through to the super class's implementation of this method.
	 * If not, proper working of action mode cannot be ensured.</em>
	 *
	 * @see #finishActionMode()
	 */
	@CallSuper
	protected void onActionModeFinished() {
		this.mActionMode = null;
	}

	/**
	 * Returns a boolean flag indicating whether the parent Activity's ActionBar is available or not.
	 *
	 * @return {@code True} if ActionBar obtained from the parent activity is available, {@code false}
	 * otherwise.
	 */
	protected boolean isActionBarAvailable() {
		return mActionBarWrapper != null;
	}

	/**
	 * Returns an instance of ActionBar that can be accessed by this fragment via its parent Activity.
	 * <b>Note</b>, that ActionBar can be accessed only between {@link #onCreate(Bundle)} and {@link #onDestroy()},
	 * otherwise exception will be thrown.
	 *
	 * @return Instance of ActionBar obtained from the parent activity.
	 * @throws IllegalStateException If this fragment is not created yet or it is already
	 *                                         destroyed.
	 */
	@Nullable
	protected ActionBar getActionBar() {
		if (!hasPrivateFlag(PFLAG_CREATED)) {
			throw new IllegalStateException(
					"Cannot access ActionBar. " + ((Object) this).getClass().getSimpleName() + " " +
							"is not created yet or it is already destroyed."
			);
		}
		return mActivityWrapper != null ? mActivityWrapper.getSupportActionBar() : null;
	}

	/**
	 */
	@Override
	protected boolean onBackPressed() {
		return finishActionMode() || super.onBackPressed();
	}

	/**
	 * Inner classes ===============================================================================
	 */

	/**
	 * A {@link ActionMode.Callback} base implementation for {@link ActionBarFragment} that can be
	 * used to simplify action mode management within an implementation of such a fragment.
	 * <p>
	 * Instance of this action mode is by default instantiated by ActionBarFragment whenever
	 * {@link #startActionMode()} is called.
	 *
	 * @author Martin Albedinsky
	 */
	public static class ActionModeCallback implements ActionMode.Callback {

		/**
		 * Instance of fragment within which context was this action mode started.
		 */
		protected final ActionBarFragment fragment;

		/**
		 * Creates a new instance of ActionModeCallback without fragment.
		 */
		public ActionModeCallback() {
			this(null);
		}

		/**
		 * Creates a new instance of ActionModeCallback for the context of the given <var>fragment</var>.
		 *
		 * @param fragment The instance of fragment in which is being action mode started.
		 */
		public ActionModeCallback(@Nullable ActionBarFragment fragment) {
			this.fragment = fragment;
		}

		/**
		 */
		@Override
		public boolean onCreateActionMode(@NonNull ActionMode actionMode, @NonNull Menu menu) {
			if (fragment == null || fragment.mAnnotationHandler == null) return false;
			final ActionBarFragmentAnnotationHandler annotationHandler = (ActionBarFragmentAnnotationHandler) fragment.mAnnotationHandler;
			return annotationHandler.handleCreateActionMode(actionMode, menu);
		}

		/**
		 */
		@Override
		public boolean onPrepareActionMode(@NonNull ActionMode actionMode, @NonNull Menu menu) {
			return false;
		}

		/**
		 */
		@Override
		public boolean onActionItemClicked(@NonNull ActionMode actionMode, @NonNull MenuItem menuItem) {
			if (fragment != null && fragment.onOptionsItemSelected(menuItem)) {
				actionMode.finish();
				return true;
			}
			return false;
		}

		/**
		 */
		@Override
		public void onDestroyActionMode(@NonNull ActionMode actionMode) {
			if (fragment != null) fragment.onActionModeFinished();
		}
	}
}
