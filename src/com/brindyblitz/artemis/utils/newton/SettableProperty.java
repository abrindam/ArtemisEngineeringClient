package com.brindyblitz.artemis.utils.newton;

public class SettableProperty<T> extends AbstractProperty<T> implements Property<T> {

	private T value;
	
	public SettableProperty() {
		this.triggerChange();
	}
	
	public SettableProperty(T value) {
		this.value = value;
		this.triggerChange();
	}
	
	public void set(T value) {
		if (this.value.equals(value)) {
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
