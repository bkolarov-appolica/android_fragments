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
package universum.studios.android.support.fragment;

import android.os.Build;
import android.util.Log;

/**
 * Configuration options for the Fragments library.
 *
 * @author Martin Albedinsky
 */
public final class FragmentsConfig {

	/**
	 * Flag indicating whether the <b>verbose</b> output for the Fragments library trough log-cat is
	 * enabled or not.
	 *
	 * @see Log#v(String, String)
	 */
	public static boolean LOG_ENABLED = true;

	/**
	 * Flag indicating whether the <b>debug</b> output for the Fragments library trough log-cat is
	 * enabled or not.
	 *
	 * @see Log#d(String, String)
	 */
	public static boolean DEBUG_LOG_ENABLED = false;

	/**
	 * Flag indicating whether the processing of annotations for the Fragments library is enabled
	 * or not.
	 * <p>
	 * If annotations processing is enabled, it may decrease performance for the parts of an Android
	 * application depending on the classes from the Fragments library that uses annotations.
	 */
	public static boolean ANNOTATIONS_PROCESSING_ENABLED = true;

	/**
	 * Flag indicating whether a transitions API for fragments is supported by the current version
	 * of the Android or not.
	 */
	public static final boolean TRANSITIONS_SUPPORTED = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;

	/**
	 */
	private FragmentsConfig() {
		// Creation of instances of this class is not publicly allowed.
	}
}
