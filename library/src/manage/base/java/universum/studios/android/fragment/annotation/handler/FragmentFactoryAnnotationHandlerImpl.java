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
package universum.studios.android.fragment.annotation.handler;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.SparseArray;

import java.lang.reflect.Field;

import universum.studios.android.fragment.annotation.FactoryFragment;
import universum.studios.android.fragment.annotation.FactoryFragments;
import universum.studios.android.fragment.manage.BaseFragmentFactory;
import universum.studios.android.fragment.manage.FragmentItem;

/**
 * A {@link FragmentFactoryAnnotationHandler} implementation for {@link BaseFragmentFactory} class.
 *
 * @author Martin Albedinsky
 */
@SuppressWarnings("unused")
final class FragmentFactoryAnnotationHandlerImpl extends BaseAnnotationHandler implements FragmentFactoryAnnotationHandler {

	/**
	 * Interface ===================================================================================
	 */

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "FragmentFactoryAnnotationHandlerImpl";

	/**
	 * Static members ==============================================================================
	 */

	/**
	 * Members =====================================================================================
	 */

	/**
	 * Array of fragment items populated from the {@link FactoryFragments @FactoryFragments} or
	 * {@link FactoryFragment @FactoryFragment} annotations if presented.
	 */
	final SparseArray<FragmentItem> mItems;

	/**
	 * Constructors ================================================================================
	 */

	/**
	 * Same as {@link BaseAnnotationHandler#BaseAnnotationHandler(Class, Class)} with
	 * {@link BaseFragmentFactory} as <var>maxSuperClass</var>.
	 */
	public FragmentFactoryAnnotationHandlerImpl(@NonNull Class<?> annotatedClass) {
		super(annotatedClass, BaseFragmentFactory.class);
		final SparseArray<FragmentItem> items = new SparseArray<>();
		final FactoryFragments fragments = findAnnotation(FactoryFragments.class);
		if (fragments != null) {
			final int[] ids = fragments.value();
			if (ids.length > 0) {
				for (int id : ids) {
					items.put(id, new FragmentItem(
							id,
							Fragment.class,
							createFragmentTag(Integer.toString(id))
					));
				}
			}
		}
		AnnotationHandlers.iterateFields(new AnnotationHandlers.FieldProcessor() {

			/**
			 */
			@Override
			public void onProcessField(@NonNull Field field, @NonNull String name) {
				if (field.isAnnotationPresent(FactoryFragment.class) && int.class.equals(field.getType())) {
					final FactoryFragment factoryFragment = field.getAnnotation(FactoryFragment.class);
					try {
						final int id = (int) field.get(mAnnotatedClass);
						items.put(id, new FragmentItem(
								id,
								factoryFragment.value(),
								createFragmentTag(
										TextUtils.isEmpty(factoryFragment.taggedName()) ?
												Integer.toString(id) :
												factoryFragment.taggedName()
								)
						));
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}, mAnnotatedClass);
		this.mItems = items.size() > 0 ? items : null;
	}

	/**
	 * Methods =====================================================================================
	 */

	/**
	 */
	@Override
	@Nullable
	public SparseArray<FragmentItem> getFragmentItems() {
		return mItems;
	}

	/**
	 * Creates a new fragment tag for the specified <var>taggedName</var> via {@link BaseFragmentFactory#createFragmentTag(Class, String)}.
	 *
	 * @param taggedName The name to be added into requested tag.
	 * @return New tag composed from class path of the annotated class and the given tagged name.
	 */
	private String createFragmentTag(String taggedName) {
		return BaseFragmentFactory.createFragmentTag(mAnnotatedClass, taggedName);
	}

	/**
	 * Inner classes ===============================================================================
	 */
}
