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

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import boxtable.table.Table;

/**
 * A cell with a nested table
 * 
 * @author Dominik Helm
 */
public class TableCell extends Cell {
	/** The table to be rendered in this cell */
	private final Table table;

	/**
	 * Creates a new TableCell for a specified table
	 * 
	 * @param table
	 *            The table to be rendered in this cell
	 */
	public TableCell(final Table table) {
		this.table = table;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see boxtable.cell.Cell#getHeight(float)
	 */
	@Override
	public float getHeight(final float width) throws IOException {
		return table.getHeight(width);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see boxtable.cell.Cell#render(org.apache.pdfbox.pdmodel.PDDocument, org.apache.pdfbox.pdmodel.PDPageContentStream, float, float, float, float)
	 */
	@Override
	public void render(final PDDocument document, final PDPageContentStream stream, final float left, final float top, final float width, final float height)
			throws IOException {
		table.renderRows(document, stream, 0, table.getNumRows(), width, left, top);
	}
}
