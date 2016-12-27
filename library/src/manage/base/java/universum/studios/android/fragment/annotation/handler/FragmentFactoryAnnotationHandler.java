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
package universum.studios.android.fragment.annotation.handler;

import android.support.annotation.Nullable;
import android.util.SparseArray;

import universum.studios.android.fragment.annotation.FactoryFragment;
import universum.studios.android.fragment.annotation.FactoryFragments;
import universum.studios.android.fragment.manage.BaseFragmentFactory;
import universum.studios.android.fragment.manage.FragmentItem;

/**
 * An {@link AnnotationHandler} extended interface for annotation handlers from the Fragments library
 * that are used to handle processing of annotations attached to classes derived from the <b>Base Fragment Factory</b>
 * classes provided by this library.
 *
 * @author Martin Albedinsky
 * @see BaseFragmentFactory
 */
public interface FragmentFactoryAnnotationHandler extends AnnotationHandler {

	/**
	 * Returns an array with FragmentItems mapped to theirs ids that has been created from
	 * {@link FactoryFragment @FactoryFragment} or {@link FactoryFragments @FactoryFragments}
	 * (if presented).
	 *
	 * @return Array with fragment items created from the processed annotations or {@code null} if
	 * there were no annotations specified.
	 */
	@Nullable
	SparseArray<FragmentItem> getFragmentItems();
}
