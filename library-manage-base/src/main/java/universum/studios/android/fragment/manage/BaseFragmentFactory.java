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
package universum.studios.android.fragment.manage;

import android.app.Fragment;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.SparseArray;

import universum.studios.android.fragment.FragmentsConfig;
import universum.studios.android.fragment.annotation.FactoryFragment;
import universum.studios.android.fragment.annotation.FactoryFragments;
import universum.studios.android.fragment.annotation.FragmentAnnotations;
import universum.studios.android.fragment.annotation.handler.BaseManagementAnnotationHandlers;
import universum.studios.android.fragment.annotation.handler.FragmentFactoryAnnotationHandler;

/**
 * A {@link FragmentFactory} base implementation that may be used for fragment factories used
 * across Android application project.
 *
 * <h3>Accepted annotations</h3>
 * <ul>
 * <li>
 * {@link universum.studios.android.fragment.annotation.FactoryFragments @FactoryFragments} <b>[class - inherited]</b>
 * <p>
 * If this annotation is presented, all ids of fragments specified via this annotation will be
 * attached to an instance of annotated BaseFragmentFactory subclass, so {@link #isFragmentProvided(int)}
 * will return always {@code true} for each of these ids.
 * <p>
 * Also, there will be automatically created default tags for all such ids, so they may be obtained
 * via {@link #createFragmentTag(int)} with the corresponding fragment id.
 * </li>
 * <li>
 * {@link universum.studios.android.fragment.annotation.FactoryFragment @FactoryFragment} <b>[member - inherited]</b>
 * <p>
 * This annotation provides same result as {@link universum.studios.android.fragment.annotation.FactoryFragments @FactoryFragments}
 * annotation, but this annotation is meant to be used to mark directly constant fields that specify
 * fragment ids and also provides more configuration options like the type of fragment that should
 * be instantiated for the specified id.
 * <p>
 * <b>Note</b>, that tagged name for fragment with the specified id will be automatically created
 * using its id but may be also specified via {@link FactoryFragment#taggedName()} attribute.
 * </li>
 * </ul>
 *
 * @author Martin Albedinsky
 */
public abstract class BaseFragmentFactory implements FragmentFactory {

	/**
	 * Interface ===================================================================================
	 */

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "BaseFragmentFactory";

	/**
	 * Static members ==============================================================================
	 */

	/**
	 * Members =====================================================================================
	 */

	/**
	 * Handler responsible for processing of all annotations of this class and also for handling all
	 * annotations related operations for this class.
	 */
	private final FragmentFactoryAnnotationHandler mAnnotationHandler;

	/**
	 * Set of fragment item holders created from annotated fields ({@link FactoryFragment @FactoryDialog})
	 * of this factory instance.
	 */
	private final SparseArray<FragmentItem> mItems;

	/**
	 * Id of the fragment that has been last checked via {@link #isFragmentProvided(int)}.
	 */
	private int mLastCheckedFragmentId = -1;

	/**
	 * Flag indicating whether an instance of fragment for {@link #mLastCheckedFragmentId} is
	 * provided by this factory or not.
	 */
	private boolean mFragmentProvided = false;

	/**
	 * Constructors ================================================================================
	 */

	/**
	 * Creates a new instance of BaseFragmentFactory.
	 * <p>
	 * If annotations processing is enabled via {@link FragmentsConfig} all annotations supported by
	 * this class will be processed/obtained here so they can be later used.
	 */
	public BaseFragmentFactory() {
		this.mAnnotationHandler = onCreateAnnotationHandler();
		this.mItems = mAnnotationHandler != null ? mAnnotationHandler.getFragmentItems() : null;
	}

	/**
	 * Methods =====================================================================================
	 */

	/**
	 * Creates a tag for fragment in the required format depending on a package name of the passed
	 * <var>classOfFactory</var> and <var>fragmentName</var>.
	 * <p>
	 * Example format: <u>com.android.app.fragment.ProfileFragments.TAG.EditProfile</u>
	 * <p>
	 * - where <b>com.android.app.fragment</b> is name of the package where is the specified
	 * <var>classOfFactory</var> placed, <b>ProfileFragments</b> is factory class name, <b>EditProfile</b>
	 * is <var>fragmentName</var> and <b>TAG</b> is the static tag identifier.
	 *
	 * @param classOfFactory Class of factory that provides fragment with the given name.
	 * @param fragmentName   Fragment name (may be fragment class name) for which the requested TAG
	 *                       should be created.
	 * @return Fragment tag in required format, or {@code null} if <var>fragmentName</var> is {@code null}
	 * or empty.
	 */
	@Nullable
	public static String createFragmentTag(@NonNull Class<?> classOfFactory, @NonNull String fragmentName) {
		if (TextUtils.isEmpty(fragmentName)) {
			return null;
		}
		return classOfFactory.getPackage().getName() + "." + classOfFactory.getSimpleName() + ".TAG." + fragmentName;
	}

	/**
	 * Invoked to create annotations handler for this instance.
	 *
	 * @return Annotations handler specific for this class.
	 */
	private FragmentFactoryAnnotationHandler onCreateAnnotationHandler() {
		return BaseManagementAnnotationHandlers.obtainFactoryHandler(getClass());
	}

	/**
	 * Returns handler that is responsible for annotations processing of this class and also for
	 * handling all annotations related operations for this class.
	 *
	 * @return Annotations handler specific for this class.
	 * @throws IllegalStateException If annotations processing is not enabled for the Fragments library.
	 */
	@NonNull
	protected FragmentFactoryAnnotationHandler getAnnotationHandler() {
		FragmentAnnotations.checkIfEnabledOrThrow();
		return mAnnotationHandler;
	}

	/**
	 */
	@Override
	public boolean isFragmentProvided(int fragmentId) {
		if (fragmentId == mLastCheckedFragmentId) {
			return mFragmentProvided;
		}
		this.mLastCheckedFragmentId = fragmentId;
		return mFragmentProvided = providesFragment(fragmentId);
	}

	/**
	 * Invoked whenever {@link #isFragmentProvided(int)} is called and the specified <var>fragmentId</var>
	 * differs from the one last time called.
	 * <p>
	 * This implementation returns {@code true} if there is {@link FactoryFragments @FactoryFragments}
	 * or {@link FactoryFragment @FactoryFragment} annotation presented for the specified <var>fragmentId</var>,
	 * {@code false} otherwise.
	 */
	protected boolean providesFragment(int fragmentId) {
		return (mItems != null) && mItems.indexOfKey(fragmentId) >= 0;
	}

	/**
	 */
	@Nullable
	@Override
	public Fragment createFragment(int fragmentId) {
		return isFragmentProvided(fragmentId) ? onCreateFragment(fragmentId) : null;
	}

	/**
	 * Invoked whenever {@link #createFragment(int)} is called and this factory provides fragment
	 * instance for the specified <var>fragmentId</var>.
	 * <p>
	 * This implementation returns the requested fragment instance instantiated from class specified
	 * via {@link FactoryFragment @FactoryFragment}. If instantiation fails an exception is thrown.
	 */
	@NonNull
	protected Fragment onCreateFragment(int fragmentId) {
		final Fragment fragment = mItems.get(fragmentId).newFragmentInstance(null);
		if (fragment == null) {
			throw new NullPointerException("Failed to instantiate fragment for the requested id(" + fragmentId + ")!");
		}
		return fragment;
	}

	/**
	 */
	@Nullable
	@Override
	public String createFragmentTag(@IntRange(from = 0) int fragmentId) {
		return isFragmentProvided(fragmentId) ? onCreateFragmentTag(fragmentId) : null;
	}

	/**
	 * Invoked whenever {@link #createFragmentTag(int)} is called and this factory provides fragment
	 * instance for the specified <var>fragmentId</var>.
	 * <p>
	 * This implementation returns the requested fragment TAG created from fragment ids specified
	 * via {@link FactoryFragments @FactoryFragments} or for a single id marked by {@link FactoryFragment @FactoryFragment}.
	 * If neither of these annotations is presented, the TAG is created via {@link #createFragmentTag(Class, String)}
	 * with the fragment id as <var>fragmentName</var>.
	 */
	@Nullable
	protected String onCreateFragmentTag(@IntRange(from = 0) int fragmentId) {
		return mItems.indexOfKey(fragmentId) >= 0 ? mItems.get(fragmentId).tag : createFragmentTag(getClass(), Integer.toString(fragmentId));
	}

	/**
	 * Inner classes ===============================================================================
	 */
}
