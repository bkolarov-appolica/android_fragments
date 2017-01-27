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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Interface that may be used to intercept a specific {@link FragmentRequest} when it is being executed
 * via its associated {@link FragmentController}.
 *
 * @author Martin Albedinsky
 * @see FragmentController#setRequestInterceptor(FragmentRequestInterceptor)
 */
public interface FragmentRequestInterceptor {

	/**
	 * Called to allow this request interceptor to intercept execution of the given fragment <var>request</var>.
	 * <p>
	 * Interceptor may also just change configuration of the request and return {@code null} to indicate
	 * that the associated fragment controller should handle the execution.
	 *
	 * @param request The request to be executed.
	 * @return Fragment associated with the request as result of the handled execution, {@code null}
	 * to let the fragment controller handle the execution.
	 */
	@Nullable
	Fragment interceptFragmentRequest(@NonNull FragmentRequest request);
}
