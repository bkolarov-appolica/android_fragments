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
package universum.studios.android.fragment.manage;

import android.app.Fragment;
import android.support.annotation.Nullable;

/**
 * FragmentFactory specifies an interface for factories that may be attached to {@link FragmentController}
 * in order to provide fragment instances with theirs corresponding TAGs for that controller.
 *
 * @author Martin Albedinsky
 */
public interface FragmentFactory {

	/**
	 * Checks whether this fragment factory provides an instance of fragment for the specified
	 * <var>fragmentId</var>.
	 *
	 * @param fragmentId Id of the desired fragment to check.
	 * @return {@code True} if fragment is provided, so {@link #createFragment(int)} may be
	 * called to create an instance of such fragment, {@code false} otherwise.
	 */
	boolean isFragmentProvided(int fragmentId);

	/**
	 * Creates a new instance of the fragment associated with the specified <var>fragmentId</var>.
	 *
	 * @param fragmentId Id of the desired fragment to create a new instance of.
	 * @return Instance of fragment associated with the <var>fragmentId</var> or {@code null} if this
	 * fragment factory does not provide fragment for the requested id.
	 * @see #createFragmentTag(int)
	 * @see #isFragmentProvided(int)
	 */
	@Nullable
	Fragment createFragment(int fragmentId);

	/**
	 * Creates a tag for the fragment associated with the specified <var>fragmentId</var>.
	 *
	 * @param fragmentId Id of the desired fragment for which to create its TAG.
	 * @return Tag for fragment associated with the <var>fragmentId</var> or {@code null} if this
	 * fragment factory does not provide fragment for the requested id.
	 * @see #isFragmentProvided(int)
	 */
	@Nullable
	String createFragmentTag(int fragmentId);
}
