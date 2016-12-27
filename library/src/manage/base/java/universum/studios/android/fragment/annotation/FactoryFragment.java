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

import android.support.v4.app.Fragment;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import universum.studios.android.fragment.manage.FragmentTransactionOptions;

/**
 * Annotation type used to mark an <b>int</b> field that specifies an id of fragment provided by
 * instance of {@link universum.studios.android.fragment.manage.BaseFragmentFactory BaseFragmentFactory}.
 *
 * <h4>Usage</h4>
 * <ul>
 * <li>{@link universum.studios.android.fragment.manage.BaseFragmentFactory BaseFragmentFactory}</li>
 * </ul>
 *
 * @author Martin Albedinsky
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface FactoryFragment {

	/**
	 * Class of the desired fragment of which instance should be instantiated when calling
	 * {@link universum.studios.android.fragment.manage.BaseFragmentFactory#createFragmentInstance(FragmentTransactionOptions) BaseFragmentFactory#createFragmentInstance(FragmentTransactionOptions)}
	 * for this id.
	 */
	Class<? extends Fragment> value() default Fragment.class;

	/**
	 * Name of the associated fragment to be placed within TAG used when showing the fragment.
	 */
	String taggedName() default "";
}
