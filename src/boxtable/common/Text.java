/*
 * Copyright 2017 Dominik Helm
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package boxtable.common;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.font.PDFont;

/**
 * Represents part of a String with formatting
 * 
 * @author Dominik Helm
 */
public class Text {
	/** String represented by this Text */
	private final String text;

	/** Font this Text will be rendered in */
	private PDFont font = null;
	/** Font size this Text will be rendered in */
	private float fontSize = -1;

	/** Underline this Text */
	private boolean underlined = false;
	/** Strike this Text through */
	private boolean striked = false;

	/** Vertical offset to the rest of the line this text is rendered at */
	private float verticalOffset = 0;

	/**
	 * Creates a Text for a given string
	 * 
	 * @param text
	 *            The string represented by this text
	 */
	public Text(final String text) {
		this.text = text;
	}

	/**
	 * Returns a Text with the same formatting but for a new string
	 * 
	 * @param text
	 *            The new string
	 * @return A Text for the new string
	 */
	public Text copy(final String text) {
		Text result = new Text(text);
		result.font = font;
		result.fontSize = fontSize;
		result.underlined = underlined;
		result.striked = striked;
		result.verticalOffset = verticalOffset;
		return result;
	}

	/**
	 * Returns the font set for this text
	 * 
	 * @return The font or null if it is to be inherited by its cell
	 */
	public PDFont getFont() {
		return font;
	}

	/**
	 * Returns the font size set for this text
	 * 
	 * @return The font size (negative if it is to be inherited by its column)
	 */
	public float getFontSize() {
		return fontSize;
	}

	/**
	 * Returs the string represented by this Text
	 * 
	 * @return The string represented by this Text
	 */
	public String getText() {
		return text;
	}

	/**
	 * Returns the vertical offset to the rest of the line this Text will be rendered at
	 * 
	 * @return The vertical offset
	 */
	public float getVerticalOffset() {
		return verticalOffset;
	}

	/**
	 * Returns the width this text will occupy
	 * 
	 * @param font
	 *            The font the text should be rendered in if it inherits its font
	 * @param fontSize
	 *            The font sise the text should be rendered in if it inherits its font size
	 * @return The width of this text when rendered
	 * @throws IOException
	 *             If accessing information needed to calculate text size fails
	 */
	public float getWidth(final PDFont font, final float fontSize) throws IOException {
		final PDFont usedFont = this.font != null ? this.font : font;
		final float usedFontSize = this.fontSize > 0 ? this.fontSize : fontSize;
		return usedFont.getStringWidth(text) / 1000 * usedFontSize;
	}

	/**
	 * Returns whether this Text should be struck through
	 * 
	 * @return True if this Text will be struck through
	 */
	public boolean isStriked() {
		return striked;
	}

	/**
	 * Returns whether this Text should be underlined
	 * 
	 * @return True if this Text will be underlined
	 */
	public boolean isUnderlined() {
		return underlined;
	}

	/**
	 * Sets the font to be used for rendering this Text
	 * 
	 * @param font
	 *            The font, null if it should be inherited
	 * @return This Text, for a fluent interface
	 */
	public Text setFont(final PDFont font) {
		this.font = font;
		return this;
	}

	/**
	 * Sets the font size to be used for rendering this Text
	 * 
	 * @param fontSize
	 *            The font size, negative if it should be inherited
	 * @return This Text, for a fluent interface
	 */
	public Text setFontSize(final float fontSize) {
		this.fontSize = fontSize;
		return this;
	}

	/**
	 * Sets whether this Text should be struck trough
	 * 
	 * @param striked
	 *            True to strike through this text, false otherwise
	 * @return This Text, for a fluent interface
	 */
	public Text setStriked(final boolean striked) {
		this.striked = striked;
		return this;
	}

	/**
	 * Sets whether this Text should be underlined
	 * 
	 * @param underlined
	 *            True to underline this text, false otherwise
	 * @return This Text, for a fluent interface
	 */
	public Text setUnderlined(final boolean underlined) {
		this.underlined = underlined;
		return this;
	}

	/**
	 * Sets the vertical offset to the rest of the line this Text will be rendered at
	 * 
	 * @param verticalOffset
	 *            The vertical offset
	 * @return This Text, for a fluent interface
	 */
	public Text setVerticalOffset(final float verticalOffset) {
		this.verticalOffset = verticalOffset;
		return this;
	}
}
