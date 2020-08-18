package InfoMod.UI;

public interface IFocusableScreenWidget extends IScreenWidget {
    public boolean isFocused();
    public void setFocus();
    public void removeFocus();
}
