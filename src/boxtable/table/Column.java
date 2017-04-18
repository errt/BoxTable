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
package boxtable.table;

import org.apache.pdfbox.pdmodel.font.PDFont;

import boxtable.common.Bordered;
import boxtable.common.HAlign;
import boxtable.common.VAlign;

/**
 * Represents a table column with formatting
 * 
 * @author Dominik Helm
 */
public class Column extends Bordered {
	/** Minimal width of this column */
	private final float minWidth;
	/** Maximal width of this column */
	private final float maxWidth;

	/** Font used for rendering text in this column */
	private final PDFont font;
	/** Minimal font size used for rendering text in this column */
	private final float minFontSize;
	/** Maximal font size used for rendering text in this column */
	private final float maxFontSize;

	/** Horizontal alignment for content in this column */
	private HAlign halign;
	/** Vertical alignment for content in this column */
	private VAlign valign = VAlign.MIDDLE;

	/**
	 * Creates a column with variable width and font size
	 * 
	 * @param minWidth
	 *            The minimal width for this column
	 * @param maxWidth
	 *            The maximal width for this column
	 * @param font
	 *            The font used for rendering text in this column
	 * @param minFontSize
	 *            The minimal font size used for rendering text in this column
	 * @param maxFontSize
	 *            The maximal font size used for rendering text in this column
	 * @param halign
	 *            The horizontal alignment for content in this column
	 */
	public Column(final float minWidth, final float maxWidth, final PDFont font, final float minFontSize, final float maxFontSize, final HAlign halign) {
		this.minWidth = minWidth;
		this.maxWidth = maxWidth;
		this.font = font;
		this.minFontSize = minFontSize;
		this.maxFontSize = maxFontSize;
		this.halign = halign;
		setBorder(0.25f, 0.25f, 0.25f, 0.25f);
	}

	/**
	 * Creates a column with fixed width and font size
	 * 
	 * @param width
	 *            The width for this column
	 * @param font
	 *            The font used for rendering text in this column
	 * @param fontSize
	 *            The font size used for rendering text in this column
	 * @param halign
	 *            The horizontal alignment for content in this column
	 */
	public Column(final float width, final PDFont font, final float fontSize, final HAlign halign) {
		this(width, width, font, fontSize, fontSize, halign);
	}

	/**
	 * Returns the font used for rendering text in this column
	 * 
	 * @return The font
	 */
	public PDFont getFont() {
		return font;
	}

	/**
	 * Returns the horizontal alignment for content in this column
	 * 
	 * @return The horizontal alignment
	 */
	public HAlign getHAlign() {
		return halign;
	}

	/**
	 * Returns the maximal font size used for rendering text in this column
	 * 
	 * @return The maximal font size
	 */
	public float getMaxFontSize() {
		return maxFontSize;
	}

	/**
	 * Returns the maximal width of this column
	 * 
	 * @return The maximal width
	 */
	public float getMaxWidth() {
		return maxWidth;
	}

	/**
	 * Returns the minimal font size used for rendering text in this column
	 * 
	 * @return The minimal font size
	 */
	public float getMinFontSize() {
		return minFontSize;
	}

	/**
	 * Returns the minimal width of this column
	 * 
	 * @return The minimal width
	 */
	public float getMinWidth() {
		return minWidth;
	}

	/**
	 * Returns the vertical alignment for content in this column
	 * 
	 * @return The vertical alignment
	 */
	public VAlign getVAlign() {
		return valign;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see boxtable.common.Bordered#setBorder(float, float, float, float)
	 */
	@Override
	public Column setBorder(final float top, final float left, final float right, final float bottom) {
		topBorder = top;
		leftBorder = left;
		rightBorder = right;
		bottomBorder = bottom;
		return this;
	}

	/**
	 * Sets the horizontal alignment for content in this column
	 * 
	 * @param halign
	 *            The horizontal alignment
	 * @return This Column, for a fluent interface
	 */
	public Column setHAlign(final HAlign halign) {
		this.halign = halign;
		return this;
	}

	/**
	 * Sets the vertical alignment for content in this column
	 * 
	 * @param valign
	 *            The vertical alignment
	 * @return This Column, for a fluent interface
	 */
	public Column setVAlign(final VAlign valign) {
		this.valign = valign;
		return this;
	}
}
