package com.brindyblitz.artemis.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventEmitter<E> {
	
	Map<E, List<Consumer<E>>> handler = new HashMap<>();
	public final EventSubscriber<E> subscriber = new EventSubscriber<>(this);

	public void emit(E event) {
		List<Consumer<E>> list = handler.get(event);
		if (list != null) {
			for (Consumer<E> consumer : list) {
				consumer.accept(event);
			}
		}
	}
	
	public void on(E event, Consumer<E> callback) {
		List<Consumer<E>> list = handler.get(event);
		if (list == null) {
			list = new ArrayList<>();
			handler.put(event, list);
		}
		list.add(callback);
	}
	
	public void on(E event, Runnable runnable) {
		on(event, (e) -> runnable.run());
	}
	
}
