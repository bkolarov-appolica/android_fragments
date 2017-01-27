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
package universum.studios.android.fragment;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * The ViewClickWatcher interface specifies one callback that may be used to dispatch a view click
 * event to a watcher that implements this interface.
 *
 * @author Martin Albedinsky
 */
public interface ViewClickWatcher {

	/**
	 * Called to dispatch a view click event to this watcher instance.
	 *
	 * @param view The clicked view.
	 * @return {@code True} if this watcher processed the click event for the specified <var>view</var>,
	 * {@code false} otherwise.
	 */
	boolean dispatchViewClick(@NonNull View view);
}
