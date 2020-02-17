package com.cosma.annihilation.Editor;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Screens.MapEditor;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;


public class TilesPanel extends VisWindow {

    private MapEditor mapEditor;
    private VisTable tilesTable;
    private TilesPanel tilesPanel;

    private String atlasRegionName;
    private String atlasPath;
    private Array<FileHandle> textureAtlasArray;
    private TabbedPane tabbedPane;

    public TilesPanel(final MapEditor mapEditor) {
        super("Tiles");
        this.mapEditor = mapEditor;
        this.tilesPanel = this;

        textureAtlasArray = new Array<>();
        TableUtils.setSpacingDefaults(this);
        columnDefaults(0).left();
        VisTextButton addTilesButton = new VisTextButton("+");
        tilesTable = new VisTable(true);
        tabbedPane = new TabbedPane();
        tabbedPane.addListener(new TabbedPaneAdapter() {
            @Override
            public void switchedTab (Tab tab) {
                tilesTable.clearChildren();
                tilesTable.add(tab.getContentTable()).expand().fill();
            }
        });

        addTilesButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
             findTextureAtlas();
            }
        });



        this.getTitleTable().add(addTilesButton);
        add(tabbedPane.getTable()).expandX().fillX();
        row();
        add(tilesTable).expand().fill();

        findTextureAtlas();
        setSize(Gdx.graphics.getWidth()*0.15f, Gdx.graphics.getHeight()*0.25f);
        setPosition(1900, 200);
    }

    private void findTextureAtlas(){
        FileHandle file = Gdx.files.local("map/map_tiles");
        for(FileHandle texture: file.list(".atlas")){
            if(!textureAtlasArray.contains(texture,false)){
                textureAtlasArray.add(texture);
            }
        }
        for(FileHandle files: textureAtlasArray){
            boolean canAdd = true;
            for(Tab tab: tabbedPane.getTabs()){
                if(tab.getTabTitle().equals(files.nameWithoutExtension())){
                    canAdd = false;
                }
            }
            if(canAdd){
                Annihilation.getAssets().load(files.path(),TextureAtlas.class);
                Annihilation.getAssets().finishLoading();
                AtlasTab atlasTab = new AtlasTab(files.nameWithoutExtension());
                atlasTab.getContentTable().add(buildTable(Annihilation.getAssets().get(files.path(),TextureAtlas.class),files.path()));
                tabbedPane.add(atlasTab);
            }
        }
        setMovable(false);
    }

    private VisTable buildTable(final TextureAtlas atlas, String path) {
        final VisTable table = new VisTable(true);
        table.setName(path);
        for (int i = 0; i < atlas.getRegions().size; i++) {
            VisImageButton button = new VisImageButton(new TextureRegionDrawable(atlas.getRegions().get(i)));
            button.setName(atlas.getRegions().get(i).name);
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {

                    tilesPanel.getTitleLabel().setText(actor.getName());
                    atlasRegionName = actor.getName();
                    atlasPath = table.getName();
                }
            });
            table.add(button).size(30, 30);
            if (i == 5 || i == 10 || i == 15 || i == 20 || i== 25) {
                table.row();
            }
        }
        return table;
    }

    public String getAtlasRegionName() {
        return atlasRegionName;
    }

    public String getAtlasPath() {
        return atlasPath;
    }

    private class AtlasTab extends Tab {
        private String title;
        private Table content;

        public AtlasTab (String title) {
            super(false, false);
            this.title = title;

            content = new VisTable();
        }

        @Override
        public String getTabTitle () {
            return title;
        }

        @Override
        public Table getContentTable () {
            return content;
        }
    }

}
