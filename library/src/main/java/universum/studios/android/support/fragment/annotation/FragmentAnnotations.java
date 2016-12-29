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
package universum.studios.android.support.fragment.annotation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import universum.studios.android.support.fragment.FragmentsConfig;

/**
 * Annotation utils for the Fragments library.
 *
 * @author Martin Albedinsky
 */
public final class FragmentAnnotations {

	/**
	 * Interface ===================================================================================
	 */

	/**
	 * Simple callback which allows processing of all declared fields of a desired class via
	 * {@link #iterateFields(FragmentAnnotations.FieldProcessor, Class, Class)}.
	 *
	 * @author Martin Albedinsky
	 */
	public interface FieldProcessor {

		/**
		 * Invoked for each of iterated fields.
		 *
		 * @param field The currently iterated field.
		 * @param name  Name of the currently iterated field.
		 */
		void onProcessField(@NonNull Field field, @NonNull String name);
	}

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "FragmentAnnotations";

	/**
	 * Static members ==============================================================================
	 */

	/**
	 * Members =====================================================================================
	 */

	/**
	 * Constructors ================================================================================
	 */

	/**
	 */
	private FragmentAnnotations() {
		// Creation of instances of this class is not publicly allowed.
	}

	/**
	 * Methods =====================================================================================
	 */

	/**
	 * Performs check for enabled state of the annotations processing for the Fragments library.
	 * <p>
	 * This check is requested mostly from parts of the Fragments library that require to be annotations
	 * processing enabled.
	 *
	 * @throws IllegalStateException If annotations processing is disabled.
	 */
	public static void checkIfEnabledOrThrow() {
		if (!FragmentsConfig.ANNOTATIONS_PROCESSING_ENABLED) {
			throw new IllegalStateException(
					"Trying to access logic that requires annotations processing to be enabled, " +
					"but it appears that the annotations processing is disabled for the Fragments library."
			);
		}
	}

	/**
	 * Obtains the requested type of annotation from the given <var>fromClass</var> if it is presented.
	 *
	 * @param classOfAnnotation Class of the requested annotation.
	 * @param fromClass         Class from which should be the requested annotation obtained.
	 * @param maxSuperClass     If {@code not null}, this method will be called (recursively) for
	 *                          all super classes of the given annotated class (max to the specified
	 *                          <var>maxSuperClass</var> excluding) until the requested annotation
	 *                          is presented and obtained, otherwise annotation will be obtained only
	 *                          from the given annotated class.
	 * @param <A>               Type of the requested annotation.
	 * @return Obtained annotation or {@code null} if the requested annotation is not presented
	 * for the given class or its supers if requested.
	 */
	@Nullable
	public static <A extends Annotation> A obtainAnnotationFrom(@NonNull Class<A> classOfAnnotation, @NonNull Class<?> fromClass, @Nullable Class<?> maxSuperClass) {
		final A annotation = fromClass.getAnnotation(classOfAnnotation);
		if (annotation != null) {
			return annotation;
		} else if (maxSuperClass != null) {
			final Class<?> parent = fromClass.getSuperclass();
			if (parent != null && !parent.equals(maxSuperClass)) {
				return obtainAnnotationFrom(classOfAnnotation, parent, maxSuperClass);
			}
		}
		return null;
	}

	/**
	 * Iterates all declared fields of the given <var>ofClass</var>.
	 *
	 * @param processor     Field processor callback to be invoked for each of iterated fields.
	 * @param ofClass       Class of which fields to iterate.
	 * @param maxSuperClass If {@code not null}, this method will be called (recursively) for all
	 *                      super classes of the given class (max to the specified <var>maxSuperClass</var>
	 *                      excluding), otherwise only fields of the given class will be iterated.
	 */
	public static void iterateFields(@NonNull FieldProcessor processor, @NonNull Class<?> ofClass, @Nullable Class<?> maxSuperClass) {
		final Field[] fields = ofClass.getDeclaredFields();
		if (fields.length > 0) {
			for (Field field : fields) {
				processor.onProcessField(field, field.getName());
			}
		}
		if (maxSuperClass != null) {
			final Class<?> parent = ofClass.getSuperclass();
			if (parent != null && !parent.equals(maxSuperClass)) {
				iterateFields(processor, parent, maxSuperClass);
			}
		}
	}

	/**
	 * Inner classes ===============================================================================
	 */
}
