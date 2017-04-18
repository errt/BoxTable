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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import boxtable.common.Bordered;

/**
 * Represents a table or part of it to which events can be registered
 * 
 * @author Dominik Helm
 */
public class TableEventSource extends Bordered {
	/** Registered event handlers */
	protected final Map<EventType, List<Consumer<TableEvent>>> eventHandlers = new HashMap<>();

	/**
	 * Adds an event handler for a specified event type to this element
	 * 
	 * @param event
	 *            The type of the event listened to
	 * @param handler
	 *            The handler to be executed once the event happens
	 * @return This TableEventSource, for a fluent interface
	 */
	public TableEventSource addEventHandler(final EventType event, final Consumer<TableEvent> handler) {
		if (!eventHandlers.containsKey(event)) {
			eventHandlers.put(event, new LinkedList<>());
		}
		eventHandlers.get(event).add(handler);

		return this;
	}

	/**
	 * Notifies all handlers registered for the specified event type
	 * 
	 * @param type
	 *            The type of the current event
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
	public void handleEvent(final EventType type, final PDDocument document, final PDPageContentStream stream, final float left, final float top,
			final float width, final float height) {
		if (eventHandlers.containsKey(type)) {
			final TableEvent event = new TableEvent(document, stream, left, top, width, height);
			for (final Consumer<TableEvent> handler : eventHandlers.get(type)) {
				handler.accept(event);
			}
		}
	}
}
