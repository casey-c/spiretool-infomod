package InfoMod.ui.screens;

import InfoMod.ui.IScreenWidget;

public interface IFocusableScreenWidget extends IScreenWidget {
    boolean isFocused();
    void setFocus();
    void removeFocus();
}
