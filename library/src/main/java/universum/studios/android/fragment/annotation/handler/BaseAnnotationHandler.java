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

import java.lang.annotation.Annotation;

import universum.studios.android.fragment.annotation.FragmentAnnotations;

/**
 * An {@link AnnotationHandler} base implementation.
 *
 * @author Martin Albedinsky
 */
abstract class BaseAnnotationHandler implements AnnotationHandler {

	/**
	 * Interface ===================================================================================
	 */

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "BaseAnnotationHandler";

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
	 * Same as {@link #BaseAnnotationHandler(Class, Class)} with {@code null} <var>maxSuperClass</var>
	 */
	BaseAnnotationHandler(Class<?> annotatedClass) {
		this(annotatedClass, null);
	}

	/**
	 * Creates a new instance of BaseAnnotationHandler for the specified <var>annotatedClass</var>.
	 *
	 * @param annotatedClass The class of which annotations processing should the new handler
	 *                       handle.
	 * @param maxSuperClass  Max super class of the annotated class till where to search for
	 *                       annotations if they are not presented in the annotated class.
	 */
	BaseAnnotationHandler(Class<?> annotatedClass, Class<?> maxSuperClass) {
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
	 * Returns the max super class with which has been this handler created (if supplied).
	 *
	 * @return Max super class that is used as boundary for recursive finding of a specific annotation
	 * via {@link #findAnnotationRecursive(Class)}.
	 */
	final Class<?> getMaxSupperClass() {
		return mMaxSuperClass;
	}

	/**
	 * Same as {@link #findAnnotationRecursive(Class, Class)}, but this tries to find the requested
	 * annotation only for the class attached to this handler.
	 *
	 * @param classOfAnnotation Class of the annotation to find.
	 * @param <A> Type of the annotation to find.
	 * @return Found annotation or {@code null} if there is no such annotation presented.
	 */
	final <A extends Annotation> A findAnnotation(Class<A> classOfAnnotation) {
		return FragmentAnnotations.obtainAnnotationFrom(classOfAnnotation, mAnnotatedClass, null);
	}

	/**
	 * Same as {@link #findAnnotationRecursive(Class, Class)} with {@link #getMaxSupperClass()} as
	 * <var>maxSuperClass</var>.
	 */
	final <A extends Annotation> A findAnnotationRecursive(Class<A> classOfAnnotation) {
		return findAnnotationRecursive(classOfAnnotation, mMaxSuperClass);
	}

	/**
	 * Tries to find annotation with the requested <var>classOfAnnotation</var> for the class attached
	 * to this handler recursively.
	 *
	 * @param classOfAnnotation Class of the annotation to find.
	 * @param maxSuperClass     Class till which should the recursion be performed (excluding).
	 * @param <A>               Type of the annotation to find.
	 * @return Found annotation or {@code null} if there is no such annotation presented.
	 */
	final <A extends Annotation> A findAnnotationRecursive(Class<A> classOfAnnotation, Class<?> maxSuperClass) {
		return FragmentAnnotations.obtainAnnotationFrom(classOfAnnotation, mAnnotatedClass, maxSuperClass);
	}

	/**
	 * Inner classes ===============================================================================
	 */
}
