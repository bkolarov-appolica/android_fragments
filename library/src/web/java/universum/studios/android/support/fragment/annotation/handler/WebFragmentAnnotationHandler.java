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

import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import universum.studios.android.support.fragment.WebFragment;
import universum.studios.android.support.fragment.annotation.WebContent;

/**
 * An {@link ActionBarFragmentAnnotationHandler} extended interface for annotation handlers from the
 * Fragments library that are used to handle processing of annotations attached to classes derived
 * from {@link WebFragment} class provided by this library.
 *
 * @author Martin Albedinsky
 * @see WebFragment
 */
public interface WebFragmentAnnotationHandler extends ActionBarFragmentAnnotationHandler {

	/**
	 * Returns the web content string resource id obtained from {@link WebContent @WebContent}
	 * annotation (if presented) from {@link WebContent#valueRes()}.
	 *
	 * @param defaultResId Default content resource id to be returned if there is no annotation
	 *                     presented or resource id is not specified.
	 * @return Via annotation specified web content resource id or <var>defaultResId</var>.
	 */
	@StringRes
	int getWebContentResId(@StringRes int defaultResId);

	/**
	 * Returns the web content string obtained from {@link WebContent @WebContent} annotation
	 * (if presented) from {@link WebContent#value()}.
	 *
	 * @param defaultContent Default content to be returned if there is no annotation presented or
	 *                       content is not specified.
	 * @return Via annotation specified web content or <var>defaultContent</var>.
	 */
	@Nullable
	String getWebContent(@Nullable String defaultContent);
}
