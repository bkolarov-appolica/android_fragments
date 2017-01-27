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

import android.app.ActionBar;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;

/**
 * ActivityDelegate is used to wrap an instance of {@link Activity} in order to hide some implementation
 * details when using Activity context within fragments.
 *
 * @author Martin Albedinsky
 */
@SuppressWarnings("WeakerAccess") public abstract class ActivityDelegate {

	/**
	 * Interface ===================================================================================
	 */

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "ActivityDelegate";

	/**
	 * Flag indicating whether the output trough log-cat is enabled or not.
	 */
	// private final boolean LOG_ENABLED = true;

	/**
	 * Flag indicating whether the debug output trough log-cat is enabled or not.
	 */
	// private final boolean DEBUG_ENABLED = true;

	/**
	 * Static members ==============================================================================
	 */

	/**
	 * Members =====================================================================================
	 */

	/**
	 * Wrapped activity instance to which will be this delegate delegating its calls.
	 */
	protected final Activity mActivity;

	/**
	 * Constructors ================================================================================
	 */

	/**
	 * Creates a new instance of ActivityDelegate for the given <var>activity</var>.
	 *
	 * @param activity The activity for which is the new delegate being created.
	 */
	protected ActivityDelegate(@NonNull Activity activity) {
		this.mActivity = activity;
	}

	/**
	 * Methods =====================================================================================
	 */

	/**
	 * Wraps the given <var>activity</var> into its corresponding delegate depending on the activity's
	 * implementation.
	 *
	 * @param activity The activity to be wrapped.
	 * @return Instance of ActivityDelegate for the specified activity.
	 */
	@NonNull
	public static ActivityDelegate create(Activity activity) {
		if (activity instanceof AppCompatActivity) {
			return new AppCompatImpl((AppCompatActivity) activity);
		}
		return new Impl(activity);
	}

	/**
	 * Delegates to {@link Activity#requestWindowFeature(int)}.
	 */
	public abstract boolean requestWindowFeature(int featureId);

	/**
	 * Delegates to {@link Activity#invalidateOptionsMenu()}.
	 */
	public abstract void invalidateOptionsMenu();

	/**
	 * Delegates to {@link Activity#getActionBar()}.
	 */
	@Nullable
	public abstract ActionBar getActionBar();

	/**
	 * Delegates to {@link AppCompatActivity#getSupportActionBar()}.
	 */
	@Nullable
	public abstract android.support.v7.app.ActionBar getSupportActionBar();

	/**
	 * Delegates to {@link Activity#startActionMode(ActionMode.Callback)}.
	 */
	@Nullable
	public abstract ActionMode startActionMode(@NonNull ActionMode.Callback callback);

	/**
	 * Inner classes ===============================================================================
	 */

	/**
	 * An {@link ActivityDelegate} implementation used to wrap basic {@link Activity}.
	 */
	private static class Impl extends ActivityDelegate {

		/**
		 * Creates a new instance of Impl to wrap the given <var>activity</var>.
		 *
		 * @param activity The Activity instance to be wrapped.
		 */
		private Impl(Activity activity) {
			super(activity);
		}

		/**
		 */
		@Override
		public boolean requestWindowFeature(int featureId) {
			return mActivity.requestWindowFeature(featureId);
		}

		/**
		 */
		@Override
		public void invalidateOptionsMenu() {
			mActivity.invalidateOptionsMenu();
		}

		/**
		 */
		@Nullable
		@Override
		public ActionBar getActionBar() {
			return mActivity.getActionBar();
		}

		/**
		 */
		@Nullable
		@Override
		public android.support.v7.app.ActionBar getSupportActionBar() {
			return null;
		}

		/**
		 */
		@Nullable
		@Override
		public ActionMode startActionMode(@NonNull ActionMode.Callback callback) {
			return mActivity.startActionMode(callback);
		}
	}

	/**
	 * A {@link Impl} implementation used to wrap {@link AppCompatActivity}.
	 */
	private static final class AppCompatImpl extends Impl {

		/**
		 * Creates a new instance of AppCompatImpl to wrap the given <var>activity</var>.
		 *
		 * @param activity The AppCompatImpl instance to be wrapped.
		 */
		private AppCompatImpl(AppCompatActivity activity) {
			super(activity);
		}

		/**
		 */
		@Override
		public boolean requestWindowFeature(int featureId) {
			return ((AppCompatActivity) mActivity).supportRequestWindowFeature(featureId);
		}

		/**
		 */
		@Override
		public void invalidateOptionsMenu() {
			((AppCompatActivity) mActivity).supportInvalidateOptionsMenu();
		}

		/**
		 */
		@Override
		public android.support.v7.app.ActionBar getSupportActionBar() {
			return ((AppCompatActivity) mActivity).getSupportActionBar();
		}
	}
}
