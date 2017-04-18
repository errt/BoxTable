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
package boxtable.event;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

/**
 * Represents an event during the rendering of a table
 * 
 * @author Dominik Helm
 */
public class TableEvent {
	/** Document the table is rendered to */
	private final PDDocument document;
	/** Stream used to render the table */
	private final PDPageContentStream stream;

	/** Left edge of current element */
	private final float left;
	/** Top edge of current element */
	private final float top;
	/** Width of current element */
	private final float width;
	/** Height of current element */
	private final float height;

	/**
	 * Creates a new TableEvent
	 * 
	 * @param document
	 *            The document the table is rendered to
	 * @param stream
	 *            The PDPageContentStream used to render the table
	 * @param left
	 *            The left edge of the current element
	 * @param top
	 *            The top edge of the current element
	 * @param width
	 *            The width of the current element
	 * @param height
	 *            The height of the current element
	 */
	public TableEvent(final PDDocument document, final PDPageContentStream stream, final float left, final float top, final float width, final float height) {
		this.document = document;
		this.stream = stream;
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
	}

	/**
	 * Returns the document the table is rendered to
	 * 
	 * @return The document
	 */
	public PDDocument getDocument() {
		return document;
	}

	/**
	 * Returns the height of the current element
	 * 
	 * @return The element's height
	 */
	public float getHeight() {
		return height;
	}

	/**
	 * Returns the left edge of the current element
	 * 
	 * @return The element's left edge
	 */
	public float getLeft() {
		return left;
	}

	/**
	 * Returns the stream used to render the table
	 * 
	 * @return The PDPageContentStream
	 */
	public PDPageContentStream getStream() {
		return stream;
	}

	/**
	 * Returns the top edge of the current element
	 * 
	 * @return The element's top edge
	 */
	public float getTop() {
		return top;
	}

	/**
	 * Returns the width of the current element
	 * 
	 * @return The element's width
	 */
	public float getWidth() {
		return width;
	}
}
