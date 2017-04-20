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
package boxtable.cell;

import java.awt.Color;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import boxtable.common.HAlign;
import boxtable.common.VAlign;
import boxtable.event.TableEventSource;

/**
 * Represents an empty cell and is the basis for other cell implementations
 * 
 * @author Dominik Helm
 */
public class Cell extends TableEventSource {
	/** Number of columns this cell extends over */
	protected int colSpan = 1;
	/** Number of rows this cell extends over */
	protected int rowSpan = 1;

	/** Minimal width of this cell */
	protected float minWidth = 0;
	/** Minimal height of this cell */
	protected float minHeight = 0;

	/** Minimal spacing between the top border of the cell and the cell's content */
	protected float topPadding = 0;
	/** Minimal spacing between the left border of the cell and the cell's content */
	protected float leftPadding = 0;
	/** Minimal spacing between the right border of the cell and the cell's content */
	protected float rightPadding = 0;
	/** Minimal spacing between the bottom border of the cell and the cell's content */
	protected float bottomPadding = 0;

	/** Horizontal alignment multiplier of this cell's content */
	protected float hAlign = -1;
	/** Vertical alignment multiplier of this cell's content */
	protected float vAlign = -1;

	/** Background color for this cell */
	protected Color background = null;

	/**
	 * Gets the number of columns this cell extends over
	 * 
	 * @return The column span of this cell
	 */
	public int getColSpan() {
		return colSpan;
	}

	/**
	 * Returns the horizontal alignment set for this cell's content
	 * 
	 * @return The horizontal alignment or null if it is to be inherited by its column
	 */
	public HAlign getHAlign() {
		if (hAlign == 0) {
			return HAlign.LEFT;
		} else if (hAlign == 0.5f) {
			return HAlign.CENTER;
		} else if (hAlign == 1) {
			return HAlign.RIGHT;
		}
		return null;
	}

	/**
	 * Gets the height this cell requires if rendered at the given width
	 * 
	 * @param width
	 *            The width this cell will have when rendered
	 * @return The required height for this cell
	 * @throws IOException
	 *             If accessing information for calculating the height fails
	 */
	public float getHeight(final float width) throws IOException {
		return minHeight;
	}

	/**
	 * Gets the number of rows this cell extends over
	 * 
	 * @return The row span of this cell
	 */
	public int getRowSpan() {
		return rowSpan;
	}

	/**
	 * Returns the vertical alignment set for this cell's content
	 * 
	 * @return The vertical alignment or null if it is to be inherited by its column
	 */
	public VAlign getVAlign() {
		if (vAlign == 0) {
			return VAlign.TOP;
		} else if (vAlign == 0.5f) {
			return VAlign.MIDDLE;
		} else if (vAlign == 1) {
			return VAlign.BOTTOM;
		}
		return null;
	}

	/**
	 * Gets the width this cell requires
	 * 
	 * @return The required width for this cell
	 * @throws IOException
	 *             If accessing information for calculating the width fails
	 */
	public float getWidth() throws IOException {
		return minWidth;
	}

	/**
	 * Renders this cell at the specified position
	 * 
	 * @param document
	 *            The document this cell is rendered to
	 * @param stream
	 *            The PDPageContentStream used to render this cell
	 * @param left
	 *            The coordinate of the left edge of the cell
	 * @param top
	 *            The coordinate of the top edge of the cell
	 * @param width
	 *            The width the cell will be rendered
	 * @param height
	 *            The height the cell will be rendered
	 * @throws IOException
	 *             If writing to the stream fails
	 */
	public void render(final PDDocument document, final PDPageContentStream stream, final float left, final float top, final float width, final float height)
			throws IOException {

		if (background != null) {
			stream.setNonStrokingColor(background);
			stream.addRect(left, top - height, width, height);
			stream.fill();
		}

		drawBorder(stream, left, top, width, height);
	}

	/**
	 * Sets the Color that will be rendered as the background of this cell
	 * 
	 * @param background
	 *            The color for the background or null if no background should be rendered
	 * @return This Cell, for a fluent interface
	 */
	public Cell setBackground(final Color background) {
		this.background = background;
		return this;
	}

	/**
	 * Sets the number of columns this cell extends over
	 * 
	 * @param colSpan
	 *            The column span of this cell
	 * @return This Cell, for a fluent interface
	 */
	public Cell setColSpan(final int colSpan) {
		this.colSpan = colSpan;
		return this;
	}

	/**
	 * Sets the horizontal alignment of this cell's content
	 * 
	 * @param hAlign
	 *            The horizontal alignment or null if it is to be inherited by it's column
	 * @return This Cell, for a fluent interface
	 */
	public Cell setHAlign(final HAlign hAlign) {
		switch (hAlign) {
		case LEFT:
			this.hAlign = 0;
			break;
		case CENTER:
			this.hAlign = 0.5f;
			break;
		case RIGHT:
			this.hAlign = 1;
			break;
		default:
			this.hAlign = -1;
			break;
		}
		return this;
	}

	/**
	 * Sets the minimum height this cell should have regardless of its content
	 * 
	 * @param minHeight
	 *            The cells minimum height
	 * @return This Cell, for a fluent interface
	 */
	public Cell setMinHeight(final float minHeight) {
		this.minHeight = minHeight;
		return this;
	}

	/**
	 * Sets the minimum width this cell should have regardless of its content
	 * 
	 * @param minWidth
	 *            The cells minimum width
	 * @return This Cell, for a fluent interface
	 */
	public Cell setMinWidth(final float minWidth) {
		this.minWidth = minWidth;
		return this;
	}

	/**
	 * Sets the minimum space between the content and the respective borders
	 * 
	 * @param top
	 *            The minimum space between the content and the top border
	 * @param left
	 *            The minimum space between the content and the left border
	 * @param right
	 *            The minimum space between the content and the right border
	 * @param bottom
	 *            The minimum space between the content and the bottom border
	 * @return This Cell, for a fluent interface
	 */
	public Cell setPadding(final float top, final float left, final float right, final float bottom) {
		topPadding = top;
		leftPadding = left;
		rightPadding = right;
		bottomPadding = bottom;
		return this;
	}

	/**
	 * Sets the number of rows this cell extends over
	 * 
	 * @param rowSpan
	 *            The row span of this cell
	 * @return This Cell, for a fluent interface
	 */
	public Cell setRowSpan(final int rowSpan) {
		this.rowSpan = rowSpan;
		return this;
	}

	/**
	 * Sets the vertical alignment of this cell's content
	 * 
	 * @param vAlign
	 *            The vertical alignment or null if it is to be inherited by it's column
	 * @return This Cell, for a fluent interface
	 */
	public Cell setVAlign(final VAlign vAlign) {
		switch (vAlign) {
		case TOP:
			this.vAlign = 0;
			break;
		case MIDDLE:
			this.vAlign = 0.5f;
			break;
		case BOTTOM:
			this.vAlign = 1;
			break;
		default:
			this.vAlign = -1;
			break;
		}
		return this;
	}
}
