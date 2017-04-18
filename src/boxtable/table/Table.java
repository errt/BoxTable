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

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import boxtable.cell.Cell;
import boxtable.cell.TextCell;
import boxtable.common.CellFiller;
import boxtable.event.EventType;
import boxtable.event.TableEventSource;

/**
 * Represents a table to be added to a PDF document
 * 
 * @author Dominik Helm
 */
public class Table extends TableEventSource {
	/** Columns of this table */
	private final List<Column> columns = new ArrayList<>();
	/** Rows of this table */
	private final List<Row> rows = new ArrayList<>();

	/** A CellFiller to paint cell backgrounds */
	private CellFiller filler = null;

	/** Number of header rows */
	private int numHeaderRows = 1;

	/** Caches the widths of the columns */
	private float[] columnWidths = null;

	/**
	 * Creates a table
	 */
	public Table() {
		setBorder(1, 1, 1, 1);
	}

	/**
	 * Adds a cell to a given row and column, setting its formatting
	 * 
	 * @param cell
	 *            The cell to add
	 * @param row
	 *            The row the cell will be added to
	 * @param column
	 *            The column to take formatting data from
	 */
	private void addCell(final Cell cell, final Row row, final int column) {
		Column col = columns.get(column);
		if (cell.getHAlign() == null) {
			cell.setHAlign(col.getHAlign());
		}
		if (cell.getVAlign() == null) {
			cell.setVAlign(col.getVAlign());
		}
		if (cell.getTopBorder() < 0) {
			cell.setTopBorder(col.getTopBorder());
		}
		if (cell.getLeftBorder() < 0) {
			cell.setLeftBorder(col.getLeftBorder());
		}
		if (cell.getRightBorder() < 0) {
			cell.setRightBorder(col.getRightBorder());
		}
		if (cell.getBottomBorder() < 0) {
			cell.setBottomBorder(col.getBottomBorder());
		}
		if (cell instanceof TextCell) {
			if (((TextCell) cell).getFont() == null) {
				((TextCell) cell).setFont(col.getFont());
			}
			if (((TextCell) cell).getMinFontSize() < 0) {
				((TextCell) cell).setMinFontSize(col.getMinFontSize());
			}
			if (((TextCell) cell).getMaxFontSize() < 0) {
				((TextCell) cell).setMaxFontSize(col.getMaxFontSize());
			}
		}

		row.addCell(cell);
	}

	/**
	 * Adds cells to this table, creating TextCells with toString() for non-cell objects
	 * 
	 * @param cells
	 *            The cells to add to the table
	 * @return This Table, for a fluent interface
	 */
	public Table addCells(final Object... cells) {
		Row row = null;
		int cols = 0;
		if (rows.isEmpty()) {
			row = new Row();
			rows.add(row);
		} else {
			row = rows.get(rows.size() - 1);
			for (final Cell cell : row.getCells()) {
				cols += cell.getColSpan();
			}
		}
		for (final Object cell : cells) {
			if (cell == null) {
				continue;
			}
			if (cols >= columns.size()) {
				row = new Row();
				rows.add(row);
				cols = 0;
			}
			if (cell instanceof Cell) {
				addCell((Cell) cell, row, cols);
				cols += ((Cell) cell).getColSpan();
			} else {
				addCell(new TextCell(cell.toString()), row, cols);
				++cols;
			}
		}
		return this;
	}

	/**
	 * Adds a column to this table
	 * 
	 * @param column
	 *            The column to be added
	 * @return This Table, for a fluent interface
	 */
	public Table addColumn(final Column column) {
		columns.add(column);
		return this;
	}

	/**
	 * Adds a row of cells to this table, creating TextCells with toString() for non-cell objects, completing the row with empty cells if necessary
	 * 
	 * @param cells
	 *            The cells to add to the table
	 * @return This Table, for a fluent interface
	 */
	public Table addRow(final Object... cells) {
		return addRowAtIndex(rows.size(), cells);
	}

	/**
	 * Adds a row of cells to this table at a specified index, creating TextCells with toString() for non-cell objects, completing the row with empty cells if
	 * necessary
	 * 
	 * @param index
	 *            The index the row is added at
	 * @param cells
	 *            The cells to add to the table
	 * @return This Table, for a fluent interface
	 */
	public Table addRowAtIndex(final int index, final Object... cells) {
		final Row row = new Row();
		for (int i = 0, j = 0; i < columns.size();) {
			if (j < cells.length) {
				final Object cell = cells[j];
				if (cell == null) {
					continue;
				}
				if (cell instanceof Cell) {
					addCell((Cell) cell, row, i);
					i += ((Cell) cell).getColSpan();
				} else {
					addCell(new TextCell(cell.toString()), row, i);
					++i;
				}
			} else {
				addCell(new Cell(), row, i);
				++i;
			}
			++j;
		}
		rows.add(index, row);

		return this;
	}

	/**
	 * Completes the current row by adding empty cells as necessary
	 * 
	 * @return This Table, for a fluent interface
	 */
	public Table completeRow() {
		if (!rows.isEmpty()) {
			final Row currentRow = rows.get(rows.size() - 1);
			int numCols = 0;
			for (int i = 0; i < currentRow.size(); ++i) {
				final Cell cell = currentRow.getCell(i);
				numCols += cell.getColSpan();
			}
			while (numCols < columns.size()) {
				addCell(new Cell(), currentRow, numCols);
				++numCols;
			}
		}
		return this;
	}

	/**
	 * Returns the widths for a column
	 * 
	 * @param index
	 *            The index of the column
	 * @return The column's width
	 * @throws IOException
	 *             If accessing information for calculating the width fails
	 */
	private float getColumnWidth(final int index) throws IOException {
		final Column column = columns.get(index);
		final float minWidth = column.getMinWidth();
		final float maxWidth = column.getMaxWidth();
		if (minWidth == maxWidth) return minWidth;
		float result = minWidth;
		for (final Row row : rows) {
			Cell cell = null;
			int i = 0;
			for (int j = 0; i <= index; ++j) {
				cell = row.getCell(j);
				i += cell.getColSpan();
			}
			if (cell.getColSpan() != 1) {
				continue;
			}
			result = Math.max(result, cell.getWidth());
		}
		return Math.min(result, maxWidth);
	}

	/**
	 * Returns the widths for the columns of this table
	 * 
	 * @param width
	 *            The width of the table
	 * @return The column widths
	 * @throws IOException
	 *             If accessing information for calculating the width fails
	 */
	private float[] getColumnWidths(final float width) throws IOException {
		if (columnWidths != null) return columnWidths;
		columnWidths = new float[columns.size()];
		float sum = 0;
		for (int i = 0; i < columns.size() - 1; ++i) {
			sum += columnWidths[i] = getColumnWidth(i);
		}
		columnWidths[columnWidths.length - 1] = width - sum;
		return columnWidths;
	}

	/**
	 * Returns the height of this table
	 * 
	 * @param width
	 *            The width of the table
	 * @return The height
	 * @throws IOException
	 *             If accessing information for calculating the height fails
	 */
	public float getHeight(final float width) throws IOException {
		float[] colWidths = getColumnWidths(width);
		float height = 0;
		for (int i = 0; i < rows.size(); ++i) {
			height += rows.get(i).getHeight(colWidths);
		}
		return height;
	}

	/**
	 * Returns the number of columns of this table
	 * 
	 * @return The number of columns
	 */
	public int getNumColumns() {
		return columns.size();
	}

	/**
	 * Returns the number of rows in this table's header
	 * 
	 * @return The number of header rows
	 */
	public int getNumHeaderRows() {
		return numHeaderRows;
	}

	/**
	 * Returns the number of rows of this table
	 * 
	 * @return The number of rows
	 */
	public int getNumRows() {
		return rows.size();
	}

	/**
	 * Returns the list of rows of this table
	 * 
	 * @return The list of rows
	 */
	public List<Row> getRows() {
		return rows;
	}

	/**
	 * Starts a new page with the same size as the last one
	 * 
	 * @param document
	 *            The document the table is rendered to
	 * @param stream
	 *            The PDPageContentStream used to render the table up to now (will be closed after calling this method)
	 * @return A new PDPageContentStream for rendering to the new page
	 * @throws IOException
	 *             If writing to the streams fails
	 */
	private PDPageContentStream newPage(final PDDocument document, final PDPageContentStream stream) throws IOException {
		final PDRectangle pageSize = document.getPage(document.getNumberOfPages() - 1).getMediaBox();
		handleEvent(EventType.END_PAGE, document, stream, 0, pageSize.getHeight(), pageSize.getWidth(), pageSize.getHeight());
		stream.close();
		final PDPage page = new PDPage(pageSize);
		document.addPage(page);
		PDPageContentStream newStream = new PDPageContentStream(document, page, AppendMode.APPEND, true);
		handleEvent(EventType.BEGIN_PAGE, document, newStream, 0, pageSize.getHeight(), pageSize.getWidth(), pageSize.getHeight());
		return newStream;
	}

	/**
	 * Renders this table to a document
	 * 
	 * @param document
	 *            The document this table will be rendered to
	 * @param width
	 *            The width of the table
	 * @param left
	 *            The left edge of the table
	 * @param top
	 *            The top edge of the table
	 * @param paddingTop
	 *            The amount of free space at the top of a new page (if a page break is necessary)
	 * @param paddingBottom
	 *            The minimal amount of free space at the bottom of the page before inserting a page break
	 * @return The bottom edge of the last rendered table part
	 * @throws IOException
	 *             If writing to the document fails
	 */
	@SuppressWarnings("resource")
	public float render(final PDDocument document, final float width, final float left, float top, final float paddingTop, final float paddingBottom)
			throws IOException {
		float yPos = top;
		final PDPage page = document.getPage(document.getNumberOfPages() - 1);
		final PDRectangle pageSize = page.getMediaBox();
		PDPageContentStream stream = new PDPageContentStream(document, page, AppendMode.APPEND, true);
		float height = getHeight(width);
		if (height > pageSize.getHeight() - paddingTop - paddingBottom) {
			final float[] colWidths = getColumnWidths(width);
			for (int i = 0; i < rows.size(); ++i) {
				if (rows.get(i).getHeight(colWidths) > yPos - paddingBottom) {
					drawBorder(stream, left, top, width, top - yPos);
					stream = newPage(document, stream);
					top = pageSize.getHeight() - paddingTop;
					yPos = top;
					yPos = renderRows(document, stream, 0, getNumHeaderRows(), width, left, yPos);
					i = Math.max(i, getNumHeaderRows());
				}
				yPos = renderRows(document, stream, i, i + 1, width, left, yPos);
			}
			drawBorder(stream, left, top, width, top - yPos);

			handleEvent(EventType.AFTER_TABLE, document, stream, left, top, width, top - yPos);
		} else {
			if (height > top - paddingBottom) {
				stream = newPage(document, stream);
				top = pageSize.getHeight() - paddingTop;
				yPos = top;
			}
			yPos = renderRows(document, stream, 0, -1, width, left, yPos);
			drawBorder(stream, left, top, width, top - yPos);
			handleEvent(EventType.AFTER_TABLE, document, stream, left, top, width, top - yPos);
		}
		stream.close();

		return yPos;
	}

	/**
	 * Renders a subset of the rows of this table
	 * 
	 * @param document
	 *            The document the table is rendered to
	 * @param stream
	 *            The PDPageContentStream used to render the rows
	 * @param startIndex
	 *            The start of the rows to be rendered (inclusive)
	 * @param endIndex
	 *            The end of the rows to be rendered (exclusive) or -1 if all rows up to the last one are to be rendered
	 * @param width
	 *            The width of the table
	 * @param left
	 *            The left edge of the rendered rows
	 * @param top
	 *            The top edge of the rendered rows
	 * @return The bottom edge of the last rendered row
	 * @throws IOException
	 *             If writing to the stream fails
	 */
	public float renderRows(final PDDocument document, final PDPageContentStream stream, final int startIndex, int endIndex, final float width,
			final float left, final float top) throws IOException {
		if (endIndex == -1) {
			endIndex = rows.size();
		}

		stream.setStrokingColor(Color.BLACK);

		final float[] colWidths = getColumnWidths(width);
		float yPos = top;

		for (int i = startIndex; i < endIndex; ++i) {
			final Row row = rows.get(i);
			final float height = row.getHeight(colWidths);

			handleEvent(EventType.BEFORE_ROW, document, stream, left, yPos, width, height);
			row.handleEvent(EventType.BEFORE_ROW, document, stream, left, yPos, width, height);

			row.render(this, document, stream, i, colWidths, filler, left, yPos, width, height);

			row.handleEvent(EventType.AFTER_ROW, document, stream, left, yPos, width, height);
			handleEvent(EventType.AFTER_ROW, document, stream, left, yPos, width, height);

			yPos -= height;
		}

		return yPos;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see boxtable.common.Bordered#setBorder(float, float, float, float)
	 */
	@Override
	public Table setBorder(final float top, final float left, final float right, final float bottom) {
		topBorder = top;
		leftBorder = left;
		rightBorder = right;
		bottomBorder = bottom;
		return this;
	}

	/**
	 * Sets a CellFiller to paint cell backgrounds
	 * 
	 * @param filler
	 *            The filler that decides the cell background colors
	 * @return This Table, for a fluent interface
	 */
	public Table setFiller(final CellFiller filler) {
		this.filler = filler;
		return this;
	}

	/**
	 * Sets the number of rows in this table's header
	 * 
	 * @param numHeaderRows
	 *            The number of header rows
	 * @return This Table, for a fluent interface
	 */
	public Table setNumHeaderRows(final int numHeaderRows) {
		this.numHeaderRows = numHeaderRows;
		return this;
	}
}
