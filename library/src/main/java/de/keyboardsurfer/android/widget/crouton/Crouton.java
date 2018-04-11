/*
 * Copyright 2012 - 2014 Benjamin Weiss
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.keyboardsurfer.android.widget.crouton;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public final class Crouton {

    private static final String NULL_PARAMETERS_ARE_NOT_ACCEPTED = "Null parameters are not accepted";
    private static final int IMAGE_ID = 0x100;
    private static final int TEXT_ID = 0x101;

    private final CharSequence text;
    private final Style style;
    private Configuration configuration = null;
    private final View customView;

    private OnClickListener onClickListener;

    private Activity activity;
    private ViewGroup viewGroup;
    private FrameLayout croutonView;
    private Animation inAnimation;
    private Animation outAnimation;
    private LifecycleCallback lifecycleCallback = null;

    private Crouton(Activity activity, CharSequence text, Style style) {

        if ((activity == null) || (text == null) || (style == null)) {
            throw new IllegalArgumentException(NULL_PARAMETERS_ARE_NOT_ACCEPTED);
        }
        this.activity = activity;
        this.viewGroup = null;
        this.text = text;
        this.style = style;
        this.customView = null;
    }

    private Crouton(Activity activity, CharSequence text, Style style, ViewGroup viewGroup) {
        if ((activity == null) || (text == null) || (style == null)) {
            throw new IllegalArgumentException(NULL_PARAMETERS_ARE_NOT_ACCEPTED);
        }

        this.activity = activity;
        this.text = text;
        this.style = style;
        this.viewGroup = viewGroup;
        this.customView = null;
    }

    private Crouton(Activity activity, View customView) {
        if ((activity == null) || (customView == null)) {
            throw new IllegalArgumentException(NULL_PARAMETERS_ARE_NOT_ACCEPTED);
        }

        this.activity = activity;
        this.viewGroup = null;
        this.customView = customView;
        this.style = new Style.Builder().build();
        this.text = null;
    }

    private Crouton(Activity activity, View customView, ViewGroup viewGroup) {
        this(activity, customView, viewGroup, Configuration.DEFAULT);
    }

    private Crouton(final Activity activity, final View customView, final ViewGroup viewGroup, final Configuration configuration) {
        if ((activity == null) || (customView == null)) {
            throw new IllegalArgumentException(NULL_PARAMETERS_ARE_NOT_ACCEPTED);
        }

        this.activity = activity;
        this.customView = customView;
        this.viewGroup = viewGroup;
        this.style = new Style.Builder().build();
        this.text = null;
        this.configuration = configuration;
    }

    /**
     * Creates a {@link Crouton} with provided text and style for a given
     * activity.
     *
     * @param activity The {@link Activity} that the {@link Crouton} should be attached
     *                 to.
     * @param text     The text you want to display.
     * @param style    The style that this {@link Crouton} should be created with.
     * @return The created {@link Crouton}.
     */
    public static Crouton makeText(Activity activity, CharSequence text, Style style) {
        return new Crouton(activity, text, style);
    }

    /**
     * Creates a {@link Crouton} with provided text and style for a given
     * activity.
     *
     * @param activity  The {@link Activity} that represents the context in which the Crouton should exist.
     * @param text      The text you want to display.
     * @param style     The style that this {@link Crouton} should be created with.
     * @param viewGroup The {@link ViewGroup} that this {@link Crouton} should be added to.
     * @return The created {@link Crouton}.
     */
    public static Crouton makeText(Activity activity, CharSequence text, Style style, ViewGroup viewGroup) {
        return new Crouton(activity, text, style, viewGroup);
    }

    public static Crouton makeText(Activity activity, CharSequence text, Style style, int viewGroupResId) {
        return new Crouton(activity, text, style, (ViewGroup) activity.findViewById(viewGroupResId));
    }

    public static Crouton makeText(Activity activity, int textResourceId, Style style) {
        return makeText(activity, activity.getString(textResourceId), style);
    }

    public static Crouton makeText(Activity activity, int textResourceId, Style style, ViewGroup viewGroup) {
        return makeText(activity, activity.getString(textResourceId), style, viewGroup);
    }

    public static Crouton makeText(Activity activity, int textResourceId, Style style, int viewGroupResId) {
        return makeText(activity, activity.getString(textResourceId), style,
                (ViewGroup) activity.findViewById(viewGroupResId));
    }

    public static Crouton make(Activity activity, View customView) {
        return new Crouton(activity, customView);
    }

    public static Crouton make(Activity activity, View customView, ViewGroup viewGroup) {
        return new Crouton(activity, customView, viewGroup);
    }

    public static Crouton make(Activity activity, View customView, int viewGroupResId) {
        return new Crouton(activity, customView, (ViewGroup) activity.findViewById(viewGroupResId));
    }

    public static Crouton make(Activity activity, View customView, int viewGroupResId, final Configuration configuration) {
        return new Crouton(activity, customView, (ViewGroup) activity.findViewById(viewGroupResId), configuration);
    }

    public static Crouton showText(Activity activity, CharSequence text, Style style) {
        return makeText(activity, text, style).show();
    }

    public static Crouton showText(Activity activity, CharSequence text, Style style, ViewGroup viewGroup) {
        return makeText(activity, text, style, viewGroup).show();
    }

    public static Crouton showText(Activity activity, CharSequence text, Style style, int viewGroupResId) {
        return makeText(activity, text, style, (ViewGroup) activity.findViewById(viewGroupResId)).show();
    }

    public static Crouton showText(Activity activity, CharSequence text, Style style, int viewGroupResId, final Configuration configuration) {
        return makeText(activity, text, style, (ViewGroup) activity.findViewById(viewGroupResId)).setConfiguration(configuration)
                .show();
    }


    public static Crouton show(Activity activity, View customView) {
        return make(activity, customView).show();
    }

    public static Crouton show(Activity activity, View customView, ViewGroup viewGroup) {
        return make(activity, customView, viewGroup).show();
    }

    public static Crouton show(Activity activity, View customView, int viewGroupResId) {
        return make(activity, customView, viewGroupResId).show();
    }

    public static Crouton showText(Activity activity, int textResourceId, Style style) {
        return showText(activity, activity.getString(textResourceId), style);
    }

    public static Crouton showText(Activity activity, int textResourceId, Style style, ViewGroup viewGroup) {
        return showText(activity, activity.getString(textResourceId), style, viewGroup);
    }

    public static Crouton showText(Activity activity, int textResourceId, Style style, int viewGroupResId) {
        return showText(activity, activity.getString(textResourceId), style, viewGroupResId);
    }

    public static void hide(Crouton crouton) {
        crouton.hide();
    }

    public static void show(Crouton crouton) {
        crouton.show();
    }

    public Crouton show() {
        showCrouton(this);
        return this;
    }

    public void hide() {
        closeCrouton(this);
    }

    public Animation getInAnimation() {

        if ((null == this.inAnimation) && (null != this.activity)) {
            if (getConfiguration().inAnimationResId > 0) {
                this.inAnimation = AnimationUtils.loadAnimation(getActivity(), getConfiguration().inAnimationResId);
            } else {
                measureCroutonView();
                this.inAnimation = DefaultAnimationsBuilder.buildDefaultSlideInDownAnimation(getView());
            }
        }
        return inAnimation;
    }

    public Animation getOutAnimation() {
        if ((null == this.outAnimation) && (null != this.activity)) {
            if (getConfiguration().outAnimationResId > 0) {
                this.outAnimation = AnimationUtils.loadAnimation(getActivity(), getConfiguration().outAnimationResId);
            } else {
                this.outAnimation = DefaultAnimationsBuilder.buildDefaultSlideOutUpAnimation(getView());
            }
        }

        return outAnimation;
    }

    public Crouton setLifecycleCallback(LifecycleCallback lifecycleCallback) {
        this.lifecycleCallback = lifecycleCallback;
        return this;
    }

    public LifecycleCallback getLifecycleCallback() {
        return lifecycleCallback;
    }

    private void onClose() {
        if (lifecycleCallback != null)
            lifecycleCallback.onClose(this);
    }

    private void onShow() {
        if (lifecycleCallback != null)
            lifecycleCallback.onShow(this);
    }

    public Crouton setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        return this;
    }

    public Crouton setConfiguration(final Configuration configuration) {
        this.configuration = configuration;
        return this;
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // You have reached the internal API of Crouton.
    // If you do not plan to develop for Crouton there is nothing of interest below here.
    //////////////////////////////////////////////////////////////////////////////////////

    public boolean isShowing() {
        return (null != activity) && (isCroutonViewNotNull() || isCustomViewNotNull());
    }

    private boolean isCroutonViewNotNull() {
        return (null != croutonView) && (null != croutonView.getParent());
    }

    private boolean isCustomViewNotNull() {
        return (null != customView) && (null != customView.getParent());
    }


    public Style getStyle() {
        return style;
    }

    public Configuration getConfiguration() {
        if (null == configuration) {
            configuration = getStyle().configuration;
        }
        return configuration;
    }

    public Activity getActivity() {
        return activity;
    }

    public ViewGroup getViewGroup() {
        return viewGroup;
    }

    public CharSequence getText() {
        return text;
    }

    //=====================

    private View getView() {
        // return the custom view if one exists
        if (null != this.customView) {
            return this.customView;
        }

        // if already setup return the view
        if (null == this.croutonView) {
            initializeCroutonView();
        }
        return croutonView;
    }

    private void measureCroutonView() {
        View view = getView();
        int widthSpec;
        if (null != viewGroup) {
            widthSpec = View.MeasureSpec.makeMeasureSpec(viewGroup.getMeasuredWidth(), View.MeasureSpec.AT_MOST);
        } else {
            widthSpec = View.MeasureSpec.makeMeasureSpec(activity.getWindow().getDecorView().getMeasuredWidth(),
                    View.MeasureSpec.AT_MOST);
        }

        view.measure(widthSpec, View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
    }

    private void initializeCroutonView() {
        Resources resources = this.activity.getResources();

        this.croutonView = initializeCroutonViewGroup(resources);

        // create content view
        RelativeLayout contentView = initializeContentView(resources);
        this.croutonView.addView(contentView);
    }

    private FrameLayout initializeCroutonViewGroup(Resources resources) {

        FrameLayout croutonView = new FrameLayout(this.activity);

        if (null != onClickListener) {
            croutonView.setOnClickListener(onClickListener);
        }

        final int height;
        if (this.style.heightDimensionResId > 0) {
            height = resources.getDimensionPixelSize(this.style.heightDimensionResId);
        } else {
            height = this.style.heightInPixels;
        }

        final int width;
        if (this.style.widthDimensionResId > 0) {
            width = resources.getDimensionPixelSize(this.style.widthDimensionResId);
        } else {
            width = this.style.widthInPixels;
        }

        croutonView.setLayoutParams(new FrameLayout.LayoutParams(width != 0 ? width : FrameLayout.LayoutParams.MATCH_PARENT, height));

        // set background
        if (this.style.backgroundColorValue != Style.NOT_SET) {
            croutonView.setBackgroundColor(this.style.backgroundColorValue);
        } else {
            croutonView.setBackgroundColor(resources.getColor(this.style.backgroundColorResourceId));
        }

        // set the background drawable if set. This will override the background
        // color.
        if (this.style.backgroundDrawableResourceId != 0) {
            Bitmap background = BitmapFactory.decodeResource(resources, this.style.backgroundDrawableResourceId);
            BitmapDrawable drawable = new BitmapDrawable(resources, background);
            if (this.style.isTileEnabled) {
                drawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            }
            croutonView.setBackgroundDrawable(drawable);
        }
        return croutonView;
    }

    private RelativeLayout initializeContentView(final Resources resources) {
        RelativeLayout contentView = new RelativeLayout(this.activity);
        contentView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));

        // set padding
        int padding = this.style.paddingInPixels;

        // if a padding dimension has been set, this will overwrite any padding
        // in pixels
        if (this.style.paddingDimensionResId > 0) {
            padding = resources.getDimensionPixelSize(this.style.paddingDimensionResId);
        }
        contentView.setPadding(padding, padding, padding, padding);

        // only setup image if one is requested
        ImageView image = null;
        if ((null != this.style.imageDrawable) || (0 != this.style.imageResId)) {
            image = initializeImageView();
            contentView.addView(image, image.getLayoutParams());
        }

        TextView text = initializeTextView(resources);

        RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if (null != image) {
            textParams.addRule(RelativeLayout.RIGHT_OF, image.getId());
        }

        if ((this.style.gravity & Gravity.CENTER) != 0) {
            textParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        } else if ((this.style.gravity & Gravity.CENTER_VERTICAL) != 0) {
            textParams.addRule(RelativeLayout.CENTER_VERTICAL);
        } else if ((this.style.gravity & Gravity.CENTER_HORIZONTAL) != 0) {
            textParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        }

        contentView.addView(text, textParams);
        return contentView;
    }

    private TextView initializeTextView(final Resources resources) {
        TextView text = new TextView(this.activity);
        text.setId(TEXT_ID);
        if (this.style.fontName != null) {
            setTextWithCustomFont(text, this.style.fontName);
        } else if (this.style.fontNameResId != 0) {
            setTextWithCustomFont(text, resources.getString(this.style.fontNameResId));
        } else {
            text.setText(this.text);
        }
        text.setTypeface(Typeface.DEFAULT_BOLD);
        text.setGravity(this.style.gravity);

        // set the text color if set
        if (this.style.textColorValue != Style.NOT_SET) {
            text.setTextColor(this.style.textColorValue);
        } else if (this.style.textColorResourceId != 0) {
            text.setTextColor(resources.getColor(this.style.textColorResourceId));
        }

        // Set the text size. If the user has set a text size and text
        // appearance, the text size in the text appearance
        // will override this.
        if (this.style.textSize != 0) {
            text.setTextSize(TypedValue.COMPLEX_UNIT_SP, this.style.textSize);
        }

        // Setup the shadow if requested
        if (this.style.textShadowColorResId != 0) {
            initializeTextViewShadow(resources, text);
        }

        // Set the text appearance
        if (this.style.textAppearanceResId != 0) {
            text.setTextAppearance(this.activity, this.style.textAppearanceResId);
        }
        return text;
    }

    private void setTextWithCustomFont(TextView text, String fontName) {
        if (this.text != null) {
            SpannableString s = new SpannableString(this.text);
            s.setSpan(new TypefaceSpan(text.getContext(), fontName), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            text.setText(s);
        }
    }

    private void initializeTextViewShadow(final Resources resources, final TextView text) {
        int textShadowColor = resources.getColor(this.style.textShadowColorResId);
        float textShadowRadius = this.style.textShadowRadius;
        float textShadowDx = this.style.textShadowDx;
        float textShadowDy = this.style.textShadowDy;
        text.setShadowLayer(textShadowRadius, textShadowDx, textShadowDy, textShadowColor);
    }

    private ImageView initializeImageView() {
        ImageView image;
        image = new ImageView(this.activity);
        image.setId(IMAGE_ID);
        image.setAdjustViewBounds(true);
        image.setScaleType(this.style.imageScaleType);

        // set the image drawable if not null
        if (null != this.style.imageDrawable) {
            image.setImageDrawable(this.style.imageDrawable);
        }

        // set the image resource if not 0. This will overwrite the drawable
        // if both are set
        if (this.style.imageResId != 0) {
            image.setImageResource(this.style.imageResId);
        }

        RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        imageParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        imageParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

        image.setLayoutParams(imageParams);

        return image;
    }

    /////////////////
    //show&close manager
    ////////////////


    public void showCrouton(final Crouton crouton) {
        // don't add if it is already showing
        if (crouton.isShowing()) {
            return;
        }

        final View croutonView = crouton.getView();

        if (null == croutonView.getParent()) {
            ViewGroup.LayoutParams params = croutonView.getLayoutParams();
            if (null == params) {
                params = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            // display Crouton in ViewGroup if it has been supplied
            if (null != crouton.getViewGroup()) {
                final ViewGroup croutonViewGroup = crouton.getViewGroup();
                if (shouldAddViewWithoutPosition(croutonViewGroup)) {
                    croutonViewGroup.addView(croutonView, params);
                } else {
                    croutonViewGroup.addView(croutonView, 0, params);
                }
            } else {
                Activity activity = crouton.getActivity();
                if (null == activity || activity.isFinishing()) {
                    return;
                }
                handleTranslucentActionBar((ViewGroup.MarginLayoutParams) params, activity);
                handleActionBarOverlay((ViewGroup.MarginLayoutParams) params, activity);
                activity.addContentView(croutonView, params);
            }
        }

        croutonView.requestLayout(); // This is needed so the animation can use the measured with/height
        ViewTreeObserver observer = croutonView.getViewTreeObserver();
        if (null != observer) {
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                @TargetApi(16)
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        croutonView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        croutonView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    crouton.onShow();
                    if (crouton.getInAnimation() != null) {
                        croutonView.startAnimation(crouton.getInAnimation());
                        announceForAccessibilityCompat(crouton.getActivity(), crouton.getText());
                    }
                }
            });
        }
    }

    protected void closeCrouton(Crouton crouton) {
        closeCrouton(crouton, true);
    }

    protected void closeCrouton(Crouton crouton, boolean withAnimation) {

        View croutonView = crouton.getView();
        ViewGroup croutonParentView = (ViewGroup) croutonView.getParent();
        if (null != croutonParentView) {
            if (withAnimation) croutonView.startAnimation(crouton.getOutAnimation());
            croutonParentView.removeView(croutonView);
            crouton.onClose();
        }
    }

    private boolean shouldAddViewWithoutPosition(ViewGroup croutonViewGroup) {
        return croutonViewGroup instanceof FrameLayout || croutonViewGroup instanceof AdapterView ||
                croutonViewGroup instanceof RelativeLayout;
    }

    @TargetApi(19)
    private void handleTranslucentActionBar(ViewGroup.MarginLayoutParams params, Activity activity) {
        // Translucent status is only available as of Android 4.4 Kit Kat.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final int flags = activity.getWindow().getAttributes().flags;
            final int translucentStatusFlag = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            if ((flags & translucentStatusFlag) == translucentStatusFlag) {
                setActionBarMargin(params, activity);
            }
        }
    }

    @TargetApi(11)
    private void handleActionBarOverlay(ViewGroup.MarginLayoutParams params, Activity activity) {
        // ActionBar overlay is only available as of Android 3.0 Honeycomb.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            final boolean flags = activity.getWindow().hasFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
            if (flags) {
                setActionBarMargin(params, activity);
            }
        }
    }

    private void setActionBarMargin(ViewGroup.MarginLayoutParams params, Activity activity) {
        final int actionBarContainerId = Resources.getSystem().getIdentifier("action_bar_container", "id", "android");
        final View actionBarContainer = activity.findViewById(actionBarContainerId);
        // The action bar is present: the app is using a Holo theme.
        if (null != actionBarContainer) {
            params.topMargin = actionBarContainer.getBottom();
        }
    }

    /**
     * Generates and dispatches an SDK-specific spoken announcement.
     * <p>
     * For backwards compatibility, we're constructing an event from scratch
     * using the appropriate event type. If your application only targets SDK
     * 16+, you can just call View.announceForAccessibility(CharSequence).
     * </p>
     * <p/>
     * note: AccessibilityManager is only available from API lvl 4.
     * <p/>
     * Adapted from https://http://eyes-free.googlecode.com/files/accessibility_codelab_demos_v2_src.zip
     * via https://github.com/coreform/android-formidable-validation
     *
     * @param context Used to get {@link AccessibilityManager}
     * @param text    The text to announce.
     */
    public static void announceForAccessibilityCompat(Context context, CharSequence text) {
        if (Build.VERSION.SDK_INT >= 4) {
            AccessibilityManager accessibilityManager = null;
            if (null != context) {
                accessibilityManager = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
            }
            if (null == accessibilityManager || !accessibilityManager.isEnabled()) {
                return;
            }

            // Prior to SDK 16, announcements could only be made through FOCUSED
            // events. Jelly Bean (SDK 16) added support for speaking text verbatim
            // using the ANNOUNCEMENT event type.
            final int eventType;
            if (Build.VERSION.SDK_INT < 16) {
                eventType = AccessibilityEvent.TYPE_VIEW_FOCUSED;
            } else {
                eventType = AccessibilityEvent.TYPE_ANNOUNCEMENT;
            }

            // Construct an accessibility event with the minimum recommended
            // attributes. An event without a class name or package may be dropped.
            final AccessibilityEvent event = AccessibilityEvent.obtain(eventType);
            event.getText().add(text);
            event.setClassName(Crouton.class.getName());
            event.setPackageName(context.getPackageName());

            // Sends the event directly through the accessibility manager. If your
            // application only targets SDK 14+, you should just call
            // getParent().requestSendAccessibilityEvent(this, event);
            accessibilityManager.sendAccessibilityEvent(event);
        }
    }

}
