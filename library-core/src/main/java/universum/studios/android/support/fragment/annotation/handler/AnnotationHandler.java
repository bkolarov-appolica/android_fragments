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

import android.support.annotation.NonNull;

/**
 * Base interface for annotation handlers from the Fragments library that are used to handle processing
 * of annotations attached to classes derived from base classes provided by this library or to theirs
 * fields.
 * <p>
 * Each handler instance has attached a single class for which annotations handling is responsible.
 * The attached class may be obtained via {@link #getAnnotatedClass()}.
 *
 * @author Martin Albedinsky
 */
public interface AnnotationHandler {

	/**
	 * Returns the class for which has been this handler created.
	 *
	 * @return Annotated class attached to this handler.
	 */
	@NonNull
	Class<?> getAnnotatedClass();
}
