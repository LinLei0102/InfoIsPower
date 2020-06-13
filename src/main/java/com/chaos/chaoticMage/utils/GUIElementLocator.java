package com.chaos.chaoticMage.utils;

import net.minecraft.client.Minecraft;

public class GUIElementLocator {
	private static final GUIElementLocator INSTANCE = new GUIElementLocator();
	private Minecraft mc = Minecraft.getInstance();
	private int counter = 0;
	
	private GUIElementLocator() {
	}
	
	public static GUIElementLocator getInstance() {
		return INSTANCE;
	}
	
	public GUIElementLocator begin(LocatorTypes type) {
		this.reset();
		this.setPos(type);
		return this;
	}
	
	public void end() {
		this.reset();
	}
	
	private void setPos(LocatorTypes type) {
		int h = mc.getMainWindow().getHeight();
		switch (type) {
		case LEFT_UP:
			this.counter = 1;
			break;
		case LEFT_CENTER:
			this.counter = h/4;
			break;
		case LEFT_DOWN:
			this.counter = h/2 - 50;
		default:
			break;
		}
	}
	
	private void reset() {
		this.counter = 0;
	}
	
	public int getNextLocation(LocatorTypes type) {
		switch (type) {
		case LEFT_UP:
			this.counter += 9;
			break;
		case LEFT_CENTER:
			this.counter -= 9;
			break;
		case LEFT_DOWN:
			this.counter -= 9;
			break;
		default:
			break;
		}
		return this.counter;
	}
	
	public int getCurrent() {
		return this.counter;
	}
	
	public GUIElementLocator returnCounter(int times, LocatorTypes type) {
		switch (type) {
		case LEFT_UP:
			this.counter -= times*9;
			break;
		case LEFT_CENTER:
			this.counter += times*20;
			break;
		case LEFT_DOWN:
			this.counter -= times*0;
			break;
		default:
			break;
		}
		return this;
	}
	
	public enum LocatorTypes {
		LEFT_UP, LEFT_CENTER, LEFT_DOWN;
	}
}
