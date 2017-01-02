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
package universum.studios.android.support.samples.fragment.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.MenuItem;

import universum.studios.android.samples.ui.SamplesNavigationActivity;
import universum.studios.android.support.samples.fragment.R;
import universum.studios.android.support.samples.fragment.ui.welcome.WelcomeActivity;

/**
 * @author Martin Albedinsky
 */
public final class MainActivity extends SamplesNavigationActivity {

	@SuppressWarnings("unused")
	private static final String TAG = "MainActivity";

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		switch (item.getItemId()) {
			case R.id.navigation_item_home:
				return true;
			case R.id.navigation_item_welcome:
				startActivity(new Intent(this, WelcomeActivity.class));
				return false;
		}
		return false;
	}
}
