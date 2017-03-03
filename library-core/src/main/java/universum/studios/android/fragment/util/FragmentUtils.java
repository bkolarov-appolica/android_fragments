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
package universum.studios.android.fragment.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.TransitionRes;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.view.ViewGroup;

import universum.studios.android.fragment.FragmentsConfig;

/**
 * Utility class for the Fragments library.
 *
 * @author Martin Albedinsky
 */
public final class FragmentUtils {

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "FragmentUtils";

	/**
	 * Boolean flag indicating whether we can use resources access in a way appropriate for
	 * {@link Build.VERSION_CODES#LOLLIPOP} Android version.
	 */
	private static final boolean ACCESS_LOLLIPOP = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;

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
	 * Constructors ================================================================================
	 */

	/**
	 */
	private FragmentUtils() {
		// Creation of instances of this class is not publicly allowed.
	}

	/**
	 * Methods =====================================================================================
	 */

	/**
	 * Inflates a desired Transition from the specified <var>resource</var>.
	 *
	 * @param context  Context used for inflation process.
	 * @param resource Resource id of the desired transition to inflate.
	 * @return Inflated transition or {@code null} if the current API level does not support transitions.
	 * @see TransitionInflater#inflateTransition(int)
	 * @see #inflateTransitionManager(Context, int, ViewGroup)
	 */
	@Nullable
	@SuppressLint("NewApi")
	public static Transition inflateTransition(@NonNull Context context, @TransitionRes int resource) {
		return FragmentsConfig.TRANSITIONS_SUPPORTED ? TransitionInflater.from(context).inflateTransition(resource) : null;
	}

	/**
	 * Inflates a desired TransitionManager from the specified <var>resource</var>.
	 *
	 * @param context   Context used for inflation process.
	 * @param resource  Resource id of the desired transition manager to inflate.
	 * @param sceneRoot Root of the scene for which to inflate transition manager.
	 * @return Inflated transition manager or {@code null} if the current API level does not support
	 * transitions.
	 * @see TransitionInflater#inflateTransitionManager(int, ViewGroup)
	 * @see #inflateTransition(Context, int)
	 */
	@Nullable
	@SuppressLint("NewApi")
	public static TransitionManager inflateTransitionManager(@NonNull Context context, @TransitionRes int resource, @NonNull ViewGroup sceneRoot) {
		return FragmentsConfig.TRANSITIONS_SUPPORTED ? TransitionInflater.from(context).inflateTransitionManager(resource, sceneRoot) : null;
	}

	/**
	 * Obtains vector drawable with the specified <var>resId</var> using the given <var>resources</var>.
	 * <p>
	 * This utility method will obtain the requested vector drawable in a way that is appropriate
	 * for the current Android version.
	 *
	 * @param resources The resources that should be used to obtain the vector drawable.
	 * @param resId     Resource id of the desired vector drawable to obtain.
	 * @param theme     Theme that will be used to resolve theme attributes for the requested drawable
	 *                  on {@link Build.VERSION_CODES#LOLLIPOP} and above Android versions.
	 * @return Instance of the requested vector drawable or {@code null} if the specified resource
	 * id is {@code 0}.
	 * @see #getDrawable(Resources, int, Resources.Theme)
	 * @see VectorDrawableCompat#create(Resources, int, Resources.Theme)
	 */
	@Nullable
	public static Drawable getVectorDrawable(@NonNull Resources resources, @DrawableRes int resId, @Nullable Resources.Theme theme) throws Resources.NotFoundException {
		if (resId == 0) return null;
		else return ACCESS_LOLLIPOP ? getDrawable(resources, resId, theme) : VectorDrawableCompat.create(resources, resId, theme);
	}

	/**
	 * Obtains drawable with the specified <var>resId</var> using the given <var>resources</var>.
	 * <p>
	 * This utility method will obtain the requested drawable in a way that is appropriate for the
	 * current Android version.
	 *
	 * @param resources The resources that should be used to obtain the drawable.
	 * @param resId     Resource id of the desired drawable to obtain.
	 * @param theme     Theme that will be used to resolve theme attributes for the requested drawable
	 *                  on {@link Build.VERSION_CODES#LOLLIPOP} and above Android versions.
	 * @return Instance of the requested drawable or {@code null} if the specified resource id is {@code 0}.
	 * @see Resources#getDrawable(int, Resources.Theme)
	 * @see Resources#getDrawable(int)
	 */
	@Nullable
	@SuppressWarnings({"NewApi", "deprecation"})
	public static Drawable getDrawable(@NonNull Resources resources, @DrawableRes int resId, @Nullable Resources.Theme theme) throws Resources.NotFoundException {
		if (resId == 0) return null;
		else return ACCESS_LOLLIPOP ? resources.getDrawable(resId, theme) : resources.getDrawable(resId);
	}

	/**
	 * Inner classes ===============================================================================
	 */
}
