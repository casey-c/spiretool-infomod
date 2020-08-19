package InfoMod.UI;

import InfoMod.RenderingUtils;
import basemod.BaseMod;
import basemod.interfaces.PostUpdateSubscriber;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

import java.util.function.Consumer;

public abstract class EditableText implements IFocusableScreenWidget, PostUpdateSubscriber, IKeyTypedUser {
    protected boolean visible = false;
    protected boolean focused = false;

    protected Consumer<EditableText> onClick;
    protected Consumer<EditableText> onTab;
    protected Consumer<EditableText> onEscape;
    protected Consumer<EditableText> onReturn;

    protected Consumer<EditableText> onEdit;

    protected Texture TEX_DEFAULT;
    protected Texture TEX_FOCUSED;

    public float TEX_WIDTH;
    public float TEX_HEIGHT;

    protected Hitbox hb;

    protected float x, y;

    protected float textOffsetX;
    protected float textOffsetY;

    protected String text;
    protected BitmapFont font;
    protected Color fontColor;

    // This constructor should be called by all inheriting classes using super() to initialize everything
    public EditableText(float x,
                        float y,
                        String defaultText,
                        BitmapFont font,
                        Color fontColor,
                        Consumer<EditableText> onClick,
                        Consumer<EditableText> onTab,
                        Consumer<EditableText> onEscape,
                        Consumer<EditableText> onReturn,
                        Consumer<EditableText> onEdit,
                        Texture unfocused,
                        Texture focused,
                        float texWidth,
                        float texHeight,
                        float textOffsetX,
                        float textOffsetY) {
        this.x = x;
        this.y = y;

        this.text = defaultText;
        this.font = font;
        this.fontColor = fontColor;

        this.onClick = onClick;
        this.onTab = onTab;
        this.onEscape = onEscape;
        this.onReturn = onReturn;
        this.onEdit = onEdit;

        this.TEX_DEFAULT = unfocused;
        this.TEX_FOCUSED = focused;

        this.TEX_WIDTH = texWidth;
        this.TEX_HEIGHT = texHeight;

        this.textOffsetX = textOffsetX;
        this.textOffsetY = textOffsetY;

        // Setup the hitbox
        hb = new Hitbox(TEX_WIDTH, TEX_HEIGHT);
        //hb.move(x + (TEX_WIDTH / 2.0f), y + (TEX_HEIGHT / 2.0f));

        // Make this text box visible
        BaseMod.subscribe(this);
        show();
    }

    public void appendChar(char c) {
        text = text + c;
    }

    public void backspace() {
        if (text.length() >= 1)
            text = text.substring(0, text.length() - 1);
    }


//    public EditableText(float x, float y, String defaultText, BitmapFont font, Color fontColor, Consumer<EditableText> clickNotify) {
//        this.x = x;
//        this.y = y;
//
//        this.text = defaultText;
//        this.font = font;
//        this.fontColor = fontColor;
//
//        hb = new Hitbox(TEX_WIDTH, TEX_HEIGHT);
//        hb.move(x + (TEX_WIDTH / 2.0f), y + (TEX_HEIGHT / 2.0f));
//
//        this.clickNotify = clickNotify;
//
//        show();
//    }

    @Override
    public void show() {
        visible = true;
        hb.move(x + (TEX_WIDTH / 2.0f), y + (TEX_HEIGHT / 2.0f));
    }

    @Override
    public void hide() {
        visible = false;
        hb.move(100000, 100000);
    }

    @Override
    public boolean isFocused() {
        return focused;
    }

    @Override
    public void setFocus() {
        focused = true;
    }

    @Override
    public void removeFocus() {
        focused = false;
    }

    @Override
    public void render(SpriteBatch sb) {
        if (!visible)
            return;

        sb.setColor(Color.WHITE);
        if (focused)
            sb.draw(TEX_FOCUSED, x, y, TEX_WIDTH, TEX_HEIGHT);
        else
            sb.draw(TEX_DEFAULT, x, y, TEX_WIDTH, TEX_HEIGHT);

        // Render the text
        //FontHelper.renderFontLeftTopAligned(sb, font, text, x + textOffsetX, y + textOffsetY, fontColor);
        RenderingUtils.renderSmarterText(sb, font, text, x + textOffsetX, y + textOffsetY, fontColor);

        // Render the glowy "_" at the end
        if (focused) {
            float tmpAlpha = (MathUtils.cosDeg((float)(System.currentTimeMillis() / 3L % 360L)) + 1.25F) / 3.0F * fontColor.a;
            //FontHelper.renderSmartText(sb,
            RenderingUtils.renderSmarterText(sb,
                    font,
                    "_",
                    x + textOffsetX + RenderingUtils.getSmarterWidth(font, text, 100000000.0f, 0.0f), //FontHelper.getSmartWidth(font, text, 1000000.0F, 0.0F),
                    y + textOffsetY,
                    100000.0F,
                    0.0F,
                    new Color(1.0F, 1.0F, 1.0F, tmpAlpha));
        }

        // DEBUG
        hb.render(sb);
    }

    @Override
    public void receivePostUpdate() {
        if (!visible)
            return;

        hb.update();

        // Update the hitbox
//        if (hb.justHovered)
//            CardCrawlGame.sound.play("UI_HOVER");

        if (InputHelper.justClickedLeft && hb.hovered) {
            CardCrawlGame.sound.play("UI_CLICK_1");
            hb.clickStarted = true;
        }

        if (hb.clicked) {
            hb.clicked = false;
            onClick.accept(this);
        }
    }

    @Override
    public void keyTyped(char c) {
        if (InputHelper.pressedEscape)
        {
            System.out.println("OJB: key typed escape! stop this from happening?");
        }


        if (c == SpecialKeys.BACKSPACE) {
            backspace();
            onEdit.accept(this);
        }
        else if (c == SpecialKeys.TAB)
            onTab.accept(this);
        else if ((int)c == SpecialKeys.ESCAPE_KEY) {
            //InputHelper.pressedEscape = false;
            onEscape.accept(this);
        }
        else if (c == SpecialKeys.RETURN)
            onReturn.accept(this);
        else if ((int)c == SpecialKeys.NON_PRINTING) {
            System.out.println("OJB: SKIPPING keytyped char: '" + c + "' | as int: '" + (int)c + "'");
        }
        else {
            System.out.println("OJB: keytyped char: '" + c + "' | as int: '" + (int)c + "'");
            appendChar(c);
            onEdit.accept(this);
        }

        System.out.println();
    }

    public void setText(String t) {
        text = t;
        onEdit.accept(this);
    }

    public String getText() {
        return text;
    }

    public void clear() {
        text = "";
        onEdit.accept(this);
    }
}
