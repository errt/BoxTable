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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import boxtable.cell.Cell;
import boxtable.common.CellFiller;
import boxtable.event.EventType;
import boxtable.event.TableEventSource;

/**
 * Represents a row in a table
 * 
 * @author Dominik Helm
 */
public class Row extends TableEventSource {
	/** Cells in this row */
	private final List<Cell> cells = new ArrayList<>();

	/** Caches the row height */
	private float height = -1;

	/**
	 * Adds a cell to this row
	 * 
	 * @param cell
	 *            The cell to add to this row
	 */
	public void addCell(final Cell cell) {
		cells.add(cell);
	}

	/**
	 * Returns the cell at a specified index
	 * 
	 * @param index
	 *            The index of the cell (not necessarily the column number)
	 * @return The cell
	 */
	public Cell getCell(final int index) {
		return cells.get(index);
	}

	/**
	 * Returns the list of all cells of this row
	 * 
	 * @return The list of cells
	 */
	public List<Cell> getCells() {
		return cells;
	}

	/**
	 * Returns the height of this row
	 * 
	 * @param colWidths
	 *            The widths of the individual columns of the table
	 * @return The height
	 * @throws IOException
	 *             If accessing information for calculating the height fails
	 */
	public float getHeight(final float[] colWidths) throws IOException {
		if (height >= 0) return height;
		height = 0;
		int colId = 0;
		for (int i = 0; i < cells.size(); ++i) {
			Cell current = cells.get(i);
			if (current.getRowSpan() > 1) continue;
			float cellWidth = colWidths[colId];
			++colId;
			for (int j = 1; j < current.getColSpan(); ++j) {
				cellWidth += colWidths[colId];
				++colId;
			}
			height = Math.max(height, current.getHeight(cellWidth));
		}
		return height;
	}

	/**
	 * Renders this row at the specified position
	 * 
	 * @param table
	 *            The table this row is part of
	 * @param document
	 *            The document this cell is rendered to
	 * @param stream
	 *            The PDPageContentStream used to render this cell
	 * @param index
	 *            The index of this row in the table
	 * @param colWidths
	 *            The widths of the individual columns of the table
	 * @param filler
	 *            The CellFiller for the table
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
	public void render(final Table table, final PDDocument document, final PDPageContentStream stream, final int index, final float[] colWidths,
			final CellFiller filler, final float left, final float top, final float width, final float height) throws IOException {
		drawBorder(stream, left, top, width, height);

		float xPos = left;
		int colId = 0;
		for (final Cell cell : getCells()) {
			float cellWidth = colWidths[colId];
			for (int j = 1; j < cell.getColSpan(); ++j) {
				cellWidth += colWidths[colId + j];
			}
			float cellHeight = height;
			for (int j = 1; j < cell.getRowSpan(); ++j) {
				cellHeight += getHeight(colWidths);
			}

			if (filler != null) {
				filler.fill(stream, index, colId, xPos, top, cellWidth, cellHeight);
			}

			table.handleEvent(EventType.BEFORE_CELL, document, stream, xPos, top, cellWidth, cellHeight);
			handleEvent(EventType.BEFORE_CELL, document, stream, xPos, top, cellWidth, cellHeight);
			cell.handleEvent(EventType.BEFORE_CELL, document, stream, xPos, top, cellWidth, cellHeight);

			cell.render(document, stream, xPos, top, cellWidth, cellHeight);

			cell.handleEvent(EventType.AFTER_CELL, document, stream, xPos, top, cellWidth, cellHeight);
			handleEvent(EventType.AFTER_CELL, document, stream, xPos, top, cellWidth, cellHeight);
			table.handleEvent(EventType.AFTER_CELL, document, stream, xPos, top, cellWidth, cellHeight);

			colId += cell.getColSpan();
			xPos += cellWidth;
		}
	}

	/**
	 * Returns the number of cells in this row
	 * 
	 * @return The number of cells
	 */
	public int size() {
		return cells.size();
	}
}
