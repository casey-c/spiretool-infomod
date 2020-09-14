package InfoMod.ui.screens;

import InfoMod.InfoMod;
import InfoMod.utils.RenderingUtils;
import InfoMod.utils.ScreenHelper;
import basemod.BaseMod;
import basemod.interfaces.PostUpdateSubscriber;
import basemod.interfaces.RenderSubscriber;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

/*
 Some testing around to replace ModTextPanel with something a bit more flexible?
public class StringInputScreen implements RenderSubscriber, PostUpdateSubscriber {
    private boolean visible = false;

    private String titleText = "Test";
    private String text = "!Potions: ";
    private String explanationText = "Set custom potion tracker text";

    // Adding some delay between inputs
    private float waitTimer = 0.0f;
    private static final float DELAY = 0.04f;

    private Hitbox okHB = null;
    private static final int HB_W = 173;
    private static final int HB_H = 74;

    // We can interpolate the alpha values later
    private Color screenColor = new Color(0.0F, 0.0F, 0.0F, 0.8F); // dim amount
    private Color uiColor = new Color(1.0F, 1.0F, 1.0F, 1.0F);

    private InputProcessor oldInputProcessor;

    private TextField textField;

    public StringInputScreen() {
        textField = new TextField("TEST", new TextField.TextFieldStyle(FontHelper.tipBodyFont, Settings.CREAM_COLOR, null, null, null));

        System.out.println("OJB: creating string input screen");
        BaseMod.subscribe(this);
    }

    //-------------------------------------------
    // modifying inputs

    public void appendLetter(char c) {
        this.text = this.text + c;
    }
    public void backspace() {
        if (text.length() > 1)
            text = text.substring(0, text.length() - 1);
    }

    public String getText() {
        return this.text;
    }

    public boolean isVisible() { return visible; }

    //-------------------------------------------


    public void show() {
        System.out.println("OJB: showing string input screen");

        oldInputProcessor = Gdx.input.getInputProcessor();
        //Gdx.input.setInputProcessor(new StringInputProcessor(this));

        // SHOW SCREEN (copied from base game)
        AbstractDungeon.player.releaseCard();
        CardCrawlGame.sound.play("DECK_OPEN");

        AbstractDungeon.dynamicBanner.hide();
        AbstractDungeon.isScreenUp = true;
        //AbstractDungeon.screen = AbstractDungeon.CurrentScreen.MASTER_DECK_VIEW;
        AbstractDungeon.overlayMenu.proceedButton.hide();
        AbstractDungeon.overlayMenu.hideCombatPanels();
        AbstractDungeon.overlayMenu.showBlackScreen();
        //AbstractDungeon.overlayMenu.cancelButton.show(TEXT[1]);


        // Setup the hitbox
        if (this.okHB == null) {
            this.okHB = new Hitbox(160.0f * Settings.scale, 70.0f * Settings.scale);
        }

        this.okHB.move(860.0F * Settings.scale, Settings.OPTION_Y - 118.0F * Settings.scale);

        // Enable the rendering
        visible = true;
    }

    public void hide() {
        System.out.println("OJB: hiding string input screen");
        // TODO: warning Don't try to call this before show()
//        if (!visible || oldInputProcessor == null)
//            return;

        //AbstractDungeon.genericScreenOverlayReset();
        ScreenHelper.closeCustomScreen("DECK_CLOSE");
        //AbstractDungeon.overlayMenu.cancelButton.hide();


        // reset to the old input processor
        Gdx.input.setInputProcessor(this.oldInputProcessor);

        visible = false;
    }

    @Override
    public void receivePostUpdate() {
        if (visible) {
            // Handle backspace
//            if (Gdx.input.isKeyPressed(Input.Keys.BACKSPACE) && !text.equals("") && this.waitTimer <= 0.0F) {
//                text = text.substring(0, text.length() - 1);
//                this.waitTimer = DELAY;
//            }
//
//            // Subtract from the delay time
//            if (this.waitTimer > 0.0F) {
//                this.waitTimer -= Gdx.graphics.getDeltaTime();
//            }

            // Save and quit
            if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
                hide();
            }

            // Input.Keys.ENTER can be a shortcut for confirm / escape for cancel :)
            // note that special behavior is required for the escape (so it doesn't fall through to other objects
            //    listening to the input handler)

            // The original code has a bunch of interp code to improve the graphics here as well (gonna ignore that
            //    for now)

            updateHB();

        }
    }

    private void updateHB() {
        this.okHB.update();

        if (okHB.justHovered)
            CardCrawlGame.sound.play("UI_HOVER");

        if (InputHelper.justClickedLeft && this.okHB.hovered) {
            CardCrawlGame.sound.play("UI_CLICK_1");
            this.okHB.clickStarted = true;
        }

        if (this.okHB.clicked) {
            this.okHB.clicked = false;

            // Handle
            System.out.println("OJB: okHB is clicked");
            hide();
        }

    }

    // Main render function
    @Override
    public void receiveRender(SpriteBatch sb) {
        if (!visible)
            return;

        // Dim the background
//        sb.setColor(this.screenColor);
//        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, (float) Settings.WIDTH, (float)Settings.HEIGHT);


        // Draw the surrounding box background
        sb.setColor(this.uiColor);
        sb.draw(
                ImageMaster.OPTION_CONFIRM,
                ((float)Settings.WIDTH / 2.0F - 300.0F) * Settings.scale,
                (Settings.OPTION_Y - 207.0F) * Settings.scale,
                180.0F * Settings.scale,
                207.0F * Settings.scale,
                600.0F * Settings.scale, //360.0F,
                414.0F * Settings.scale, //414.0F,
                1.0f, //Settings.scale,
                1.0f, //Settings.scale,
                0.0F,
                0,
                0,
                360,
                414,
                false,
                false);

        // Draw the text box (tbh im not sure what this actually does)
        sb.draw(
                ImageMaster.RENAME_BOX,
                //ImageMaster.WHITE_SQUARE_IMG,
                (float)Settings.WIDTH / 2.0F - 160.0F,
                Settings.OPTION_Y - 160.0F,
                160.0F,
                160.0F,
                320.0F,
                320.0F,
                Settings.scale,
                Settings.scale,
                0.0F,
                0,
                0,
                320,
                320,
                false,
                false);

        // Render the text inside the input area
        FontHelper.renderSmartText(sb,
                FontHelper.cardTitleFont_small,
                text,
                (float)Settings.WIDTH / 2.0F - 120.0F * Settings.scale,
                Settings.OPTION_Y + 4.0F * Settings.scale,
                100000.0F,
                0.0F,
                this.uiColor);


        // Render a "_" after the text input to show we're currently editing this text area
        float tmpAlpha = (MathUtils.cosDeg((float)(System.currentTimeMillis() / 3L % 360L)) + 1.25F) / 3.0F * this.uiColor.a;
        FontHelper.renderSmartText(sb,
                FontHelper.cardTitleFont_small,
                "_",
                (float)Settings.WIDTH / 2.0F - 122.0F * Settings.scale + FontHelper.getSmartWidth(FontHelper.cardTitleFont_small, text, 1000000.0F, 0.0F),
                Settings.OPTION_Y + 4.0F * Settings.scale,
                100000.0F,
                0.0F,
                new Color(1.0F, 1.0F, 1.0F, tmpAlpha));


        // A descriptive explanation text of what this input is for
        Color c = Settings.GOLD_COLOR.cpy();
        c.a = this.uiColor.a;
        FontHelper.renderFontCentered(sb,
                FontHelper.cardTitleFont,
                this.explanationText,
                (float)Settings.WIDTH / 2.0F,
                Settings.OPTION_Y + 126.0F * Settings.scale,
                c);

        // Buttons
        if (this.okHB.clickStarted) {
            sb.setColor(new Color(1.0F, 1.0F, 1.0F, this.uiColor.a * 0.9F));
            sb.draw(ImageMaster.OPTION_YES, (float)Settings.WIDTH / 2.0F - 86.5F - 100.0F * Settings.scale, Settings.OPTION_Y - 37.0F - 120.0F * Settings.scale, 86.5F, 37.0F, 173.0F, 74.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 173, 74, false, false);
            sb.setColor(new Color(this.uiColor));
        } else {
            sb.draw(ImageMaster.OPTION_YES, (float)Settings.WIDTH / 2.0F - 86.5F - 100.0F * Settings.scale, Settings.OPTION_Y - 37.0F - 120.0F * Settings.scale, 86.5F, 37.0F, 173.0F, 74.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 173, 74, false, false);
        }

        if (!this.okHB.clickStarted && this.okHB.hovered) {
            sb.setColor(new Color(1.0F, 1.0F, 1.0F, this.uiColor.a * 0.25F));
            sb.setBlendFunction(770, 1);
            sb.draw(ImageMaster.OPTION_YES, (float)Settings.WIDTH / 2.0F - 86.5F - 100.0F * Settings.scale, Settings.OPTION_Y - 37.0F - 120.0F * Settings.scale, 86.5F, 37.0F, 173.0F, 74.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 173, 74, false, false);
            sb.setBlendFunction(770, 771);
            sb.setColor(this.uiColor);
        }

        // Draw the confirm text inside the ok box
        if (this.okHB.clickStarted)
            c = Color.LIGHT_GRAY.cpy();
        else if (this.okHB.hovered)
            c = Settings.CREAM_COLOR.cpy();
        else
            c = Settings.GOLD_COLOR.cpy();

        c.a = this.uiColor.a;
        FontHelper.renderFontCentered(sb,
                FontHelper.cardTitleFont_small,
                "Confirm",
                (float)Settings.WIDTH / 2.0F - 110.0F * Settings.scale,
                Settings.OPTION_Y - 118.0F * Settings.scale,
                c,
                1.0F);


        // TODO
        textField.draw(sb, 1.0f);


        // NO
//        sb.draw(ImageMaster.OPTION_NO, (float)Settings.WIDTH / 2.0F - 80.5F + 106.0F * Settings.scale, Settings.OPTION_Y - 37.0F - 120.0F * Settings.scale, 80.5F, 37.0F, 161.0F, 74.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 161, 74, false, false);
//        if (!this.noHb.clickStarted && this.noHb.hovered) {
//            sb.setColor(new Color(1.0F, 1.0F, 1.0F, this.uiColor.a * 0.25F));
//            sb.setBlendFunction(770, 1);
//            sb.draw(ImageMaster.OPTION_NO, (float)Settings.WIDTH / 2.0F - 80.5F + 106.0F * Settings.scale, Settings.OPTION_Y - 37.0F - 120.0F * Settings.scale, 80.5F, 37.0F, 161.0F, 74.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 161, 74, false, false);
//            sb.setBlendFunction(770, 771);
//            sb.setColor(this.uiColor);
//        }

        /*
        if (this.noHb.clickStarted) {
            c = Color.LIGHT_GRAY.cpy();
        } else if (this.noHb.hovered) {
            c = Settings.CREAM_COLOR.cpy();
        } else {
            c = Settings.GOLD_COLOR.cpy();
        }

        c.a = this.uiColor.a;
        FontHelper.renderFontCentered(sb, FontHelper.cardTitleFont_small_N, "Cancel", (float)Settings.WIDTH / 2.0F + 110.0F * Settings.scale, Settings.OPTION_Y - 118.0F * Settings.scale, c, 1.0F);

        //if (this.visible) {
        // show the hitbox when debug true is set
            this.okHB.render(sb);
            //this.noHb.render(sb);
        //}

    }

}
                    */
