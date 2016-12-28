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

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import universum.studios.android.fragment.FragmentsConfig;
import universum.studios.android.fragment.annotation.FactoryFragment;
import universum.studios.android.fragment.annotation.FactoryFragments;
import universum.studios.android.fragment.annotation.FragmentAnnotations;
import universum.studios.android.fragment.annotation.handler.BaseManagementAnnotationHandlers;
import universum.studios.android.fragment.annotation.handler.FragmentFactoryAnnotationHandler;
import universum.studios.android.fragment.transition.BasicFragmentTransition;

/**
 * Fragment factory that provides base implementation of required {@link universum.studios.android.fragment.manage.FragmentController.FragmentFactory}
 * interface for {@link universum.studios.android.fragment.manage.FragmentController}.
 *
 * <h3>Accepted annotations</h3>
 * <ul>
 * <li>
 * {@link universum.studios.android.fragment.annotation.FactoryFragments @FactoryFragments} <b>[class - inherited]</b>
 * <p>
 * If this annotation is presented, all ids of fragments presented within this annotation will be
 * attached to an instance of annotated BaseFragmentFactory sub-class, so {@link #isFragmentProvided(int)}
 * will returns always {@code true} for each of these ids.
 * <p>
 * Also, there will be automatically created default tags for all such ids, so they can be obtained
 * via {@link #getFragmentTag(int)} with the specific fragment id.
 * </li>
 * <li>
 * {@link universum.studios.android.fragment.annotation.FactoryFragment @FactoryFragment} <b>[member - inherited]</b>
 * <p>
 * This annotation provides same results as {@link universum.studios.android.fragment.annotation.FactoryFragments @FactoryFragments}
 * annotation, but this annotation is meant to be used to mark directly constant fields that specify
 * fragment ids and also provides more configuration options like the type of fragment that should
 * be instantiated for the specified id.
 * <p>
 * <b>Note</b>, that tagged name for fragment with the specified id will be automatically created using
 * its id but can be also specified via {@link FactoryFragment#taggedName()} attribute.
 * </li>
 * </ul>
 *
 * @author Martin Albedinsky
 */
public abstract class BaseFragmentFactory implements FragmentController.FragmentFactory {

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
	final FragmentFactoryAnnotationHandler mAnnotationHandler;

	/**
	 * Set of fragment item holders created from annotated fields ({@link FactoryFragment @FactoryDialog})
	 * of this factory instance.
	 */
	private final SparseArray<FragmentItem> mItems;

	/**
	 * List with joined factories. Fragment instances and tags requested from this factory are firstly
	 * obtained from these factories then from this one.
	 */
	private List<FragmentController.FragmentFactory> mFactories;

	/**
	 * Id of the fragment which was last checked by {@link #isFragmentProvided(int)}.
	 */
	private int mLastCheckedFragmentId = -1;

	/**
	 * Flag indicating whether an instance of fragment for {@link #mLastCheckedFragmentId} can be
	 * provided by this factory or not.
	 */
	private boolean mFragmentProvided = false;

	/**
	 * Inflater that can be used to inflate transitions for this fragment.
	 */
	private final TransitionInflater mTransitionInflater;

	/**
	 * Constructors ================================================================================
	 */

	/**
	 * Creates a new instance of BaseFragmentFactory.
	 */
	public BaseFragmentFactory() {
		this.mAnnotationHandler = onCreateAnnotationHandler();
		this.mItems = mAnnotationHandler != null ? mAnnotationHandler.getFragmentItems() : null;
		this.mTransitionInflater = null;
	}

	/**
	 * Like {@link #BaseFragmentFactory()} creates a new instance of BaseFragmentFactory and also
	 * initializes an instance of {@link TransitionInflater} that can be used to inflate desired
	 * fragment transitions via {@link #inflateTransition(int)}.
	 * <p>
	 * <b>Note</b>, that inflater will be initialized only for <b>post</b> {@link android.os.Build.VERSION_CODES#LOLLIPOP LOLLIPOP}
	 * Android versions.
	 *
	 * @param context Context that will be used to obtain an instance of {@link TransitionInflater}
	 *                that can be used to inflate transitions for fragments provided by this factory.
	 */
	@SuppressLint("NewApi")
	public BaseFragmentFactory(@NonNull Context context) {
		this.mAnnotationHandler = onCreateAnnotationHandler();
		this.mItems = mAnnotationHandler != null ? mAnnotationHandler.getFragmentItems() : null;
		this.mTransitionInflater = FragmentsConfig.TRANSITIONS_SUPPORTED ? TransitionInflater.from(context) : null;
	}

	/**
	 * Methods =====================================================================================
	 */

	/**
	 * Invoked to create annotations handler for this instance.
	 *
	 * @return Annotations handler specific for this class.
	 */
	FragmentFactoryAnnotationHandler onCreateAnnotationHandler() {
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
	 * @param fragmentName   Fragment name (can be fragment class name) for which the tag should be created.
	 * @return Fragment tag in required format, or {@code ""} if <var>fragmentName</var> is {@code null}
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
	 */
	@Override
	@SuppressWarnings("ResourceType")
	public boolean isFragmentProvided(@IntRange(from = 0) int fragmentId) {
		if (fragmentId == mLastCheckedFragmentId) {
			return mFragmentProvided;
		}
		// Store last checked fragment id.
		this.mLastCheckedFragmentId = fragmentId;
		// Check joined factories.
		if (hasJoinedFactories()) {
			for (FragmentController.FragmentFactory factory : mFactories) {
				if (factory.isFragmentProvided(fragmentId)) {
					return mFragmentProvided = true;
				}
			}
		}
		return mFragmentProvided = providesFragment(fragmentId);
	}

	/**
	 * Invoked whenever {@link #isFragmentProvided(int)} is called and none of the current joined
	 * factories provides fragment for the specified <var>fragmentId</var>.
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
	public Fragment createFragmentInstance(@NonNull FragmentTransactionOptions options) {
		if (hasJoinedFactories()) {
			// Try to obtain dialog fragment from the current joined factories.
			for (FragmentController.FragmentFactory factory : mFactories) {
				if (factory.isFragmentProvided(options.incomingFragmentId)) {
					return factory.createFragmentInstance(options);
				}
			}
		}
		// Create fragment within this factory.
		return onCreateFragmentInstance(options);
	}

	/**
	 * Invoked whenever {@link #createFragmentInstance(FragmentTransactionOptions)} is called and none
	 * of the current joined factories provides fragment for the specified <var>options</var>.
	 * <p>
	 * This implementation returns the requested fragment instance if there is {@link FactoryFragment @FactoryFragment}
	 * annotation presented for the associated {@link FragmentTransactionOptions#incomingFragmentId}
	 * with valid fragment class ({@link FactoryFragment#value()}), {@code null} otherwise.
	 */
	@Nullable
	protected Fragment onCreateFragmentInstance(@NonNull FragmentTransactionOptions options) {
		final int fragmentId = options.incomingFragmentId;
		return providesFragment(fragmentId) ? mItems.get(fragmentId).newFragmentInstance(options.mArguments) : null;
	}

	/**
	 */
	@Nullable
	@Override
	public String getFragmentTag(@IntRange(from = 0) int fragmentId) {
		if (hasJoinedFactories()) {
			// Try to obtain tag from the joined factories.
			for (FragmentController.FragmentFactory factory : mFactories) {
				if (factory.isFragmentProvided(fragmentId)) {
					return factory.getFragmentTag(fragmentId);
				}
			}
		}
		return onGetFragmentTag(fragmentId);
	}

	/**
	 * Invoked whenever {@link #getFragmentTag(int)} is called and none of the current joined factories
	 * provides fragment for the specified <var>fragmentId</var>.
	 * <p>
	 * This implementation returns requested tag if there is {@link FactoryFragments @FactoryFragments}
	 * or {@link FactoryFragment @FactoryFragment} annotation presented for the specified <var>fragmentId</var>,
	 * otherwise {@link #createFragmentTag(Class, String)} will be used to create requested tag.
	 */
	@Nullable
	protected String onGetFragmentTag(@IntRange(from = 0) int fragmentId) {
		return providesFragment(fragmentId) ? mItems.get(fragmentId).tag : createFragmentTag(getClass(), Integer.toString(fragmentId));
	}

	/**
	 */
	@NonNull
	@Override
	public FragmentTransactionOptions configureTransactionOptions(@NonNull FragmentTransactionOptions options) {
		if (hasJoinedFactories()) {
			// Try to obtain TransactionOptions from the joined factories.
			for (FragmentController.FragmentFactory factory : mFactories) {
				if (factory.isFragmentProvided(options.incomingFragmentId)) {
					return factory.configureTransactionOptions(options);
				}
			}
		}
		return onConfigureTransactionOptions(options);
	}

	/**
	 * Invoked whenever {@link #configureTransactionOptions(FragmentTransactionOptions)} is called
	 * and none of the current joined factories provides fragment for the specified <var>options</var>.
	 * <p>
	 * This implementation performs default configuration of the given options with {@link BasicFragmentTransition#CROSS_FADE}
	 * transition and tag for the associated {@link FragmentTransactionOptions#incomingFragmentId}
	 * obtained via {@link #getFragmentTag(int)}, if these parameters has not been specified yet.
	 */
	@NonNull
	protected FragmentTransactionOptions onConfigureTransactionOptions(@NonNull FragmentTransactionOptions options) {
		if (providesFragment(options.incomingFragmentId)) {
			if (options.mTransition == null) options.transition(BasicFragmentTransition.CROSS_FADE);
			if (options.mTag == null) options.tag(getFragmentTag(options.incomingFragmentId));
		}
		return options;
	}

	/**
	 * Inflates a new transition from the specified Xml <var>resource</var>.
	 *
	 * @param resource Resource id of the desired transition to be inflated.
	 * @return Inflated transition or {@code null} if the current Android version does not support
	 * inflating of transitions from the Xml or this factory was not instantiated with valid context.
	 */
	@Nullable
	@SuppressLint("NewApi")
	public Transition inflateTransition(int resource) {
		if (!FragmentsConfig.TRANSITIONS_SUPPORTED || mTransitionInflater == null) return null;
		return mTransitionInflater.inflateTransition(resource);
	}

	/**
	 * Checks whether this factory instance has some joined factories or not.
	 *
	 * @return {@code True} if there are some joined factories, {@code false} otherwise.
	 * @see #getJoinedFactories()
	 */
	public boolean hasJoinedFactories() {
		return mFactories != null && !mFactories.isEmpty();
	}

	/**
	 * Joins the given fragment <var>factory</var> with this one.
	 * <p>
	 * <b>Note</b>, that fragment instances (and their tags) requested upon this factory are
	 * obtained from the current joined factories in order as they were joined. If none of the current
	 * joined factories provides requested fragment, this factory will handle such a request.
	 *
	 * @param factory Fragment factory to join with this one.
	 * @return {@code True} if the specified factory has been added into the joined factories,
	 * {@code false} if the factory is already joined.
	 * @see #getJoinedFactories()
	 */
	public final boolean joinFactory(@NonNull FragmentController.FragmentFactory factory) {
		if (mFactories == null) {
			this.mFactories = new ArrayList<>();
		}
		if (!mFactories.contains(factory)) {
			mFactories.add(factory);
			return true;
		}
		return false;
	}

	/**
	 * Removes the given <var>factory</var> from the current joined factories.
	 *
	 * @param factory The desired factory to be removed.
	 * @return {@code True} whenever the specified factory has been removed from the joined ones,
	 * {@code false} otherwise.
	 */
	public final boolean removeJoinedFactory(@NonNull FragmentController.FragmentFactory factory) {
		return mFactories != null && mFactories.remove(factory);
	}

	/**
	 * Returns the current joined factories.
	 *
	 * @return Set of dialog factories or {@link Collections#EMPTY_LIST} if there are no factories
	 * joined to this one.
	 * @see #hasJoinedFactories()
	 * @see #joinFactory(FragmentController.FragmentFactory)
	 */
	@NonNull
	@SuppressWarnings("unchecked")
	public final List<FragmentController.FragmentFactory> getJoinedFactories() {
		return mFactories != null ? mFactories : Collections.EMPTY_LIST;
	}

	/**
	 * Inner classes ===============================================================================
	 */
}
