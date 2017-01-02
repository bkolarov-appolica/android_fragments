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
package universum.studios.android.support.fragment.annotation.handler;

import android.support.v4.app.Fragment;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.v7.view.ActionMode;
import android.view.Menu;

import universum.studios.android.support.fragment.ActionBarFragment;
import universum.studios.android.support.fragment.ActionBarDelegate;
import universum.studios.android.support.fragment.annotation.ActionBarOptions;
import universum.studios.android.support.fragment.annotation.ActionModeOptions;
import universum.studios.android.support.fragment.annotation.MenuOptions;

/**
 * A {@link FragmentAnnotationHandler} extended interface for annotation handlers from the Fragments
 * library that are used to handle processing of annotations attached to classes derived from the
 * <b>ActionBar Fragment</b> classes provided by this library.
 *
 * @author Martin Albedinsky
 * @see ActionBarFragment
 */
public interface ActionBarFragmentAnnotationHandler extends FragmentAnnotationHandler {

	/**
	 * Performs configuration of the ActionBar wrapped by the given <var>actionBarDelegate</var> based
	 * on {@link ActionBarOptions @ActionBarOptions} annotation (if presented).
	 *
	 * @param actionBarDelegate The delegate with ActionBar to configure.
	 */
	void configureActionBar(@NonNull ActionBarDelegate actionBarDelegate);

	/**
	 * Returns a boolean flag indicating whether there is {@link MenuOptions @MenuOptions} annotation
	 * presented or {@link ActionBarOptions @ActionBarOptions} annotation with <b>home as up</b>
	 * indicator enabled.
	 *
	 * @return {@code True} if the related fragment should indicate that it wants to participate on
	 * population of the options menu via {@link Fragment#setHasOptionsMenu(boolean)}, {@code false}
	 * otherwise.
	 */
	boolean hasOptionsMenu();

	/**
	 * Returns a boolean flag obtained from {@link MenuOptions @MenuOptions} annotation (if presented)
	 * from {@link MenuOptions#clear()} attribute.
	 *
	 * @return {@code True} to clear menu in context of the related fragment before it is populated
	 * with menu items of that fragment, {@code false} otherwise.
	 */
	boolean shouldClearOptionsMenu();

	/**
	 * Returns the menu resource obtained from {@link MenuOptions @MenuOptions} annotation (if presented)
	 * from {@link MenuOptions#value()} attribute.
	 *
	 * @param defaultResource Default menu resource to be returned if there is no annotation presented
	 *                        or resource is not specified.
	 * @return Via annotation specified menu resource or <var>defaultResource</var>.
	 */
	@MenuRes
	int getOptionsMenuResource(@MenuRes int defaultResource);

	/**
	 * Returns the menu flags obtained from {@link MenuOptions @MenuOptions} annotation (if presented)
	 * from {@link MenuOptions#flags()} attribute.
	 *
	 * @param defaultFlags Default menu flags to be returned if there is no annotation presented.
	 * @return Via annotation specified menu flags or <var>defaultFlags</var>.
	 */
	int getOptionsMenuFlags(int defaultFlags);

	/**
	 * Handles creation of the specified <var>actionMode</var> by inflating its menu with options
	 * specified via {@link ActionModeOptions @ActionModeOptions} annotation.
	 *
	 * @param actionMode The started action mode of which creation to handle.
	 * @param menu       The menu where to possibly inflate menu options.
	 * @return {@code True} if there were inflated some menu options into the specified <var>menu</var>,
	 * {@code false} otherwise.
	 */
	boolean handleCreateActionMode(@NonNull ActionMode actionMode, @NonNull Menu menu);
}
