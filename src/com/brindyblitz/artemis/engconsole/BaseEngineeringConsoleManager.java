package com.brindyblitz.artemis.engconsole;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseEngineeringConsoleManager implements EngineeringConsoleManager {

	private List<EngineeringConsoleChangeListener> listeners = new ArrayList<>();
	
	
	protected void fireChange() {
		for (EngineeringConsoleChangeListener listener: listeners) {
			listener.onChange();
		}
	}
	
	public void addChangeListener(EngineeringConsoleChangeListener listener) {
		this.listeners.add(listener);
	}

}
