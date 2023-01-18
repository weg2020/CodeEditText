package com.weg.android.editor;

import static android.graphics.PorterDuff.Mode;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static com.weg.android.editor.TextStyle.BOLD;
import static com.weg.android.editor.TextStyle.BOLD_ITALIC;
import static com.weg.android.editor.TextStyle.ITALIC;
import static com.weg.android.editor.TextStyle.NORMAL;
import static com.weg.android.editor.syntax.Highlighting.Highlight;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.System.currentTimeMillis;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.weg.android.editor.scheme.ColorSchemeLight;
import com.weg.android.editor.syntax.Highlighting;
import com.weg.android.editor.text.TextChangeListener;

import java.util.Timer;
import java.util.TimerTask;

public class EditorView extends Component {
	public EditorView(Context context) {
		super(context);
		init();
	}
	
	public EditorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public EditorView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private static final String TAG = "EditorView";
	
	private static final int LINE_FEED = 0x0A;
	private static final int CARRIAGE_RETURN = 0x0D;
	private static final char TAB_CHAR = '\t';
	private static final char SPACE_CHAR = ' ';
	
	private DisplayMetrics displayMetrics;
	private Paint painter;
	private Typeface typeface;
	private String fontFeatures;
	private int fontSize;
	private Paint.FontMetricsInt fontMetrics;
	private int fontTop;
	private int fontBottom;
	private int fontHeight;
	private int monoAdvance;
	private int orgTabAdvance;
	private int tabAdvance;
	
	private int tabSize;
	private boolean useSpacesInsteadTabs;
	private int indentationSize;
	private boolean indentationEnabled;
	
	private boolean caretLineVisible;
	private boolean lineNumberVisible;
	private boolean whitespaceVisible;
	private boolean hyperlinkVisible;
	private boolean todoVisible;
	private boolean breakpointVisible;
	
	private int maxLineLength;
	private int maxWidth;
	private int maxHeight;
	
	private int sideBarWidth;
	private int lineNumberWidth;
	private int breakpointWidth;
	private ColorScheme colorScheme;
	private Highlighting highlighting;
	private int lastHighlightIndex;
	
	private Timer caretTimer;
	private TimerTask caretBlinksTask;
	private boolean caretBlinks;
	private long caretBlinksRate;
	private boolean caretShowing;
	private boolean caretVisible;
	
	private long lastCaretModifiedTime;
	private int lastCaretLine;
	private int lastCaretColumn;
	
	private int caretLine = 0;
	private int caretColumn = 0;
	private int caretX;
	private int caretY;
	
	private int selectionAnchorLine;
	private int selectionAnchorColumn;
	private int selectionAnchorX;
	private int selectionAnchorY;
	
	private int selectionEdgeLine;
	private int selectionEdgeColumn;
	private int selectionEdgeX;
	private int selectionEdgeY;
	
	private boolean selectionMode;
	private int selectedFirstLine;
	private int selectedFirstColumn;
	private int selectedLastLine;
	private int selectedLastColumn;
	private Drawable handleMiddle;
	private Drawable handleLeft;
	private Drawable handleRight;
	
	private boolean handleMiddlePressed;
	private boolean handleLeftPressed;
	private boolean handleRightPressed;
	
	private boolean editable;
	private TextChange textChange;
	private EditorModel model;
	
	private void init() {
		//set default values
		colorScheme = new ColorSchemeLight();
		displayMetrics = new DisplayMetrics();
		painter = new Paint(Paint.ANTI_ALIAS_FLAG);
		typeface = Typeface.MONOSPACE;
		fontFeatures = "";
		fontSize = 14;
		fontMetrics = new Paint.FontMetricsInt();
		tabSize = 4;
		indentationSize = 4;
		caretBlinksRate = 500;
		caretVisible = true;
		lineNumberVisible = true;
		caretLineVisible = true;
		
		int handleColor = colorScheme.getSelectionHandleColor();
		if (SDK_INT >= LOLLIPOP) {
			handleMiddle = getContext().getResources().getDrawable(R.drawable.text_select_handle_middle, null);
			handleLeft = getContext().getResources().getDrawable(R.drawable.text_select_handle_left, null);
			handleRight = getContext().getResources().getDrawable(R.drawable.text_select_handle_right, null);
			handleMiddle.setTint(handleColor);
			handleLeft.setTint(handleColor);
			handleRight.setTint(handleColor);
		} else {
			handleMiddle = getContext().getResources().getDrawable(R.drawable.text_select_handle_middle);
			handleLeft = getContext().getResources().getDrawable(R.drawable.text_select_handle_left);
			handleRight = getContext().getResources().getDrawable(R.drawable.text_select_handle_right);
			handleMiddle.setColorFilter(handleColor, Mode.SRC_OVER);
			handleLeft.setColorFilter(handleColor, Mode.SRC_OVER);
			handleRight.setColorFilter(handleColor, Mode.SRC_OVER);
		}
		
		textChange = new TextChange();
		model = new EditorModel();
		model.addTextChangeListener(textChange);
		configure();
		//reset();
	}
	
	private void reset() {
		indexLayout();
		invalidate();
	}
	
	public void configure() {
		painter.setTypeface(typeface);
		
		if (SDK_INT >= LOLLIPOP)
			painter.setFontFeatureSettings(fontFeatures);
		
		painter.setTextSize(getDeviceFontSize());
		painter.getFontMetricsInt(fontMetrics);
		fontTop = -fontMetrics.top;
		fontBottom = fontMetrics.bottom + fontMetrics.leading;
		fontHeight = fontBottom + fontTop;
		
		monoAdvance = (int) painter.measureText(Character.toString(SPACE_CHAR));
		orgTabAdvance = (int) painter.measureText(Character.toString(TAB_CHAR));
		tabAdvance = monoAdvance * tabSize;
	}
	
	public void configureTab() {
		tabAdvance = monoAdvance * tabSize;
	}
	
	public void setModel(@NonNull EditorModel model) {
		this.model.removeTextChangeListener(textChange);
		this.model = model;
		this.model.addTextChangeListener(textChange);
	}
	
	@NonNull
	public EditorModel getModel() {
		return model;
	}
	
	public void addTextChangeListener(@NonNull TextChangeListener listener) {
		model.addTextChangeListener(listener);
	}
	
	public void removeTextChangeListener(@NonNull TextChangeListener listener) {
		model.removeTextChangeListener(listener);
	}
	
	public void setText(@NonNull CharSequence text) {
		model.setText(text);
	}
	
	public void insert(int index, @NonNull CharSequence text) {
		model.insert(index, text);
	}
	
	public void delete(int start, int end) {
		model.delete(start, end);
	}
	
	public void replace(int start, int end, @NonNull CharSequence newText) {
		model.replace(start, end, newText);
	}
	
	public int getCharCount() {
		return model.length();
	}
	
	public String getText() {
		return model.toString();
	}
	
	public int getLineCount() {
		return model.getLineCount();
	}
	
	public int getPosition(int line, int column) {
		int len = getLineLength(line);
		return getLineStart(line) + Math.min(column, len);
	}
	
	public int getLineAtPosition(int pos) {
		return model.getLineAtOffset(pos);
	}
	
	public int getLineStart(int line) {
		return model.getOffsetAtLine(line);
	}
	
	public int getLineLength(int line) {
		return model.getLineLength(line);
	}
	
	public void setCaretLineVisible(boolean caretLineVisible) {
		this.caretLineVisible = caretLineVisible;
	}
	
	public boolean isCaretLineVisible() {
		return caretLineVisible;
	}
	
	public void setLineNumberVisible(boolean lineNumberVisible) {
		this.lineNumberVisible = lineNumberVisible;
	}
	
	public boolean isLineNumberVisible() {
		return lineNumberVisible;
	}
	
	public void setWhitespaceVisible(boolean whitespaceVisible) {
		this.whitespaceVisible = whitespaceVisible;
	}
	
	public boolean isWhitespaceVisible() {
		return whitespaceVisible;
	}
	
	public void setHyperlinkVisible(boolean hyperlinkVisible) {
		this.hyperlinkVisible = hyperlinkVisible;
	}
	
	public boolean isHyperlinkVisible() {
		return hyperlinkVisible;
	}
	
	public void setTodoVisible(boolean todoVisible) {
		this.todoVisible = todoVisible;
	}
	
	public boolean isTodoVisible() {
		return todoVisible;
	}
	
	public void setBreakpointVisible(boolean breakpointVisible) {
		this.breakpointVisible = breakpointVisible;
	}
	
	public boolean isBreakpointVisible() {
		return breakpointVisible;
	}
	
	public void setSelectionHandleMiddleDrawable(@NonNull Drawable handleMiddle) {
		this.handleMiddle = handleMiddle;
		if (SDK_INT >= LOLLIPOP)
			this.handleMiddle.setTint(colorScheme.getSelectionHandleColor());
		else
			this.handleMiddle.setColorFilter(colorScheme.getSelectionHandleColor(), Mode.SRC_OVER);
	}
	
	public Drawable getSelectionHandleMiddleDrawable() {
		return handleMiddle;
	}
	
	public void setSelectionHandleLeftDrawable(@NonNull Drawable handleLeft) {
		this.handleLeft = handleLeft;
		if (SDK_INT >= LOLLIPOP)
			this.handleLeft.setTint(colorScheme.getSelectionHandleColor());
		else
			this.handleLeft.setColorFilter(colorScheme.getSelectionHandleColor(), Mode.SRC_OVER);
	}
	
	public Drawable getSelectionHandleLeftDrawable() {
		return handleLeft;
	}
	
	public void setSelectionHandleRightDrawable(Drawable handleRight) {
		this.handleRight = handleRight;
		if (SDK_INT >= LOLLIPOP)
			this.handleRight.setTint(colorScheme.getSelectionHandleColor());
		else
			this.handleRight.setColorFilter(colorScheme.getSelectionHandleColor(), Mode.SRC_OVER);
	}
	
	public Drawable getSelectionHandleRightDrawable() {
		return handleRight;
	}
	
	public boolean isSelectionHandleMiddlePressed() {
		return handleMiddlePressed;
	}
	
	public boolean isSelectionHandleLeftPressed() {
		return handleLeftPressed;
	}
	
	public boolean isSelectionHandleRightPressed() {
		return handleRightPressed;
	}
	
	public void setColorScheme(@NonNull ColorScheme colorScheme) {
		this.colorScheme = colorScheme;
	}
	
	@NonNull
	public ColorScheme getColorScheme() {
		return colorScheme;
	}
	
	public void setTabSize(int tabSize) {
		this.tabSize = tabSize;
	}
	
	public int getTabSize() {
		return tabSize;
	}
	
	public void setUseSpacesInsteadTabs(boolean useSpacesInsteadTabs) {
		this.useSpacesInsteadTabs = useSpacesInsteadTabs;
	}
	
	public boolean isUseSpacesInsteadTabs() {
		return useSpacesInsteadTabs;
	}
	
	public void setIndentationSize(int indentationSize) {
		this.indentationSize = indentationSize;
	}
	
	public int getIndentationSize() {
		return indentationSize;
	}
	
	public void setIndentationEnabled(boolean indentationEnabled) {
		this.indentationEnabled = indentationEnabled;
	}
	
	public boolean isIndentationEnabled() {
		return indentationEnabled;
	}
	
	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}
	
	public int getFontSize() {
		return fontSize;
	}
	
	public float getDeviceFontSize() {
		return fontSize * max(1.0f, getDensity());
	}
	
	public int getFontTop() {
		return fontTop;
	}
	
	public int getFontBottom() {
		return fontBottom;
	}
	
	public int getFontHeight() {
		return fontHeight;
	}
	
	public int getMonoAdvance() {
		return monoAdvance;
	}
	
	public int getTabAdvance() {
		return tabAdvance;
	}
	
	public float getDensity() {
		return getContext().getResources().getDisplayMetrics().density;
	}
	
	public float getScaleDensity() {
		return getContext().getResources().getDisplayMetrics().scaledDensity;
	}
	
	public void setTypeface(@Nullable Typeface typeface) {
		if (typeface == null) typeface = Typeface.DEFAULT;
		this.typeface = typeface;
	}
	
	@NonNull
	public Typeface getTypeface() {
		return typeface;
	}
	
	public void setFontFeatures(@Nullable String fontFeatures) {
		if (fontFeatures == null) fontFeatures = "";
		this.fontFeatures = fontFeatures;
	}
	
	@NonNull
	public String getFontFeatures() {
		return fontFeatures;
	}
	
	public void setCaretBlinks(boolean caretBlinks) {
		if (this.caretBlinks == caretBlinks) return;
		this.caretBlinks = caretBlinks;
		if (this.caretBlinks)
			scheduleCaretBlinks();
		else
			cancelCaretBlinks();
	}
	
	public boolean isCaretBlinks() {
		return caretBlinks;
	}
	
	public void setCaretBlinksRate(long caretBlinksRate) {
		caretBlinksRate = max(100, caretBlinksRate);
		if (this.caretBlinksRate == caretBlinksRate) return;
		this.caretBlinksRate = caretBlinksRate;
		scheduleCaretBlinks();
	}
	
	public long getCaretBlinksRate() {
		return caretBlinksRate;
	}
	
	public void setCaretVisible(boolean caretVisible) {
		this.caretVisible = caretVisible;
	}
	
	public boolean isCaretVisible() {
		return caretVisible;
	}
	
	public boolean isCaretShowing() {
		if (caretBlinks)
			return caretVisible && caretShowing;
		return caretVisible;
	}
	
	public int getCaretLine() {
		return caretLine;
	}
	
	public int getCaretColumn() {
		return caretColumn;
	}
	
	public int getCaretX() {
		return caretX;
	}
	
	public int getCaretY() {
		return caretY;
	}
	
	public int getSelectionAnchorLine() {
		return selectionAnchorLine;
	}
	
	public int getSelectionAnchorColumn() {
		return selectionAnchorColumn;
	}
	
	public int getSelectionAnchorX() {
		return selectionAnchorX;
	}
	
	public int getSelectionAnchorY() {
		return selectionAnchorY;
	}
	
	public int getSelectionEdgeLine() {
		return selectionEdgeLine;
	}
	
	public int getSelectionEdgeColumn() {
		return selectionEdgeColumn;
	}
	
	public int getSelectionEdgeX() {
		return selectionEdgeX;
	}
	
	public int getSelectionEdgeY() {
		return selectionEdgeY;
	}
	
	public int getSelectedFirstLine() {
		return selectedFirstLine;
	}
	
	public int getSelectedFirstColumn() {
		return selectedFirstColumn;
	}
	
	public int getSelectedLastLine() {
		return selectedLastLine;
	}
	
	public int getSelectedLastColumn() {
		return selectedLastColumn;
	}
	
	public boolean isSelection() {
		return selectionMode;
	}
	
	@Override
	protected void onMeasured(int width, int height) {
		displayMetrics.setTo(getContext().getResources().getDisplayMetrics());
	}
	
	public int getSideBarWidth() {
		return sideBarWidth;
	}
	
	public int getLineNumberWidth() {
		return lineNumberWidth;
	}
	
	public int getBreakpointWidth() {
		return breakpointWidth;
	}
	
	private void indexLayout() {
		maxWidth = 0;
		maxLineLength = 0;
		for (int i = 0; i < getLineCount(); i++) {
			maxLineLength = max(maxLineLength, getLineLength(i));
		}
		maxWidth = (maxLineLength + 64) * monoAdvance;
		maxHeight = getLineCount() * fontHeight;
		sideBarWidth = 0;
		lineNumberWidth = (int) ((monoAdvance * 2) + painter.measureText(Integer.toString(getLineCount())));
		breakpointWidth = monoAdvance * 2;
		if (lineNumberVisible)
			sideBarWidth += lineNumberWidth;
		
		if (breakpointVisible)
			sideBarWidth += breakpointWidth;
	}
	
	public int getMaxWidth() {
		return maxWidth;
	}
	
	public int getMaxHeight() {
		return maxHeight;
	}
	
	public int getMaxScrollX() {
		return max(0, getMaxWidth() - getComponentWidth());
	}
	
	public int getMaxScrollY() {
		return max(0, getMaxHeight() - getComponentHeight());
	}
	
	@Override
	protected int computeHorizontalScrollRange() {
		return getMaxWidth() + getPaddingLeft() + getPaddingRight();
	}
	
	@Override
	protected int computeVerticalScrollRange() {
		return getMaxHeight() + getPaddingTop() + getPaddingBottom();
	}
	
	@Override
	public void computeScroll() {
		super.computeScroll();
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (caretBlinks)
			scheduleCaretBlinks();
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (caretBlinks)
			cancelCaretBlinks();
	}
	
	@Override
	protected void onPreRedraw() {
	
	}
	
	@Override
	protected void onRedraw(Canvas canvas, Rect bounds) {
		if (colorScheme.getBackgroundColor() != 0)
			canvas.drawColor(colorScheme.getBackgroundColor());
		
		lastHighlightIndex = 0;
		int firstLine = bounds.top / fontHeight;
		int lastLine = (bounds.bottom - 1) / fontHeight;
		firstLine = max(0, min(firstLine, getLineCount()));
		lastLine = max(0, min(lastLine, getLineCount()));
		int x = sideBarWidth;
		int y = (firstLine + 1) * fontHeight;
		for (int line = firstLine; line < lastLine; line++) {
			int start = getLineStart(line);
			int length = getLineLength(line);
			redrawLine(canvas, bounds, line, start, length, x, y);
			y += fontHeight;
		}
	}
	
	protected void redrawLine(Canvas canvas, Rect bounds, int line, int start, int length, int x, int y) {
		int end = start + length;
		if (editable && !selectionMode && caretLineVisible && caretLine == line && colorScheme.getCaretLineColor() != 0) {
			painter.setColor(colorScheme.getCaretLineColor());
			canvas.drawRect(x, y - fontTop, bounds.right, y + fontBottom, painter);
		}
		
		if (lineNumberVisible && colorScheme.getLineNumberColor() != 0) {
			if (line == caretLine && colorScheme.getCaretLineNumberColor() != 0)
				painter.setColor(colorScheme.getCaretLineNumberColor());
			else
				painter.setColor(colorScheme.getLineNumberColor());
			canvas.drawText(Integer.toString(line + 1), monoAdvance, y, painter);
		}
		
		int textColor = colorScheme.getForegroundColor();
		int textOffset = start;
		if (highlighting != null) {
			int findIndex = -1;
			for (int i = start; i < end; i++) {
				findIndex = findHighlight(lastHighlightIndex, i);
				if (findIndex >= 0) break;
			}
			
			if (findIndex >= 0) {
				for (int i = findIndex; i < highlighting.highlights.length; i++) {
					Highlight highlight = highlighting.highlights[i];
					if (highlight.startIndex > end) break;
					int spanStart = max(textOffset, highlight.startIndex);
					int spanEnd = min(end, highlight.stopIndex);
					
					if (spanStart > textOffset) {
						resetPainter();
						painter.setColor(textColor);
						if (textColor == 0)
							x += measureText(textOffset, spanStart);
						else
							x += drawText(canvas, textOffset, spanStart, x, y);
					}
					
					TextStyle style = colorScheme.getStyle(highlight.type);
					if (style != null) {
						applyPainter(style);
						int foregroundColor = style.foregroundColor;
						if (foregroundColor != 0)
							painter.setColor(foregroundColor);
						else
							painter.setColor(textColor);
					}
					if (painter.getColor() == 0)
						x += measureText(spanStart, spanEnd);
					else
						x += drawText(canvas, spanStart, spanEnd, x, y);
					textOffset = spanEnd;
					lastHighlightIndex = i;
					resetPainter();
				}
			}
		}
		
		if (textOffset < end) {
			if (colorScheme.getForegroundColor() != 0) {
				painter.setColor(colorScheme.getForegroundColor());
				x += drawText(canvas, textOffset, end, x, y);
			} else
				x += measureText(textOffset, end);
		}
		
	}
	
	private void resetPainter() {
		painter.setFakeBoldText(false);
		painter.setTextSkewX(0);
	}
	
	private void applyPainter(TextStyle style) {
		switch (style.fontStyle) {
			case NORMAL:
			default:
				break;
			case BOLD:
				painter.setFakeBoldText(true);
				break;
			case ITALIC:
				painter.setTextSkewX(-0.25f);
				break;
			case BOLD_ITALIC:
				painter.setFakeBoldText(true);
				painter.setTextSkewX(-0.25f);
				break;
		}
	}
	
	public int measureText(int start, int end) {
		int w = model.measureText(start, end, painter);
		for (int i = start; i < end; i++) {
			if (model.charAt(i) == TAB_CHAR) {
				w -= orgTabAdvance;
				w += tabAdvance;
			}
		}
		return w;
	}
	
	public int getTextWidths(int start, int end, float[] widths) {
		int w = model.getTextWidths(start, end, widths, painter);
		for (int i = start; i < end; i++) {
			if (model.charAt(i) == TAB_CHAR) {
				w -= orgTabAdvance;
				w += tabAdvance;
				widths[i - start] = tabAdvance;
			}
		}
		return w;
	}
	
	private int drawText(Canvas canvas, int start, int end, int x, int y) {
		int offset = 0;
		int from = start;
		for (int i = start; i < end; i++) {
			if (model.charAt(i) == TAB_CHAR) {
				model.drawText(canvas, from, i, x, y, painter);
				int w = measureText(from, i + 1);
				offset += w;
				x += w;
				from = i + 1;
			}
		}
		if (from < end) {
			model.drawText(canvas, from, end, x, y, painter);
			offset += measureText(from, end);
		}
		return offset;
	}
	
	private int drawText(Canvas canvas, int start, int end, int x, int y, int backgroundColor, int foregroundColor) {
		int offset = 0;
		int from = start;
		for (int i = start; i < end; i++) {
			if (model.charAt(i) == TAB_CHAR) {
				int w = measureText(from, i + 1);
				if (backgroundColor != 0) {
					painter.setColor(backgroundColor);
					canvas.drawRect(x, y - fontTop, x + w, y + fontBottom, painter);
				}
				painter.setColor(foregroundColor);
				model.drawText(canvas, from, i, x, y, painter);
				
				offset += w;
				x += w;
				from = i + 1;
			}
		}
		if (from < end) {
			int w = measureText(from, end);
			if (backgroundColor != 0) {
				painter.setColor(backgroundColor);
				canvas.drawRect(x, y - fontTop, x + w, y + fontBottom, painter);
			}
			painter.setColor(foregroundColor);
			model.drawText(canvas, from, end, x, y, painter);
			offset += w;
		}
		return offset;
	}
	
	private void scheduleCaretBlinks() {
		cancelCaretBlinks();
		if (caretTimer == null)
			caretTimer = new Timer("Caret", true);
		if (caretBlinksTask == null)
			caretBlinksTask = new BlinksTask();
		caretTimer.scheduleAtFixedRate(caretBlinksTask, 100L, caretBlinksRate);
	}
	
	private void cancelCaretBlinks() {
		if (caretBlinksTask != null) {
			caretBlinksTask.cancel();
			caretBlinksTask = null;
		}
		if (caretTimer != null) {
			caretTimer.cancel();
			caretTimer = null;
		}
		caretShowing = false;
	}
	
	private class BlinksTask extends TimerTask {
		
		@Override
		public void run() {
			if (!caretBlinks) return;
			if (currentTimeMillis() - lastCaretModifiedTime < 3000)
				caretShowing = true;
			else
				caretShowing = !caretShowing;
			postInvalidate();
		}
	}
	
	public void setHighlighting(@Nullable Highlighting highlighting) {
		this.highlighting = highlighting;
		invalidate();
		Log.d(TAG, "setHighlighting: ");
	}
	
	@Nullable
	public Highlighting getHighlighting() {
		return highlighting;
	}
	
	private int findHighlight(int fromIndex, int offset) {
		int low = fromIndex;
		int high = highlighting.highlights.length - 1;
		
		while (low <= high) {
			int mid = (low + high) >>> 1;
			Highlight midVal = highlighting.highlights[mid];
			
			if (midVal.stopIndex < offset)
				low = mid + 1;
			else if (midVal.startIndex > offset)
				high = mid - 1;
			else
				return mid; // key found
		}
		return -(low + 1);  // key not found.
	}
	
	protected void onTextSet() {
		reset();
	}
	
	protected void onTextChanging(int start, int end, @Nullable CharSequence newText) {
	
	}
	
	protected void onTextInserted(int index, @NonNull CharSequence text) {
		indexLayout();
		invalidate();
	}
	
	protected void onTextDeleted(int start, int end) {
		indexLayout();
		invalidate();
	}
	
	protected void onTextReplaced(int start, int end, @NonNull CharSequence newText) {
		indexLayout();
		invalidate();
	}
	
	private class TextChange implements TextChangeListener {
		
		@Override
		public void textSet() {
			onTextSet();
		}
		
		@Override
		public void textChanging(int start, int end, @Nullable CharSequence newText) {
			onTextChanging(start, end, newText);
		}
		
		@Override
		public void textInserted(int index, @NonNull CharSequence text) {
			onTextInserted(index, text);
		}
		
		@Override
		public void textDeleted(int start, int end) {
			onTextDeleted(start, end);
		}
		
		@Override
		public void textReplaced(int start, int end, @NonNull CharSequence newText) {
			onTextReplaced(start, end, newText);
		}
	}
}
