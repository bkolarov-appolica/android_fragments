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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import universum.studios.android.samples.ui.SamplesActivity;
import universum.studios.android.support.fragment.ViewClickWatcher;
import universum.studios.android.support.fragment.manage.FragmentController;
import universum.studios.android.support.fragment.manage.FragmentRequest;
import universum.studios.android.support.fragment.manage.FragmentRequestInterceptor;
import universum.studios.android.support.fragment.transition.FragmentTransitions;
import universum.studios.android.support.samples.fragment.R;

/**
 * @author Martin Albedinsky
 */
public final class WelcomeActivity extends SamplesActivity implements FragmentRequestInterceptor {

	@SuppressWarnings("unused")
	private static final String TAG = "WelcomeActivity";

	private FragmentController fragmentController;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		this.fragmentController = new FragmentController(this);
		this.fragmentController.setViewContainerId(R.id.container);
		this.fragmentController.setFactory(new WelcomeFragments());
		if (savedInstanceState == null) {
			fragmentController.newRequest(WelcomeFragments.WELCOME).immediate(true).execute();
		}
	}

	@Nullable
	@Override
	public Fragment interceptFragmentRequest(@NonNull FragmentRequest request) {
		switch (request.fragmentId()) {
			case WelcomeFragments.SIGN_IN:
				request.transition(FragmentTransitions.CROSS_FADE_AND_HOLD).addToBackStack(true);
				break;
			case WelcomeFragments.SIGN_UP:
				request.transition(FragmentTransitions.CROSS_FADE).addToBackStack(true);
				break;
			case WelcomeFragments.LOST_PASSWORD:
				request.transition(FragmentTransitions.CROSS_FADE).addToBackStack(true);
				break;
		}
		return null;
	}

	@SuppressWarnings("unused")
	public void onViewClick(@NonNull View view) {
		final Fragment fragment = fragmentController.findCurrentFragment();
		if (fragment instanceof ViewClickWatcher && ((ViewClickWatcher) fragment).dispatchViewClick(view)) {
			return;
		}
		switch (view.getId()) {
			case R.id.sign_in:
				fragmentController.newRequest(WelcomeFragments.SIGN_IN).execute();
				break;
			case R.id.sign_up:
				fragmentController.newRequest(WelcomeFragments.SIGN_UP).execute();
				break;
			case R.id.lost_password:
				fragmentController.newRequest(WelcomeFragments.LOST_PASSWORD).execute();
				break;
		}
	}
}
