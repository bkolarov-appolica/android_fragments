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

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;

import universum.studios.android.fragment.util.FragmentUtils;

/**
 * Wrapper class used to wrap an instance of {@link ActionBar} or {@link android.support.v7.app.ActionBar}
 * to hide some implementation details when using such ActionBar within Fragment's context.
 *
 * @author Martin Albedinsky
 */
public abstract class ActionBarWrapper {

	/**
	 * Interface ===================================================================================
	 */

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "ActionBarWrapper";

	/**
	 * Static members ==============================================================================
	 */

	/**
	 * Members =====================================================================================
	 */

	/**
	 * Instance of context used to access application data.
	 */
	final Context mContext;

	/**
	 * Constructors ================================================================================
	 */

	/**
	 * Creates a new instance of ActionBarWrapper with the specified <var>context</var>.
	 *
	 * @param context The context used to access application data.
	 */
	ActionBarWrapper(Context context) {
		this.mContext = context;
	}

	/**
	 * Methods =====================================================================================
	 */

	/**
	 * Returns a flag indicating whether this wrapper has its ActionBar valid or not.
	 *
	 * @return {@code True} if action bar is none {@code null}, {@code false} otherwise.
	 */
	public abstract boolean hasActionBar();

	/**
	 * Delegate method for {@link ActionBar#setTitle(int)}.
	 */
	public abstract void setTitle(@StringRes int resId);

	/**
	 * Delegate method for {@link ActionBar#setTitle(CharSequence)}.
	 */
	public abstract void setTitle(@Nullable CharSequence title);

	/**
	 * Delegate method for {@link ActionBar#setIcon(int)}.
	 */
	public abstract void setIcon(@DrawableRes int resId);

	/**
	 * Delegate method for {@link ActionBar#setIcon(Drawable)}.
	 */
	public abstract void setIcon(@Nullable Drawable icon);

	/**
	 * Delegate method for {@link ActionBar#setDisplayHomeAsUpEnabled(boolean)}.
	 */
	public abstract void setDisplayHomeAsUpEnabled(boolean enabled);

	/**
	 * Delegate method for {@link ActionBar#setHomeAsUpIndicator(int)} for vector drawable
	 * indicator.
	 */
	public abstract void setHomeAsUpVectorIndicator(@DrawableRes int resId);

	/**
	 * Delegate method for {@link ActionBar#setHomeAsUpIndicator(int)}.
	 */
	public abstract void setHomeAsUpIndicator(@DrawableRes int resId);

	/**
	 * Delegate method for {@link ActionBar#setHomeAsUpIndicator(Drawable)}.
	 */
	public abstract void setHomeAsUpIndicator(@Nullable Drawable indicator);

	/**
	 * Wraps an ActionBar of the specified <var>activity</var> to its corresponding wrapper depends
	 * on the activity's implementation.
	 * <p>
	 * <b>Note</b>, that this method will return {@code null} if the specified activity does not have
	 * its ActionBar accessible at the time.
	 *
	 * @param activity The activity of which action bar to be wrapped.
	 * @return An instance of ActionBarWrapper for action bar of the specified activity.
	 */
	static ActionBarWrapper wrapActionBarOfActivity(Activity activity) {
		ActionBarWrapper wrapper;
		if (activity instanceof AppCompatActivity) {
			wrapper = wrapSupportActionBar(activity, ((AppCompatActivity) activity).getSupportActionBar());
		} else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
			wrapper = wrapActionBar(activity, activity.getActionBar());
		} else {
			throw new IllegalStateException(
					"Cannot wrap ActionBar of " + activity + " at the current API level(" + Build.VERSION.SDK_INT + ")."
			);
		}
		return wrapper.hasActionBar() ? wrapper : null;
	}

	/**
	 * Wraps the given <var>actionBar</var> into ActionBarWrapper.
	 *
	 * @param context   Context used to access application data.
	 * @param actionBar The desired action bar to wrap. Can be {@code null} to create mock wrapper.
	 * @return New instance of ActionBarWrapper with the given action bar.
	 */
	@NonNull
	public static ActionBarWrapper wrapActionBar(@NonNull Context context, @Nullable ActionBar actionBar) {
		return new WrapperImpl(context, actionBar);
	}

	/**
	 * Wraps the given support <var>actionBar</var> into ActionBarWrapper.
	 *
	 * @param context   Context used to access application data.
	 * @param actionBar The desired action bar to wrap. Can be {@code null} to create mock wrapper.
	 * @return New instance of ActionBarWrapper with the given action bar.
	 */
	@NonNull
	public static ActionBarWrapper wrapSupportActionBar(@NonNull Context context, @Nullable android.support.v7.app.ActionBar actionBar) {
		return new SupportWrapperImpl(context, actionBar);
	}

	/**
	 * Inner classes ===============================================================================
	 */

	/**
	 * An {@link ActionBarWrapper} implementation used to wrap {@link ActionBar} implementation.
	 */
	private static final class WrapperImpl extends ActionBarWrapper {

		/**
		 * Wrapped action bar instance.
		 */
		final ActionBar actionBar;

		/**
		 * Creates a new instance of WrapperImpl to wrap the specified <var>actionBar</var>.
		 *
		 * @param context   Context used to access application data.
		 * @param actionBar The native action bar to be wrapped.
		 */
		WrapperImpl(Context context, ActionBar actionBar) {
			super(context);
			this.actionBar = actionBar;
		}

		/**
		 */
		@Override
		public boolean hasActionBar() {
			return actionBar != null;
		}

		/**
		 */
		@Override
		public void setTitle(@StringRes int resId) {
			if (actionBar != null && Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB)
				actionBar.setTitle(resId);
		}

		/**
		 */
		@Override
		public void setTitle(@Nullable CharSequence title) {
			if (actionBar != null && Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB)
				actionBar.setTitle(title);
		}

		/**
		 */
		@Override
		public void setIcon(@DrawableRes int resId) {
			if (actionBar != null && Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH)
				actionBar.setIcon(resId);
		}

		/**
		 */
		@Override
		public void setIcon(@Nullable Drawable icon) {
			if (actionBar != null && Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH)
				actionBar.setIcon(icon);
		}

		/**
		 */
		@Override
		public void setDisplayHomeAsUpEnabled(boolean enabled) {
			if (actionBar != null && Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB)
				actionBar.setDisplayHomeAsUpEnabled(enabled);
		}

		/**
		 */
		@Override
		public void setHomeAsUpVectorIndicator(@DrawableRes int resId) {
			if (actionBar != null && Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2)
				actionBar.setHomeAsUpIndicator(FragmentUtils.getVectorDrawable(
						mContext.getResources(),
						resId,
						mContext.getTheme()
				));
		}

		/**
		 */
		@Override
		public void setHomeAsUpIndicator(@DrawableRes int resId) {
			if (actionBar != null && Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2)
				actionBar.setHomeAsUpIndicator(resId);
		}

		/**
		 */
		@Override
		public void setHomeAsUpIndicator(@Nullable Drawable indicator) {
			if (actionBar != null && Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2)
				actionBar.setHomeAsUpIndicator(indicator);
		}
	}

	/**
	 * An {@link ActionBarWrapper} implementation used to wrap {@link android.support.v7.app.ActionBar}
	 * implementation.
	 */
	private static final class SupportWrapperImpl extends ActionBarWrapper {

		/**
		 * Wrapped support action bar instance.
		 */
		final android.support.v7.app.ActionBar actionBar;

		/**
		 * Creates a new instance of SupportImpl to wrap the specified <var>actionBar</var>.
		 *
		 * @param context   Context used to access application data.
		 * @param actionBar The support action bar to be wrapped.
		 */
		SupportWrapperImpl(Context context, android.support.v7.app.ActionBar actionBar) {
			super(context);
			this.actionBar = actionBar;
		}

		/**
		 */
		@Override
		public boolean hasActionBar() {
			return actionBar != null;
		}

		/**
		 */
		@Override
		public void setTitle(@StringRes int resId) {
			if (actionBar != null) actionBar.setTitle(resId);
		}

		/**
		 */
		@Override
		public void setTitle(@Nullable CharSequence title) {
			if (actionBar != null) actionBar.setTitle(title);
		}

		/**
		 */
		@Override
		public void setIcon(@DrawableRes int resId) {
			if (actionBar != null) actionBar.setIcon(resId);
		}

		/**
		 */
		@Override
		public void setIcon(@Nullable Drawable icon) {
			if (actionBar != null) actionBar.setIcon(icon);
		}

		/**
		 */
		@Override
		public void setDisplayHomeAsUpEnabled(boolean enabled) {
			if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(enabled);
		}

		/**
		 */
		@Override
		public void setHomeAsUpVectorIndicator(@DrawableRes int resId) {
			if (actionBar != null) actionBar.setHomeAsUpIndicator(FragmentUtils.getVectorDrawable(
					mContext.getResources(),
					resId,
					mContext.getTheme()
			));
		}

		/**
		 */
		@Override
		public void setHomeAsUpIndicator(@DrawableRes int resId) {
			if (actionBar != null) actionBar.setHomeAsUpIndicator(resId);
		}

		/**
		 */
		@Override
		public void setHomeAsUpIndicator(@Nullable Drawable indicator) {
			if (actionBar != null) actionBar.setHomeAsUpIndicator(indicator);
		}
	}
}
