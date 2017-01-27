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

import android.os.Parcel;
import android.support.annotation.AnimatorRes;
import android.support.annotation.NonNull;

import universum.studios.android.fragment.manage.FragmentTransition;

/**
 * Basic implementation of {@link FragmentTransition} that may be used to create instances of fragment
 * transactions with desired fragment animations.
 *
 * @author Martin Albedinsky
 */
public class BasicFragmentTransition implements FragmentTransition {

	/**
	 * Interface ===================================================================================
	 */

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "BasicFragmentTransition";

	/**
	 * Static members ==============================================================================
	 */

	/**
	 * Creator used to create an instance or array of instances of BasicFragmentTransition from {@link Parcel}.
	 */
	public static final Creator<BasicFragmentTransition> CREATOR = new Creator<BasicFragmentTransition>() {

		/**
		 */
		@Override
		public BasicFragmentTransition createFromParcel(@NonNull Parcel source) {
			return new BasicFragmentTransition(source);
		}

		/**
		 */
		@Override
		public BasicFragmentTransition[] newArray(int size) {
			return new BasicFragmentTransition[size];
		}
	};

	/**
	 * Members =====================================================================================
	 */

	/**
	 * Animation resource for a new incoming fragment.
	 */
	private final int mInAnimRes;

	/**
	 * Animation resource for an old outgoing fragment.
	 */
	private final int mOutAnimRes;

	/**
	 * Animation resource for an old incoming fragment when it is being popped from the back stack.
	 */
	private final int mInBackAnimRes;

	/**
	 * Animation resource for a current outgoing fragment when it is being popped from the back stack.
	 */
	private final int mOutBackAnimRes;

	/**
	 * Name of this transition.
	 */
	private final String mName;

	/**
	 * Constructors ================================================================================
	 */

	/**
	 * Same as {@link #BasicFragmentTransition(int, int, int, int)} with back-stack animations set
	 * to {@link #NO_ANIMATION}.
	 */
	public BasicFragmentTransition(@AnimatorRes int inAnim, @AnimatorRes int outAnim) {
		this(inAnim, outAnim, NO_ANIMATION, NO_ANIMATION);
	}

	/**
	 * Same as {@link #BasicFragmentTransition(int, int, int, int, String)} with name specified
	 * as {@code "UNKNOWN"}.
	 */
	public BasicFragmentTransition(@AnimatorRes int inAnim, @AnimatorRes int outAnim, @AnimatorRes int inBackAnim, @AnimatorRes int outBackAnim) {
		this(inAnim, outAnim, inBackAnim, outBackAnim, "UNKNOWN");
	}

	/**
	 * Creates a new instance of BasicFragmentTransition with the specified animations and name.
	 *
	 * @param inAnim      A resource id of the animation for an incoming fragment.
	 * @param outAnim     A resource id of the animation for an outgoing fragment to be added to the
	 *                    back stack or to be destroyed and replaced by the incoming one.
	 * @param inBackAnim  A resource id of the animation for an incoming fragment to be showed from
	 *                    the back stack.
	 * @param outBackAnim A resource id of the animation for an outgoing fragment to be destroyed and
	 *                    replaced by the incoming one.
	 * @param name        Name for the new transition.
	 */
	public BasicFragmentTransition(@AnimatorRes int inAnim, @AnimatorRes int outAnim, @AnimatorRes int inBackAnim, @AnimatorRes int outBackAnim, @NonNull String name) {
		this.mInAnimRes = inAnim;
		this.mOutAnimRes = outAnim;
		this.mInBackAnimRes = inBackAnim;
		this.mOutBackAnimRes = outBackAnim;
		this.mName = name;
	}

	/**
	 * Called form {@link #CREATOR} to create an instance of FragmentTransition form the given parcel
	 * <var>source</var>.
	 *
	 * @param source Parcel with data for the new instance.
	 */
	protected BasicFragmentTransition(@NonNull Parcel source) {
		this.mInAnimRes = source.readInt();
		this.mOutAnimRes = source.readInt();
		this.mInBackAnimRes = source.readInt();
		this.mOutBackAnimRes = source.readInt();
		this.mName = source.readString();
	}

	/**
	 * Methods =====================================================================================
	 */

	/**
	 */
	@Override
	public void writeToParcel(@NonNull Parcel dest, int flags) {
		dest.writeInt(mInAnimRes);
		dest.writeInt(mOutAnimRes);
		dest.writeInt(mInBackAnimRes);
		dest.writeInt(mOutBackAnimRes);
		dest.writeString(mName);
	}

	/**
	 */
	@Override
	public int describeContents() {
		return 0;
	}

	/**
	 */
	@Override
	@AnimatorRes
	public int getIncomingAnimation() {
		return mInAnimRes;
	}

	/**
	 */
	@Override
	@AnimatorRes
	public int getOutgoingAnimation() {
		return mOutAnimRes;
	}

	/**
	 */
	@Override
	@AnimatorRes
	public int getIncomingBackStackAnimation() {
		return mInBackAnimRes;
	}

	/**
	 */
	@Override
	@AnimatorRes
	public int getOutgoingBackStackAnimation() {
		return mOutBackAnimRes;
	}

	/**
	 */
	@NonNull
	@Override
	public String getName() {
		return mName;
	}

	/**
	 * Inner classes ===============================================================================
	 */
}
