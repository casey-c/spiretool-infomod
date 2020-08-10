package InfoMod;

import basemod.abstracts.CustomSavableRaw;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

/*
TODO:
  I struggled to make this work correctly as a saveable on its own (probably due to my own tiredness)
  I might take another look at it later, but as a workaround all the saving/loading is done in the main class instead
  it's uglier, but at least it works and doesn't crash :)
 */
public class BossStringsSaveable {
    public String act1, act2, act3_1, act3_2;
    public String combined;

//    @Override
//    public JsonElement onSaveRaw() {
//    }
//
//    @Override
//    public void onLoadRaw(JsonElement jsonElement) {
//    }
}
