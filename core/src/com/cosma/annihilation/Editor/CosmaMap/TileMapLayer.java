package com.cosma.annihilation.Editor.CosmaMap;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class TileMapLayer extends MapLayer implements Json.Serializable {
    private int width;
    private int height;
    private Tile[][] tiles;

    public TileMapLayer(int width, int height, String name) {
        super(width, height, name);
        this.height = height;
        this.width = width;
        this.tiles = new Tile[width][height];
    }

    public TileMapLayer() {

    }

    public Tile getTile(int x, int y) {
        if (x < 0 || x >= width) return null;
        if (y < 0 || y >= height) return null;
        if(tiles[x][y] == null) return null;
        return tiles[x][y];
    }

    public void setTile(int x, int y, Tile tile) {
        if (x < 0 || x >= width) return;
        if (y < 0 || y >= height) return;
        tiles[x][y] = tile;
    }

    @Override
    public void write(Json json) {
        json.writeValue("name", this.getName());
        json.writeValue("height", height);
        json.writeValue("width", width);
        json.writeArrayStart("tiles");
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if(getTile(x,y) == null){
                    continue;
                }
                Tile tile = getTile(x,y);
                json.writeObjectStart();
                json.writeValue("position",x+","+y);
                json.writeValue("texture",tile.getTextureDate());
                json.writeObjectEnd();
            }
        }
        json.writeArrayEnd();

    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.setName(jsonData.get("name").asString());
        height = jsonData.get("height").asInt();
        width = jsonData.get("width").asInt();
        this.tiles = new Tile[width][height];
        for(JsonValue value: jsonData.get("tiles")){
            Tile tile = new Tile();
            String texture = value.get("texture").asString();
            String position = value.get("position").asString();
            tile.setTextureRegion(texture.split(",")[1],texture.split(",")[0]);
            setTile(Integer.parseInt(position.split(",")[0]),Integer.parseInt(position.split(",")[1]),tile);
        }
    }
}
