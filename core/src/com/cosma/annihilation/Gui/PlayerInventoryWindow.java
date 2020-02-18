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
import com.cosma.annihilation.Gui.Inventory.*;
import com.cosma.annihilation.Items.InventoryItem;
import com.cosma.annihilation.Items.ItemFactory;
import com.cosma.annihilation.Items.WeaponItem;
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
        rightTable = new Table(skin);

        TextureAtlas atlas = Annihilation.getAssets().get("gfx/atlas/gui_human.atlas",TextureAtlas.class);
        Animation<TextureAtlas.AtlasRegion> animation = new Animation<>(0.1f,atlas.getRegions(), Animation.PlayMode.LOOP);
        animatedActor = new AnimatedActor(animation);

        this.background(new TextureRegionDrawable(new TextureRegion(Annihilation.getAssets().get("gfx/interface/clear.png",Texture.class))));

        listener = new ActorGestureListener() {
            @Override
            public void touchDown(InputEvent event, float x, float y, int pointer, int button) {

                InventorySlot inventorySlot = (InventorySlot) event.getListenerActor();
                if (inventorySlot.hasItem()) {
                    if (inventorySlot.getInventoryItem() instanceof WeaponItem) {
                        showItemStats((WeaponItem) inventorySlot.getInventoryItem());
                    }
                }
                super.touchDown(event, x, y, pointer, button);
            }
        };
        this.add(leftTable);
        this.add(rightTable).expandX();

        createStatsTable();
        createMedicalTable();
        createEquipmentTable();
        createInventoryTable();

        leftTable.add(inventorySlotsTable.bottom()).colspan(2).bottom().expandX().padLeft(Util.setWindowHeight(0.06f));
        rightTable.add(medicalTable);
        rightTable.row();
        rightTable.add(equipmentSlotsTable).expandY().expandX();
    }

    private void createMedicalTable(){
        medicalTable = new Table();
        medicalTable.add(animatedActor).size(250,250).expandY();

    }

    private void addItemLabels(){
        weaponName = new Label("", skin);
        weaponName.setColor(0, 82, 0, 255);
        weaponName.setFontScale(1.1f);
        rightTable.add(weaponName).left().expandX().top().pad(4).top();
        rightTable.row();

        weaponDescription = new Label("", skin);
        weaponDescription.setColor(0, 82, 0, 255);
        weaponDescription.setWrap(true);
        rightTable.add(weaponDescription).left().pad(4).expandX().fillX();
        rightTable.row();

        weaponDamage = new Label("", skin);
        weaponDamage.setColor(0, 82, 0, 255);
        weaponDamage.setFontScale(0.9f);
        rightTable.add(weaponDamage).left().expandX().top().pad(4).padTop(8);
        rightTable.row();

        weaponAccuracy = new Label("", skin);
        weaponAccuracy.setColor(0, 79, 0, 255);
        weaponAccuracy.setFontScale(0.9f);
        rightTable.add(weaponAccuracy).left().expandX().top().pad(4);
        rightTable.row();

        weaponAmmo = new Label("", skin);
        weaponAmmo.setColor(0, 82, 0, 255);
        weaponAmmo.setFontScale(0.9f);
        rightTable.add(weaponAmmo).left().expandX().top().pad(4);
        rightTable.row();

        weaponAmmoInMagazine = new Label("", skin);
        weaponAmmoInMagazine.setColor(0, 82, 0, 255);
        weaponAmmoInMagazine.setFontScale(0.9f);
        rightTable.add(weaponAmmoInMagazine).left().expandX().top().pad(4);
        rightTable.row();
    }

    private void showItemStats(WeaponItem item){
        weaponName.setText(item.getItemName());
        weaponDescription.setText(item.getItemShortDescription());
        weaponDamage.setText("Weapon damage: " + item.getDamage());
        weaponAccuracy.setText("Weapon accuracy: " + item.getAccuracy());
        weaponAmmo.setText("Ammunition: " + ItemFactory.getInstance().getItem(item.getAmmoID()).getItemName());
        weaponAmmoInMagazine.setText("Magazine capacity: " + item.getMaxAmmoInMagazine());
    }


    private void createStatsTable() {
        statsTable = new Table();
        statsTable.center();
        dmgLabel = new Label("dmg:", skin);
        dmgLabel.setColor(0, 82, 0, 255);
        defLabel = new Label("def:", skin);
        defLabel.setColor(0, 82, 0, 255);
        statsTable.add(dmgLabel).expand().bottom().fillX().padRight(50);
        statsTable.add(defLabel).right();
    }

    private void createEquipmentTable() {
        equipmentSlotsTable = new Table();

        armourInventorySlot = new InventorySlot(InventoryItem.ItemType.ARMOUR.getValue(), new Image(Annihilation.getAssets().get("gfx/interface/gui_armour_slot.png",Texture.class)));

        weaponInventorySlot = new InventorySlot(InventoryItem.ItemType.WEAPON_DISTANCE.getValue() | InventoryItem.ItemType.WEAPON_DISTANCE_ENERGETIC.getValue()
                | InventoryItem.ItemType.WEAPON_MELEE.getValue() | InventoryItem.ItemType.WEAPON_DISTANCE_LONG.getValue() | InventoryItem.ItemType.WEAPON_DISTANCE_ENERGETIC_LONG.getValue(),
                new Image(Annihilation.getAssets().get("gfx/interface/gui_weapon_slot.png",Texture.class)));
        weaponInventorySlot.register(this);

        dragAndDrop.addTarget(new InventorySlotTarget(armourInventorySlot));
        dragAndDrop.addTarget(new InventorySlotTarget(weaponInventorySlot));

        equipmentSlotsTable.add(weaponInventorySlot).size(slotSize*1.33f, slotSize).pad(Util.setWindowHeight(0.006f)).colspan(2);

        equipmentSlotsTable.add(armourInventorySlot).size(slotSize*1.33f, slotSize).pad(Util.setWindowHeight(0.006f));

        equipmentSlotsTable.center();
        equipmentSlotsTable.pad(20);
    }

    public void setInventorySize(int size) {
        inventorySize = size;
    }


    private void createInventoryTable() {
        inventorySlotsTable = new Table();
        inventorySlotsTable.center();
        inventorySlotsTable.setDebug(false);
        inventorySlotsTable.setFillParent(true);

        for (int i = 1; i <= 21; i++) {
            InventorySlot inventorySlot = new InventorySlot();
//            inventorySlot.addListener(listener);
            dragAndDrop.addTarget(new InventorySlotTarget(inventorySlot));
            inventorySlotsTable.add(inventorySlot).size(slotSize, slotSize).pad(Util.setWindowHeight(0.005f));
            if (i == 3||i == 6||i == 9||i == 12|| i == 15||i == 18) inventorySlotsTable.row();
        }
    }

    private static void clearItemsTable(Table targetTable) {
        Array<Cell> cells = targetTable.getCells();
        for (int i = 0; i < cells.size; i++) {
            InventorySlot inventorySlot = (InventorySlot) cells.get(i).getActor();
            if (inventorySlot == null) continue;
            inventorySlot.clearAllItems();
        }
    }

    private static Array<InventoryItemLocation> getItemsTable(Table targetTable) {
        Array<Cell> cells = targetTable.getCells();
        Array<InventoryItemLocation> items = new Array<>();
        for (int i = 0; i < cells.size; i++) {
            InventorySlot inventorySlot = ((InventorySlot) cells.get(i).getActor());
            if (inventorySlot == null) continue;

            int itemNumber = inventorySlot.getItemsNumber();
            if (itemNumber > 0) {

                items.add(new InventoryItemLocation(i, inventorySlot.getInventoryItem().getItemID().toString(), itemNumber));
            }
        }
        return items;
    }

    public static void fillInventory(Table targetTable, Array<InventoryItemLocation> inventoryItems, DragAndDrop dragAndDrop) {
        clearItemsTable(targetTable);
        Array<Cell> cells = targetTable.getCells();
        for (int i = 0; i < inventoryItems.size; i++) {
            InventoryItemLocation itemLocation = inventoryItems.get(i);
            InventoryItem.ItemID itemID = InventoryItem.ItemID.valueOf(itemLocation.getItemID());
            InventorySlot inventorySlot = ((InventorySlot) cells.get(itemLocation.getTableIndex()).getActor());
            for (int index = 0; index < itemLocation.getItemsAmount(); index++) {
                InventoryItem item = ItemFactory.getInstance().getItem(itemID);
                inventorySlot.add(item);
                dragAndDrop.addSource(new InventorySlotSource(inventorySlot, dragAndDrop));
            }
        }
    }

    private void setDmgLabel() {
        if (getActiveWeapon() != null) {
            dmgLabel.setText("dmg " + getActiveWeapon().getDamage());
        } else
            dmgLabel.setText("no weapon");
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
            Array<InventoryItemLocation> equipment = player.getComponent(PlayerInventoryComponent.class).equippedItem;
            fillInventory(equipmentSlotsTable, equipment, dragAndDrop);
        }
        if (player.getComponent(PlayerInventoryComponent.class).inventoryItem != null) {
            Array<InventoryItemLocation> inventory = player.getComponent(PlayerInventoryComponent.class).inventoryItem;
            fillInventory(inventorySlotsTable, inventory, dragAndDrop);
        }
        setDmgLabel();
    }

    private WeaponItem getActiveWeapon() {
        if (weaponInventorySlot.getInventoryItem() == null) {
            return null;
        }
        return (WeaponItem) weaponInventorySlot.getInventoryItem();
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
            setDmgLabel();
            setActivePlayerWeapon();
        }
        if (event == InventorySlotEvent.REMOVED_ITEM) {
            dmgLabel.setText("no weapon");
            removeActivePlayerWeapon();
        }
    }
}
