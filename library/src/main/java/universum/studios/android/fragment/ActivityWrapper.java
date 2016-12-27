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

import android.app.Activity;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;

/**
 * Wrapper class used to wrap an instance of {@link Activity} to hide some implementation details
 * when using Activity context within fragments of the Fragments library.
 *
 * @author Martin Albedinsky
 */
abstract class ActivityWrapper {

	/**
	 * Interface ===================================================================================
	 */

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "ActivityWrapper";

	/**
	 * Static members ==============================================================================
	 */

	/**
	 * Members =====================================================================================
	 */

	/**
	 * Wrapped activity instance.
	 */
	@SuppressWarnings("WeakerAccess") final Activity mActivity;

	/**
	 * Constructors ================================================================================
	 */

	/**
	 * Creates a new instance of ActivityWrapper.
	 *
	 * @param activity The activity instance to be wrapped.
	 */
	private ActivityWrapper(Activity activity) {
		this.mActivity = activity;
	}

	/**
	 * Methods =====================================================================================
	 */

	/**
	 * Wraps the specified <var>activity</var> to its corresponding wrapper depends on the activity's
	 * implementation.
	 *
	 * @param activity The activity to be wrapped.
	 * @return An instance of ActivityWrapper for the specified activity.
	 */
	static ActivityWrapper wrapActivity(Activity activity) {
		if (activity instanceof AppCompatActivity) {
			return new ActionBarWrapperImpl(activity);
		} else if (activity instanceof FragmentActivity) {
			return new SupportWrapperImpl(activity);
		}
		return new WrapperImpl(activity);
	}

	/**
	 * Returns the wrapped activity.
	 *
	 * @return Activity of this wrapper.
	 */
	final Activity getActivity() {
		return mActivity;
	}

	/**
	 * Delegate method for {@link android.support.v7.app.AppCompatActivity#getSupportActionBar()}.
	 */
	abstract android.support.v7.app.ActionBar getSupportActionBar();

	/**
	 * Delegate method for {@link Activity#startActionMode(android.view.ActionMode.Callback)}.
	 */
	abstract ActionMode startActionMode(ActionMode.Callback callback);

	/**
	 * Delegate method for {@link Activity#invalidateOptionsMenu()}.
	 */
	abstract void invalidateOptionsMenu();

	/**
	 * Delegate method for {@link Activity#requestWindowFeature(int)}.
	 */
	abstract boolean requestWindowFeature(int featureId);

	/**
	 * Inner classes ===============================================================================
	 */

	/**
	 * An {@link ActivityWrapper} implementation used to wrap base {@link FragmentActivity} implementation.
	 */
	private static class WrapperImpl extends ActivityWrapper {

		/**
		 * Creates a new instance of WrapperImpl to wrap the specified <var>activity</var>.
		 *
		 * @param activity The base FragmentActivity instance to be wrapped.
		 */
		WrapperImpl(Activity activity) {
			super(activity);
		}

		/**
		 */
		@Override
		android.support.v7.app.ActionBar getSupportActionBar() {
			return null;
		}

		/**
		 */
		@Override
		ActionMode startActionMode(ActionMode.Callback callback) {
			// Ignored for the support package version.
			return null;
		}

		/**
		 */
		@Override
		void invalidateOptionsMenu() {
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB)
				mActivity.invalidateOptionsMenu();
		}

		@Override
		boolean requestWindowFeature(int featureId) {
			return mActivity.requestWindowFeature(featureId);
		}
	}

	/**
	 * A {@link WrapperImpl} implementation used to wrap {@link FragmentActivity} implementation.
	 */
	private static class SupportWrapperImpl extends WrapperImpl {

		/**
		 * Creates a new instance of SupportWrapperImpl to wrap the specified <var>activity</var>.
		 *
		 * @param activity The FragmentActivity instance to be wrapped.
		 */
		SupportWrapperImpl(Activity activity) {
			super(activity);
		}
	}

	/**
	 * A {@link SupportWrapperImpl} implementation used to wrap {@link AppCompatActivity} implementation.
	 */
	private static final class ActionBarWrapperImpl extends SupportWrapperImpl {

		/**
		 * Creates a new instance of ActionBarWrapperImpl to wrap the specified <var>activity</var>.
		 *
		 * @param activity The AppCompatActivity instance to be wrapped.
		 */
		ActionBarWrapperImpl(Activity activity) {
			super(activity);
		}

		/**
		 */
		@Override
		android.support.v7.app.ActionBar getSupportActionBar() {
			return ((AppCompatActivity) mActivity).getSupportActionBar();
		}

		/**
		 */
		@Override
		ActionMode startActionMode(ActionMode.Callback callback) {
			return ((AppCompatActivity) mActivity).startSupportActionMode(callback);
		}

		/**
		 */
		@Override
		void invalidateOptionsMenu() {
			((AppCompatActivity) mActivity).supportInvalidateOptionsMenu();
		}

		/**
		 */
		@Override
		boolean requestWindowFeature(int featureId) {
			return ((AppCompatActivity) mActivity).supportRequestWindowFeature(featureId);
		}
	}
}
