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
package universum.studios.android.support.fragment.manage;

import android.app.FragmentTransaction;
import android.support.annotation.AnimatorRes;
import android.support.annotation.NonNull;

/**
 * FragmentTransition provides a foursome of animation resources that are meant for {@link FragmentTransaction}.
 * <p>
 * Implementations of FragmentTransition class may be supplied to {@link FragmentTransactionOptions}
 * to animate changes between desired fragments.
 *
 * @author Martin Albedinsky
 */
public interface FragmentTransition {

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Constant used to identify no animation resource provided by fragment transition.
	 */
	int NO_ANIMATION = 0;

	/**
	 * Methods =====================================================================================
	 */

	/**
	 * todo:
	 *
	 * @return
	 */
	@AnimatorRes
	int getIncomingAnimation();

	/**
	 * todo:
	 *
	 * @return
	 */
	@AnimatorRes
	int getOutgoingAnimation();

	/**
	 * todo:
	 *
	 * @return
	 */
	@AnimatorRes
	int getIncomingBackStackAnimation();

	/**
	 * todo:
	 *
	 * @return
	 */
	@AnimatorRes
	int getOutgoingBackStackAnimation();

	/**
	 * todo:
	 *
	 * @return
	 */
	@NonNull
	String getName();
}
