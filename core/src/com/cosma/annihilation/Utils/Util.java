package com.cosma.annihilation.Utils;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import java.math.BigDecimal;

public class Util {


    public static float setWindowWidth(float scale) {
        return (Gdx.app.getGraphics().getWidth()) * scale;
    }

    public static float setWindowHeight(float scale) {
        return (Gdx.app.getGraphics().getHeight()) * scale;
    }

    public static float setButtonWidth(float scale) {
        return (Gdx.app.getGraphics().getWidth() / 12.8f) * scale;
    }

    public static float setButtonHeight(float scale) {
        return (Gdx.graphics.getHeight() / 24) * scale;
    }

    public static float setFontScale(float scale) {
        return (Gdx.graphics.getHeight() / 500) * scale;
    }

    public static void setButtonColor(TextButton button) {
        button.getLabel().setColor(0, 82, 0, 255);
    }

    public static void setLabelColorToGreen(Label label) {
        label.setColor(0, 82, 0, 255);
    }
    /**
     * Return true if number is in range
     **/
    public static boolean isFloatInRange(float number, float min, float max) {
        if (number > min && number < max) {
            return true;
        }
        return false;
    }

    public  static boolean hasComponent(Entity entity,Class<? extends Component> componentClass ){
        return entity.getComponent(componentClass) != null;
    }

    public static float roundFloat(float number, int decimalPlace) {
        BigDecimal bd = new BigDecimal(number);
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    public static Vector3 getWorldCoordinates(int screenX, int screenY, OrthographicCamera camera) {
        Vector3 worldCoordinates = new Vector3(screenX, screenY, 0);
        return camera.unproject(worldCoordinates);
    }

    public static Vector2 jsonStringToVector2(String string){
        return new Vector2(Float.parseFloat(string.split(",")[0]),Float.parseFloat(string.split(",")[1]));
    }

    public static void setCursorMove(){
        Pixmap pm = new Pixmap(Gdx.files.internal("gfx/textures/cursor_move.png"));
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(pm, 16, 16));
        pm.dispose();
    }
    public static void setCursorSize(){
        Pixmap pm = new Pixmap(Gdx.files.internal("gfx/textures/cursor_size.png"));
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(pm, 16, 16));
        pm.dispose();
    }

    public static void setCursorSizeHorizontal(){
        Pixmap pm = new Pixmap(Gdx.files.internal("gfx/textures/cursor_size_horizontal.png"));
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(pm, 16, 16));
        pm.dispose();
    }
    public static void setCursorSystem(){
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
    }

    public static void createBox2dObject(World world, float x, float y, float width, float height, BodyDef.BodyType bodyType, String name,float rotation,String userDate) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;
        bodyDef.position.set(width / 2 + x, height / 2 + y);
        Body body = world.createBody(bodyDef);
        body.setUserData(name);
        //Physic fixture
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 5f;
        fixtureDef.friction = 1f;
        fixtureDef.filter.categoryBits = CollisionID.SCENERY;
        fixtureDef.filter.maskBits = CollisionID.MASK_SCENERY;
        body.createFixture(fixtureDef).setUserData(userDate);
        body.setTransform(body.getPosition(),rotation);
    }


    public static void createPointLight(){

    }
}
