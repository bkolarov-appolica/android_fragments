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
package universum.studios.android.fragment.transition;

import universum.studios.android.fragment.R;
import universum.studios.android.fragment.manage.FragmentTransition;

/**
 * Factory providing <b>extra</b> {@link FragmentTransition FragmentTransitions}.
 * <ul>
 * <li>{@link #SCALE_IN_AND_SLIDE_TO_LEFT}</li>
 * <li>{@link #SCALE_IN_AND_SLIDE_TO_RIGHT}</li>
 * <li>{@link #SCALE_IN_AND_SLIDE_TO_TOP}</li>
 * <li>{@link #SCALE_IN_AND_SLIDE_TO_BOTTOM}</li>
 * <li>{@link #SLIDE_TO_RIGHT_AND_SCALE_OUT}</li>
 * <li>{@link #SLIDE_TO_LEFT_AND_SCALE_OUT}</li>
 * <li>{@link #SLIDE_TO_BOTTOM_AND_SCALE_OUT}</li>
 * <li>{@link #SLIDE_TO_TOP_AND_SCALE_OUT}</li>
 * </ul>
 *
 * @author Martin Albedinsky
 */
@SuppressWarnings("unused")
public final class ExtraFragmentTransitions {

	/**
	 * Transition that may be used to scale (with fade) a new incoming fragment into the screen from
	 * its background and an outgoing (the current one) will be slided out of the screen to the left.
	 *
	 * <h3>Powered by animations:</h3>
	 * <ul>
	 * <li><b>Incoming:</b> {@link R.animator#fragment_scale_in}</li>
	 * <li><b>Outgoing:</b> {@link R.animator#fragment_slide_out_left}</li>
	 * <li><b>Incoming (back-stack):</b> {@link R.animator#fragment_slide_in_right_back}</li>
	 * <li><b>Outgoing (back-stack):</b> {@link R.animator#fragment_scale_out_back}</li>
	 * </ul>
	 */
	public static final FragmentTransition SCALE_IN_AND_SLIDE_TO_LEFT = new BasicFragmentTransition(
			// Incoming animation.
			R.animator.fragment_scale_in,
			// Outgoing animation.
			R.animator.fragment_slide_out_left,
			// Incoming back-stack animation.
			R.animator.fragment_slide_in_right_back,
			// Outgoing back-stack animation.
			R.animator.fragment_scale_out_back,
			"SCALE_IN_AND_SLIDE_TO_LEFT"
	);

	/**
	 * Transition that may be used to scale (with fade) a new incoming fragment into the screen from
	 * its background and an outgoing (the current one) will be slided out of the screen to the right.
	 *
	 * <h3>Powered by animations:</h3>
	 * <ul>
	 * <li><b>Incoming:</b> {@link R.animator#fragment_scale_in}</li>
	 * <li><b>Outgoing:</b> {@link R.animator#fragment_slide_out_right}</li>
	 * <li><b>Incoming (back-stack):</b> {@link R.animator#fragment_slide_in_left_back}</li>
	 * <li><b>Outgoing (back-stack):</b> {@link R.animator#fragment_scale_out_back}</li>
	 * </ul>
	 */
	public static final FragmentTransition SCALE_IN_AND_SLIDE_TO_RIGHT = new BasicFragmentTransition(
			// Incoming animation.
			R.animator.fragment_scale_in,
			// Outgoing animation.
			R.animator.fragment_slide_out_right,
			// Incoming back-stack animation.
			R.animator.fragment_slide_in_left_back,
			// Outgoing back-stack animation.
			R.animator.fragment_scale_out_back,
			"SCALE_IN_AND_SLIDE_TO_RIGHT"
	);

	/**
	 * Transition that may be used to scale (with fade) a new incoming fragment into the screen from
	 * its background and an outgoing (the current one) will be slided out of the screen to the top.
	 *
	 * <h3>Powered by animations:</h3>
	 * <ul>
	 * <li><b>Incoming:</b> {@link R.animator#fragment_scale_in}</li>
	 * <li><b>Outgoing:</b> {@link R.animator#fragment_slide_out_top}</li>
	 * <li><b>Incoming (back-stack):</b> {@link R.animator#fragment_slide_in_bottom_back}</li>
	 * <li><b>Outgoing (back-stack):</b> {@link R.animator#fragment_scale_out_back}</li>
	 * </ul>
	 */
	public static final FragmentTransition SCALE_IN_AND_SLIDE_TO_TOP = new BasicFragmentTransition(
			// Incoming animation.
			R.animator.fragment_scale_in,
			// Outgoing animation.
			R.animator.fragment_slide_out_top,
			// Incoming back-stack animation.
			R.animator.fragment_slide_in_bottom_back,
			// Outgoing back-stack animation.
			R.animator.fragment_scale_out_back,
			"SCALE_IN_AND_SLIDE_TO_TOP"
	);

	/**
	 * Transition that may be used to scale (with fade) a new incoming fragment into the screen from
	 * its background and an outgoing (the current one) will be slided out of the screen to the bottom.
	 *
	 * <h3>Powered by animations:</h3>
	 * <ul>
	 * <li><b>Incoming:</b> {@link R.animator#fragment_scale_in}</li>
	 * <li><b>Outgoing:</b> {@link R.animator#fragment_slide_out_bottom}</li>
	 * <li><b>Incoming (back-stack):</b> {@link R.animator#fragment_slide_in_top_back}</li>
	 * <li><b>Outgoing (back-stack):</b> {@link R.animator#fragment_scale_out_back}</li>
	 * </ul>
	 */
	public static final FragmentTransition SCALE_IN_AND_SLIDE_TO_BOTTOM = new BasicFragmentTransition(
			// Incoming animation.
			R.animator.fragment_scale_in,
			// Outgoing animation.
			R.animator.fragment_slide_out_bottom,
			// Incoming back-stack animation.
			R.animator.fragment_slide_in_top_back,
			// Outgoing back-stack animation.
			R.animator.fragment_scale_out_back,
			"SCALE_IN_AND_SLIDE_TO_BOTTOM"
	);

	/**
	 * Transition that may be used to slide a new incoming fragment into the screen from the right and
	 * an outgoing (the current one) will be scaled out (with fade) out of the screen to its background.
	 *
	 * <h3>Powered by animations:</h3>
	 * <ul>
	 * <li><b>Incoming:</b> {@link R.animator#fragment_slide_in_left}</li>
	 * <li><b>Outgoing:</b> {@link R.animator#fragment_scale_out}</li>
	 * <li><b>Incoming (back-stack):</b> {@link R.animator#fragment_scale_in_back}</li>
	 * <li><b>Outgoing (back-stack):</b> {@link R.animator#fragment_slide_out_right_back}</li>
	 * </ul>
	 */
	public static final FragmentTransition SLIDE_TO_LEFT_AND_SCALE_OUT = new BasicFragmentTransition(
			// Incoming animation.
			R.animator.fragment_slide_in_left,
			// Outgoing animation.
			R.animator.fragment_scale_out,
			// Incoming back-stack animation.
			R.animator.fragment_scale_in_back,
			// Outgoing back-stack animation.
			R.animator.fragment_slide_out_right_back,
			"SLIDE_TO_LEFT_AND_SCALE_OUT"
	);

	/**
	 * Transition that may be used to slide a new incoming fragment into the screen from the left and
	 * an outgoing (the current one) will be scaled out (with fade) out of the screen to its background.
	 *
	 * <h3>Powered by animations:</h3>
	 * <ul>
	 * <li><b>Incoming:</b> {@link R.animator#fragment_slide_in_right}</li>
	 * <li><b>Outgoing:</b> {@link R.animator#fragment_scale_out}</li>
	 * <li><b>Incoming (back-stack):</b> {@link R.animator#fragment_scale_in_back}</li>
	 * <li><b>Outgoing (back-stack):</b> {@link R.animator#fragment_slide_out_left_back}</li>
	 * </ul>
	 */
	public static final FragmentTransition SLIDE_TO_RIGHT_AND_SCALE_OUT = new BasicFragmentTransition(
			// Incoming animation.
			R.animator.fragment_slide_in_right,
			// Outgoing animation.
			R.animator.fragment_scale_out,
			// Incoming back-stack animation.
			R.animator.fragment_scale_in_back,
			// Outgoing back-stack animation.
			R.animator.fragment_slide_out_left_back,
			"SLIDE_TO_RIGHT_AND_SCALE_OUT"
	);

	/**
	 * Transition that may be used to slide a new incoming fragment into the screen from the bottom
	 * and an outgoing (the current one) will be scaled out (with fade) out of the screen to its background.
	 *
	 * <h3>Powered by animations:</h3>
	 * <ul>
	 * <li><b>Incoming:</b> {@link R.animator#fragment_slide_in_top}</li>
	 * <li><b>Outgoing:</b> {@link R.animator#fragment_scale_out}</li>
	 * <li><b>Incoming (back-stack):</b> {@link R.animator#fragment_scale_in_back}</li>
	 * <li><b>Outgoing (back-stack):</b> {@link R.animator#fragment_slide_out_bottom_back}</li>
	 * </ul>
	 */
	public static final FragmentTransition SLIDE_TO_TOP_AND_SCALE_OUT = new BasicFragmentTransition(
			// Incoming animation.
			R.animator.fragment_slide_in_top,
			// Outgoing animation.
			R.animator.fragment_scale_out,
			// Incoming back-stack animation.
			R.animator.fragment_scale_in_back,
			// Outgoing back-stack animation.
			R.animator.fragment_slide_out_bottom_back,
			"SLIDE_TO_TOP_AND_SCALE_OUT"
	);

	/**
	 * Transition that may be used to slide a new incoming fragment into the screen from the top and
	 * an outgoing (the current one) will be scaled out (with fade) out of the screen to its background.
	 *
	 * <h3>Powered by animations:</h3>
	 * <ul>
	 * <li><b>Incoming:</b> {@link R.animator#fragment_slide_in_bottom}</li>
	 * <li><b>Outgoing:</b> {@link R.animator#fragment_scale_out}</li>
	 * <li><b>Incoming (back-stack):</b> {@link R.animator#fragment_scale_in_back}</li>
	 * <li><b>Outgoing (back-stack):</b> {@link R.animator#fragment_slide_out_top_back}</li>
	 * </ul>
	 */
	public static final FragmentTransition SLIDE_TO_BOTTOM_AND_SCALE_OUT = new BasicFragmentTransition(
			// Incoming animation.
			R.animator.fragment_slide_in_bottom,
			// Outgoing animation.
			R.animator.fragment_scale_out,
			// Incoming back-stack animation.
			R.animator.fragment_scale_in_back,
			// Outgoing back-stack animation.
			R.animator.fragment_slide_out_top_back,
			"SLIDE_TO_BOTTOM_AND_SCALE_OUT"
	);

	/**
	 */
	private ExtraFragmentTransitions() {
		// Creation of instances of this class is not publicly allowed.
	}
}
