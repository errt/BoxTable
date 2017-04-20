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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import boxtable.common.Text;

/**
 * A cell containing text
 * 
 * @author Dominik Helm
 */
public class TextCell extends Cell {
	/** Individual parts of this cell's text */
	private final List<Text> text = new ArrayList<>();

	/** Font used for rendering the text in this cell */
	private PDFont font = null;
	/** Minimal font size for rendering the text in this cell */
	private float minFontSize = -1;
	/** Maximal font size for rendering the text in this cell */
	private float maxFontSize = -1;

	/** Lay out text with equal width for each part */
	private boolean equallySpaced = false;

	/** Draw lines between rows of text */
	private boolean drawRows = false;

	/** Caches lines of the broken text */
	private List<List<Text>> lines = null;

	/**
	 * Creates an empty TextCell
	 */
	public TextCell() {
		leftPadding = 1;
		rightPadding = 1;
	}

	/**
	 * Creates a TextCell with a given text
	 * 
	 * @param text
	 *            The text for this cell
	 */
	public TextCell(final String text) {
		this();
		addText(new Text(text));
	}

	/**
	 * Creates a TextCell with a given text, font and font size
	 * 
	 * @param text
	 *            The text for this cell
	 * @param font
	 *            The font used for this cell's text
	 * @param minFontSize
	 *            The minimal font size for this cell's text
	 * @param maxFontSize
	 *            The maximal font size for this cell's text
	 */
	public TextCell(final String text, final PDFont font, final float minFontSize, final float maxFontSize) {
		this();
		this.minFontSize = minFontSize;
		this.maxFontSize = maxFontSize;
		this.font = font;
		addText(text);
	}

	/**
	 * Adds a part of text to this cell
	 * 
	 * @param text
	 *            The text to add to the cell
	 * @return This TextCell, for a fluent interface
	 */
	public TextCell addText(final String text) {
		addText(new Text(text));
		return this;
	}

	/**
	 * Adds a part of formatted text to this cell
	 * 
	 * @param text
	 *            The text to add to the cell
	 * @return This TextCell, for a fluent interface
	 */
	public TextCell addText(final Text text) {
		this.text.add(text);
		return this;
	}

	/**
	 * Divides this cell's text into lines as given by line breaks in the text
	 * This method also divides texts into words for easier line breaking when calculating cell height/text size
	 * The result is cached in lines, as it should not change after adding the cell to the table
	 */
	private void breakLines() {
		if (lines == null) {
			lines = new ArrayList<>();
			lines.add(new ArrayList<>());
			if (text.size() == 0) {
				lines.get(0).add(new Text(""));
			} else {
				for (Text current : text) {
					final String[] split = current.getText().split("\r\n|\r|\n"); // Split at line breaks
					for (int i = 0; i < split.length; ++i) {
						if (i == 0) { // Add first part to the last line
							List<Text> currentLine = lines.get(lines.size() - 1);
							String[] words = split[i].split("\\s", -1);
							if (words.length == 0) {
								currentLine.add(current.copy(""));
							} else {
								for (String word : words) {
									currentLine.add(current.copy(word));
								}
							}
						} else if (i == split.length - 1) { // Create a new line for the last part
							List<Text> currentLine = new ArrayList<>();
							String[] words = split[i].split("\\s");
							if (words.length == 0) {
								currentLine.add(current.copy(""));
							} else {
								for (String word : words) {
									currentLine.add(current.copy(word));
								}
							}
							lines.add(currentLine);
						} else { // Inner parts form a line of themselves
							lines.add(Collections.singletonList(current.copy(split[i])));
						}
					}
				}
			}
		}
	}

	/**
	 * Breaks the text into lines to fulfill width constraint
	 * 
	 * @param width
	 *            The width of the cell
	 * @param fontSize
	 *            The font size the text will be rendered in
	 * @return A list of lines, each being a list of Texts that constitute that line
	 * @throws IOException
	 *             If accessing information needed to calculate text size fails
	 */
	private List<List<Text>> breakText(final float width, final float fontSize) throws IOException {
		// Equally spaced text is never broken
		if (equallySpaced) {
			return Collections.singletonList(text);
		}

		// Ensure that lines contains the lines from the text
		breakLines();

		final float adjustedWidth = width - leftPadding - rightPadding - (leftBorder + rightBorder) / 2;

		List<List<Text>> result = new ArrayList<>();
		for (List<Text> line : lines) {
			if (line.size() == 1) {
				result.add(line);
				continue;
			}
			List<Text> currentLine = new ArrayList<>();
			currentLine.add(line.get(0));
			float lineWidth = line.get(0).getWidth(font, fontSize);
			for (int i = 1; i < line.size(); ++i) {
				Text part = line.get(i);
				float textWidth = part.getWidth(font, fontSize);
				float spaceWidth = getTextWidth(" ", part.getFont() != null ? part.getFont() : font, part.getFontSize() > 0 ? part.getFontSize() : fontSize);
				if (lineWidth + textWidth + spaceWidth <= adjustedWidth) { // Current part fits into line, add it
					currentLine.add(part);
					lineWidth += textWidth + spaceWidth;
				} else { // Current part does not fit into line, start new line
					result.add(currentLine);
					currentLine = new ArrayList<>();
					currentLine.add(part);
					lineWidth = textWidth;
				}
			}
			result.add(currentLine);
		}
		return result;
	}

	/**
	 * Checks whether the text rendered at a specified font size fits into the given height and width
	 * 
	 * @param fontSize
	 *            The font size to check
	 * @param width
	 *            The width of the cell
	 * @param height
	 *            The height of the cell
	 * @return True, if the text fits, false otherwise
	 * @throws IOException
	 *             If accessing information needed to calculate text size fails
	 */
	private boolean checkFontSize(final float fontSize, final float width, final float height) throws IOException {
		if (getHeight(fontSize, width) > height) {
			return false;
		}
		final List<List<Text>> lines = breakText(width, fontSize);
		for (final List<Text> line : lines) {
			final float lineWidth = getLineWidth(line, fontSize);
			if (lineWidth > width) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Helper method to draw a fine line
	 * 
	 * @param stream
	 *            The stream used to draw the line
	 * @param left
	 *            The left end of the line
	 * @param top
	 *            The vertical position of the line
	 * @param width
	 *            The width of the line
	 * @throws IOException
	 *             If writing to the stream fails
	 */
	private void drawLine(final PDPageContentStream stream, final float left, final float top, final float width) throws IOException {
		stream.setLineWidth(0.25f);
		stream.moveTo(left, top);
		stream.lineTo(left + width, top);
		stream.stroke();
	}

	/**
	 * Returns the font set for this cell
	 * 
	 * @return The font or null if it is to be inherited by its column
	 */
	public PDFont getFont() {
		return font;
	}

	/**
	 * Finds the maximal font size in the font size constraints that fits the given cell width and height
	 * 
	 * @param width
	 *            The width of the cell
	 * @param height
	 *            The height of the cell
	 * @return A font size between minFontSize and maxFontSize
	 * @throws IOException
	 *             If accessing information needed to calculate text size fails
	 */
	private float getFontSize(final float width, final float height) throws IOException {
		if (checkFontSize(maxFontSize, width, height)) return maxFontSize;
		float lastFit = minFontSize;
		float fontSize = (minFontSize + maxFontSize) / 2;
		for (float step = (maxFontSize - minFontSize) / 4; step >= 0.01f; step /= 2) {
			if (checkFontSize(fontSize, width, height)) {
				lastFit = fontSize;
				fontSize += step;
			} else {
				fontSize -= step;
			}
		}
		return lastFit;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see boxtable.cell.Cell#getHeight(float)
	 */
	@Override
	public float getHeight(final float width) throws IOException {
		final float singleLineHeight = (font.getFontDescriptor().getAscent() - font.getFontDescriptor().getDescent()) / 1000 * maxFontSize + topPadding
				+ bottomPadding + (topBorder + bottomBorder) / 2;
		float fontSize = getFontSize(width, singleLineHeight);
		float height = Math.max(minHeight, getHeight(fontSize, width));
		if (height > singleLineHeight) {
			fontSize = getFontSize(width, Float.POSITIVE_INFINITY);
			height = Math.max(minHeight, getHeight(fontSize, width));
		}
		return height;
	}

	/**
	 * Returns the height of the complete text including padding and borders
	 * 
	 * @param fontSize
	 *            The font size used to render the text
	 * @param width
	 *            The width of the cell
	 * @return The height of the text without borders
	 * @throws IOException
	 *             If accessing information needed to calculate text size fails
	 */
	private float getHeight(final float fontSize, final float width) throws IOException {
		final int lines = breakText(width, fontSize).size();
		return font.getFontDescriptor().getAscent() / 1000 * fontSize * lines - font.getFontDescriptor().getDescent() / 1000 * fontSize
				+ (lines - 1) * fontSize / 10.0f + topPadding + bottomPadding + (topBorder + bottomBorder) / 2;
	}

	/**
	 * Returns the width of a line of text
	 * 
	 * @param line
	 *            The line of text as a list of its parts
	 * @param fontSize
	 *            The font size used to render the text
	 * @return The width of the line including padding and borders
	 * @throws IOException
	 *             If accessing information needed to calculate text size fails
	 */
	private float getLineWidth(final List<Text> line, final float fontSize) throws IOException {
		float lineWidth = leftPadding + rightPadding + (leftBorder + rightBorder) / 2;
		boolean first = true;
		for (final Text part : line) {
			if (first) {
				first = false;
			} else {
				lineWidth += getTextWidth(" ", part.getFont() != null ? part.getFont() : font, part.getFontSize() > 0 ? part.getFontSize() : fontSize);
			}
			lineWidth += part.getWidth(font, fontSize);
		}
		return lineWidth;
	}

	/**
	 * Returns the maximal font size used to render the text of this cell
	 * 
	 * @return The maximal font size
	 */
	public float getMaxFontSize() {
		return maxFontSize;
	}

	/**
	 * Returns the minimal font size used to render the text of this cell
	 * 
	 * @return The minimal font size
	 */
	public float getMinFontSize() {
		return minFontSize;
	}

	/**
	 * Returns the width of a particular string rendered in a give font and size
	 * 
	 * @param text
	 *            The string
	 * @param font
	 *            The font used to render the string
	 * @param fontSize
	 *            The font size used to render the string
	 * @return The width of the rendered string, without padding or borders
	 * @throws IOException
	 *             If accessing information needed to calculate text size fails
	 */
	private float getTextWidth(final String text, final PDFont font, final float fontSize) throws IOException {
		return font.getStringWidth(text) / 1000 * fontSize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see boxtable.cell.Cell#getWidth()
	 */
	@Override
	public float getWidth() throws IOException {
		if (colSpan > 1) return 0;
		float width = leftPadding + rightPadding + (leftBorder + rightBorder) / 2;
		for (final Text part : text) {
			width += part.getWidth(font, minFontSize);
		}
		return width * 1.001f; // Add a safety coefficient to prevent problems with rounding
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see boxtable.cell.Cell#render(org.apache.pdfbox.pdmodel.PDDocument, org.apache.pdfbox.pdmodel.PDPageContentStream, float, float, float, float)
	 */
	@Override
	public void render(final PDDocument document, final PDPageContentStream stream, final float left, final float top, final float width, final float height)
			throws IOException {
		final float fontSize = getFontSize(width, height);
		final float textHeight = font.getFontDescriptor().getAscent() / 1000 * fontSize;

		super.render(document, stream, left, top, width, height);

		stream.setNonStrokingColor(Color.BLACK);

		if (equallySpaced) {
			stream.setFont(font, fontSize);
			final float xStart = left + leftBorder / 2 + leftPadding;
			final float yPos = top - textHeight - font.getFontDescriptor().getDescent() / 1000 * fontSize - (height - textHeight) * vAlign - topBorder
					- topPadding - fontSize / 15;
			final float partialWidth = (width - leftPadding - rightPadding - (leftBorder + rightBorder) / 2) / (text.size() + 1);
			for (int i = 0; i < text.size(); ++i) {
				float xPos = xStart + (i + 1) * partialWidth - text.get(i).getWidth(font, fontSize) / 2;
				stream.beginText();
				stream.newLineAtOffset(xPos, yPos);
				stream.showText(text.get(i).getText());
				stream.endText();
			}
		} else {
			final List<List<Text>> lines = breakText(width, fontSize);

			if (drawRows) {
				stream.setLineWidth(bottomBorder);
				for (int i = 0; i < lines.size(); ++i) {
					stream.moveTo(left + leftBorder / 2, top - height - bottomBorder / 2 + i * height / lines.size());
					stream.lineTo(left + width - rightBorder / 2, top - height - bottomBorder / 2 + i * height / lines.size());
				}
				stream.stroke();
			}

			final float xStart = left + leftBorder + leftPadding;
			final float yStart = top - font.getFontDescriptor().getDescent() / 1000 * fontSize - topBorder / 2 - topPadding - fontSize / 15;

			for (int i = 0; i < lines.size(); ++i) {
				final List<Text> currentLine = lines.get(i);
				final float lineWidth = getLineWidth(currentLine, fontSize);
				float xPos = xStart + (width - lineWidth) * hAlign;
				final float yPos = yStart - textHeight * (i + 1) - i * fontSize / 10
						- (height - textHeight * lines.size() - (lines.size() - 1) * fontSize / 10) * vAlign;
				for (int j = 0; j < currentLine.size(); ++j) {
					final Text current = currentLine.get(j);
					PDFont textFont = current.getFont() != null ? current.getFont() : font;
					float textSize = current.getFontSize() > 0 ? current.getFontSize() : fontSize;
					stream.beginText();
					stream.newLineAtOffset(xPos, yPos + current.getVerticalOffset());
					stream.setFont(textFont, textSize);
					if (j != 0) {
						stream.showText(" ");
						xPos += getTextWidth(" ", textFont, textSize);
					}
					float textWidth = getTextWidth(current.getText(), textFont, textSize);
					stream.showText(current.getText());
					stream.endText();
					if (current.isStriked()) {
						drawLine(stream, xPos, yPos + current.getVerticalOffset() + font.getFontDescriptor().getXHeight() / 1000 * fontSize / 2, textWidth);
					}
					if (current.isUnderlined()) {
						drawLine(stream, xPos, yPos + current.getVerticalOffset() - fontSize / 10, textWidth);
					}
					xPos += textWidth;
				}
			}
		}
	}

	/**
	 * Sets whether to draw lines between rows of text
	 * 
	 * @param drawRows
	 *            True if lines should be drawn, false otherwise
	 * @return This TextCell, for a fluent interface
	 */
	public TextCell setDrawRows(final boolean drawRows) {
		this.drawRows = drawRows;
		return this;
	}

	/**
	 * Sets whether to lay out the parts of text with equal spacing
	 * 
	 * @param equallySpaced
	 *            True if text parts should be layed out with equal spacing, false otherwise
	 * @return This TextCell, for a fluent interface
	 */
	public TextCell setEquallySpaced(final boolean equallySpaced) {
		this.equallySpaced = equallySpaced;
		return this;
	}

	/**
	 * Sets the font to be used for rendering the text of this cell
	 * 
	 * @param font
	 *            The font, null if it should be inherited from the cell's columns
	 * @return This TextCell, for a fluent interface
	 */
	public TextCell setFont(final PDFont font) {
		this.font = font;
		return this;
	}

	/**
	 * Sets the maximal font size to be used for rendering the text of this cell
	 * 
	 * @param maxFontSize
	 *            The maximal font size
	 * @return This TextCell, for a fluent interface
	 */
	public TextCell setMaxFontSize(final float maxFontSize) {
		this.maxFontSize = maxFontSize;
		return this;
	}

	/**
	 * Sets the minimal font size to be used for rendering the text of this cell
	 * 
	 * @param minFontSize
	 *            The minimal font size
	 * @return This TextCell, for a fluent interface
	 */
	public TextCell setMinFontSize(final float minFontSize) {
		this.minFontSize = minFontSize;
		return this;
	}
}
