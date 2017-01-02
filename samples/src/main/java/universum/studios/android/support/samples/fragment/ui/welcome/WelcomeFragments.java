/*
 * =================================================================================================
 *                             Copyright (C) 2017 Universum Studios
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
package universum.studios.android.support.samples.fragment.ui.welcome;

import universum.studios.android.fragment.annotation.FactoryFragment;
import universum.studios.android.fragment.manage.BaseFragmentFactory;

/**
 * @author Martin Albedinsky
 */
final class WelcomeFragments extends BaseFragmentFactory {

	@FactoryFragment(WelcomeFragment.class)
	static final int WELCOME = 0x01;

	@FactoryFragment(SignInFragment.class)
	static final int SIGN_IN = 0x02;

	@FactoryFragment(SignUpFragment.class)
	static final int SIGN_UP = 0x03;

	@FactoryFragment(LostPasswordFragment.class)
	static final int LOST_PASSWORD = 0x04;
}
