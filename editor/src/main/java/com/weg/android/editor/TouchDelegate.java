package com.weg.android.editor;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import static androidx.annotation.VisibleForTesting.PACKAGE_PRIVATE;
import androidx.annotation.VisibleForTesting;
@VisibleForTesting(otherwise = PACKAGE_PRIVATE)
class TouchDelegate implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, ScaleGestureDetector.OnScaleGestureListener {
	private final Component component;
	
	TouchDelegate(Component component) {
		this.component = component;
	}
	
	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		return component.onSingleTapConfirmed(e);
	}
	
	@Override
	public boolean onDoubleTap(MotionEvent e) {
		return component.onDoubleTap(e);
	}
	
	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		return component.onDoubleTapEvent(e);
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		return component.onDown(e);
	}
	
	@Override
	public void onShowPress(MotionEvent e) {
		component.onShowPress(e);
	}
	
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return component.onSingleTapUp(e);
	}
	
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return component.onScroll(e1, e2, distanceX, distanceY);
	}
	
	@Override
	public void onLongPress(MotionEvent e) {
		component.onLongPress(e);
	}
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		return component.onFling(e1, e2, velocityX, velocityY);
	}
	
	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		return component.onScale(detector);
	}
	
	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		return component.onScaleBegin(detector);
	}
	
	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {
		component.onScaleEnd(detector);
	}
}
