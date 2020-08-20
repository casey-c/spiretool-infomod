package InfoMod;

import basemod.IUIElement;
import basemod.ModPanel;
import basemod.ModToggleButton;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;

import java.util.function.Consumer;

/*

 A UI element for the config menu: a "descriptive bool" is ~ 500px wide by 130px tall and includes a checkbox for
 toggling the setting, a title area to name the setting, and a description area to provide a short summary of what the
 option does.

 The 500x130 target size is so that we can have two columns in the config menu and align things nicely. This is sort of
 hardcoded for the approximately 6 (2 columns of 3 each) settings currently needed for InfoMod, which will probably
 require some more rewrites once more settings are required / added. However, this should do fine for the meantime.

 This class is essentially a beefed up version of the ModLabeledToggleButton.

 */
public class InfoModConfigDescBool implements IUIElement {
    private String title, desc;
    private float x, y;

    private BitmapFont titleFont, descFont;
    private Color titleColor, descColor;

    public ModPanel parent;
    private ModToggleButton checkBox;

    // NOTE: These values should be scaled to the UI by Settings.scale when used in the render() call
    private static final float lineSpacing = 28.0f;
    private static final float lineWidth = 500.0f;

    private static final float checkboxGapX = 40.0f;
    private static final float checkboxGapY = 8.0f;

    private static final float descGapY = 45.0f;

    public InfoModConfigDescBool(float topLeftX, float topLeftY, String title, String desc, ModPanel parent, Config.ConfigOptions key) {
        this(topLeftX,
                topLeftY,
                title,
                desc,
                FontHelper.tipHeaderFont,
                FontHelper.tipBodyFont,
                Settings.GOLD_COLOR,
                //Settings.CREAM_COLOR,
                RenderingUtils.OJB_GRAY_COLOR,
                parent,
                Config.getBool(key),
                //ConfigHelper.getInstance().getBool(key),
                modToggleButton -> {
                    //ConfigHelper.getInstance().setBool(key, modToggleButton.enabled);
                    Config.setBoolean(key, modToggleButton.enabled);
                }
        );
    }

    // Includes a custom update function (no need to manually adjust ConfigHelper setBool as it does automatically)
    public InfoModConfigDescBool(float topLeftX, float topLeftY, String title, String desc, ModPanel parent, Config.ConfigOptions key, Consumer<ModToggleButton> update) {
        this(topLeftX,
                topLeftY,
                title,
                desc,
                FontHelper.tipHeaderFont,
                FontHelper.tipBodyFont,
                Settings.GOLD_COLOR,
                //Settings.CREAM_COLOR,
                RenderingUtils.OJB_GRAY_COLOR,
                parent,
                //ConfigHelper.getInstance().getBool(key),
                Config.getBool(key),
                //update
                modToggleButton -> {
                    update.accept(modToggleButton);
                    //ConfigHelper.getInstance().setBool(key, modToggleButton.enabled);
                    Config.setBoolean(key, modToggleButton.enabled);
                }
//                modToggleButton -> {
//                    ConfigHelper.getInstance().setBool(key, modToggleButton.enabled);
//                }
        );
    }

    // Lazier constructor assumes default font and colors
//    @Deprecated
//    public InfoModConfigDescBool(float topLeftX, float topLeftY, String title, String desc, ModPanel parent, boolean startsEnabled, Consumer<ModToggleButton> update) {
//        this(topLeftX,
//                topLeftY,
//                title,
//                desc,
//                FontHelper.tipHeaderFont,
//                FontHelper.tipBodyFont,
//                Settings.GOLD_COLOR,
//                Settings.CREAM_COLOR,
//                parent,
//                startsEnabled,
//                update
//        );
//    }

    public InfoModConfigDescBool(float topLeftX,
                                 float topLeftY,
                                 String title,
                                 String desc,
                                 BitmapFont titleFont,
                                 BitmapFont descFont,
                                 Color titleColor,
                                 Color descColor,
                                 ModPanel parent,
                                 boolean startsEnabled,
                                 Consumer<ModToggleButton> update) {
        this.x = topLeftX;
        this.y = topLeftY;

        this.title = title;
        this.desc = desc;

        this.titleFont = titleFont;
        this.descFont = descFont;

        this.titleColor = titleColor;
        this.descColor = descColor;

        this.parent = parent;

        // Checkbox assumes bottom left (though we use top left)
        checkBox = new ModToggleButton(x, y - 32.0f, startsEnabled, true, parent, update);

        // TODO: wrap hitbox of the checkbox to the full area
        //   currently not possible because ModToggleButton::hitbox is private :(
        //   will need to probably not use the mod toggle button to have desired behavior
    }


    @Override
    public void render(SpriteBatch sb) {
        this.checkBox.render(sb);

        // Title
        FontHelper.renderFontLeftTopAligned(sb,
                titleFont,
                title,
                (x + checkboxGapX) * Settings.scale,
                (y - checkboxGapY) * Settings.scale,
                titleColor
                );

        // Description
        FontHelper.renderSmartText(sb,
                descFont,
                desc,
                x * Settings.scale,
                (y - descGapY) * Settings.scale,
                lineWidth * Settings.scale,
                lineSpacing * Settings.scale,
                descColor);
    }

    @Override
    public void update() {
        this.checkBox.update();

    }

    @Override
    public int renderLayer() {
        return 0;
    }

    @Override
    public int updateOrder() {
        return 0;
    }
}
