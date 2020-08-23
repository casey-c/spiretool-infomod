package InfoMod.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface IScreenWidget {
    void show();
    void hide();
    void render(SpriteBatch sb);
}
