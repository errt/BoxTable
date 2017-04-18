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

import org.apache.pdfbox.pdmodel.PDPageContentStream;

/**
 * Basis for objects with border
 * 
 * @author Dominik Helm
 */
public class Bordered {
	/** Line width for the top border */
	protected float topBorder = -1;
	/** Line width for the top border */
	protected float leftBorder = -1;
	/** Line width for the top border */
	protected float rightBorder = -1;
	/** Line width for the top border */
	protected float bottomBorder = -1;

	/** Line width for the top border */

	/**
	 * Renders the borders
	 * 
	 * @param stream
	 *            The stream used to render the borders
	 * @param left
	 *            The coordinate of the left edge of the object
	 * @param top
	 *            The coordinate of the top edge of the object
	 * @param width
	 *            The width the object will be rendered
	 * @param height
	 *            The height the object will be rendered
	 * @throws IOException
	 *             If writing to the stream fails
	 */
	protected void drawBorder(final PDPageContentStream stream, final float left, final float top, final float width, final float height) throws IOException {
		stream.moveTo(left, top);
		float lineWidth = -1;
		if (topBorder > 0) {
			stream.setLineWidth(topBorder);
			lineWidth = topBorder;
			stream.lineTo(left + width, top);
		} else {
			stream.moveTo(left + width, top);
		}
		if (rightBorder > 0) {
			if (lineWidth != rightBorder) {
				stream.setLineWidth(rightBorder);
			}
			lineWidth = rightBorder;
			stream.lineTo(left + width, top - height);
		} else {
			stream.moveTo(left + width, top - height);
		}
		if (bottomBorder > 0) {
			if (lineWidth != bottomBorder) {
				stream.setLineWidth(bottomBorder);
			}
			lineWidth = bottomBorder;
			stream.lineTo(left, top - height);
		} else {
			stream.moveTo(left, top - height);
		}
		if (leftBorder > 0) {
			if (lineWidth != leftBorder) {
				stream.setLineWidth(leftBorder);
			}
			lineWidth = leftBorder;
			stream.setLineWidth(leftBorder);
			stream.lineTo(left, top);
		} else {
			stream.moveTo(left, top);
		}
		if (topBorder > 0) {
			if (lineWidth != topBorder) {
				stream.setLineWidth(topBorder);
			}
			lineWidth = topBorder;
			stream.setLineWidth(topBorder);
			stream.lineTo(left + width, top);
		}
		stream.stroke();
	}

	/**
	 * Returns the line width of the bottom border
	 * 
	 * @return The line width of the bottom border
	 */
	public float getBottomBorder() {
		return bottomBorder;
	}

	/**
	 * Returns the line width of the left border
	 * 
	 * @return The line width of the left border
	 */
	public float getLeftBorder() {
		return leftBorder;
	}

	/**
	 * Returns the line width of the right border
	 * 
	 * @return The line width of the right border
	 */
	public float getRightBorder() {
		return rightBorder;
	}

	/**
	 * Returns the line width of the top border
	 * 
	 * @return The line width of the top border
	 */
	public float getTopBorder() {
		return topBorder;
	}

	/**
	 * Sets the line widths for the borders
	 * 
	 * @param top
	 *            The line width for the top border, negative if it is to be inherited
	 * @param left
	 *            The line width for the left border, negative if it is to be inherited
	 * @param right
	 *            The line width for the right border, negative if it is to be inherited
	 * @param bottom
	 *            The line width for the bottom border, negative if it is to be inherited
	 * @return This Bordered, for a fluent interface
	 */
	public Bordered setBorder(final float top, final float left, final float right, final float bottom) {
		topBorder = top;
		leftBorder = left;
		rightBorder = right;
		bottomBorder = bottom;
		return this;
	}

	/**
	 * Sets the line width for the bottom border
	 * 
	 * @param bottomBorder
	 *            The line width for the bottom border, negative if it is to be inherited
	 * @return This Bordered, for a fluent interface
	 */
	public Bordered setBottomBorder(final float bottomBorder) {
		this.bottomBorder = bottomBorder;
		return this;
	}

	/**
	 * Sets the line width for the left border
	 * 
	 * @param leftBorder
	 *            The line width for the left border, negative if it is to be inherited
	 * @return This Bordered, for a fluent interface
	 */
	public Bordered setLeftBorder(final float leftBorder) {
		this.leftBorder = leftBorder;
		return this;
	}

	/**
	 * Sets the line width for the right border
	 * 
	 * @param rightBorder
	 *            The line width for the right border, negative if it is to be inherited
	 * @return This Bordered, for a fluent interface
	 */
	public Bordered setRightBorder(final float rightBorder) {
		this.rightBorder = rightBorder;
		return this;
	}

	/**
	 * Sets the line width for the top border
	 * 
	 * @param topBorder
	 *            The line width for the top border, negative if it is to be inherited
	 * @return This Bordered, for a fluent interface
	 */
	public Bordered setTopBorder(final float topBorder) {
		this.topBorder = topBorder;
		return this;
	}
}
