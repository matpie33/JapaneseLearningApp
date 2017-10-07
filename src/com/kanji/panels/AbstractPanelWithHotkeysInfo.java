package com.kanji.panels;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.ComponentType;
import com.guimaker.enums.FillType;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRow;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.utilities.CommonActionsMaker;
import com.guimaker.utilities.HotkeyWrapper;
import com.guimaker.utilities.KeyModifiers;
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.HotkeysDescriptions;
import com.kanji.constants.Titles;
import com.kanji.windows.DialogWindow;

public abstract class AbstractPanelWithHotkeysInfo {
    protected MainPanel mainPanel;
    private MainPanel hotkeysPanel;
    DialogWindow parentDialog;
    private int hotkeysPanelIndex;
    private AbstractButton[] navigationButtons;
    private Anchor buttonsAnchor = Anchor.EAST;


    public AbstractPanelWithHotkeysInfo() {
        mainPanel = new MainPanel(BasicColors.OCEAN_BLUE);
        mainPanel.setRowColor(BasicColors.VERY_LIGHT_BLUE);
        mainPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        createHotkeysPanel();
    }

    void addHotkeysPanelHere() {
        hotkeysPanelIndex = mainPanel.getNumberOfRows();
    }

    void setNavigationButtons(AbstractButton... buttons) {
        navigationButtons = buttons;
    }

    void setNavigationButtons(Anchor anchor, AbstractButton... buttons) {
        navigationButtons = buttons;
        buttonsAnchor = anchor;
    }

    private void createHotkeysPanel() {
        hotkeysPanel = new MainPanel(BasicColors.VERY_LIGHT_BLUE);
        hotkeysPanelIndex = -1;
        JLabel title = new JLabel(Titles.HOTKEYS);
        title.setForeground(BasicColors.VERY_BLUE);
        hotkeysPanel.addRows(SimpleRowBuilder.createRow(FillType.NONE, Anchor.WEST, title));
    }

    private void addHotkeysPanel() {
        SimpleRow row = SimpleRowBuilder.createRow(FillType.HORIZONTAL, Anchor.SOUTH, hotkeysPanel.getPanel());
        if (hotkeysPanelIndex == -1) {
            mainPanel.addRows(row);
        } else if (hotkeysPanelIndex > 0) {
            mainPanel.insertRow(hotkeysPanelIndex, row);
        }
        if (navigationButtons != null)
            mainPanel.addRows( // TODO fix in gui maker: if putting rows as
                    // highest
                    // as
                    // possible, then west
                    // should be as highest as possible, but now I need
                    // to
                    // use northwest
					SimpleRowBuilder.createRow(FillType.NONE, buttonsAnchor, navigationButtons).disableBorder()
                            .setNotOpaque());

    }


    void addHotkeysInformation(int keyEvent, String hotkeyDescription) {
        HotkeyWrapper wrapper = new HotkeyWrapper(KeyModifiers.NONE, keyEvent);
        addHotkeyInformation(hotkeyDescription, wrapper);
    }

    void addHotkey(int keyEvent, AbstractAction action, JComponent component,
                          String hotkeyDescription) {
        addHotkey(KeyModifiers.NONE, keyEvent, action, component, hotkeyDescription);
    }

    private void addHotkey(KeyModifiers keyModifier, int keyEvent, AbstractAction action,
                          JComponent component, String hotkeyDescription) {
        HotkeyWrapper wrapper = new HotkeyWrapper(keyModifier, keyEvent);
        CommonActionsMaker.addHotkey(keyEvent, wrapper.getKeyMask(), action, component);
        addHotkeyInformation(hotkeyDescription, wrapper);
    }

    AbstractButton createButtonWithHotkey(int keyEvent, AbstractAction action,
                                                 String buttonLabel, String hotkeyDescription) {
        return createButtonWithHotkey(KeyModifiers.NONE, keyEvent, action, buttonLabel,
                hotkeyDescription);
    }

    AbstractButton createButtonWithHotkey(KeyModifiers keyModifier, int keyEvent,
                                                 AbstractAction action, String buttonLabel, String hotkeyDescription) {
        AbstractButton button = GuiMaker.createButtonlikeComponent(ComponentType.BUTTON,
                buttonLabel, action);
        addHotkey(keyModifier, keyEvent, action, button, hotkeyDescription);
        return button;
    }

    private void addHotkeyInformation(String hotkeyDescription, HotkeyWrapper hotkey) {
        if (hotkeyDescription.isEmpty()) {
            return;
        }
        JLabel hotkeyInfo = new JLabel(createInformationAboutHotkey(hotkey, hotkeyDescription));
        hotkeysPanel.addRows(SimpleRowBuilder.createRow(FillType.HORIZONTAL, hotkeyInfo));
    }

    private String createInformationAboutHotkey(HotkeyWrapper hotkey, String description) {
        return (hotkey.hasKeyModifier() ? InputEvent.getModifiersExText(hotkey.getKeyMask()) + " + "
                : "") + KeyEvent.getKeyText(hotkey.getKeyEvent()) + " : " + description;
    }

    public void setParentDialog(DialogWindow parent) {
        parentDialog = parent;
    }

    public JPanel createPanel() {
        createElements();
        addHotkeysPanel();
        return mainPanel.getPanel();
    }


    public DialogWindow getDialog() {
        return parentDialog;
    }

    public boolean isDisplayAble() {
        return parentDialog.getContainer().isDisplayable();
    }

    abstract void createElements();

    public void afterVisible() {
        // not required
    }

    AbstractButton createButtonClose() {
        AbstractAction dispose = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentDialog.getContainer().dispose();
            }
        };
        return createButtonWithHotkey(KeyEvent.VK_ESCAPE, dispose, ButtonsNames.CLOSE_WINDOW,
                HotkeysDescriptions.CLOSE_WINDOW);
    }


}
