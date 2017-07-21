package com.kanji.keyEvents;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class HotkeyWrapper {
	private int keyModifier, keyEvent, keyMask;

	private enum KeyModifiers {
		CONTROL(KeyEvent.VK_CONTROL, InputEvent.CTRL_DOWN_MASK), SHIFT(KeyEvent.VK_SHIFT,
				InputEvent.SHIFT_DOWN_MASK), ALT(KeyEvent.VK_ALT, InputEvent.ALT_DOWN_MASK);
		private int keyCode;
		private int keyMask;

		private KeyModifiers(int keyCode, int keyMask) {
			this.keyCode = keyCode;
			this.keyMask = keyMask;
		}

		public int getKeyCode() {
			return keyCode;
		}

		public int getKeyMask() {
			return keyMask;
		}

	}

	public HotkeyWrapper(int keyModifier, int keyEvent) {
		this.keyModifier = keyModifier;
		this.keyEvent = keyEvent;
		keyMask = getKeyMask(keyModifier);
	}

	private int getKeyMask(int keyModifier) {
		for (KeyModifiers modifier : KeyModifiers.values()) {

			if (modifier.getKeyCode() == keyModifier) {
				return modifier.getKeyMask();
			}
		}
		return 0;

	}

	public HotkeyWrapper(int keyEvent) {
		this.keyEvent = keyEvent;
	}

	public int getKeyMask() {
		return keyMask;
	}

	public int getKeyModifier() {
		return keyModifier;
	}

	public int getKeyEvent() {
		return keyEvent;
	}

	public boolean hasProperKeyModifier() {
		for (KeyModifiers modifier : KeyModifiers.values()) {
			if (keyModifier == modifier.getKeyCode()) {
				return true;
			}
		}
		return false;
	}

}
