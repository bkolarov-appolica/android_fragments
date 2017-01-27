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

import java.util.HashMap;

import universum.studios.android.fragment.FragmentsConfig;

/**
 * Base factory and cache for {@link AnnotationHandler} instances for a specific classes from the
 * Fragments library.
 *
 * @author Martin Albedinsky
 */
public class AnnotationHandlers {

	/**
	 * Interface ===================================================================================
	 */

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "AnnotationHandlers";

	/**
	 * Static members ==============================================================================
	 */

	/**
	 * Lock used for synchronized operations.
	 */
	private static final Object LOCK = new Object();

	/**
	 * Initial capacity for the handlers map.
	 */
	private static final int HANDLERS_INITIAL_CAPACITY = 20;

	/**
	 * Map with annotation handlers where each handler is mapped to a particular class for which
	 * has been that handler instantiated.
	 */
	private static HashMap<Class<?>, Object> sHandlers;

	/**
	 * Members =====================================================================================
	 */

	/**
	 * Constructors ================================================================================
	 */

	/**
	 */
	AnnotationHandlers() {
		// Creation of instances of this class is not publicly allowed.
	}

	/**
	 * Methods =====================================================================================
	 */

	/**
	 * Obtains an annotation handler with the specified <var>classOfHandler</var> class for the
	 * given <var>annotatedClass</var>. If there is no such handler already instantiated and cached,
	 * its instance will be created and cached.
	 * <p>
	 * Each handler is mapped to its annotated class, so there may be only one handler of the same
	 * type created for the same annotated class.
	 *
	 * @param classOfHandler Class of the handler used to instantiate the requested handler instance
	 *                       if needed.
	 * @param annotatedClass Class for which to obtain the requested handler.
	 * @param <T>            Type of the handler to be obtained.
	 * @return Always valid instance of the requested handler or {@code null} if annotations processing
	 * is disabled for the Fragments library.
	 * @throws ClassCastException If there is already an annotation handler instantiated for the
	 *                            specified annotated class but it is of different type as requested.
	 * @see FragmentsConfig#ANNOTATIONS_PROCESSING_ENABLED
	 */
	@Nullable
	@SuppressWarnings({"unchecked", "ConstantConditions"})
	public static <T extends AnnotationHandler> T obtainHandler(@NonNull Class<T> classOfHandler, @NonNull Class<?> annotatedClass) {
		if (!FragmentsConfig.ANNOTATIONS_PROCESSING_ENABLED) return null;
		Object handler;
		synchronized (LOCK) {
			if (sHandlers == null) {
				sHandlers = new HashMap<>(HANDLERS_INITIAL_CAPACITY);
			}
			handler = sHandlers.get(annotatedClass);
			if (handler == null) {
				handler = instantiateHandler(classOfHandler, annotatedClass);
				sHandlers.put(annotatedClass, handler);
			} else if (!handler.getClass().equals(classOfHandler)) {
				final String newHandlerName = classOfHandler.getSimpleName();
				final String currentHandlerName = handler.getClass().getSimpleName();
				final String className = annotatedClass.getSimpleName();
				throw new ClassCastException(
						"Trying to obtain handler(" + newHandlerName + ") for class(" + className + ") while there " +
								"is already handler(" + currentHandlerName + ") of different type for that class!"
				);
			}
		}
		return (T) handler;
	}

	/**
	 * Instantiates a new annotation handler instance of the specified <var>classOfHandler</var> class
	 * for the given <var>annotatedClass</var>.
	 *
	 * @param classOfHandler Class of the handler to instantiate.
	 * @param annotatedClass Class for which to instantiate the requested handler.
	 * @param <T>            Type of the handler that will be instantiated.
	 * @return New instance of the requested handler with the annotated class attached.
	 * @throws IllegalStateException If the requested handler failed to be instantiated.
	 */
	private static <T> T instantiateHandler(Class<T> classOfHandler, Class<?> annotatedClass) {
		try {
			return classOfHandler.getConstructor(Class.class).newInstance(annotatedClass);
		} catch (Exception e) {
			// Happens when the handler implementation is not properly implemented:
			// - handler class is not visible,
			// - handler class does not have public constructor taking annotated class as single argument.
			final String handlerName = classOfHandler.getSimpleName();
			final String className = annotatedClass.getSimpleName();
			throw new IllegalStateException("Failed to instantiate annotation handler(" + handlerName + ") for(" + className + ").", e);
		}
	}

	/**
	 * Inner classes ===============================================================================
	 */
}
