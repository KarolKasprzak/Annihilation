package com.cosma.annihilation.Editor;


import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.cosma.annihilation.Editor.CosmaMap.*;
import com.cosma.annihilation.Screens.MapEditor;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.util.adapter.AbstractListAdapter;
import com.kotcrab.vis.ui.util.adapter.ArrayAdapter;
import com.kotcrab.vis.ui.util.adapter.ListSelectionAdapter;
import com.kotcrab.vis.ui.widget.*;

public class LayersPanel extends VisWindow {

    private MapEditor mapEditor;
    private VisTable layersTable;
    private MapLayer selectedLayer;

    public <T extends MapLayer> T getSelectedLayer(Class<T> type) {
        if (selectedLayer == null) {
            return null;
        }
        return type.cast(selectedLayer);
    }

    public LayersPanel(final MapEditor mapEditor) {
        super("Map layers");
        this.mapEditor = mapEditor;
        TableUtils.setSpacingDefaults(this);
        columnDefaults(0).left();

        VisTextButton addLayerButton = new VisTextButton("+");
        VisTextButton removeLayerButton = new VisTextButton("-");
        VisTextButton layerUp = new VisTextButton("^");
        VisTextButton layerDown = new VisTextButton("v");


        final MapLayerAdapter adapter = new MapLayerAdapter(mapEditor.getMap().getLayers().getLayers(), mapEditor);
        final ListView<MapLayer> view = new ListView<MapLayer>(adapter);
        view.setUpdatePolicy(ListView.UpdatePolicy.ON_DRAW);

        VisTable footerTable = new VisTable();
        footerTable.addSeparator();
        footerTable.add("");
        view.setFooter(footerTable);

        VisTable buttonTable = new VisTable(true);

        MenuBar menuBar = new MenuBar();
        Menu layerMenu = new Menu("+");
        layerMenu.addItem(new MenuItem("Tile", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (mapEditor.getMap() != null) {
                    mapEditor.getMap().createTileMapLayer();
                    view.rebuildView();
                }
            }
        }));
        layerMenu.addItem(new MenuItem("Obj", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (mapEditor.getMap() != null) {
                    mapEditor.getMap().createObjectMapLayer();
                    view.rebuildView();
                }
            }
        }));
        layerMenu.addItem(new MenuItem("Light", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (mapEditor.getMap() != null && mapEditor.getMap().getLayers().getByType(LightsMapLayer.class).size == 0) {
                    mapEditor.getMap().createLightsLayer();
                    view.rebuildView();
                }
            }
        }));
        layerMenu.addItem(new MenuItem("Sprite", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (mapEditor.getMap() != null) {
                    mapEditor.getMap().createSpriteLayer();
                    view.rebuildView();
                }
            }
        }));
        menuBar.addMenu(layerMenu);

        this.getTitleTable().add(menuBar.getTable());
        this.getTitleTable().add(removeLayerButton);
        this.getTitleTable().add(layerUp);
        this.getTitleTable().add(layerDown);

        row();
        add(view.getMainTable()).fill().expandY().center();
        row();
        add(buttonTable).padBottom(3);
        row();

        pack();
        setSize(getWidth() * 2, getHeight() + 500);
        setMovable(false);
        setResizable(false);

        removeLayerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                if (selectedLayer != null) {
                    System.out.println(mapEditor.getMap().getLayers().isEmpty());
                    mapEditor.getMap().getLayers().remove(selectedLayer.getName());
                    view.rebuildView();
                }
            }
        });

        layerUp.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (selectedLayer != null && !mapEditor.getMap().getLayers().isEmpty()) {
                    int index = mapEditor.getMap().getLayers().getIndex(selectedLayer.getName());
                    if (index != 0) {
                        mapEditor.getMap().getLayers().swapLayerOrder(index, index - 1);
                        view.rebuildView();
                    }
                }
            }
        });

        layerDown.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (selectedLayer != null && !mapEditor.getMap().getLayers().isEmpty()) {
                    int index = mapEditor.getMap().getLayers().getIndex(selectedLayer.getName());
                    if (index != mapEditor.getMap().getLayers().getCount() - 1) {
                        mapEditor.getMap().getLayers().swapLayerOrder(index, index + 1);
                        view.rebuildView();
                    }
                }
            }
        });

        adapter.setSelectionMode(AbstractListAdapter.SelectionMode.SINGLE);
        view.setItemClickListener(new ListView.ItemClickListener<MapLayer>() {
            @Override
            public void clicked(MapLayer item) {
            }
        });
        adapter.getSelectionManager().setListener(new ListSelectionAdapter<MapLayer, VisTable>() {
            @Override
            public void selected(MapLayer item, VisTable view) {
                selectedLayer = mapEditor.getMap().getLayers().getLayer(item.getName());
                if (selectedLayer instanceof TileMapLayer) {
                    mapEditor.setTileLayerSelected(true);
                    mapEditor.lightsPanel.setPanelButtonsDisable(true);
                    mapEditor.objectPanel.setPanelButtonsDisable(true);
                } else mapEditor.setTileLayerSelected(false);
                if (selectedLayer instanceof ObjectMapLayer) {
                    mapEditor.lightsPanel.setPanelButtonsDisable(true);
                    mapEditor.objectPanel.setPanelButtonsDisable(false);
                    mapEditor.setObjectLayerSelected(true);
                } else mapEditor.setObjectLayerSelected(false);
                if (selectedLayer instanceof LightsMapLayer) {
                    mapEditor.objectPanel.setPanelButtonsDisable(true);
                    mapEditor.lightsPanel.setPanelButtonsDisable(false);
                    mapEditor.setLightsLayerSelected(true);
                } else mapEditor.setLightsLayerSelected(false);
                if (selectedLayer instanceof SpriteMapLayer) {
                    mapEditor.objectPanel.setPanelButtonsDisable(true);
                    mapEditor.lightsPanel.setPanelButtonsDisable(true);
                    mapEditor.setSpriteLayerSelected(true);
                } else mapEditor.setSpriteLayerSelected(false);

            }

            @Override
            public void deselected(MapLayer item, VisTable view) {
                selectedLayer = null;
                mapEditor.setTileLayerSelected(false);
                mapEditor.setEntityLayerSelected(false);
                mapEditor.setLightsLayerSelected(false);
                mapEditor.setObjectLayerSelected(false);
                mapEditor.setSpriteLayerSelected(false);
            }
        });
    }


    private static class MapLayerAdapter extends ArrayAdapter<MapLayer, VisTable> {
        private final Drawable bg = VisUI.getSkin().getDrawable("window-bg");
        private final Drawable selection = VisUI.getSkin().getDrawable("list-selection");
        private MapEditor mapEditor;

        MapLayerAdapter(Array<MapLayer> array, MapEditor mapEditor) {
            super(array);
            setSelectionMode(SelectionMode.SINGLE);
            this.mapEditor = mapEditor;
        }

        @Override
        protected void selectView(VisTable view) {
            view.setBackground(selection);
        }

        @Override
        protected void updateView(VisTable view, MapLayer item) {
            super.updateView(view, item);
        }

        @Override
        protected void deselectView(VisTable view) {
            view.setBackground(bg);
        }

        @Override
        protected VisTable createView(final MapLayer item) {
            final MapLayer mapLayer = mapEditor.getMap().getLayers().getLayer(item.getName());
            VisLabel label = new VisLabel(item.getName());
            VisCheckBox box = new VisCheckBox("", mapLayer.isLayerVisible());

            box.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (mapLayer.isLayerVisible()) {
                        mapLayer.setVisible(false);
                    } else
                        mapLayer.setVisible(true);
                }
            });

            VisTable table = new VisTable();
            table.center();
            table.add(label).fill().expandX();
            table.add(box);
            return table;
        }
    }
}
