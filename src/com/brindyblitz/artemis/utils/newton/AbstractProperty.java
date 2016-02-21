package com.brindyblitz.artemis.utils.newton;

import java.util.function.Consumer;

import com.brindyblitz.artemis.utils.EventEmitter;

public abstract class AbstractProperty<T> implements Property<T> {

	private static final Object CHANGE = new Object();
	private EventEmitter<Object> eventEmitter = new EventEmitter<>();
	
	protected void triggerChange() {
		eventEmitter.emit(CHANGE);
	}
	
	@Override
	public void onChange(Consumer<T> callback) {
		eventEmitter.on(CHANGE, () -> callback.accept(get()));

	}

	@Override
	public void onChange(Runnable callback) {
		eventEmitter.on(CHANGE, callback);

	}
	
}
