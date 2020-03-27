package com.cosma.annihilation.Gui;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Components.PlayerComponent;
import com.cosma.annihilation.Components.PlayerInventoryComponent;
import com.cosma.annihilation.Items.*;
import com.cosma.annihilation.Utils.*;


public class PlayerInventoryWindow extends Window implements InventorySlotObserver {
    private Engine engine;
    private DragAndDrop dragAndDrop;
    private Table leftTable;
    private Table rightTable;

    private Table inventorySlotsTable;
    private Table equipmentSlotsTable;
    private Table playerViewTable;
    private Table statsTable;
    private Table medicalTable;

    private Skin skin;
    private InventorySlot weaponInventorySlot;
    private Label dmgLabel;
    private Label defLabel;
    private int inventorySize = 16;
    private float slotSize = Util.setWindowHeight(0.09f);
    private ActorGestureListener listener;
    private PlayerInventoryWindow playerInventoryWindow;

    private Label weaponName;
    private Label weaponDescription;
    private Label weaponDamage;
    private Label weaponAccuracy;
    private Label weaponAmmo;
    private Label weaponAmmoInMagazine;
    private InventorySlot armourInventorySlot;

    private AnimatedActor animatedActor;


    PlayerInventoryWindow(String title, Skin skin, final Engine engine) {
        super(title, skin);
        this.engine = engine;
        this.skin = skin;

//        this.debugAll();
        playerInventoryWindow = this;
        dragAndDrop = new DragAndDrop();
        leftTable = new Table(skin);
        leftTable.debugAll();
        rightTable = new Table(skin);

        debugAll();

        TextureAtlas atlas = Annihilation.getAssets().get("gfx/atlas/gui_human.atlas",TextureAtlas.class);
        Animation<TextureAtlas.AtlasRegion> animation = new Animation<>(0.1f,atlas.getRegions(), Animation.PlayMode.LOOP);
        animatedActor = new AnimatedActor(animation);

        this.background(new TextureRegionDrawable(new TextureRegion(Annihilation.getAssets().get("gfx/interface/clear.png",Texture.class))));

        listener = new ActorGestureListener() {
            @Override
            public void touchDown(InputEvent event, float x, float y, int pointer, int button) {

                InventorySlot inventorySlot = (InventorySlot) event.getListenerActor();
                if (inventorySlot.hasItem()) {
                }
                super.touchDown(event, x, y, pointer, button);
            }
        };
        this.add(leftTable);
        this.add(rightTable).expandX();

       createMedicalTable();
     createEquipmentTable();
        createInventoryTable();
        leftTable.add(inventorySlotsTable.bottom()).colspan(2).bottom().expandX().padLeft(Util.setWindowHeight(0.06f));

    }

    private void createMedicalTable(){
        medicalTable = new Table();
        medicalTable.add(animatedActor).size(250,250).expandY();
    }



    private void createEquipmentTable() {
        equipmentSlotsTable = new Table();

        armourInventorySlot = new InventorySlot(new Image(Annihilation.getAssets().get("gfx/interface/gui_armour_slot.png",Texture.class)));

        weaponInventorySlot = new InventorySlot(new Image(Annihilation.getAssets().get("gfx/interface/gui_weapon_slot.png",Texture.class)),ItemType.WEAPON_SHORT,ItemType.WEAPON_LONG);
        weaponInventorySlot.register(this);

        dragAndDrop.addTarget(new InventorySlotTarget(armourInventorySlot));
        dragAndDrop.addTarget(new InventorySlotTarget(weaponInventorySlot));

        equipmentSlotsTable.add(weaponInventorySlot).size(slotSize*1.33f, slotSize).pad(Util.setWindowHeight(0.006f)).colspan(2);

        equipmentSlotsTable.add(armourInventorySlot).size(slotSize*1.33f, slotSize).pad(Util.setWindowHeight(0.006f));

        equipmentSlotsTable.center();
        equipmentSlotsTable.pad(20);
        equipmentSlotsTable.debugAll();
    }

    public void setInventorySize(int size) {
        inventorySize = size;
    }


    private void createInventoryTable() {
        inventorySlotsTable = new Table();
        inventorySlotsTable.center();
        inventorySlotsTable.setDebug(true);
        inventorySlotsTable.setFillParent(true);



        for (int i = 1; i <= 21; i++) {
            InventorySlot inventorySlot = new InventorySlot();
//            inventorySlot.addListener(listener);
            inventorySlot.setImageScale(1f);
            dragAndDrop.addTarget(new InventorySlotTarget(inventorySlot));
            inventorySlotsTable.add(inventorySlot).size(slotSize, slotSize).pad(Util.setWindowHeight(0.005f));
            if (i == 3||i == 6||i == 9||i == 12|| i == 15||i == 18) inventorySlotsTable.row();
        }
    }

    public static void clearItemsTable(Table targetTable) {
        Array<Cell> cells = targetTable.getCells();
        for (int i = 0; i < cells.size; i++) {
            InventorySlot inventorySlot = (InventorySlot) cells.get(i).getActor();
            if (inventorySlot == null) continue;
            inventorySlot.clearItems();
        }
    }

    private static Array<Item> getItemsTable(Table targetTable) {
        Array<Cell> cells = targetTable.getCells();
        Array<Item> items = new Array<>();
        for (int i = 0; i < cells.size; i++) {
            InventorySlot inventorySlot = ((InventorySlot) cells.get(i).getActor());
            if (inventorySlot == null) continue;
            if (inventorySlot.getItemsNumber() > 0) {
                items.add(inventorySlot.getItem());
            }
        }
        return items;
    }

    public static void fillTable(Table targetTable, Array<Item> inventoryItems, DragAndDrop dragAndDrop) {
        clearItemsTable(targetTable);
        Array<Cell> cells = targetTable.getCells();
        for (int i = 0; i < inventoryItems.size; i++) {
            Item item = inventoryItems.get(i);
            InventorySlot inventorySlot = ((InventorySlot) cells.get(item.getTableIndex()).getActor());
            for (int index = 0; index < item.getItemAmount(); index++) {
                inventorySlot.add(item);
                dragAndDrop.addSource(new InventorySlotSource(inventorySlot, dragAndDrop));
            }
        }
    }

    void saveInventory(Engine engine) {
        Entity player = engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
        player.getComponent(PlayerInventoryComponent.class).inventoryItem = getItemsTable(inventorySlotsTable);
        player.getComponent(PlayerInventoryComponent.class).equippedItem = getItemsTable(equipmentSlotsTable);
    }

    void loadInventory(Engine engine) {
        clearItemsTable(equipmentSlotsTable);
        clearItemsTable(inventorySlotsTable);
        Entity player = engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
        if (player.getComponent(PlayerInventoryComponent.class).equippedItem != null) {
            Array<Item> equipment = player.getComponent(PlayerInventoryComponent.class).equippedItem;
            fillTable(equipmentSlotsTable, equipment, dragAndDrop);
        }
        if (player.getComponent(PlayerInventoryComponent.class).inventoryItem != null) {
            Array<Item> inventory = player.getComponent(PlayerInventoryComponent.class).inventoryItem;
            fillTable(inventorySlotsTable, inventory, dragAndDrop);
        }
    }

    private Item getActiveWeapon() {
        if (weaponInventorySlot.getItem() == null) {
            return null;
        }
        return weaponInventorySlot.getItem();
    }

    private void setActivePlayerWeapon() {
        engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first()
                .getComponent(PlayerComponent.class).activeWeapon = getActiveWeapon();
        if (!weaponInventorySlot.hasItem()) {
            engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first()
                    .getComponent(PlayerComponent.class).activeWeapon = null;

        }
    }

    private void removeActivePlayerWeapon() {
        if (weaponInventorySlot.hasItem()) {
            engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first()
                    .getComponent(PlayerComponent.class).activeWeapon = null;
        }
    }


    @Override
    public void onNotify(InventorySlot inventorySlot, InventorySlotEvent event) {
        if (event == InventorySlotEvent.ADDED_ITEM) {
            setActivePlayerWeapon();
        }
        if (event == InventorySlotEvent.REMOVED_ITEM) {
            removeActivePlayerWeapon();
        }
    }
}
