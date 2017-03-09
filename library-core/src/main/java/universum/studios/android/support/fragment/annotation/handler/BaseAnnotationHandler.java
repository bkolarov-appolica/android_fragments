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
package universum.studios.android.support.fragment.annotation.handler;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.annotation.Annotation;

import universum.studios.android.support.fragment.annotation.FragmentAnnotations;

/**
 * An {@link AnnotationHandler} base implementation.
 *
 * @author Martin Albedinsky
 */
abstract class BaseAnnotationHandler implements AnnotationHandler {

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "BaseAnnotationHandler";

	/**
	 * Interface ===================================================================================
	 */

	/**
	 * Static members ==============================================================================
	 */

	/**
	 * Members =====================================================================================
	 */

	/**
	 * Class for which has been this handler created.
	 */
	final Class<?> mAnnotatedClass;

	/**
	 * Class that is used when obtaining annotations from {@link #mAnnotatedClass} recursively via
	 * {@link FragmentAnnotations#obtainAnnotationFrom(Class, Class, Class)}.
	 */
	final Class<?> mMaxSuperClass;

	/**
	 * Constructors ================================================================================
	 */

	/**
	 * Creates a new instance of BaseAnnotationHandler for the specified <var>annotatedClass</var>.
	 *
	 * @param annotatedClass The class of which annotations processing should the new handler handle.
	 * @param maxSuperClass  Max super class of the annotated class up to which to search for annotations
	 *                       recursively when searching for requested annotation via {@link #findAnnotationRecursive(Class)}.
	 */
	BaseAnnotationHandler(@NonNull Class<?> annotatedClass, @Nullable Class<?> maxSuperClass) {
		this.mAnnotatedClass = annotatedClass;
		this.mMaxSuperClass = maxSuperClass;
	}

	/**
	 * Methods =====================================================================================
	 */

	/**
	 */
	@NonNull
	@Override
	public final Class<?> getAnnotatedClass() {
		return mAnnotatedClass;
	}

	/**
	 * Like {@link #findAnnotationRecursive(Class)} but this method tries to find the requested
	 * annotation only for the class attached to this handler.
	 *
	 * @param classOfAnnotation Class of the annotation to find.
	 * @param <A>               Type of the annotation to find.
	 * @return Found annotation or {@code null} if there is no such annotation presented.
	 */
	final <A extends Annotation> A findAnnotation(Class<A> classOfAnnotation) {
		return FragmentAnnotations.obtainAnnotationFrom(classOfAnnotation, mAnnotatedClass, null);
	}

	/**
	 * Tries to find annotation with the requested <var>classOfAnnotation</var> for the class attached
	 * to this handler recursively using also max super class specified for this handler (if any).
	 *
	 * @param classOfAnnotation Class of the annotation to find.
	 * @param <A>               Type of the annotation to find.
	 * @return Found annotation or {@code null} if there is no such annotation presented.
	 */
	final <A extends Annotation> A findAnnotationRecursive(Class<A> classOfAnnotation) {
		return FragmentAnnotations.obtainAnnotationFrom(classOfAnnotation, mAnnotatedClass, mMaxSuperClass);
	}

	/**
	 * Inner classes ===============================================================================
	 */
}
