package InfoMod.UI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import java.util.function.Consumer;

public class EditableTextLong extends EditableText {
    private static final float TEXT_OFFSET_X = 44.0f;
    private static final float TEXT_OFFSET_Y = 49.0f;

    public static final float WIDTH = 600.0f;
    public static final float HEIGHT = 80.0f;

    public EditableTextLong(float x,
                            float y,
                            String defaultText,
                            BitmapFont font,
                            Color fontColor,
                            Consumer<EditableText> onClick,
                            Consumer<EditableText> onTab,
                            Consumer<EditableText> onEscape,
                            Consumer<EditableText> onReturn,
                            Consumer<EditableText> onEdit
                            ) {
        super(x,
                y,
                defaultText,
                font,
                fontColor,
                onClick,
                onTab,
                onEscape,
                onReturn,
                onEdit,
                new Texture("images/text_long_default.png"),
                new Texture("images/text_long_focused.png"),
                WIDTH,
                HEIGHT,
                TEXT_OFFSET_X,
                TEXT_OFFSET_Y);
    }
}
