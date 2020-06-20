package com.cosma.annihilation.Editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Editor.CosmaMap.Sprite;
import com.cosma.annihilation.Screens.EditorScreen;
import com.cosma.annihilation.Utils.Util;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTree;
import com.kotcrab.vis.ui.widget.VisWindow;


public class SpriteTreeWindow extends VisWindow implements InputProcessor {

    private EditorScreen editorScreen;
    private boolean canAddSprite = false;
    private String texturePath;
    private String textureRegionName;
    private Sprite selectedSprite;
    private VisCheckBox snapSprite,editSprite;

    private boolean canMove = false;
    private boolean isMoving = false;
    private boolean createAnimatedSprite = false;

    private boolean canRotate = false;
    private boolean isRotating = false;

    public SpriteTreeWindow(EditorScreen editorScreen) {

        super("Sprite:");
        this.editorScreen = editorScreen;
        TableUtils.setSpacingDefaults(this);
        columnDefaults(0).left();
        final VisTree tree = new VisTree();
        Node treeRoot = new Node(new VisLabel("Sprite"));
        FileHandle file = Gdx.files.local("map/map_sprites");
        for (FileHandle atlasFile : file.list("atlas")) {
            Node node = new Node(new VisLabel(atlasFile.nameWithoutExtension()));

            node.setObject(atlasFile);

            treeRoot.add(node);
            TextureAtlas atlas = Annihilation.getAssets().get(atlasFile.path(),TextureAtlas.class);
            for(TextureAtlas.AtlasRegion textureRegion : atlas.getRegions()){
                if(textureRegion.index < 0){
                    VisLabel label = new VisLabel(textureRegion.name);
                    label.setName(textureRegion.name);
                    Node childrenNode = new Node(label);
                    childrenNode.setObject(textureRegion);
                    childrenNode.setIcon(new TextureRegionDrawable(textureRegion));
                    node.add(childrenNode);
                }
                if(textureRegion.index == 1){
                    VisLabel label = new VisLabel(textureRegion.name+"*");
                    label.setName(textureRegion.name);
                    Node childrenNode = new Node(label);
                    childrenNode.setObject(textureRegion);
                    node.add(childrenNode);
                }
            }
        }
        treeRoot.setExpanded(true);
        tree.add(treeRoot);
        ScrollPane scrollPane = new ScrollPane(tree);

        snapSprite = new VisCheckBox("Snap to grid", true);
        editSprite = new VisCheckBox("Enable options", false);

        add(snapSprite);
        row();
        add(editSprite);
        row();
        add(scrollPane).expand().top();
        setSize(150, 380);
        setPosition(0, 0);
        tree.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                if (!tree.getSelection().isEmpty()) {
                    if(tree.getSelection().first().getObject() instanceof TextureAtlas.AtlasRegion && editorScreen.isSpriteEditModeSelected()){
                        canAddSprite = true;
                        if(((TextureAtlas.AtlasRegion) tree.getSelection().first().getObject()).index > 0){
                            createAnimatedSprite = true;
                        }
                        textureRegionName = ((TextureAtlas.AtlasRegion) tree.getSelection().first().getObject()).name;
                        texturePath = ((FileTextureData) ((TextureAtlas.AtlasRegion) tree.getSelection().first().getObject()).getTexture().getTextureData()).getFileHandle().pathWithoutExtension()+".atlas";
                        Util.setCursorMove();
                    }
                }
            }
        });

        ClickListener clickListener = new ClickListener();

       scrollPane.addListener(new ClickListener(){

           @Override
           public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
               super.enter(event, x, y, pointer, fromActor);
               getStage().setScrollFocus(scrollPane);
           }

           @Override
           public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
               super.exit(event, x, y, pointer, toActor);
               getStage().setScrollFocus(null);
           }


       });



//        this.addListener(new ClickListener(){
//
//            @Override
//            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
//                super.enter(event, x, y, pointer, fromActor);
//                System.out.println("enter");
//            }
//
//            @Override
//            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
//                super.exit(event, x, y, pointer, toActor);
//                System.out.println("exit");
//
//            }
//        });

    }

    private void findSprite(int x, int y){
        Vector3 worldCoordinates = new Vector3(x, y, 0);
        final Vector3 vec = editorScreen.getCamera().unproject(worldCoordinates);
        boolean isSpriteSelected = false;
        for(Sprite sprite: editorScreen.getMap().getSpriteMapLayer().getSpriteArray()){
            if(vec.x >= sprite.getX() && vec.x <= sprite.getX() + sprite.getWidth() && vec.y <= sprite.getY()+sprite.getHeight() && vec.y >= sprite.getY()){
                    Util.setCursorMove();
                    canMove = true;
                    canRotate = true;
                    selectedSprite = sprite;
                    isSpriteSelected = true;
            }
        }
        if(!isSpriteSelected){
            Util.setCursorSystem();
            canMove = false;
            canRotate = false;
            selectedSprite = null;
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.FORWARD_DEL && selectedSprite != null) {
            editorScreen.getMap().getSpriteMapLayer().getSpriteArray().removeValue(selectedSprite,true);
            selectedSprite = null;
            Util.setCursorSystem();
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(final int screenX, final int screenY, int pointer, int button) {
        Vector3 worldCoordinates = new Vector3(screenX, screenY, 0);
        final Vector3 vec = editorScreen.getCamera().unproject(worldCoordinates);
        if (canAddSprite && button == Input.Buttons.LEFT && editorScreen.isSpriteEditModeSelected()) {
            if(createAnimatedSprite){
                editorScreen.getMap().getSpriteMapLayer().createAnimatedSprite(textureRegionName,texturePath,
                        Util.roundFloat(vec.x,0),Util.roundFloat(vec.y,0),0);
            }else{
                editorScreen.getMap().getSpriteMapLayer().createSprite(textureRegionName,texturePath,
                        Util.roundFloat(vec.x,0),Util.roundFloat(vec.y,0),0);
            }

            canAddSprite = false;
            createAnimatedSprite = false;
            Util.setCursorSystem();
        }
        if (canAddSprite && button == Input.Buttons.RIGHT) {
            canAddSprite = false;
            Util.setCursorSystem();
        }
        if (canMove && button == Input.Buttons.LEFT && selectedSprite != null) {
            isMoving = true;
        }
        if (canRotate && button == Input.Buttons.RIGHT && selectedSprite != null) {
            isRotating = true;
            Util.setCursorSize();
        }

        if(editSprite.isChecked() && button == Input.Buttons.RIGHT && selectedSprite != null){
            SpriteEditWindow spriteEditWindow = new SpriteEditWindow(selectedSprite);
            this.getStage().addActor(spriteEditWindow);
        }

      return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (canMove && button == Input.Buttons.LEFT && selectedSprite != null) {
            isMoving = false;
            selectedSprite = null;
            Util.setCursorSystem();
        }
        if (canRotate && button == Input.Buttons.RIGHT && selectedSprite != null) {
            isRotating = false;
            selectedSprite = null;
            Util.setCursorSystem();
        }


        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Vector3 worldCoordinates = new Vector3(screenX, screenY, 0);
        final Vector3 vec = editorScreen.getCamera().unproject(worldCoordinates);
        float amountY;
        if(isMoving){
                selectedSprite.setSpritePosition(vec.x,vec.y);
                if(snapSprite.isChecked()){
                    selectedSprite.setSpritePosition(Util.roundFloat(vec.x,0),Util.roundFloat(vec.y,0));
                }
        }
        if (isRotating){
            Vector3 deltaWorldCoordinates = new Vector3(screenX - Gdx.input.getDeltaX(), screenY - Gdx.input.getDeltaY(), 0);
            Vector3 deltaVec = editorScreen.getCamera().unproject(deltaWorldCoordinates);
            amountY = vec.y - deltaVec.y;
            selectedSprite.setSpriteAngle(selectedSprite.getAngle() + amountY * 10);
        }

        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {

        if(editorScreen.isSpriteEditModeSelected()){
            findSprite(screenX,screenY);
        }

        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}

