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

import java.awt.Color;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

/**
 * Abstract class for coloring a cell's background
 * 
 * @author Dominik Helm
 */
public abstract class CellFiller {
	/**
	 * A simple CellFiller that paints a background for every other column
	 * 
	 * @author Dominik Helm
	 */
	public static class ColumnStripe extends CellFiller {
		/** Color for the cell's background */
		protected final Color color;

		/** Color odd columns instead of even ones */
		private boolean inverted = false;

		/**
		 * Creates a ColumnStripe with a given background color
		 * 
		 * @param color
		 *            The color for the cell's background
		 */
		public ColumnStripe(final Color color) {
			this.color = color;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see boxtable.common.CellFiller#fill(org.apache.pdfbox.pdmodel.PDPageContentStream, int, int, float, float, float, float)
		 */
		@Override
		public void fill(final PDPageContentStream stream, final int row, final int column, final float left, final float top, final float width,
				final float height) throws IOException {
			if (column % 2 == (inverted ? 1 : 0)) {
				stream.setNonStrokingColor(color);
				stream.addRect(left, top - height, width, height);
				stream.fill();
			}
		}

		/**
		 * Sets whether odd columns should be painted (default is even columns)
		 * 
		 * @param invert
		 *            True to color odd columns, false for even columns
		 * @return This CellFiller for a fluent interface
		 */
		public ColumnStripe invert(final boolean invert) {
			inverted = invert;
			return this;
		}
	};

	/**
	 * A simple CellFiller that paints a background for every other row
	 * 
	 * @author Dominik Helm
	 */
	public static class RowStripe extends CellFiller {
		/** Color for the cell's background */
		protected final Color color;

		/** Color odd rows instead of even ones */
		private boolean inverted = false;

		/**
		 * Creates a RowStripe with a given background color
		 * 
		 * @param color
		 *            The color for the cell's background
		 */
		public RowStripe(final Color color) {
			this.color = color;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see boxtable.common.CellFiller#fill(org.apache.pdfbox.pdmodel.PDPageContentStream, int, int, float, float, float, float)
		 */
		@Override
		public void fill(final PDPageContentStream stream, final int row, final int column, final float left, final float top, final float width,
				final float height) throws IOException {
			if (row % 2 == (inverted ? 1 : 0)) {
				stream.setNonStrokingColor(color);
				stream.addRect(left, top - height, width, height);
				stream.fill();
			}
		}

		/**
		 * Sets whether odd rows should be painted (default is even rows)
		 * 
		 * @param invert
		 *            True to color odd rows, false for even rows
		 * @return This CellFiller for a fluent interface
		 */
		public RowStripe invert(final boolean invert) {
			inverted = invert;
			return this;
		}
	};

	/**
	 * Paints a cell's background
	 * 
	 * @param stream
	 *            The PDPageContentStream used to paint the background
	 * @param row
	 *            The index of the cell's row
	 * @param column
	 *            The index of the cell's column
	 * @param left
	 *            The left edge of the cell
	 * @param top
	 *            The top edge of the cell
	 * @param width
	 *            The width of the cell
	 * @param height
	 *            The height of the cell
	 * @throws IOException
	 *             If writing to the stream fails
	 */
	public abstract void fill(final PDPageContentStream stream, final int row, final int column, final float left, final float top, final float width,
			final float height) throws IOException;
}
