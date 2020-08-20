package InfoMod.UI;

import InfoMod.RenderingUtils;
import basemod.BaseMod;
import basemod.interfaces.RenderSubscriber;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;

import java.util.ArrayList;

public class MediumLabeledScreen extends AbstractScreen {
    //protected boolean visible = false;
    protected String title;

    //protected ArrayList<IScreenWidget> childWidgets = new ArrayList<>();

    protected ColorableSmallTextButton confirmButton;//, cancelButton;
    protected SimpleButtonWidget cancelButton;

    protected Color titleColor;
    protected BitmapFont titleFont;

    protected String cancelButtonToolTipHeader = "Cancel";
    protected String cancelButtonToolTipBody = "Close this screen.";

    //--------------------------------------------------------------------------------
    // Layout

    private static final float TITLE_AREA_HEIGHT = 85.0f;

    private static final float ABSOLUTE_CONTENT_LEFT_X = 584.5f;
    private static final float ABSOLUTE_CONTENT_RIGHT_X = 1335.5f;
    private static final float ABSOLUTE_CONTENT_BOTTOM_Y = 247.0f;
    private static final float ABSOLUTE_CONTENT_TITLE_TOP_Y = 833.0f;
    private static final float ABSOLUTE_CONTENT_BODY_TOP_Y = ABSOLUTE_CONTENT_TITLE_TOP_Y - TITLE_AREA_HEIGHT - 5.0f;

    private static final float CONTENT_PADDING_SM = 16.0f;
    private static final float CONTENT_PADDING_MD = 31.0f;
    private static final float CONTENT_PADDING_LG = 45.0f;

    private static final float TITLE_X = ABSOLUTE_CONTENT_LEFT_X + 30.0f;
    private static final float TITLE_Y = ABSOLUTE_CONTENT_TITLE_TOP_Y - 30.5f;


    private static final float CLOSE_BUTTON_SIZE = 50.0f;
    private static final float CONFIRM_BUTTON_WIDTH = 230.0f;
    private static final float CONFIRM_BUTTON_HEIGHT = 90.0f;


    //--------------------------------------------------------------------------------
    public MediumLabeledScreen(String title) {
        super();

        this.SCREEN_BG = new Texture("images/screens/medium_labeled.png");
        this.SCREEN_W = 765.0f;
        this.SCREEN_H = 600.0f;

        this.title = title;

        this.titleColor = Settings.CREAM_COLOR;
        this.titleFont = FontHelper.turnNumFont;


        confirmButton = new ColorableSmallTextButton(
                ABSOLUTE_CONTENT_RIGHT_X - CONTENT_PADDING_SM - CONFIRM_BUTTON_WIDTH,
                ABSOLUTE_CONTENT_BOTTOM_Y + CONTENT_PADDING_SM,
                21.0f,
                -2.0f,
                "Save",
                RenderingUtils.OJB_GREEN_BUTTON_COLOR,
                button -> { saveAndClose(); });

        cancelButton = (SimpleButtonWidget) ButtonFactory.buildCloseButton(
                ABSOLUTE_CONTENT_RIGHT_X - CONTENT_PADDING_SM - CLOSE_BUTTON_SIZE,
                ABSOLUTE_CONTENT_TITLE_TOP_Y - CONTENT_PADDING_SM - CLOSE_BUTTON_SIZE,
                button -> { revertAndClose(); })
                .with_tooltip(cancelButtonToolTipHeader, cancelButtonToolTipBody);

        //FontHelper.renderSmartText(sb, FontHelper.turnNumFont);

        childWidgets.add(confirmButton);
        childWidgets.add(cancelButton);
    }

//    @Override
////    public void show() {
////        if (visible)
////            return;
////
////        visible = true;
////
////        RenderingUtils.openCustomScreen("DECK_OPEN");
////
////        for (IScreenWidget w : childWidgets)
////            w.show();
////    }

//    @Override
//    public void hide() {
//        if (!visible)
//            return;
//
//        RenderingUtils.closeCustomScreen("DECK_CLOSE");
//
//        for (IScreenWidget w : childWidgets)
//            w.hide();
//
//        visible = false;
//    }

//    public void saveAndClose() {
//        hide();
//        // TODO: subclasses should override as needed
//    }
//
//    public void revertAndClose() {
//        hide();
//        // TODO: subclasses should override as needed
//    }


    @Override
    public void receiveRender(SpriteBatch sb) {
        super.receiveRender(sb);

        if (!visible)
            return;

//        if (!visible)
//            return;
//
//        // Render the screen background
//        sb.setColor(Color.WHITE);
//        sb.draw( SCREEN_BG,
//                ((float) Settings.WIDTH - SCREEN_W * Settings.scale) * 0.5f,
//                ((float)Settings.HEIGHT - SCREEN_H * Settings.scale) * 0.5f
//        );

        // Render the title
        FontHelper.renderSmartText(sb,
                titleFont,
                title,
                TITLE_X,
                TITLE_Y,
                titleColor);

//        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipHeaderFont, "Customize the text displayed by the Potion Tracker.", CONTENT_X, TITLE_TEXT_Y, Settings.GOLD_COLOR);
//        FontHelper.renderSmartText(sb, FontHelper.tipHeaderFont, "Customize the positioning of the text. NL (0,0) bottom left -> (1920, 1080) top right", CONTENT_X, POSITION_INFO_Y,  Settings.GOLD_COLOR);

//        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "x", CONTENT_X, POSITION_BOTTOM_Y + EditableTextShort.TEXT_OFFSET_Y, Settings.CREAM_COLOR);
//        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "y", TEXTY_X, POSITION_BOTTOM_Y + EditableTextShort.TEXT_OFFSET_Y, Settings.CREAM_COLOR);

        // Render all children
//        for (IScreenWidget w : childWidgets)
//            w.render(sb);

//        mainTextArea.render(sb);
//        confirmButton.render(sb);
//        cancelButton.render(sb);
//        resetButton.render(sb);
//
//        textX.render(sb);
//        textY.render(sb);
    }
}
