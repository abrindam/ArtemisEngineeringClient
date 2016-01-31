package com.brindyblitz.artemis.utils;

import java.util.function.Consumer;

public class EventSubscriber<E> {
	private EventEmitter<E> emitter;
	
	EventSubscriber(EventEmitter<E> emitter) {
		this.emitter = emitter;
	}
	
	public void on(E event, Consumer<E> callback) {
		this.emitter.on(event, callback);
	}
	
	public void on(E event, Runnable runnable) {
		this.emitter.on(event, runnable);
	}
}
