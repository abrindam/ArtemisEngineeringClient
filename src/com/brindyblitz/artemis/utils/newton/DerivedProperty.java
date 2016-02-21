package com.brindyblitz.artemis.utils.newton;

import java.util.function.Supplier;

public class DerivedProperty<T> extends AbstractProperty<T> implements Property<T> {

	
	private Supplier<T> calculator;
	private T value;
	
	public DerivedProperty(Supplier<T> calculator, Observable<?>...dependencies) {
		this.calculator = calculator;
		
		for (Observable<?> observable : dependencies) {
			observable.onChange(() -> doUpdate(false));
		}
		
		doUpdate(true);
	}
	
	private void doUpdate(boolean firstTime) {
		T value = calculator.get();
		if (!firstTime && ((this.value == null && value == null) || this.value.equals(value))) {
			return;
		};
		this.value = value;
		this.triggerChange();
	}
	
	@Override
	public T get() {
		return value;
	}

}
