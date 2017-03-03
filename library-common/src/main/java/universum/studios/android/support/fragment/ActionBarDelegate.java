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
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;

import universum.studios.android.support.fragment.util.FragmentUtils;

/**
 * ActionBarDelegate is used to wrap an instance of {@link ActionBar} or {@link android.support.v7.app.ActionBar}
 * in order to hide some implementation details when using ActionBar within fragments.
 *
 * @author Martin Albedinsky
 */
@SuppressWarnings("WeakerAccess") public abstract class ActionBarDelegate {

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "ActionBarDelegate";

	/**
	 * Interface ===================================================================================
	 */

	/**
	 * Static members ==============================================================================
	 */

	/**
	 * Members =====================================================================================
	 */

	/**
	 * Instance of context used to access application data.
	 */
	protected final Context mContext;

	/**
	 * Constructors ================================================================================
	 */

	/**
	 * Creates a new instance of ActionBarDelegate with the given <var>context</var>.
	 *
	 * @param context The context used to access application data.
	 */
	protected ActionBarDelegate(@NonNull Context context) {
		this.mContext = context;
	}

	/**
	 * Methods =====================================================================================
	 */

	/**
	 * Wraps an ActionBar of the given <var>activity</var> to its corresponding delegate depending
	 * on the activity's implementation.
	 * <p>
	 * <b>Note</b>, that this method will return {@code null} if the specified activity does not have
	 * its ActionBar available at the time.
	 *
	 * @param activity The activity of which action bar to wrap.
	 * @return Instance of ActionBarDelegate for ActionBar of the specified activity.
	 * @see Activity#getActionBar()
	 * @see AppCompatActivity#getSupportActionBar()
	 */
	@Nullable
	public static ActionBarDelegate create(@NonNull Activity activity) {
		if (activity instanceof AppCompatActivity) {
			final android.support.v7.app.ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
			return actionBar == null ? null : create(activity, actionBar);
		} else {
			final ActionBar actionBar = activity.getActionBar();
			return actionBar == null ? null : create(activity, actionBar);
		}
	}

	/**
	 * Wraps the given <var>actionBar</var> into its corresponding delegate.
	 *
	 * @param context   Context used to access application data.
	 * @param actionBar The desired action bar to wrap. May be {@code null} to create mock delegate.
	 * @return New instance of ActionBarDelegate with the given action bar.
	 */
	@NonNull
	public static ActionBarDelegate create(@NonNull Context context, @Nullable ActionBar actionBar) {
		return new Impl(context, actionBar);
	}

	/**
	 * Wraps the given support <var>actionBar</var> into its corresponding delegate.
	 *
	 * @param context   Context used to access application data.
	 * @param actionBar The desired action bar to wrap. May be {@code null} to create mock delegate.
	 * @return New instance of ActionBarDelegate with the given action bar.
	 */
	@NonNull
	public static ActionBarDelegate create(@NonNull Context context, @Nullable android.support.v7.app.ActionBar actionBar) {
		return new SupportImpl(context, actionBar);
	}

	/**
	 * Delegates to {@link ActionBar#setDisplayHomeAsUpEnabled(boolean)}.
	 */
	public abstract void setDisplayHomeAsUpEnabled(boolean enabled);

	/**
	 * Delegates to {@link ActionBar#setHomeAsUpIndicator(int)}.
	 */
	public abstract void setHomeAsUpIndicator(@DrawableRes int resId);

	/**
	 * Delegates to {@link ActionBar#setHomeAsUpIndicator(int)} for vector drawable indicator.
	 */
	public abstract void setHomeAsUpVectorIndicator(@DrawableRes int resId);

	/**
	 * Delegates to {@link ActionBar#setHomeAsUpIndicator(Drawable)}.
	 */
	public abstract void setHomeAsUpIndicator(@Nullable Drawable indicator);

	/**
	 * Delegates to {@link ActionBar#setIcon(int)}.
	 */
	public abstract void setIcon(@DrawableRes int resId);

	/**
	 * Delegates to {@link ActionBar#setIcon(Drawable)}.
	 */
	public abstract void setIcon(@Nullable Drawable icon);

	/**
	 * Delegates to {@link ActionBar#setTitle(int)}.
	 */
	public abstract void setTitle(@StringRes int resId);

	/**
	 * Delegates to {@link ActionBar#setTitle(CharSequence)}.
	 */
	public abstract void setTitle(@Nullable CharSequence title);

	/**
	 * Inner classes ===============================================================================
	 */

	/**
	 * An {@link ActionBarDelegate} implementation used to wrap {@link ActionBar}.
	 */
	private static final class Impl extends ActionBarDelegate {

		/**
		 * Wrapped action bar instance.
		 */
		private final ActionBar actionBar;

		/**
		 * Creates a new instance of Impl to wrap the given <var>actionBar</var>.
		 *
		 * @param context   Context used to access application data.
		 * @param actionBar The native action bar to be wrapped.
		 */
		private Impl(Context context, ActionBar actionBar) {
			super(context);
			this.actionBar = actionBar;
		}

		/**
		 */
		@Override
		public void setDisplayHomeAsUpEnabled(boolean enabled) {
			if (actionBar != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) actionBar.setDisplayHomeAsUpEnabled(enabled);
		}

		/**
		 */
		@Override
		public void setHomeAsUpIndicator(@DrawableRes int resId) {
			if (actionBar != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) actionBar.setHomeAsUpIndicator(resId);
		}

		/**
		 */
		@Override
		public void setHomeAsUpVectorIndicator(@DrawableRes int resId) {
			setHomeAsUpIndicator(FragmentUtils.getVectorDrawable(
					mContext.getResources(),
					resId,
					mContext.getTheme()
			));
		}

		/**
		 */
		@Override
		public void setHomeAsUpIndicator(@Nullable Drawable indicator) {
			if (actionBar != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) actionBar.setHomeAsUpIndicator(indicator);
		}

		/**
		 */
		@Override
		public void setTitle(@StringRes int resId) {
			if (actionBar != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) actionBar.setTitle(resId);
		}

		/**
		 */
		@Override
		public void setTitle(@Nullable CharSequence title) {
			if (actionBar != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) actionBar.setTitle(title);
		}

		/**
		 */
		@Override
		public void setIcon(@DrawableRes int resId) {
			if (actionBar != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) actionBar.setIcon(resId);
		}

		/**
		 */
		@Override
		public void setIcon(@Nullable Drawable icon) {
			if (actionBar != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) actionBar.setIcon(icon);
		}
	}

	/**
	 * An {@link ActionBarDelegate} implementation used to wrap {@link android.support.v7.app.ActionBar}.
	 */
	private static final class SupportImpl extends ActionBarDelegate {

		/**
		 * Wrapped support action bar instance.
		 */
		private final android.support.v7.app.ActionBar actionBar;

		/**
		 * Creates a new instance of SupportImpl to wrap the given <var>actionBar</var>.
		 *
		 * @param context   Context used to access application data.
		 * @param actionBar The support action bar to be wrapped.
		 */
		private SupportImpl(Context context, android.support.v7.app.ActionBar actionBar) {
			super(context);
			this.actionBar = actionBar;
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
		public void setHomeAsUpIndicator(@DrawableRes int resId) {
			if (actionBar != null) actionBar.setHomeAsUpIndicator(resId);
		}

		/**
		 */
		@Override
		public void setHomeAsUpVectorIndicator(@DrawableRes int resId) {
			setHomeAsUpIndicator(FragmentUtils.getVectorDrawable(
					mContext.getResources(),
					resId,
					mContext.getTheme()
			));
		}

		/**
		 */
		@Override
		public void setHomeAsUpIndicator(@Nullable Drawable indicator) {
			if (actionBar != null) actionBar.setHomeAsUpIndicator(indicator);
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
		public void setTitle(@StringRes int resId) {
			if (actionBar != null) actionBar.setTitle(resId);
		}

		/**
		 */
		@Override
		public void setTitle(@Nullable CharSequence title) {
			if (actionBar != null) actionBar.setTitle(title);
		}
	}
}
