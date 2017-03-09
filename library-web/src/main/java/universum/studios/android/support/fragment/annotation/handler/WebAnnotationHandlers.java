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
import android.support.annotation.StringRes;
import android.text.TextUtils;

import universum.studios.android.support.fragment.WebFragment;
import universum.studios.android.support.fragment.annotation.WebContent;

/**
 * An {@link AnnotationHandlers} implementation providing {@link AnnotationHandler} instances for
 * <b>web</b> associated fragments and classes.
 *
 * @author Martin Albedinsky
 */
public final class WebAnnotationHandlers extends AnnotationHandlers {

	/**
	 * Constructors ================================================================================
	 */

	/**
	 */
	private WebAnnotationHandlers() {
		super();
		// Creation of instances of this class is not publicly allowed.
	}

	/**
	 * Methods =====================================================================================
	 */

	/**
	 * Obtains a {@link WebFragmentAnnotationHandler} implementation for the given <var>classOfFragment</var>.
	 *
	 * @see AnnotationHandlers#obtainHandler(Class, Class)
	 */
	@Nullable
	public static WebFragmentAnnotationHandler obtainWebFragmentHandler(@NonNull Class<?> classOfFragment) {
		return obtainHandler(WebFragmentHandler.class, classOfFragment);
	}

	/**
	 * Inner classes ===============================================================================
	 */

	/**
	 * A {@link WebFragmentAnnotationHandler} implementation for {@link WebFragment} class.
	 */
	@SuppressWarnings("WeakerAccess") static final class WebFragmentHandler extends ActionBarAnnotationHandlers.ActionBarFragmentHandler implements WebFragmentAnnotationHandler {

		/**
		 * String resource id of a web content obtained from the annotated class.
		 * <p>
		 * Obtained via {@link WebContent @WebContent} annotation.
		 */
		private final int webContentResId;

		/**
		 * Web content string obtained from the annotated class.
		 * <p>
		 * Obtained via {@link WebContent @WebContent} annotation.
		 */
		private final String webContent;

		/**
		 * Same as {@link BaseAnnotationHandler#BaseAnnotationHandler(Class, Class)} with
		 * {@link WebFragment} as <var>maxSuperClass</var>.
		 */
		public WebFragmentHandler(@NonNull Class<?> annotatedClass) {
			super(annotatedClass, WebFragment.class);
			final WebContent webContent = findAnnotation(WebContent.class);
			this.webContentResId = webContent == null ? NO_RES : webContent.valueRes();
			this.webContent = webContent == null ? null : webContent.value();
		}

		/**
		 */
		@Override
		@StringRes
		public int getWebContentResId(@StringRes int defaultResId) {
			return webContentResId == NO_RES ? defaultResId : webContentResId;
		}

		/**
		 */
		@Nullable
		@Override
		public String getWebContent(@Nullable String defaultContent) {
			return TextUtils.isEmpty(webContent) ? defaultContent : webContent;
		}
	}
}
