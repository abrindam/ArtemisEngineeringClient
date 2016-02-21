package com.brindyblitz.artemis.utils.newton;

import java.util.function.Consumer;

public interface Observable<T> {
	void onChange(Consumer<T> callback);
	void onChange(Runnable callback);
}
