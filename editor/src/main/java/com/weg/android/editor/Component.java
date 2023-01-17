package com.weg.android.editor;

import static android.os.Build.VERSION;
import static android.os.Build.VERSION_CODES;
import static android.view.MotionEvent.ACTION_MASK;
import static android.view.MotionEvent.ACTION_UP;
import static android.view.View.MeasureSpec.AT_MOST;
import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.getMode;
import static android.view.View.MeasureSpec.getSize;
import static java.lang.Math.max;
import static java.lang.Math.min;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.method.CharacterPickerDialog;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.OverScroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class Component extends View implements ViewTreeObserver.OnPreDrawListener {
	public Component(Context context) {
		super(context);
		init();
	}
	
	public Component(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public Component(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private OverScroller overScroller;
	private GestureDetector gestureDetector;
	private ScaleGestureDetector scaleGestureDetector;
	private boolean preDrawRegistered;
	private final Rect clipRect = new Rect();
	private InputMethodManager inputMethodManager;
	
	private void init() {
		setFocusable(true);
		setFocusableInTouchMode(true);
		setClickable(true);
		setLongClickable(true);
		setHapticFeedbackEnabled(true);
		setSaveEnabled(true);
		setScrollContainer(true);
		setOverScrollMode(OVER_SCROLL_ALWAYS);
		setScrollbarFadingEnabled(true);
		setScrollBarStyle(SCROLLBARS_INSIDE_OVERLAY);
		setHorizontalScrollBarEnabled(true);
		setHorizontalFadingEdgeEnabled(true);
		setVerticalScrollBarEnabled(true);
		setVerticalFadingEdgeEnabled(true);
		setVerticalScrollbarPosition(SCROLLBAR_POSITION_RIGHT);
		
		overScroller = new OverScroller(getContext());
		TouchDelegate touchDelegate = new TouchDelegate(this);
		gestureDetector = new GestureDetector(getContext(), touchDelegate);
		gestureDetector.setOnDoubleTapListener(touchDelegate);
		scaleGestureDetector = new ScaleGestureDetector(getContext(), touchDelegate);
		inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
	}
	
	final public void toggleSoftKeyboard(boolean shouldShowSoftKeyboard) {
		if (shouldShowSoftKeyboard)
			showSoftKeyboard();
		else
			hideSoftKeyboard();
	}
	
	final public void showSoftKeyboard() {
		inputMethodManager.showSoftInput(this, 0);
	}
	
	final public void hideSoftKeyboard() {
		inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
	}
	
	final public boolean isSoftKeyboardShowing() {
		return inputMethodManager.isActive();
	}
	
	final public boolean isInViewSoftKeyboardShowing() {
		return inputMethodManager.isActive(this);
	}
	
	final protected InputMethodManager getInputMethodManager() {
		return inputMethodManager;
	}
	
	@Override
	public boolean onCheckIsTextEditor() {
		return true;
	}
	
	@RequiresApi(api = VERSION_CODES.KITKAT)
	final protected void setQuickScaleEnabled(boolean quickScaleEnabled) {
		scaleGestureDetector.setQuickScaleEnabled(quickScaleEnabled);
	}
	
	@RequiresApi(api = VERSION_CODES.KITKAT)
	final protected boolean isQuickScaleEnabled() {
		return scaleGestureDetector.isQuickScaleEnabled();
	}
	
	@RequiresApi(api = VERSION_CODES.M)
	final protected void setStylusScaleEnabled(boolean stylusScaleEnabled) {
		scaleGestureDetector.setStylusScaleEnabled(stylusScaleEnabled);
	}
	
	@RequiresApi(api = VERSION_CODES.M)
	final protected boolean isStylusScaleEnabled() {
		return scaleGestureDetector.isStylusScaleEnabled();
	}
	
	final protected boolean isInScaling() {
		return scaleGestureDetector.isInProgress();
	}
	
	final protected void setGestureLongPressEnabled(boolean isLongPressEnabled) {
		gestureDetector.setIsLongpressEnabled(isLongPressEnabled);
	}
	
	final protected boolean isGestureLongPressEnabled() {
		return gestureDetector.isLongpressEnabled();
	}
	
	@RequiresApi(api = VERSION_CODES.M)
	final protected void setGestureContextClickListener(GestureDetector.OnContextClickListener contextListener) {
		gestureDetector.setContextClickListener(contextListener);
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean handled = false;
		if (isFocused()) {
			handled = gestureDetector.onTouchEvent(event);
			scaleGestureDetector.onTouchEvent(event);
		} else {
			if ((event.getAction() & ACTION_MASK) == ACTION_UP && inView(event.getX(), event.getY())) {
				requestFocus();
			}
		}
		if (handled) {
			return true;
		}
		return super.onTouchEvent(event);
	}
	
	private boolean inView(float x, float y) {
		return (x >= 0 && x < getWidth() &&
				y >= 0 && y < getHeight());
	}
	
	@Override
	public boolean onGenericMotionEvent(MotionEvent event) {
		if (VERSION.SDK_INT >= VERSION_CODES.M) {
			return gestureDetector.onGenericMotionEvent(event);
		}
		return false;
	}
	
	protected boolean onSingleTapConfirmed(MotionEvent e) {
		return false;
	}
	
	protected boolean onDoubleTap(MotionEvent e) {
		return false;
	}
	
	protected boolean onDoubleTapEvent(MotionEvent e) {
		return false;
	}
	
	protected boolean onDown(MotionEvent e) {
		return false;
	}
	
	protected void onShowPress(MotionEvent e) {
	
	}
	
	protected boolean onSingleTapUp(MotionEvent e) {
		return false;
	}
	
	protected boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return false;
	}
	
	protected void onLongPress(MotionEvent e) {
	
	}
	
	protected boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		return false;
	}
	
	protected boolean onScale(ScaleGestureDetector detector) {
		return false;
	}
	
	protected boolean onScaleBegin(ScaleGestureDetector detector) {
		return false;
	}
	
	protected void onScaleEnd(ScaleGestureDetector detector) {
	
	}
	
	public void setOverScroller(@Nullable OverScroller overScroller) {
		this.overScroller = overScroller;
	}
	
	@Nullable
	public OverScroller getOverScroller() {
		return overScroller;
	}
	
	public int getComponentWidth() {
		return getWidth() - getPaddingLeft() - getPaddingRight();
	}
	
	public int getComponentHeight() {
		return getHeight() - getPaddingTop() - getPaddingBottom();
	}
	
	@Override
	final protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = getMode(widthMeasureSpec);
		int heightMode = getMode(heightMeasureSpec);
		int widthSize = getSize(widthMeasureSpec);
		int heightSize = getSize(heightMeasureSpec);
		int width = widthSize;
		int height = heightSize;
		if (widthMode != EXACTLY) {
			width = max(width, getSuggestedMeasuredWidth());
			if (widthMode == AT_MOST)
				width = min(widthSize, width);
		}
		
		if (heightMode != EXACTLY) {
			height = max(height, getSuggestedMeasuredHeight());
			if (heightMode == AT_MOST)
				height = min(height, heightSize);
		}
		
		if (!preDrawRegistered) {
			preDrawRegistered = true;
			getViewTreeObserver().addOnPreDrawListener(this);
		}
		setMeasuredDimension(width, height);
		onMeasured(width,height);
	}
	
	protected void onMeasured(int width,int height){
	
	}
	protected int getSuggestedMeasuredWidth() {
		return getSuggestedMinimumHeight();
	}
	
	protected int getSuggestedMeasuredHeight() {
		return getSuggestedMinimumHeight();
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (!preDrawRegistered) {
			preDrawRegistered = true;
			getViewTreeObserver().addOnPreDrawListener(this);
		}
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (preDrawRegistered) {
			preDrawRegistered = false;
			getViewTreeObserver().removeOnPreDrawListener(this);
		}
	}
	
	@Override
	final public boolean onPreDraw() {
		onPreRedraw();
		preDrawRegistered = false;
		getViewTreeObserver().removeOnPreDrawListener(this);
		return true;
	}
	
	protected void onPreRedraw() {
	
	}
	
	@Override
	final protected void onDraw(Canvas canvas) {
		canvas.save();
		canvas.clipRect(
				getScrollX() + getPaddingLeft(),
				getScrollY() + getPaddingTop(),
				getScrollX() + getWidth() - getPaddingRight(),
				getScrollY() + getHeight() - getPaddingBottom());
		canvas.translate(getPaddingLeft(), getPaddingTop());
		canvas.getClipBounds(clipRect);
		onRedraw(canvas, clipRect);
		canvas.restore();
	}
	
	protected void onRedraw(Canvas canvas, Rect bounds) {
	
	}
	
	@NonNull
	protected static String toTitleCase(String src) {
		return Character.toUpperCase(src.charAt(0)) + src.substring(1);
	}
	protected final boolean showCharacterPicker(char c, boolean insert) {
		SpannableStringBuilder dummyString = new SpannableStringBuilder();
		Selection.setSelection(dummyString, 0);
		String set = PICKER_SETS.get(c);
		if (set == null) {
			return false;
		}
		
		Dialog dialog = new CharacterPickerDialog(getContext(), this, dummyString, set, insert);
		dialog.setOnDismissListener(dialog1 -> onCharacterPickerDialogDismiss());
		dialog.show();
		return true;
	}
	
	protected void onCharacterPickerDialogDismiss() {
	
	}
	
	/**
	 * form {@link android.text.method.QwertyKeyListener}
	 */
	private static final SparseArray<String> PICKER_SETS = new SparseArray<>();
	
	static {
		PICKER_SETS.put('A', "\u00C0\u00C1\u00C2\u00C4\u00C6\u00C3\u00C5\u0104\u0100");
		PICKER_SETS.put('C', "\u00C7\u0106\u010C");
		PICKER_SETS.put('D', "\u010E");
		PICKER_SETS.put('E', "\u00C8\u00C9\u00CA\u00CB\u0118\u011A\u0112");
		PICKER_SETS.put('G', "\u011E");
		PICKER_SETS.put('L', "\u0141");
		PICKER_SETS.put('I', "\u00CC\u00CD\u00CE\u00CF\u012A\u0130");
		PICKER_SETS.put('N', "\u00D1\u0143\u0147");
		PICKER_SETS.put('O', "\u00D8\u0152\u00D5\u00D2\u00D3\u00D4\u00D6\u014C");
		PICKER_SETS.put('R', "\u0158");
		PICKER_SETS.put('S', "\u015A\u0160\u015E");
		PICKER_SETS.put('T', "\u0164");
		PICKER_SETS.put('U', "\u00D9\u00DA\u00DB\u00DC\u016E\u016A");
		PICKER_SETS.put('Y', "\u00DD\u0178");
		PICKER_SETS.put('Z', "\u0179\u017B\u017D");
		PICKER_SETS.put('a', "\u00E0\u00E1\u00E2\u00E4\u00E6\u00E3\u00E5\u0105\u0101");
		PICKER_SETS.put('c', "\u00E7\u0107\u010D");
		PICKER_SETS.put('d', "\u010F");
		PICKER_SETS.put('e', "\u00E8\u00E9\u00EA\u00EB\u0119\u011B\u0113");
		PICKER_SETS.put('g', "\u011F");
		PICKER_SETS.put('i', "\u00EC\u00ED\u00EE\u00EF\u012B\u0131");
		PICKER_SETS.put('l', "\u0142");
		PICKER_SETS.put('n', "\u00F1\u0144\u0148");
		PICKER_SETS.put('o', "\u00F8\u0153\u00F5\u00F2\u00F3\u00F4\u00F6\u014D");
		PICKER_SETS.put('r', "\u0159");
		PICKER_SETS.put('s', "\u00A7\u00DF\u015B\u0161\u015F");
		PICKER_SETS.put('t', "\u0165");
		PICKER_SETS.put('u', "\u00F9\u00FA\u00FB\u00FC\u016F\u016B");
		PICKER_SETS.put('y', "\u00FD\u00FF");
		PICKER_SETS.put('z', "\u017A\u017C\u017E");
		PICKER_SETS.put(KeyCharacterMap.PICKER_DIALOG_INPUT,
				"\u2026\u00A5\u2022\u00AE\u00A9\u00B1[]{}\\|");
		PICKER_SETS.put('/', "\\");
		
		// From packages/inputmethods/LatinIME/res/xml/kbd_symbols.xml
		
		PICKER_SETS.put('1', "\u00b9\u00bd\u2153\u00bc\u215b");
		PICKER_SETS.put('2', "\u00b2\u2154");
		PICKER_SETS.put('3', "\u00b3\u00be\u215c");
		PICKER_SETS.put('4', "\u2074");
		PICKER_SETS.put('5', "\u215d");
		PICKER_SETS.put('7', "\u215e");
		PICKER_SETS.put('0', "\u207f\u2205");
		PICKER_SETS.put('$', "\u00a2\u00a3\u20ac\u00a5\u20a3\u20a4\u20b1");
		PICKER_SETS.put('%', "\u2030");
		PICKER_SETS.put('*', "\u2020\u2021");
		PICKER_SETS.put('-', "\u2013\u2014");
		PICKER_SETS.put('+', "\u00b1");
		PICKER_SETS.put('(', "[{<");
		PICKER_SETS.put(')', "]}>");
		PICKER_SETS.put('!', "\u00a1");
		PICKER_SETS.put('"', "\u201c\u201d\u00ab\u00bb\u02dd");
		PICKER_SETS.put('?', "\u00bf");
		PICKER_SETS.put(',', "\u201a\u201e");
		
		// From packages/inputmethods/LatinIME/res/xml/kbd_symbols_shift.xml
		
		PICKER_SETS.put('=', "\u2260\u2248\u221e");
		PICKER_SETS.put('<', "\u2264\u00ab\u2039");
		PICKER_SETS.put('>', "\u2265\u00bb\u203a");
	}
	
	;
}
