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

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.view.ActionMode;
import android.view.Menu;

import universum.studios.android.support.fragment.ActionBarDelegate;
import universum.studios.android.support.fragment.ActionBarFragment;
import universum.studios.android.support.fragment.annotation.ActionBarOptions;
import universum.studios.android.support.fragment.annotation.ActionModeOptions;
import universum.studios.android.support.fragment.annotation.MenuOptions;

/**
 * An {@link AnnotationHandlers} implementation providing {@link AnnotationHandler} instances for
 * <b>ActionBar</b> associated fragments and classes.
 *
 * @author Martin Albedinsky
 */
@SuppressWarnings("unused")
public final class ActionBarAnnotationHandlers extends AnnotationHandlers {

	/**
	 * Constructors ================================================================================
	 */

	/**
	 */
	private ActionBarAnnotationHandlers() {
		super();
		// Creation of instances of this class is not publicly allowed.
	}

	/**
	 * Methods =====================================================================================
	 */

	/**
	 * Obtains a {@link ActionBarFragmentAnnotationHandler} implementation for the given <var>classOfFragment</var>.
	 *
	 * @see AnnotationHandlers#obtainHandler(Class, Class)
	 */
	@Nullable
	public static ActionBarFragmentAnnotationHandler obtainActionBarFragmentHandler(@NonNull Class<?> classOfFragment) {
		return obtainHandler(ActionBarFragmentHandler.class, classOfFragment);
	}

	/**
	 * Inner classes ===============================================================================
	 */

	/**
	 * An {@link ActionBarFragmentAnnotationHandler} implementation for {@link ActionBarFragment} class.
	 */
	@SuppressWarnings("WeakerAccess") static class ActionBarFragmentHandler extends BaseAnnotationHandlers.FragmentHandler implements ActionBarFragmentAnnotationHandler {

		/**
		 * Action bar's home as up flag obtained from the annotated class.
		 * <p>
		 * Obtained via {@link ActionBarOptions @ActionBarOptions} annotation.
		 */
		private int homeAsUp = ActionBarOptions.UNCHANGED;

		/**
		 * Vector drawable resource id of a home as up indicator obtained from the annotated class.
		 * <p>
		 * Obtained via {@link ActionBarOptions @ActionBarOptions} annotation.
		 */
		private int homeAsUpVectorIndicator = ActionBarOptions.UNCHANGED;

		/**
		 * Drawable resource id of a home as up indicator obtained from the annotated class.
		 * <p>
		 * Obtained via {@link ActionBarOptions @ActionBarOptions} annotation.
		 */
		private int homeAsUpIndicator = ActionBarOptions.UNCHANGED;

		/**
		 * Drawable resource id of an action bar's icon obtained from the annotated class.
		 * <p>
		 * Obtained via {@link ActionBarOptions @ActionBarOptions} annotation.
		 */
		private int icon = ActionBarOptions.UNCHANGED;

		/**
		 * Text resource id of an action bar's title obtained from the annotated class.
		 * <p>
		 * Obtained via {@link ActionBarOptions @ActionBarOptions} annotation.
		 */
		private int title = ActionBarOptions.UNCHANGED;

		/**
		 * Boolean flag indicating whether there were {@link MenuOptions @MenuOptions} annotation
		 * presented for the annotated class or {@link ActionBarOptions @ActionBarOptions} with
		 * home as up indicator enabled.
		 */
		private boolean hasOptionsMenu;

		/**
		 * Boolean flag indicating whether to clear options menu for the related fragment or not.
		 * <p>
		 * Obtained via {@link MenuOptions @MenuOptions} annotation.
		 */
		private boolean clearOptionsMenu;

		/**
		 * Menu resource id for a menu of the related fragment obtained from the annotated class.
		 * <p>
		 * Obtained via {@link MenuOptions @MenuOptions} annotation.
		 */
		private int optionsMenuResource = NO_RES;

		/**
		 * Flags for the options menu of the related fragment obtained from the annotated class.
		 * <p>
		 * Obtained via {@link MenuOptions @MenuOptions} annotation.
		 */
		private int optionsMenuFlags = -1;

		/**
		 * Menu resource id for an action mode of the related fragment obtained from the annotated
		 * class.
		 * <p>
		 * Obtained via {@link ActionModeOptions @ActionModeOptions} annotation.
		 */
		private int actionModeMenuResource = NO_RES;

		/**
		 * Same as {@link #ActionBarFragmentHandler(Class, Class)} with {@link ActionBarFragment}
		 * as <var>maxSuperClass</var>.
		 */
		public ActionBarFragmentHandler(@NonNull Class<?> annotatedClass) {
			this(annotatedClass, ActionBarFragment.class);
		}

		/**
		 * Creates a new instance of ActionBarFragmentHandler for the specified <var>annotatedClass</var>.
		 *
		 * @see BaseAnnotationHandler#BaseAnnotationHandler(Class, Class)
		 */
		ActionBarFragmentHandler(Class<?> annotatedClass, Class<?> maxSuperClass) {
			super(annotatedClass, maxSuperClass);
			final ActionBarOptions actionBarOptions = findAnnotationRecursive(ActionBarOptions.class);
			if (actionBarOptions != null) {
				this.homeAsUp = actionBarOptions.homeAsUp();
				this.homeAsUpVectorIndicator = actionBarOptions.homeAsUpVectorIndicator();
				this.homeAsUpIndicator = actionBarOptions.homeAsUpIndicator();
				this.icon = actionBarOptions.icon();
				this.title = actionBarOptions.title();
			}
			final MenuOptions menuOptions = findAnnotationRecursive(MenuOptions.class);
			if (menuOptions != null) {
				this.hasOptionsMenu = true;
				this.clearOptionsMenu = menuOptions.clear();
				this.optionsMenuFlags = menuOptions.flags();
				this.optionsMenuResource = menuOptions.value();
			}
			final ActionModeOptions actionModeOptions = findAnnotationRecursive(ActionModeOptions.class);
			if (actionModeOptions != null) {
				this.actionModeMenuResource = actionModeOptions.menu();
			}
		}

		/**
		 */
		@Override
		public void configureActionBar(@NonNull ActionBarDelegate actionBarDelegate) {
			switch (homeAsUp) {
				case ActionBarOptions.HOME_AS_UP_DISABLED:
					actionBarDelegate.setDisplayHomeAsUpEnabled(false);
					break;
				case ActionBarOptions.HOME_AS_UP_ENABLED:
					actionBarDelegate.setDisplayHomeAsUpEnabled(true);
					this.hasOptionsMenu = true;
					break;
				default:
					// Do not "touch" ActionBar's enabled state.
					break;
			}
			if (homeAsUpVectorIndicator != ActionBarOptions.UNCHANGED) {
				switch (homeAsUpVectorIndicator) {
					case ActionBarOptions.NONE:
						actionBarDelegate.setHomeAsUpIndicator(new ColorDrawable(Color.TRANSPARENT));
						break;
					default:
						actionBarDelegate.setHomeAsUpVectorIndicator(homeAsUpVectorIndicator);
						break;
				}
			} else if (homeAsUpIndicator != ActionBarOptions.UNCHANGED) {
				switch (homeAsUpIndicator) {
					case ActionBarOptions.NONE:
						actionBarDelegate.setHomeAsUpIndicator(new ColorDrawable(Color.TRANSPARENT));
						break;
					default:
						actionBarDelegate.setHomeAsUpIndicator(homeAsUpIndicator);
						break;
				}
			}
			switch (icon) {
				case ActionBarOptions.UNCHANGED:
					break;
				case ActionBarOptions.NONE:
					actionBarDelegate.setIcon(new ColorDrawable(Color.TRANSPARENT));
					break;
				default:
					actionBarDelegate.setIcon(icon);
					break;
			}
			switch (title) {
				case ActionBarOptions.UNCHANGED:
					break;
				case ActionBarOptions.NONE:
					actionBarDelegate.setTitle("");
					break;
				default:
					actionBarDelegate.setTitle(title);
					break;
			}
		}

		/**
		 */
		@Override
		public boolean hasOptionsMenu() {
			return hasOptionsMenu;
		}

		/**
		 */
		@Override
		public boolean shouldClearOptionsMenu() {
			return clearOptionsMenu;
		}

		/**
		 */
		@MenuRes
		@Override
		public int getOptionsMenuFlags(@MenuRes int defaultFlags) {
			return optionsMenuFlags == -1 ? defaultFlags : optionsMenuFlags;
		}

		/**
		 */
		@Override
		public int getOptionsMenuResource(int defaultResource) {
			return optionsMenuResource == NO_RES ? defaultResource : optionsMenuResource;
		}

		/**
		 */
		@Override
		public boolean handleCreateActionMode(@NonNull ActionMode actionMode, @NonNull Menu menu) {
			if (actionModeMenuResource == NO_RES) {
				return false;
			}
			actionMode.getMenuInflater().inflate(actionModeMenuResource, menu);
			return true;
		}
	}
}
