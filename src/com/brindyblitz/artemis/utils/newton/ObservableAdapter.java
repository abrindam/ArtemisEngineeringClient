package com.brindyblitz.artemis.utils.newton;

import java.util.function.Consumer;

import com.brindyblitz.artemis.utils.EventEmitter;

public class ObservableAdapter implements Observable<Void> {

	private static final Object CHANGE = new Object();
	private EventEmitter<Object> eventEmitter = new EventEmitter<>();
	
	public void triggerChange() {
		eventEmitter.emit(CHANGE);
	}
	
	@Override
	public void onChange(Consumer<Void> callback) {
		eventEmitter.on(CHANGE, () -> callback.accept(null));
	}

	@Override
	public void onChange(Runnable callback) {
		eventEmitter.on(CHANGE, callback);

	}
	
}
