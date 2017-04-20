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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

/**
 * A cell that shows an image
 * 
 * @author Dominik Helm
 */
public class ImageCell extends Cell {
	/** The file the image resides at */
	private final File file;

	/**
	 * Creates an ImageCell for a specific image
	 * 
	 * @param file
	 *            The file the image resides at
	 */
	public ImageCell(final File file) {
		this.file = file;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see boxtable.cell.Cell#render(org.apache.pdfbox.pdmodel.PDDocument, org.apache.pdfbox.pdmodel.PDPageContentStream, float, float, float, float)
	 */
	@Override
	public void render(final PDDocument document, final PDPageContentStream stream, final float left, final float top, final float width, final float height)
			throws IOException {
		final BufferedImage image = ImageIO.read(file);

		float contentWidth = width - (leftBorder + rightBorder) / 2 - leftPadding - rightPadding;
		float contentHeight = height - (topBorder + bottomBorder) / 2 - topPadding - bottomPadding;

		float imgWidth = image.getWidth();
		float imgHeight = image.getHeight();
		final float ratio = imgWidth / imgHeight;
		if (imgWidth > contentWidth) {
			imgWidth = contentWidth;
			imgHeight = 1 / ratio * imgWidth;
		}
		if (imgHeight > contentHeight) {
			imgHeight = contentHeight;
			imgWidth = ratio * imgHeight;
		}

		super.render(document, stream, left + (width - imgWidth) * hAlign, top - (height - imgHeight) * vAlign, imgWidth, imgHeight);

		final PDImageXObject imageObject = JPEGFactory.createFromImage(document, image);
		stream.drawImage(imageObject, left + (width - imgWidth) * hAlign + leftBorder / 2 + leftPadding,
				top - height + (height - imgHeight) * (1 - vAlign) - topBorder / 2 - topPadding,
				imgWidth, imgHeight);
	}
}
