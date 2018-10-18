package com.kanji.list.myList;

import com.guimaker.enums.PanelDisplayMode;
import com.guimaker.inputSelection.ListInputsSelectionManager;
import com.guimaker.list.listElements.ListElement;
import com.kanji.utilities.Pair;

import javax.swing.*;

public class ListConfiguration {

	private boolean enableWordAdding = true;
	private boolean inheritScrollbar = false;
	private boolean showButtonsLoadNextPreviousWords = true;
	private boolean enableWordSearching = true;
	private boolean skipTitle = false;
	private boolean scrollBarSizeFittingContent = false;
	private PanelDisplayMode displayMode = PanelDisplayMode.EDIT;
	private AbstractButton[] additionalNavigationButtons = new AbstractButton[] {};
	private ListInputsSelectionManager allInputsSelectionManager;
	private Pair<MyList, ListElement> parentListAndWordContainingThisList;

	public PanelDisplayMode getDisplayMode() {
		return displayMode;
	}

	public ListConfiguration displayMode(PanelDisplayMode displayMode) {
		this.displayMode = displayMode;
		return this;
	}

	public Pair<MyList, ListElement> getParentListAndWordContainingThisList() {
		return parentListAndWordContainingThisList;
	}

	public ListConfiguration parentListAndWordContainingThisList(
			MyList parentList, ListElement parentWordContainingThisList) {
		parentListAndWordContainingThisList = new Pair<>(parentList,
				parentWordContainingThisList);
		return this;
	}

	public boolean isSkipTitle() {
		return skipTitle;
	}

	public ListConfiguration skipTitle(boolean skipTitle) {
		this.skipTitle = skipTitle;
		return this;
	}

	public ListInputsSelectionManager getAllInputsSelectionManager() {
		return allInputsSelectionManager;
	}

	public ListConfiguration allInputsSelectionManager(
			ListInputsSelectionManager allInputsSelectionManager) {
		this.allInputsSelectionManager = allInputsSelectionManager;
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
