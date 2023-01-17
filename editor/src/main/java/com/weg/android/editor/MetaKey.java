package com.weg.android.editor;

import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import static androidx.annotation.VisibleForTesting.PACKAGE_PRIVATE;
import androidx.annotation.VisibleForTesting;
/**
 * Meta key helper
 * from {@link android.text.method.MetaKeyKeyListener}
 */

@VisibleForTesting(otherwise = PACKAGE_PRIVATE)
 class MetaKey {
	/**
	 * Flag that indicates that the SHIFT key is on.
	 * Value equals {@link KeyEvent#META_SHIFT_ON}.
	 */
	public static final int META_SHIFT_ON = KeyEvent.META_SHIFT_ON;
	/**
	 * Flag that indicates that the ALT key is on.
	 * Value equals {@link KeyEvent#META_ALT_ON}.
	 */
	public static final int META_ALT_ON = KeyEvent.META_ALT_ON;
	/**
	 * Flag that indicates that the SYM key is on.
	 * Value equals {@link KeyEvent#META_SYM_ON}.
	 */
	public static final int META_SYM_ON = KeyEvent.META_SYM_ON;
	
	/**
	 * Flag that indicates that the SHIFT key is locked in CAPS mode.
	 */
	public static final int META_CAP_LOCKED = KeyEvent.META_CAP_LOCKED;
	/**
	 * Flag that indicates that the ALT key is locked.
	 */
	public static final int META_ALT_LOCKED = KeyEvent.META_ALT_LOCKED;
	/**
	 * Flag that indicates that the SYM key is locked.
	 */
	public static final int META_SYM_LOCKED = KeyEvent.META_SYM_LOCKED;
	
	/**
	 * @hide pending API review
	 */
	public static final int META_SELECTING = KeyEvent.META_SELECTING;
	
	// These bits are privately used by the meta key key listener.
	// They are deliberately assigned values outside of the representable range of an 'int'
	// so as not to conflict with any meta key states publicly defined by KeyEvent.
	private static final long META_CAP_USED = 1L << 32;
	private static final long META_ALT_USED = 1L << 33;
	private static final long META_SYM_USED = 1L << 34;
	
	private static final long META_CAP_PRESSED = 1L << 40;
	private static final long META_ALT_PRESSED = 1L << 41;
	private static final long META_SYM_PRESSED = 1L << 42;
	
	private static final long META_CAP_RELEASED = 1L << 48;
	private static final long META_ALT_RELEASED = 1L << 49;
	private static final long META_SYM_RELEASED = 1L << 50;
	
	private static final long META_SHIFT_MASK = META_SHIFT_ON
			| META_CAP_LOCKED | META_CAP_USED
			| META_CAP_PRESSED | META_CAP_RELEASED;
	private static final long META_ALT_MASK = META_ALT_ON
			| META_ALT_LOCKED | META_ALT_USED
			| META_ALT_PRESSED | META_ALT_RELEASED;
	private static final long META_SYM_MASK = META_SYM_ON
			| META_SYM_LOCKED | META_SYM_USED
			| META_SYM_PRESSED | META_SYM_RELEASED;
	
	private static final int PRESSED_RETURN_VALUE = 1;
	private static final int LOCKED_RETURN_VALUE = 2;
	
	
	/**
	 * Call this if you are a method that ignores the locked meta state
	 * (arrow keys, for example) and you handle a key.
	 */
	public static long resetLockedMeta(long state) {
		if ((state & META_CAP_LOCKED) != 0) {
			state &= ~META_SHIFT_MASK;
		}
		if ((state & META_ALT_LOCKED) != 0) {
			state &= ~META_ALT_MASK;
		}
		if ((state & META_SYM_LOCKED) != 0) {
			state &= ~META_SYM_MASK;
		}
		return state;
	}
	
	// ---------------------------------------------------------------------
	// Version of API that operates on a state bit mask
	// ---------------------------------------------------------------------
	
	/**
	 * Gets the state of the meta keys.
	 *
	 * @param state the current meta state bits.
	 * @return an integer in which each bit set to one represents a pressed
	 * or locked meta key.
	 */
	public static int getMetaState(long state) {
		int result = 0;
		
		if ((state & META_CAP_LOCKED) != 0) {
			result |= META_CAP_LOCKED;
		} else if ((state & META_SHIFT_ON) != 0) {
			result |= META_SHIFT_ON;
		}
		
		if ((state & META_ALT_LOCKED) != 0) {
			result |= META_ALT_LOCKED;
		} else if ((state & META_ALT_ON) != 0) {
			result |= META_ALT_ON;
		}
		
		if ((state & META_SYM_LOCKED) != 0) {
			result |= META_SYM_LOCKED;
		} else if ((state & META_SYM_ON) != 0) {
			result |= META_SYM_ON;
		}
		
		return result;
	}
	
	/**
	 * Gets the state of a particular meta key.
	 *
	 * @param state the current state bits.
	 * @param meta  META_SHIFT_ON, META_ALT_ON, or META_SYM_ON
	 * @return 0 if inactive, 1 if active, 2 if locked.
	 */
	public static int getMetaState(long state, int meta) {
		switch (meta) {
			case META_SHIFT_ON:
				if ((state & META_CAP_LOCKED) != 0) return LOCKED_RETURN_VALUE;
				if ((state & META_SHIFT_ON) != 0) return PRESSED_RETURN_VALUE;
				return 0;
			
			case META_ALT_ON:
				if ((state & META_ALT_LOCKED) != 0) return LOCKED_RETURN_VALUE;
				if ((state & META_ALT_ON) != 0) return PRESSED_RETURN_VALUE;
				return 0;
			
			case META_SYM_ON:
				if ((state & META_SYM_LOCKED) != 0) return LOCKED_RETURN_VALUE;
				if ((state & META_SYM_ON) != 0) return PRESSED_RETURN_VALUE;
				return 0;
			
			default:
				return 0;
		}
	}
	
	/**
	 * Call this method after you handle a keypress so that the meta
	 * state will be reset to unshifted (if it is not still down)
	 * or primed to be reset to unshifted (once it is released).  Takes
	 * the current state, returns the new state.
	 */
	public static long adjustMetaAfterKeyPress(long state) {
		if ((state & META_CAP_PRESSED) != 0) {
			state = (state & ~META_SHIFT_MASK) | META_SHIFT_ON | META_CAP_USED;
		} else if ((state & META_CAP_RELEASED) != 0) {
			state &= ~META_SHIFT_MASK;
		}
		
		if ((state & META_ALT_PRESSED) != 0) {
			state = (state & ~META_ALT_MASK) | META_ALT_ON | META_ALT_USED;
		} else if ((state & META_ALT_RELEASED) != 0) {
			state &= ~META_ALT_MASK;
		}
		
		if ((state & META_SYM_PRESSED) != 0) {
			state = (state & ~META_SYM_MASK) | META_SYM_ON | META_SYM_USED;
		} else if ((state & META_SYM_RELEASED) != 0) {
			state &= ~META_SYM_MASK;
		}
		return state;
	}
	
	/**
	 * Handles presses of the meta keys.
	 */
	public static long handleKeyDown(long state, int keyCode) {
		if (keyCode == KeyEvent.KEYCODE_SHIFT_LEFT || keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT) {
			return press(state, META_SHIFT_ON, META_SHIFT_MASK,
					META_CAP_LOCKED, META_CAP_PRESSED, META_CAP_RELEASED, META_CAP_USED);
		}
		
		if (keyCode == KeyEvent.KEYCODE_ALT_LEFT || keyCode == KeyEvent.KEYCODE_ALT_RIGHT
				|| keyCode == KeyEvent.KEYCODE_NUM) {
			return press(state, META_ALT_ON, META_ALT_MASK,
					META_ALT_LOCKED, META_ALT_PRESSED, META_ALT_RELEASED, META_ALT_USED);
		}
		
		if (keyCode == KeyEvent.KEYCODE_SYM) {
			return press(state, META_SYM_ON, META_SYM_MASK,
					META_SYM_LOCKED, META_SYM_PRESSED, META_SYM_RELEASED, META_SYM_USED);
		}
		return state;
	}
	
	private static long press(long state, int what, long mask, long locked, long pressed, long released, long used) {
		if ((state & pressed) != 0) {
			// repeat before use
		} else if ((state & released) != 0) {
			state = (state & ~mask) | what | locked;
		} else if ((state & used) != 0) {
			// repeat after use
		} else if ((state & locked) != 0) {
			state &= ~mask;
		} else {
			state |= what | pressed;
		}
		return state;
	}
	
	/**
	 * Handles release of the meta keys.
	 */
	public static long handleKeyUp(long state, int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_SHIFT_LEFT || keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT) {
			return release(state, META_SHIFT_ON, META_SHIFT_MASK,
					META_CAP_PRESSED, META_CAP_RELEASED, META_CAP_USED, event);
		}
		
		if (keyCode == KeyEvent.KEYCODE_ALT_LEFT || keyCode == KeyEvent.KEYCODE_ALT_RIGHT
				|| keyCode == KeyEvent.KEYCODE_NUM) {
			return release(state, META_ALT_ON, META_ALT_MASK,
					META_ALT_PRESSED, META_ALT_RELEASED, META_ALT_USED, event);
		}
		
		if (keyCode == KeyEvent.KEYCODE_SYM) {
			return release(state, META_SYM_ON, META_SYM_MASK,
					META_SYM_PRESSED, META_SYM_RELEASED, META_SYM_USED, event);
		}
		return state;
	}
	
	private static long release(long state, int what, long mask, long pressed, long released, long used, KeyEvent event) {
		if (event.getKeyCharacterMap().getModifierBehavior() == KeyCharacterMap.MODIFIER_BEHAVIOR_CHORDED_OR_TOGGLED) {
			if ((state & used) != 0) {
				state &= ~mask;
			} else if ((state & pressed) != 0) {
				state |= what | released;
			}
		} else {
			state &= ~mask;
		}
		return state;
	}
	
	/**
	 * Clears the state of the specified meta key if it is locked.
	 *
	 * @param state the meta key state
	 * @param which meta keys to clear, may be a combination of {@link #META_SHIFT_ON},
	 *              {@link #META_ALT_ON} or {@link #META_SYM_ON}.
	 */
	public long clearMetaKeyState(long state, int which) {
		if ((which & META_SHIFT_ON) != 0 && (state & META_CAP_LOCKED) != 0) {
			state &= ~META_SHIFT_MASK;
		}
		if ((which & META_ALT_ON) != 0 && (state & META_ALT_LOCKED) != 0) {
			state &= ~META_ALT_MASK;
		}
		if ((which & META_SYM_ON) != 0 && (state & META_SYM_LOCKED) != 0) {
			state &= ~META_SYM_MASK;
		}
		return state;
	}
}
