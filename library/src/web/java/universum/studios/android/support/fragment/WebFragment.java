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
package universum.studios.android.support.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import universum.studios.android.support.fragment.annotation.FragmentAnnotations;
import universum.studios.android.support.fragment.annotation.handler.WebAnnotationHandlers;
import universum.studios.android.support.fragment.annotation.handler.WebFragmentAnnotationHandler;

/**
 * An {@link ActionBarFragment} implementation that allows to show a web content within a {@link WebView}.
 * This fragment creates WebView as its root view when {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
 * is called. That WebView is used to present the web content supplied to this web fragment.
 * <p>
 * The desired web content may be specified through {@link WebOptions} when creating new instance of
 * WebFragment via {@link #newInstance(WebFragment.WebOptions)} or when you have access to already
 * visible and showing web fragment via {@link #loadContent(String)}.
 *
 * <h3>Web content types</h3>
 * Following content types are supported as content that may be loaded into WebView:
 * <ul>
 * <li>
 * <b>URL</b>
 * <p>
 * http://www.google.com
 * </li>
 * <li>
 * <b>HTML</b>
 * <pre>
 * &lt;h6&gt;Content heading&lt;/h6&gt;
 * &lt;p&gt;
 * Content paragraph.
 * &lt;/p&gt;
 * </pre>
 * </li>
 * <li>
 * <b>FILE</b>
 * <p>
 * file://PATH_TO_THE_FILE_WITH_WEB_CONTENT
 * </li>
 * </ul>
 *
 * <h3>Accepted annotations</h3>
 * <ul>
 * <li>
 * {@link universum.studios.android.support.fragment.annotation.WebContent @WebContent} <b>[class]</b>
 * <p>
 * If this annotation is presented, the content provided by this annotation will be loaded into
 * {@link WebView} of an instance of annotated WebFragment sub-class.
 * </li>
 * </ul>
 *
 * @author Martin Albedinsky
 * @see WebFragment.OnWebContentLoadingListener
 */
public class WebFragment extends ActionBarFragment {

	/**
	 * Interface ===================================================================================
	 */

	/**
	 * Simple listener for {@link WebFragment} with callbacks fired whenever loading process of a
	 * specific <b>web url</b> was started/finished.
	 *
	 * @author Martin Albedinsky
	 */
	public interface OnWebContentLoadingListener {

		/**
		 * Invoked whenever loading process of the specified <var>webUrl</var> within an instance of
		 * {@link WebFragment} for which is this callback registered just started.
		 *
		 * @param webUrl The web url that is currently being loaded into web view.
		 */
		void onLoadingStarted(@NonNull String webUrl);

		/**
		 * Invoked whenever loading process of the specified <var>webUrl</var> within an instance of
		 * {@link WebFragment} for which is this callback registered just finished.
		 *
		 * @param webUrl The web url that was currently loaded into web view.
		 */
		void onLoadingFinished(@NonNull String webUrl);
	}

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	private static final String TAG = "WebFragment";

	/**
	 * Bundle key for the web view content.
	 */
	private static final String BUNDLE_WEB_VIEW_CONTENT = "universum.studios.android.support.fragment.WebFragment.BUNDLE.Content";

	/**
	 * Bundle key for the private flags.
	 */
	private static final String BUNDLE_PRIVATE_FLAGS = "universum.studios.android.support.fragment.WebFragment.BUNDLE.PrivateFlags";

	/**
	 * Source code copied from Android SDK [START] =================================================
	 * to preserve min library SDK version at 7.
	 */
	private static final String GOOD_IRI_CHAR = "a-zA-Z0-9\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF";

	/**
	 * Regular expression to match all IANA top-level domains for WEB_URL_MATCHER.
	 * List accurate as of 2011/07/18.  List taken from:
	 * http://data.iana.org/TLD/tlds-alpha-by-domain.txt
	 * This pattern is auto-generated by frameworks/ex/common/tools/make-iana-tld-pattern.py
	 */
	private static final String TOP_LEVEL_DOMAIN_STR_FOR_WEB_URL =
			"(?:"
					+ "(?:aero|arpa|asia|a[cdefgilmnoqrstuwxz])"
					+ "|(?:biz|b[abdefghijmnorstvwyz])"
					+ "|(?:cat|com|coop|c[acdfghiklmnoruvxyz])"
					+ "|d[ejkmoz]"
					+ "|(?:edu|e[cegrstu])"
					+ "|f[ijkmor]"
					+ "|(?:gov|g[abdefghilmnpqrstuwy])"
					+ "|h[kmnrtu]"
					+ "|(?:info|int|i[delmnoqrst])"
					+ "|(?:jobs|j[emop])"
					+ "|k[eghimnprwyz]"
					+ "|l[abcikrstuvy]"
					+ "|(?:mil|mobi|museum|m[acdeghklmnopqrstuvwxyz])"
					+ "|(?:name|net|n[acefgilopruz])"
					+ "|(?:org|om)"
					+ "|(?:pro|p[aefghklmnrstwy])"
					+ "|qa"
					+ "|r[eosuw]"
					+ "|s[abcdeghijklmnortuvyz]"
					+ "|(?:tel|travel|t[cdfghjklmnoprtvwz])"
					+ "|u[agksyz]"
					+ "|v[aceginu]"
					+ "|w[fs]"
					+ "|(?:\u03b4\u03bf\u03ba\u03b9\u03bc\u03ae|\u0438\u0441\u043f\u044b\u0442\u0430\u043d\u0438\u0435|\u0440\u0444|\u0441\u0440\u0431|\u05d8\u05e2\u05e1\u05d8|\u0622\u0632\u0645\u0627\u06cc\u0634\u06cc|\u0625\u062e\u062a\u0628\u0627\u0631|\u0627\u0644\u0627\u0631\u062f\u0646|\u0627\u0644\u062c\u0632\u0627\u0626\u0631|\u0627\u0644\u0633\u0639\u0648\u062f\u064a\u0629|\u0627\u0644\u0645\u063a\u0631\u0628|\u0627\u0645\u0627\u0631\u0627\u062a|\u0628\u06be\u0627\u0631\u062a|\u062a\u0648\u0646\u0633|\u0633\u0648\u0631\u064a\u0629|\u0641\u0644\u0633\u0637\u064a\u0646|\u0642\u0637\u0631|\u0645\u0635\u0631|\u092a\u0930\u0940\u0915\u094d\u0937\u093e|\u092d\u093e\u0930\u0924|\u09ad\u09be\u09b0\u09a4|\u0a2d\u0a3e\u0a30\u0a24|\u0aad\u0abe\u0ab0\u0aa4|\u0b87\u0ba8\u0bcd\u0ba4\u0bbf\u0baf\u0bbe|\u0b87\u0bb2\u0b99\u0bcd\u0b95\u0bc8|\u0b9a\u0bbf\u0b99\u0bcd\u0b95\u0baa\u0bcd\u0baa\u0bc2\u0bb0\u0bcd|\u0baa\u0bb0\u0bbf\u0b9f\u0bcd\u0b9a\u0bc8|\u0c2d\u0c3e\u0c30\u0c24\u0c4d|\u0dbd\u0d82\u0d9a\u0dcf|\u0e44\u0e17\u0e22|\u30c6\u30b9\u30c8|\u4e2d\u56fd|\u4e2d\u570b|\u53f0\u6e7e|\u53f0\u7063|\u65b0\u52a0\u5761|\u6d4b\u8bd5|\u6e2c\u8a66|\u9999\u6e2f|\ud14c\uc2a4\ud2b8|\ud55c\uad6d|xn\\-\\-0zwm56d|xn\\-\\-11b5bs3a9aj6g|xn\\-\\-3e0b707e|xn\\-\\-45brj9c|xn\\-\\-80akhbyknj4f|xn\\-\\-90a3ac|xn\\-\\-9t4b11yi5a|xn\\-\\-clchc0ea0b2g2a9gcd|xn\\-\\-deba0ad|xn\\-\\-fiqs8s|xn\\-\\-fiqz9s|xn\\-\\-fpcrj9c3d|xn\\-\\-fzc2c9e2c|xn\\-\\-g6w251d|xn\\-\\-gecrj9c|xn\\-\\-h2brj9c|xn\\-\\-hgbk6aj7f53bba|xn\\-\\-hlcj6aya9esc7a|xn\\-\\-j6w193g|xn\\-\\-jxalpdlp|xn\\-\\-kgbechtv|xn\\-\\-kprw13d|xn\\-\\-kpry57d|xn\\-\\-lgbbat1ad8j|xn\\-\\-mgbaam7a8h|xn\\-\\-mgbayh7gpa|xn\\-\\-mgbbh1a71e|xn\\-\\-mgbc0a9azcg|xn\\-\\-mgberp4a5d4ar|xn\\-\\-o3cw4h|xn\\-\\-ogbpf8fl|xn\\-\\-p1ai|xn\\-\\-pgbs0dh|xn\\-\\-s9brj9c|xn\\-\\-wgbh1c|xn\\-\\-wgbl6a|xn\\-\\-xkc2al3hye2a|xn\\-\\-xkc2dl3a5ee0h|xn\\-\\-yfro4i67o|xn\\-\\-ygbi2ammx|xn\\-\\-zckzah|xxx)"
					+ "|y[et]"
					+ "|z[amw]))";

	private static final Matcher WEB_URL_MATCHER = Pattern.compile(
			"((?:(http|https|Http|Https|rtsp|Rtsp):\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)"
					+ "\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_"
					+ "\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?"
					+ "((?:(?:[" + GOOD_IRI_CHAR + "][" + GOOD_IRI_CHAR + "\\-]{0,64}\\.)+"   // named host
					+ TOP_LEVEL_DOMAIN_STR_FOR_WEB_URL
					+ "|(?:(?:25[0-5]|2[0-4]" // or ip address
					+ "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(?:25[0-5]|2[0-4][0-9]"
					+ "|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(?:25[0-5]|2[0-4][0-9]|[0-1]"
					+ "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(?:25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
					+ "|[1-9][0-9]|[0-9])))"
					+ "(?:\\:\\d{1,5})?)" // plus option port number
					+ "(\\/(?:(?:[" + GOOD_IRI_CHAR + "\\;\\/\\?\\:\\@\\&\\=\\#\\~"  // plus option query params
					+ "\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?"
					+ "(?:\\b|$)"
	)
			.matcher(""); // and finally, a word boundary or end of input.  This is to stop foo.sure from matching as foo.su
	/**
	 * Source code copied from Android SDK [END] ===================================================
	 */

	/**
	 * Defines an annotation for determining set of allowed content type flags for
	 * {@link #onLoadContent(String, int)} method.
	 */
	@Retention(RetentionPolicy.SOURCE)
	@IntDef({CONTENT_EMPTY, CONTENT_URL, CONTENT_HTML, CONTENT_FILE})
	public @interface ContentType {
	}

	/**
	 * Flag indicating no content to load.
	 */
	protected static final int CONTENT_EMPTY = 0x00;

	/**
	 * Flag indicating that {@link #mContent} should be loaded as HTML.
	 */
	protected static final int CONTENT_HTML = 0x01;

	/**
	 * Flag indicating that {@link #mContent} should be loaded as URL.
	 */
	protected static final int CONTENT_URL = 0x02;

	/**
	 * Flag indicating that {@link #mContent} should be loaded as FILE.
	 */
	protected static final int CONTENT_FILE = 0x03;

	/**
	 * Content data encoding.
	 */
	private static final String DATA_ENCODING = "UTF-8";

	/**
	 * Content data mime type.
	 */
	private static final String DATA_MIME_TYPE = "text/html";

	/**
	 * Private flag indicating whether to enable java script or not.
	 */
	private static final int PFLAG_JAVA_SCRIPT_ENABLED = 0x00000001;

	/**
	 * Private flag indicating if the current content was changed or not.
	 */
	private static final int PFLAG_CONTENT_CHANGED = 0x00000001 << 1;

	/**
	 * Private flag indicating whether this fragment is ready to load the current content or not.
	 */
	private static final int PFLAG_READY_TO_LOAD_CONTENT = 0x00000001 << 2;

	/**
	 * The maximum length of the substring of the current content to log with log cat output.
	 */
	private static final int LOG_CONTENT_MAX_LENGTH = 256;

	/**
	 * Static members ==============================================================================
	 */

	/**
	 * Matcher for the {@code file://PATH_TO_FILE} pattern.
	 */
	private static final Matcher FILE_URL_MATCHER = Pattern.compile("file://(.*)").matcher("");

	/**
	 * Members =====================================================================================
	 */

	/**
	 * Web view to manage HTML or URL content.
	 */
	private WebView mWebView;

	/**
	 * Content to load into the web view.
	 */
	private String mContent;

	/**
	 * Type of the current content.
	 */
	private int mContentType = CONTENT_EMPTY;

	/**
	 * Content loading listener.
	 */
	private OnWebContentLoadingListener mContentLoadingListener;

	/**
	 * Stores all private flags for this fragment.
	 */
	private int mPrivateFlags;

	/**
	 * Constructors ================================================================================
	 */

	/**
	 * Methods =====================================================================================
	 */

	/**
	 * Checks whether the given <var>url</var> is valid and can be loaded into web view.
	 *
	 * @param url Url to check.
	 * @return {@code True} if url matches valid web URL format, {@code false} otherwise.
	 */
	public static boolean isValidWebUrl(@Nullable String url) {
		return WEB_URL_MATCHER.reset(url).matches();
	}

	/**
	 * Creates a new instance of WebFragment with no initial content to load.
	 *
	 * @return New instance of WebFragment.
	 */
	@NonNull
	public static WebFragment newInstance() {
		return new WebFragment();
	}

	/**
	 * Creates a new instance of WebFragment with the given options.
	 *
	 * @param options Options to manage WebFragment.
	 * @return New instance of WebFragment.
	 */
	@NonNull
	public static WebFragment newInstance(@NonNull WebOptions options) {
		final WebFragment fragment = new WebFragment();
		final Bundle args = new Bundle();
		args.putInt(BUNDLE_PRIVATE_FLAGS, options.javascriptEnabled ? PFLAG_JAVA_SCRIPT_ENABLED : 0);
		args.putString(BUNDLE_WEB_VIEW_CONTENT, options.content);
		fragment.setArguments(args);
		return fragment;
	}

	/**
	 */
	@NonNull
	@Override
	protected WebFragmentAnnotationHandler getAnnotationHandler() {
		FragmentAnnotations.checkIfEnabledOrThrow();
		return (WebFragmentAnnotationHandler) mAnnotationHandler;
	}

	/**
	 */
	@Override
	WebFragmentAnnotationHandler onCreateAnnotationHandler() {
		return WebAnnotationHandlers.obtainWebFragmentHandler(getClass());
	}

	/**
	 * Updates the current private flags.
	 *
	 * @param flag Value of the desired flag to add/remove to/from the current private flags.
	 * @param add  Boolean flag indicating whether to add or remove the specified <var>flag</var>.
	 * @see #hasPrivateFlag(int)
	 */
	private void updatePrivateFlags(int flag, boolean add) {
		if (add) this.mPrivateFlags |= flag;
		else this.mPrivateFlags &= ~flag;
	}

	/**
	 * Returns a boolean flag indicating whether the specified <var>flag</var> is contained within
	 * the current private flags or not.
	 *
	 * @param flag Value of the flag to check.
	 * @return {@code True} if the requested flag is contained, {@code false} otherwise.
	 * @see #updatePrivateFlags(int, boolean)
	 */
	private boolean hasPrivateFlag(int flag) {
		return (mPrivateFlags & flag) != 0;
	}

	/**
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (mAnnotationHandler != null) {
			final WebFragmentAnnotationHandler annotationHandler = (WebFragmentAnnotationHandler) mAnnotationHandler;
			final int contentResId = annotationHandler.getWebContentResId(-1);
			if (contentResId != -1) {
				this.mContent = getString(contentResId);
			} else {
				this.mContent = annotationHandler.getWebContent(null);
			}
			this.updatePrivateFlags(PFLAG_CONTENT_CHANGED, true);
		}

		final Bundle args = getArguments();
		if (savedInstanceState == null && args != null) {
			this.mPrivateFlags = args.getInt(BUNDLE_PRIVATE_FLAGS);
			this.mContent = args.getString(BUNDLE_WEB_VIEW_CONTENT);
			this.updatePrivateFlags(PFLAG_CONTENT_CHANGED, true);
		}
	}

	/**
	 * Registers a callback to be invoked when loading process of the current content into web view
	 * starts or finishes.
	 *
	 * @param listener The desired listener callback. May be {@code null} to clear the current one.
	 */
	public void setOnWebContentLoadingListener(@Nullable OnWebContentLoadingListener listener) {
		this.mContentLoadingListener = listener;
	}

	/**
	 * Called to notify, that loading process of the specified <var>webUrl</var> just started.
	 * <p>
	 * By default this will dispatch {@link OnWebContentLoadingListener#onLoadingStarted(String)}
	 * callback to the current OnWebContentLoadingListener listener.
	 *
	 * @param webUrl Web url which is currently being loaded into the current web view.
	 */
	protected void notifyLoadingStarted(@NonNull String webUrl) {
		if (mContentLoadingListener != null) mContentLoadingListener.onLoadingStarted(webUrl);
	}

	/**
	 * Called to notify, that loading process of the specified <var>webUrl</var> was finished.
	 * <p>
	 * By default, this will dispatch {@link OnWebContentLoadingListener#onLoadingFinished(String)}
	 * callback to the current OnWebContentLoadingListener listener.
	 *
	 * @param webUrl Web url which was currently loaded into the current web view.
	 */
	protected void notifyLoadingFinished(@NonNull String webUrl) {
		if (mContentLoadingListener != null) mContentLoadingListener.onLoadingFinished(webUrl);
	}

	/**
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.mWebView = new WebView(inflater.getContext());
		mWebView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		// Set custom WebViewClient and WebChromeClient.
		final WebViewClient client = onCreateWebViewClient();
		if (client != null) {
			mWebView.setWebViewClient(client);
		}
		final WebChromeClient chromeClient = onCreateWebChromeClient();
		if (chromeClient != null) {
			mWebView.setWebChromeClient(chromeClient);
		}
		return mWebView;
	}

	/**
	 * Invoked during web view's initialization process. You can create here your custom implementation
	 * of WebViewClient to manage specific callbacks for such a client.
	 *
	 * @return Default web view client.
	 * @see #onCreateWebChromeClient()
	 */
	@Nullable
	protected WebViewClient onCreateWebViewClient() {
		return new WebViewClient() {

			/**
			 */
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				notifyLoadingFinished(url);
			}

			/**
			 */
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				notifyLoadingStarted(url);
			}
		};
	}

	/**
	 * Invoked during web view's initialization process. You can create here your custom implementation
	 * of WebChromeClient to manage specific callbacks for such a client.
	 *
	 * @return Default web chrome client.
	 * @see #onCreateWebViewClient()
	 */
	@Nullable
	protected WebChromeClient onCreateWebChromeClient() {
		return new WebChromeClient();
	}

	/**
	 */
	@Override
	@SuppressLint("SetJavaScriptEnabled")
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (savedInstanceState != null) {
			this.mPrivateFlags = savedInstanceState.getInt(BUNDLE_PRIVATE_FLAGS);
			this.mContent = savedInstanceState.getString(BUNDLE_WEB_VIEW_CONTENT);
			this.updatePrivateFlags(PFLAG_CONTENT_CHANGED, true);
		}

		if (mWebView != null) {
			mWebView.getSettings().setJavaScriptEnabled(hasPrivateFlag(PFLAG_JAVA_SCRIPT_ENABLED));
		}
	}

	/**
	 * Returns the instance of WebView of this fragment instance.
	 *
	 * @return WebView of this web fragment instance.
	 */
	@Nullable
	public WebView getWebView() {
		return mWebView;
	}

	/**
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.updatePrivateFlags(PFLAG_READY_TO_LOAD_CONTENT, true);
		this.resolveContentType();
		// Restore web view state.
		if (savedInstanceState != null && mWebView != null && mContentType != CONTENT_EMPTY && mContentType != CONTENT_HTML) {
			mWebView.restoreState(savedInstanceState);
		}
		// Load content.
		else {
			onLoadContent(mContent, mContentType);
		}
	}

	/**
	 * Loads the given content into the WebView of this web fragment instance.
	 *
	 * @param content Content to load. May be a raw <b>HTML</b>, web <b>URL</b> or path to a <b>FILE</b>
	 *                with HTML content.
	 * @return {@code True} if content was loaded, {@code false} if it was prepared for loading and
	 * will be loaded in the feature when WebView is ready.
	 * @see #getContent()
	 */
	public boolean loadContent(@Nullable String content) {
		this.mContent = content;
		this.updatePrivateFlags(PFLAG_CONTENT_CHANGED, true);
		if ((mPrivateFlags & PFLAG_READY_TO_LOAD_CONTENT) != 0) {
			onLoadContent(mContent, resolveContentType());
			return true;
		}
		return false;
	}

	/**
	 * Runs resolving process of the current content.
	 *
	 * @return One of the flags {@link #CONTENT_EMPTY}, {@link #CONTENT_HTML}, {@link #CONTENT_URL}
	 * or {@link #CONTENT_FILE}.
	 */
	@ContentType
	private int resolveContentType() {
		if (hasPrivateFlag(PFLAG_CONTENT_CHANGED)) {
			if (!TextUtils.isEmpty(mContent)) {
				if (WEB_URL_MATCHER.reset(mContent).matches()) {
					this.mContentType = CONTENT_URL;
				} else if (FILE_URL_MATCHER.reset(mContent).matches()) {
					this.mContentType = CONTENT_FILE;
				} else {
					this.mContentType = CONTENT_HTML;
				}
			} else {
				this.mContentType = CONTENT_EMPTY;
			}
		}
		return mContentType;
	}

	/**
	 * Invoked whenever {@link #loadContent(String)} is called and this fragment is ready (READY means
	 * after {@link #onActivityCreated(Bundle)} was called) to load that specific content
	 * or from {@link #onActivityCreated(Bundle)} when this fragment is being first time
	 * created.
	 *
	 * @param content Content to load. This can be a raw HTML, web URL or a path to FILE.
	 * @param type    A type of the specified <var>content</var>. One of flags {@link #CONTENT_EMPTY},
	 *                {@link #CONTENT_HTML}, {@link #CONTENT_URL} or {@link #CONTENT_FILE}.
	 */
	protected void onLoadContent(@Nullable String content, @ContentType int type) {
		if (mWebView != null) {
			if (FragmentsConfig.LOG_ENABLED && !TextUtils.isEmpty(content)) {
				if (content.length() > LOG_CONTENT_MAX_LENGTH) {
					Log.v(TAG, "Loading content('" + content.substring(0, LOG_CONTENT_MAX_LENGTH) + "') into web view.");
				} else {
					Log.v(TAG, "Loading content('" + content + "') into web view.");
				}
			}
			switch (type) {
				case CONTENT_EMPTY:
					mWebView.loadDataWithBaseURL("", "", DATA_MIME_TYPE, DATA_ENCODING, "");
					break;
				case CONTENT_URL:
				case CONTENT_FILE:
					mWebView.loadUrl(content);
					break;
				case CONTENT_HTML:
					mWebView.loadDataWithBaseURL("", content, DATA_MIME_TYPE, DATA_ENCODING, "");
					break;
			}
		}
	}

	/**
	 * Returns the current content that is loaded or prepared to load into the web view of this
	 * web fragment instance.
	 *
	 * @return Current content. This can be a raw HTML or web URL or a FILE path.
	 * @see #loadContent(String)
	 */
	@Nullable
	public String getContent() {
		return mContent;
	}

	/**
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// Save web view state.
		if (mWebView != null) {
			mWebView.saveState(outState);
		}
		outState.putInt(BUNDLE_PRIVATE_FLAGS, mPrivateFlags);
		outState.putString(BUNDLE_WEB_VIEW_CONTENT, mContent);
	}

	/**
	 */
	@Override
	protected boolean onBackPress() {
		if (mWebView != null && mWebView.canGoBack()) {
			mWebView.goBack();
			return true;
		}
		return false;
	}

	/**
	 * Inner classes ===============================================================================
	 */

	/**
	 * Simple class specifying options for {@link WebFragment} like {@link #content(String)}
	 * {@link #javaScriptEnabled(boolean)}.
	 *
	 * @author Martin Albedinsky
	 */
	public static class WebOptions {

		/**
		 * Content to load into web view.
		 */
		private String content = "";

		/**
		 * Flag indicating whether Java-Script should be enabled or not.
		 */
		private boolean javascriptEnabled = true;

		/**
		 * Sets a content to load into web view.
		 *
		 * @param content Content to load. This can be a raw <b>HTML</b>, web <b>URL</b> or path to
		 *                a <b>FILE</b> with HTML content.
		 * @return These options to allow methods chaining.
		 */
		public WebOptions content(@NonNull String content) {
			this.content = content;
			return this;
		}

		/**
		 * Sets a boolean flag indicating whether to enable Java-Script or not.
		 *
		 * @param enabled {@code True} to enable, {@code false} otherwise.
		 * @return These options to allow methods chaining.
		 */
		public WebOptions javaScriptEnabled(boolean enabled) {
			this.javascriptEnabled = enabled;
			return this;
		}
	}
}
