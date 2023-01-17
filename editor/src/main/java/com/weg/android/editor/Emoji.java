package com.weg.android.editor;


import static androidx.annotation.VisibleForTesting.PACKAGE_PRIVATE;

import android.icu.lang.UCharacter;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.annotation.VisibleForTesting;

/**
 * An utility class for Emoji.
 */
@VisibleForTesting(otherwise = PACKAGE_PRIVATE)
class Emoji {
	private static final int EMOJI_MODIFIER = 59;
	private static final int EMOJI_MODIFIER_BASE = 60;
	private static final int EMOJI = 57;
	public static int COMBINING_ENCLOSING_KEYCAP = 0x20E3;
	
	public static int ZERO_WIDTH_JOINER = 0x200D;
	
	public static int VARIATION_SELECTOR_16 = 0xFE0F;
	
	public static int CANCEL_TAG = 0xE007F;
	
	/**
	 * Returns true if the given code point is regional indicator symbol.
	 */
	public static boolean isRegionalIndicatorSymbol(int codePoint) {
		return 0x1F1E6 <= codePoint && codePoint <= 0x1F1FF;
	}
	
	/**
	 * Returns true if the given code point is emoji modifier.
	 */
	@RequiresApi(api = Build.VERSION_CODES.N)
	public static boolean isEmojiModifier(int codePoint) {
		return UCharacter.hasBinaryProperty(codePoint, EMOJI_MODIFIER);
	}
	
	//
	
	/**
	 * Returns true if the given code point is emoji modifier base.
	 *
	 * @param c codepoint to check
	 * @return true if is emoji modifier base
	 */
	@RequiresApi(api = Build.VERSION_CODES.N)
	public static boolean isEmojiModifierBase(int c) {
		// These two characters were removed from Emoji_Modifier_Base in Emoji 4.0, but we need to
		// keep them as emoji modifier bases since there are fonts and user-generated text out there
		// that treats these as potential emoji bases.
		if (c == 0x1F91D || c == 0x1F93C) {
			return true;
		}
		// If Android's copy of ICU is behind, check for new codepoints here.
		// Consult log for implementation pattern.
		return UCharacter.hasBinaryProperty(c, EMOJI_MODIFIER_BASE);
	}
	
	/**
	 * Returns true if the character has Emoji property.
	 */
	@RequiresApi(api = Build.VERSION_CODES.N)
	public static boolean isEmoji(int codePoint) {
		return UCharacter.hasBinaryProperty(codePoint, EMOJI);
	}
	
	// Returns true if the character can be a base character of COMBINING ENCLOSING KEYCAP.
	public static boolean isKeycapBase(int codePoint) {
		return ('0' <= codePoint && codePoint <= '9') || codePoint == '#' || codePoint == '*';
	}
	
	/**
	 * Returns true if the character can be a part of tag_spec in emoji tag sequence.
	 * <p>
	 * Note that 0xE007F (CANCEL TAG) is not included.
	 */
	public static boolean isTagSpecChar(int codePoint) {
		return 0xE0020 <= codePoint && codePoint <= 0xE007E;
	}
}

