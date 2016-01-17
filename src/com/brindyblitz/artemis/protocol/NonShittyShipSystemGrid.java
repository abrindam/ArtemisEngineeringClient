package com.brindyblitz.artemis.protocol;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import net.dhleong.acl.enums.ShipSystem;
import net.dhleong.acl.util.GridCoord;
import net.dhleong.acl.util.ShipSystemGrid;

public class NonShittyShipSystemGrid extends ShipSystemGrid {
	
	public static final int GRID_SIZE_X = 5;
	public static final int GRID_SIZE_Y = 5;
	public static final int GRID_SIZE_Z = 10;
	
	
	public void addNode(ShipSystem system, GridCoord coord) {
		GridEntry entry = createGridEntry(system, getSystemCount(system));
		addEntry(coord, entry);
		incrementSystemCount(system);
	}
	
	
	
	private void incrementSystemCount(ShipSystem system){
		try {
			Field field = this.getClass().getSuperclass().getDeclaredField("mSystemCounts");
			field.setAccessible(true);
			int[] mSystemCounts = (int[]) field.get(this);
			mSystemCounts[system.ordinal()]++;
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException("Reflection black magic failed", e);
		}
		
	}

	@SuppressWarnings("unchecked")
	private void addEntry(GridCoord coord, GridEntry entry){
		try {
			Field field = this.getClass().getSuperclass().getDeclaredField("mSystems");
			field.setAccessible(true);
			Map<GridCoord, GridEntry> systems = (Map<GridCoord, GridEntry>) field.get(this);
			systems.put(coord, entry);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException("Reflection black magic failed", e);	
		}
		
	}

	private GridEntry createGridEntry(ShipSystem system, int systemCount) {
		try {
			Constructor<?> constructor = GridEntry.class.getDeclaredConstructors()[0];
			constructor.setAccessible(true);
			return (GridEntry) constructor.newInstance(system, systemCount);
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException | InstantiationException e) {
			throw new RuntimeException("Reflection black magic failed", e);	
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof RuntimeException) {
				throw (RuntimeException) e.getTargetException();
			}
			throw new RuntimeException("Unexpected non-runtime exception", e);
		}
	}
}
