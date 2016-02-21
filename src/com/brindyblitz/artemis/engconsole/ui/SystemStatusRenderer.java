package com.brindyblitz.artemis.engconsole.ui;

import java.util.Arrays;
import java.util.List;

import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager;

import net.dhleong.acl.enums.ShipSystem;

public class SystemStatusRenderer {

	
	private EngineeringConsoleManager engineeringConsoleManager;

	public SystemStatusRenderer(EngineeringConsoleManager engineeringConsoleManager) {
		this.engineeringConsoleManager = engineeringConsoleManager;
	}
	
	public List<Interval> getSystemStatusAsIntervals(ShipSystem system) {
		int energy = engineeringConsoleManager.getSystemEnergyAllocated().get().get(system);
		int coolant = engineeringConsoleManager.getSystemCoolantAllocated().get().get(system);
		
		int cooledEnergyThreshold = SystemStatusRenderer.getCooledEnergyThreshold(coolant);
		
		if (energy == 100) {
			if (cooledEnergyThreshold > 100) {
				return Arrays.asList(
					new Interval(100, cooledEnergyThreshold, IntervalType.OVERCOOLED)
				);
			}
			else {
				return Arrays.asList();				
			}
		}
		else if (energy > 100) {
			if (cooledEnergyThreshold == energy) {
				return Arrays.asList(
						new Interval(100, energy, IntervalType.OVERCHARGED_COOLED)
				);
			}
			else if (cooledEnergyThreshold > energy) {
				return Arrays.asList(
					new Interval(100, energy, IntervalType.OVERCHARGED_COOLED),
					new Interval(energy, cooledEnergyThreshold, IntervalType.OVERCOOLED)
				);
			}
			else /*if (cooledEnergyThreshold < energy)*/ {
				return Arrays.asList(
					new Interval(100, cooledEnergyThreshold, IntervalType.OVERCHARGED_COOLED),
					new Interval(cooledEnergyThreshold, energy, IntervalType.OVERCHARGED_UNCOOLED)
				);
			}
		}
		else /*if (energy < 100)*/ {
			if (cooledEnergyThreshold > 100) {
				return Arrays.asList(
					new Interval(energy, 100, IntervalType.UNDERCHARGED),
					new Interval(100, cooledEnergyThreshold, IntervalType.OVERCOOLED)
				);
			}
			else {
				return Arrays.asList(
					new Interval(energy, 100, IntervalType.UNDERCHARGED)
				);
			}
		}
	}
	
	public static int getCooledEnergyThreshold(int coolant) {
		return  (int) (Math.sqrt(0.65 * coolant + 1) * 100);
	}

	public static class Interval {

		public int start;
		public int end;
		public IntervalType type;
		
		private Interval(int start, int end, IntervalType type) {
			this.start = start;
			this.end = end;
			this.type = type;
		}
		
		@Override
		public String toString() {
			return "Interval [start=" + start + ", end=" + end + ", type=" + type + "]";
		}
	}
	
	public static enum IntervalType {
		OVERCHARGED_COOLED, OVERCHARGED_UNCOOLED, OVERCOOLED, UNDERCHARGED
	}
}
