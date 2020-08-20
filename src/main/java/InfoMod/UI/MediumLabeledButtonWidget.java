package InfoMod.UI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;

import java.util.function.Consumer;

public class MediumLabeledButtonWidget extends ButtonWidget {
    private String text;
    private BitmapFont font = FontHelper.tipHeaderFont;
    private Color fontColor = Settings.GOLD_COLOR;

    private static final float BUTTON_WIDTH = 230.0f;
    private static final float BUTTON_HEIGHT = 90.0f;
    private static final float textOffsetX = 70.0f;
    private static final float textOffsetY = 54.0f;

    private float additionalTextOffsetX;
    private float additionalTextOffsetY;

    private static final Texture TEX_NORMAL = new Texture("images/widgets/button_med.png");
    private static final Texture TEX_HOVERED = new Texture("images/widgets/button_med_hovered.png");

    public MediumLabeledButtonWidget(float x, float y, float additionalTextOffsetX, float additionalTextOffsetY, String text, Consumer<ButtonWidget> onClick) {
        super(x, y, BUTTON_WIDTH, BUTTON_HEIGHT, onClick);

        this.text = text;

        this.additionalTextOffsetX = additionalTextOffsetX;
        this.additionalTextOffsetY = additionalTextOffsetY;
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);

        if (!visible)
            return;

        sb.setColor(Color.WHITE);
        if (!hb.hovered)
            sb.draw(TEX_NORMAL, x, y, BUTTON_WIDTH, BUTTON_HEIGHT);
        else
            sb.draw(TEX_HOVERED, x, y, BUTTON_WIDTH, BUTTON_HEIGHT);

        // Render the text
        FontHelper.renderFontLeftTopAligned(sb, font, text, x + textOffsetX + additionalTextOffsetX, y + textOffsetY + additionalTextOffsetY, fontColor);
    }
}
