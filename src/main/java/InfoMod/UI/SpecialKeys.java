package InfoMod.UI;

/*
  Some helper defines that most certainly exist in some other form in libgdx but which I couldn't find.

  There's the Input.Keys.??? defines but they don't place nice on the return from keyTyped()->char. i.e. the keyTyped
  event from the InputProcessor function returns a char even when you type something like backspace or enter

  E.g. The char is NOT caught by the condition: (c == Input.Keys.ESCAPE), but is caught instead by
  ((int)c == ESCAPE_KEY).

  There's probably better ways to do this, but I wanted access to all special characters (e.g. !@#$%^ etc.) in the
  keyTyped event without thinking too hard about it, and this appears to work. (it may be fragile and bad, but it
  works currently so i'll take it)
 */
public class SpecialKeys {
    // note that this requires a cast to int if coming from a char
    public static final int ESCAPE_KEY = 27;

    // without filtering out this, you can press SHIFT / WINDOWS KEY / and possibly other keys and have them
    // show up as part of the string but not really. this is a good thing to avoid if using keyTyped
    public static final int NON_PRINTING = 0;

    public static final char BACKSPACE = '\b';
    public static final char TAB = '\t';
    public static final char RETURN = '\r';
}
