package InfoMod.UI;

import InfoMod.SaveableManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.function.Consumer;

/*
    Integers only currently
 */
public class EditableNumberWidget implements IScreenWidget {
    protected boolean visible = false;

    private float x, y;
    private static final float TEXT_BUTTON_GAP = 12.0f;
    private static final float PLUS_OFFSET_Y = 50.0f;
    private static final float MINUS_OFFSET_Y = 10.0f;

    public EditableTextShort textArea;
    private SimpleButtonWidget plusButton;
    private SimpleButtonWidget minusButton;

    private int minValue, maxValue;
    private int currValue;

    private Consumer<EditableNumberWidget> onEditText;

    public EditableNumberWidget(float x,
                                float y,
                                int minValue,
                                int maxValue,
                                int defaultValue,
                                BitmapFont font,
                                Color fontColor,
                                Consumer<EditableNumberWidget> onClick,
                                Consumer<EditableNumberWidget> onTab,
                                Consumer<EditableNumberWidget> onEscape,
                                Consumer<EditableNumberWidget> onReturn,
                                Consumer<EditableNumberWidget> onEditText) {
        this.x = x;
        this.y = y;

        this.onEditText = onEditText;

        this.minValue = minValue;
        this.maxValue = maxValue;
        this.currValue = defaultValue;

        if (currValue < minValue)
            currValue = minValue;
        if (currValue > maxValue)
            currValue = maxValue;

        textArea = new EditableTextShort(x,
                y,
                "" + currValue,
                font,
                fontColor,
                editableText -> { onClick.accept(this); },
                editableText -> { onTab.accept(this); },
                editableText -> { onEscape.accept(this); },
                editableText -> { onReturn.accept(this); },
                editableText -> { onEditText.accept(this); }
                ) {
            @Override
            public void keyTyped(char c) {
                if (c == SpecialKeys.BACKSPACE) {
                    currValue = (int) Math.floor((float)currValue / 10.0f);
                    add(0);
                }
                else if (c == SpecialKeys.TAB)
                    onTab.accept(this);
                else if ((int)c == SpecialKeys.ESCAPE_KEY)
                    onEscape.accept(this);
                else if (c == SpecialKeys.RETURN)
                    onReturn.accept(this);
                else if (Character.isDigit(c)) {
                    int x = Integer.parseInt("" + c);
                    currValue = currValue * 10;
                    add(x);

                    System.out.println("OJB: typed an integer: " + x);
                }
                else {
                    System.out.println("OJB: keytyped char: '" + c + "' | as int: '" + (int)c + "'");
                    //appendChar(c);
                }

                System.out.println();
            }

            @Override
            public void clear() {
                currValue = 0;
                add(0);
            }

        };

        plusButton = ButtonFactory.buildPlusButton(x + textArea.TEX_WIDTH + TEXT_BUTTON_GAP,
                y + PLUS_OFFSET_Y,
                buttonWidget -> {
                    if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
                        add(10);
                    else
                        add(1);
                });

        minusButton = ButtonFactory.buildMinusButton(x + textArea.TEX_WIDTH + TEXT_BUTTON_GAP,
                y + MINUS_OFFSET_Y,
                buttonWidget -> {
                    if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
                        add(-10);
                    else
                        add(-1);
                });

        show();

    }

//    public EditableNumberWidget(float x,
//                                float y,
//                                int minValue,
//                                int maxValue,
//                                int defaultValue,
//                                BitmapFont font,
//                                Color fontColor,
//                                Consumer<EditableNumberWidget> onClick,
//                                Consumer<EditableNumberWidget> onTab,
//                                Consumer<EditableNumberWidget> onEscape,
//                                Consumer<EditableNumberWidget> onReturn) {
//        this(x, y, minValue, maxValue, defaultValue, font, fontColor, onClick, onTab, onEscape, onReturn, editableNumberWidget -> {});
//    }

    @Override
    public void show() {
        visible = true;

        plusButton.show();
        minusButton.show();
        textArea.show();
    }

    @Override
    public void hide() {
        visible = false;

        plusButton.hide();
        minusButton.hide();
        textArea.hide();
    }


    @Override
    public void render(SpriteBatch sb) {
        if (!visible)
            return;

        textArea.render(sb);
        plusButton.render(sb);
        minusButton.render(sb);
    }

    public void add(int amt) {
        currValue += amt;
        if (currValue < minValue)
            currValue = minValue;
        if (currValue > maxValue)
            currValue = maxValue;

        textArea.setText("" + currValue);

        onEditText.accept(this);
    }

    public int getValue() {
        return currValue;
    }

    public void setValue(int x) {
        currValue = x;
        add(0);

        onEditText.accept(this);
    }
}
