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
package universum.studios.android.fragment.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import universum.studios.android.fragment.manage.FragmentFactory;

/**
 * Defines an annotation for determining set of Fragment ids that are provided by a specific
 * {@link FragmentFactory}.
 *
 * <h3>Usage</h3>
 * <ul>
 * <li>{@link universum.studios.android.fragment.manage.BaseFragmentFactory BaseFragmentFactory}</li>
 * </ul>
 *
 * @author Martin Albedinsky
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FactoryFragments {

	/**
	 * An array with Fragment ids to be provided by FragmentFactory.
	 *
	 * @see FragmentFactory#isFragmentProvided(int)
	 */
	int[] value();
}
