package InfoMod.utils.config;

import InfoMod.InfoMod;
import basemod.IUIElement;
import basemod.ModPanel;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;

import java.util.function.Consumer;

public class InfoModConfigWrappedLabel implements IUIElement {
    public ModPanel parent;

    private String text;
    private float x, y;
    private float lineWidth, lineSpacing;

    private Color color;
    private BitmapFont font;

    public InfoModConfigWrappedLabel(String text, float topLeftX, float topLeftY, Color color, BitmapFont font, ModPanel parent) {
        this(text, topLeftX, topLeftY, 1000.0f, 40.0f, color, font, parent);
    }

    public InfoModConfigWrappedLabel(String text, float topLeftX, float topLeftY, float lineWidth, float lineSpacing, Color color, BitmapFont font, ModPanel parent) {
        this.text = text;
        this.x = topLeftX;
        this.y = topLeftY;
        this.lineWidth = lineWidth;
        this.lineSpacing = lineSpacing;
        this.parent = parent;
        this.color = color;
        this.font = font;
    }


    @Override
    public void render(SpriteBatch sb) {
        //FontHelper.renderFontLeftDownAligned(sb, this.font, this.text, this.x, this.y, this.color);
        //FontHelper.renderFontLeftTopAligned(sb, this.font, this.text, this.x, this.y, this.color);
        FontHelper.renderSmartText(sb,
                font,
                text,
                x * Settings.scale,
                y * Settings.scale,
                lineWidth * Settings.scale,
                lineSpacing * Settings.scale,
                color);
    }

    @Override
    public void update() {
    }

    @Override
    public int renderLayer() {
        // TODO: figure out what the desired ordering should be
        return 2;
    }

    @Override
    public int updateOrder() {
        // TODO: figure out what this means
        return 0;
    }
    /*

public class ModLabel implements IUIElement {
    private Consumer<ModLabel> update;
    public ModPanel parent;
    public String text;
    public float x;
    public float y;
    public Color color;
    public BitmapFont font;

    public ModLabel(String labelText, float xPos, float yPos, ModPanel p, Consumer<ModLabel> updateFunc) {
        this(labelText, xPos, yPos, Color.WHITE, FontHelper.buttonLabelFont, p, updateFunc);
    }

    public ModLabel(String labelText, float xPos, float yPos, Color color, ModPanel p, Consumer<ModLabel> updateFunc) {
        this(labelText, xPos, yPos, color, FontHelper.buttonLabelFont, p, updateFunc);
    }

    public ModLabel(String labelText, float xPos, float yPos, BitmapFont font, ModPanel p, Consumer<ModLabel> updateFunc) {
        this(labelText, xPos, yPos, Color.WHITE, font, p, updateFunc);
    }

    public ModLabel(String labelText, float xPos, float yPos, Color color, BitmapFont font, ModPanel p, Consumer<ModLabel> updateFunc) {
        this.text = labelText;
        this.x = xPos * Settings.scale;
        this.y = yPos * Settings.scale;
        this.color = color;
        this.font = font;
        this.parent = p;
        this.update = updateFunc;
    }

    public void render(SpriteBatch sb) {
        FontHelper.renderFontLeftDownAligned(sb, this.font, this.text, this.x, this.y, this.color);
    }

    public void update() {
        this.update.accept(this);
    }

    public int renderLayer() {
        return 2;
    }

    public int updateOrder() {
        return 0;
    }
}
     */
}
