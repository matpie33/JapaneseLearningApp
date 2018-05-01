package com.kanji.list.myList;

import javax.swing.*;

public class ListConfiguration {

	private boolean enableWordAdding = true;
	private boolean inheritScrollbar = false;
	private boolean showButtonsLoadNextPreviousWords = true;
	private boolean enableWordSearching = true;
	private boolean skipTitle = false;
	private boolean scrollBarSizeFittingContent = false;
	private AbstractButton[] additionalNavigationButtons = new AbstractButton[] {};

	public boolean isSkipTitle() {
		return skipTitle;
	}

	public ListConfiguration skipTitle(boolean skipTitle) {
		this.skipTitle = skipTitle;
		return this;
	}

	public boolean isWordSearchingEnabled() {
		return enableWordSearching;
	}

	public ListConfiguration enableWordSearching(boolean enableWordSearching) {
		this.enableWordSearching = enableWordSearching;
		return this;
	}

	public boolean isWordAddingEnabled() {
		return enableWordAdding;
	}

	public ListConfiguration enableWordAdding(boolean enableWordAdding) {
		this.enableWordAdding = enableWordAdding;
		return this;
	}

	public boolean isScrollBarInherited() {
		return inheritScrollbar;
	}

	public ListConfiguration inheritScrollbar(boolean inheritScrollbar) {
		this.inheritScrollbar = inheritScrollbar;
		return this;
	}

	public ListConfiguration scrollBarFitsContent(boolean fitsContent) {
		this.scrollBarSizeFittingContent = fitsContent;
		return this;
	}

	public boolean isShowButtonsLoadNextPreviousWords() {
		return showButtonsLoadNextPreviousWords;
	}

	public ListConfiguration showButtonsLoadNextPreviousWords(
			boolean showButtonsLoadNextPreviousWords) {
		this.showButtonsLoadNextPreviousWords = showButtonsLoadNextPreviousWords;
		return this;
	}

	public ListConfiguration withAdditionalNavigationButtons(
			AbstractButton... buttons) {
		this.additionalNavigationButtons = buttons;
		return this;
	}

	public AbstractButton[] getAdditionalNavigationButtons() {
		return additionalNavigationButtons;
	}

	public boolean isScrollBarSizeFittingContent() {
		return scrollBarSizeFittingContent;
	}
}
