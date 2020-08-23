package InfoMod.ui.buttons;

import InfoMod.ui.buttons.ButtonWidget;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;

import java.util.function.Consumer;

public class ColorableSmallTextButton extends ButtonWidget {
    private Color buttonColor;

    private String text;
    private BitmapFont font = FontHelper.tipHeaderFont;
    private Color fontColor = Settings.GOLD_COLOR;

    private static final float BUTTON_WIDTH = 230.0f;
    private static final float BUTTON_HEIGHT = 90.0f;
    private static final float textOffsetX = 70.0f;
    private static final float textOffsetY = 54.0f;

    private float additionalTextOffsetX = 0.0f;
    private float additionalTextOffsetY = 0.0f;

    private static final Texture TEX_NORMAL_COLOR = new Texture("images/button_normal_color.png");
    private static final Texture TEX_NORMAL_FRAME = new Texture("images/button_normal_frame.png");
    private static final Texture TEX_PRESSED_FRAME = new Texture("images/button_pressed_frame.png");

    public ColorableSmallTextButton(float x, float y, float additionalTextOffsetX, float additionalTextOffsetY, String text, Color buttonColor, Consumer<ButtonWidget> onClick) {
        super(x, y, BUTTON_WIDTH, BUTTON_HEIGHT, onClick);

        this.text = text;
        this.buttonColor = buttonColor;

        this.additionalTextOffsetX = additionalTextOffsetX;
        this.additionalTextOffsetY = additionalTextOffsetY;
    }

    public ColorableSmallTextButton(float x, float y, String text, Color buttonColor, Consumer<ButtonWidget> onClick) {
        this(x, y, 0.0f, 0.0f, text, buttonColor, onClick);
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);

        if (!visible)
            return;

        // Render the color portion
        sb.setColor(buttonColor);
        sb.draw(TEX_NORMAL_COLOR, x, y, BUTTON_WIDTH, BUTTON_HEIGHT);

        // Render the frame
        sb.setColor(Color.WHITE);
        if (!hb.hovered)
            sb.draw(TEX_NORMAL_FRAME, x, y, BUTTON_WIDTH, BUTTON_HEIGHT);
        else
            sb.draw(TEX_PRESSED_FRAME, x, y, BUTTON_WIDTH, BUTTON_HEIGHT);

        // Render the text
        FontHelper.renderFontLeftTopAligned(sb, font, text, x + textOffsetX + additionalTextOffsetX, y + textOffsetY + additionalTextOffsetY, fontColor);
    }
}

/*
public class ColorableSmallTextButton implements IScreenWidget, PostUpdateSubscriber {
    private Color buttonColor;

    //private boolean visible = false;

    // bottom left of the button texture [i.e. it goes from (x,y) -> (x+w, y+h)]
    private float x, y;

    private Hitbox hb;

    //private float textOffsetX = 46.0f;
    //private float textOffsetY = 61.0f;
    private float textOffsetX = 70.0f;
    private float textOffsetY = 54.0f;

    private String text;
    private BitmapFont font = FontHelper.tipHeaderFont;
    private Color fontColor = Settings.GOLD_COLOR;

    private static final float BUTTON_WIDTH = 230.0f;
    private static final float BUTTON_HEIGHT = 90.0f;

    private static final Texture TEX_NORMAL_COLOR = new Texture("images/button_normal_color.png");
    private static final Texture TEX_NORMAL_FRAME = new Texture("images/button_normal_frame.png");
    private static final Texture TEX_PRESSED_FRAME = new Texture("images/button_pressed_frame.png");

    private Consumer<ColorableSmallTextButton> onClick;

    // TODO: more detailed constructors
    public ColorableSmallTextButton(float x, float y, String text, Color buttonColor, Consumer<ColorableSmallTextButton> onClick) {
        this.x = x;
        this.y = y;
        this.text = text;

        this.buttonColor = buttonColor;

        this.onClick = onClick;

        hb = new Hitbox(BUTTON_WIDTH, BUTTON_HEIGHT);
        hb.move(x + (BUTTON_WIDTH / 2.0f), y + (BUTTON_HEIGHT / 2.0f));

        BaseMod.subscribe(this);
        show();
    }

    @Override
    public void hide() {
        this.visible = false;
    }

    @Override
    public void show() {
        this.visible = true;
    }

    @Override
    public void render(SpriteBatch sb) {
        if (!visible)
            return;

        // Render the color portion
        sb.setColor(buttonColor);
        sb.draw(TEX_NORMAL_COLOR, x, y, BUTTON_WIDTH, BUTTON_HEIGHT);

        // Render the frame
        sb.setColor(Color.WHITE);
        if (!hb.hovered)
            sb.draw(TEX_NORMAL_FRAME, x, y, BUTTON_WIDTH, BUTTON_HEIGHT);
        else
            sb.draw(TEX_PRESSED_FRAME, x, y, BUTTON_WIDTH, BUTTON_HEIGHT);

        // Render the text
        FontHelper.renderFontLeftTopAligned(sb, font, text, x + textOffsetX, y + textOffsetY, fontColor);

        // DEBUG
        hb.render(sb);
    }

    @Override
    public void receivePostUpdate() {
        if (!visible)
            return;

        hb.update();

        // Update the hitbox
        if (hb.justHovered)
            CardCrawlGame.sound.play("UI_HOVER");

        if (InputHelper.justClickedLeft && hb.hovered) {
            CardCrawlGame.sound.play("UI_CLICK_1");
            hb.clickStarted = true;
        }

        if (hb.clicked) {
            hb.clicked = false;

            // Handle
            System.out.println("OJB: okHB is clicked");

            onClick.accept(this);
            //hide();
        }

    }
}
 */












