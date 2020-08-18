package InfoMod.UI;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface IScreenWidget {
    public void show();
    public void hide();
    public void render(SpriteBatch sb);
}
