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
import android.util.Log;
import android.util.SparseArray;

import java.lang.reflect.Field;

import universum.studios.android.fragment.annotation.FactoryFragment;
import universum.studios.android.fragment.annotation.FactoryFragments;
import universum.studios.android.fragment.annotation.FragmentAnnotations;
import universum.studios.android.fragment.manage.BaseFragmentFactory;
import universum.studios.android.fragment.manage.FragmentItem;

/**
 * An {@link AnnotationHandlers} implementation providing {@link AnnotationHandler} instances for
 * <b>web</b> associated fragments and classes.
 *
 * @author Martin Albedinsky
 */
public final class BaseManagementAnnotationHandlers extends AnnotationHandlers {

	/**
	 * Constructors ================================================================================
	 */

	/**
	 */
	private BaseManagementAnnotationHandlers() {
		// Creation of instances of this class is not publicly allowed.
	}

	/**
	 * Methods =====================================================================================
	 */

	/**
	 * Obtains a {@link FragmentFactoryAnnotationHandler} implementation for the given <var>classOfFactory</var>.
	 *
	 * @see AnnotationHandlers#obtainHandler(Class, Class)
	 */
	@Nullable
	public static FragmentFactoryAnnotationHandler obtainFactoryHandler(@NonNull Class<?> classOfFactory) {
		return obtainHandler(FragmentFactoryHandler.class, classOfFactory);
	}

	/**
	 * Inner classes ===============================================================================
	 */

	/**
	 * A {@link FragmentFactoryAnnotationHandler} implementation for {@link BaseFragmentFactory} class.
	 */
	@SuppressWarnings("WeakerAccess") static final class FragmentFactoryHandler extends BaseAnnotationHandler implements FragmentFactoryAnnotationHandler {

		/**
		 * Array of fragment items populated from the {@link FactoryFragments @FactoryFragments} or
		 * {@link FactoryFragment @FactoryFragment} annotations if presented.
		 */
		private final SparseArray<FragmentItem> items;

		/**
		 * Same as {@link BaseAnnotationHandler#BaseAnnotationHandler(Class, Class)} with
		 * {@link BaseFragmentFactory} as <var>maxSuperClass</var>.
		 */
		public FragmentFactoryHandler(@NonNull Class<?> annotatedClass) {
			super(annotatedClass, BaseFragmentFactory.class);
			final SparseArray<FragmentItem> items = new SparseArray<>();
			final FactoryFragments fragments = findAnnotationRecursive(FactoryFragments.class);
			if (fragments != null) {
				final int[] ids = fragments.value();
				if (ids.length > 0) {
					for (int id : ids) {
						items.put(id, new FragmentItem(
								id,
								Fragment.class,
								BaseFragmentFactory.createFragmentTag(mAnnotatedClass, Integer.toString(id))
						));
					}
				}
			}
			FragmentAnnotations.iterateFields(new FragmentAnnotations.FieldProcessor() {

				/**
				 */
				@Override
				public void onProcessField(@NonNull Field field, @NonNull String name) {
					if (field.isAnnotationPresent(FactoryFragment.class) && int.class.equals(field.getType())) {
						final FactoryFragment factoryFragment = field.getAnnotation(FactoryFragment.class);
						try {
							field.setAccessible(true);
							final int id = (int) field.get(null);
							items.put(id, new FragmentItem(
									id,
									factoryFragment.value(),
									BaseFragmentFactory.createFragmentTag(
											mAnnotatedClass,
											TextUtils.isEmpty(factoryFragment.taggedName()) ?
													Integer.toString(id) :
													factoryFragment.taggedName()
									)
							));
						} catch (IllegalAccessException e) {
							Log.e(
									FragmentFactoryAnnotationHandler.class.getSimpleName(),
									"Failed to obtain id value from @FactoryFragment " + name + "!",
									e
							);
						}
					}
				}
			}, mAnnotatedClass, mMaxSuperClass);
			this.items = items.size() > 0 ? items : null;
		}

		/**
		 */
		@Override
		@Nullable
		public SparseArray<FragmentItem> getFragmentItems() {
			return items;
		}
	}
}
