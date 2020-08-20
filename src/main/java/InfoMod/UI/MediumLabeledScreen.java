package InfoMod.UI;

import InfoMod.RenderingUtils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;

public class MediumLabeledScreen extends AbstractScreen {
    protected String title;

    protected ColorableSmallTextButton confirmButton;
    protected SimpleButtonWidget cancelButton;

    protected Color titleColor;
    protected BitmapFont titleFont;


    //--------------------------------------------------------------------------------
    // Layout

    protected static final float TITLE_AREA_HEIGHT = 85.0f;

    protected static final float ABSOLUTE_CONTENT_LEFT_X = 584.5f;
    protected static final float ABSOLUTE_CONTENT_RIGHT_X = 1335.5f;
    protected static final float ABSOLUTE_CONTENT_BOTTOM_Y = 247.0f;
    protected static final float ABSOLUTE_CONTENT_TITLE_TOP_Y = 833.0f;
    protected static final float ABSOLUTE_CONTENT_BODY_TOP_Y = ABSOLUTE_CONTENT_TITLE_TOP_Y - TITLE_AREA_HEIGHT - 5.0f;

    protected static final float CONTENT_PADDING_SM = 16.0f;
    protected static final float CONTENT_PADDING_MD = 31.0f;
    protected static final float CONTENT_PADDING_LG = 45.0f;

    protected static final float TITLE_X = ABSOLUTE_CONTENT_LEFT_X + 30.0f;
    protected static final float TITLE_Y = ABSOLUTE_CONTENT_TITLE_TOP_Y - 30.5f;

    protected static final float CLOSE_BUTTON_SIZE = 50.0f;
    protected static final float CONFIRM_BUTTON_WIDTH = 230.0f;

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
                .with_tooltip("Cancel", "Close this screen.");

        childWidgets.add(confirmButton);
        childWidgets.add(cancelButton);
    }

    @Override
    public void receiveRender(SpriteBatch sb) {
        super.receiveRender(sb);

        if (!visible)
            return;

        // Render the title
        FontHelper.renderSmartText(sb,
                titleFont,
                title,
                TITLE_X,
                TITLE_Y,
                titleColor);
    }
}
