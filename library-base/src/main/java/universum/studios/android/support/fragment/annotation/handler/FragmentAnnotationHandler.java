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

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;

import universum.studios.android.support.fragment.BaseFragment;
import universum.studios.android.support.fragment.annotation.ContentView;

/**
 * An {@link AnnotationHandler} extended interface for annotation handlers from the Fragments library
 * that are used to handle processing of annotations attached to classes derived from {@link BaseFragment}
 * class provided by this library.
 *
 * @author Martin Albedinsky
 * @see BaseFragment
 */
public interface FragmentAnnotationHandler extends AnnotationHandler {

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Constant used to determine that no resource has been specified.
	 */
	int NO_RES = 0;

	/**
	 * Methods =====================================================================================
	 */

	/**
	 * Returns a boolean flag obtained from {@link ContentView @ContentView} annotation (if presented)
	 * from {@link ContentView#attachToContainer()} attribute.
	 *
	 * @return {@code True} if attaching to container has been requested via annotation, {@code false}
	 * otherwise.
	 */
	boolean shouldAttachContentViewToContainer();

	/**
	 * Returns the content view layout resource obtained from {@link ContentView @ContentView}
	 * annotation (if presented) from {@link ContentView#value()} attribute.
	 *
	 * @param defaultViewResource Default layout resource to be returned if there is no annotation
	 *                            presented or resource is not specified.
	 * @return Via annotation specified layout resource or <var>defaultViewResource</var>.
	 */
	@LayoutRes
	int getContentViewResource(@LayoutRes int defaultViewResource);

	/**
	 * Returns the background resource id obtained from {@link ContentView @ContentView} annotation
	 * (if presented) from {@link ContentView#background()} attribute.
	 *
	 * @param defaultResId Default background resource id to be returned if there is no annotation
	 *                     presented or resource is not specified.
	 * @return Via annotation specified background resource id or <var>defaultResId</var>.
	 */
	@ColorRes
	@DrawableRes
	int getContentViewBackgroundResId(int defaultResId);
}
