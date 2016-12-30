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
package universum.studios.android.samples.fragment.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MenuItem;

import universum.studios.android.fragment.manage.FragmentController;
import universum.studios.android.fragment.manage.FragmentRequest;
import universum.studios.android.fragment.manage.FragmentRequestInterceptor;
import universum.studios.android.fragment.transition.FragmentTransitions;
import universum.studios.android.samples.ui.SamplesNavigationActivity;

/**
 * @author Martin Albedinsky
 */
public final class MainActivity extends SamplesNavigationActivity implements FragmentRequestInterceptor {

	@SuppressWarnings("unused")
	private static final String TAG = "MainActivity";

	private FragmentController fragmentController;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.fragmentController = new FragmentController(this);
		this.fragmentController.setFactory(new SampleFragmentsFactory());
		if (savedInstanceState == null) {
			fragmentController.newRequest(SampleFragmentsFactory.ACTION_BAR)
					.transition(FragmentTransitions.CROSS_FADE)
					.execute();
		}
	}

	@Override
	public boolean interceptFragmentRequest(@NonNull FragmentRequest request) {
		// todo:
		return false;
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		return true;
	}
}
