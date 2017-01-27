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

import android.support.v4.app.FragmentTransaction;
import android.os.Parcelable;
import android.support.annotation.AnimRes;
import android.support.annotation.NonNull;

/**
 * FragmentTransition provides a foursome of animation resources that are meant for {@link FragmentTransaction}.
 * <p>
 * Implementations of FragmentTransition class may be supplied to {@link FragmentRequest} to animate
 * changes between desired fragments.
 *
 * @author Martin Albedinsky
 */
public interface FragmentTransition extends Parcelable {

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
	 * Returns the animation resource for a new incoming fragment.
	 *
	 * @return Animation resource or {@link #NO_ANIMATION} if no animation should be played for
	 * incoming fragment.
	 */
	@AnimRes
	int getIncomingAnimation();

	/**
	 * Returns the animation resource for the current outgoing fragment.
	 *
	 * @return Animation resource or {@link #NO_ANIMATION} if no animation should be played for
	 * outgoing fragment.
	 */
	@AnimRes
	int getOutgoingAnimation();

	/**
	 * Returns the animation resource for an old incoming fragment when it is being popped from
	 * the back stack.
	 *
	 * @return Animation resource or {@link #NO_ANIMATION} if no animation should be played for
	 * outgoing back-stacked fragment.
	 */
	@AnimRes
	int getIncomingBackStackAnimation();

	/**
	 * Returns the animation resource for the current outgoing fragment when it is being popped from
	 * the back stack.
	 *
	 * @return Animation resource or {@link #NO_ANIMATION} if no animation should be played for
	 * outgoing back-stacked fragment.
	 */
	@AnimRes
	int getOutgoingBackStackAnimation();

	/**
	 * Returns the name of this fragment transition.
	 *
	 * @return Name of this transition.
	 */
	@NonNull
	String getName();
}
