package com.cosma.annihilation.Items;



public class WeaponItem extends InventoryItem {


    private int damage;
    private boolean automatic;
    private int ammoInMagazine;
    private int maxAmmoInMagazine;
    private float reloadTime;
    private float accuracy;
    private ItemID ammoID;



    public WeaponItem(){}

    public WeaponItem(String textureName,ItemID itemID,int damage,int itemAttributes, int itemUseType, int itemValue,String name,boolean stackable, String itemShortDescription,ItemID ammoID){
        super(textureName,itemID,itemAttributes,itemUseType,itemValue,name,stackable,itemShortDescription);
//        this.damage = damage;
    }

    public WeaponItem(WeaponItem weaponItem){
        super(weaponItem);
        this.damage = weaponItem.getDamage();
        this.automatic = weaponItem.isAutomatic();
        this.ammoInMagazine = weaponItem.getAmmoInMagazine();
        this.maxAmmoInMagazine = weaponItem.getMaxAmmoInMagazine();
        this.reloadTime = weaponItem.getReloadTime();
        this.accuracy = weaponItem.getAccuracy();
        this.ammoID = weaponItem.getAmmoID();
    }

    public boolean isAutomatic() {
        return automatic;
    }

    public void setAutomatic(boolean automatic) {
        this.automatic = automatic;
    }

    public int getAmmoInMagazine() {
        return ammoInMagazine;
    }

    public void setAmmoInMagazine(int ammoInMagazine) {
        this.ammoInMagazine = ammoInMagazine;
    }

    public int getMaxAmmoInMagazine() {
        return maxAmmoInMagazine;
    }

    public void setMaxAmmoInMagazine(int maxAmmoInMagazine) {
        this.maxAmmoInMagazine = maxAmmoInMagazine;
    }

    public float getReloadTime() {
        return reloadTime;
    }

    public void setReloadTime(float reloadTime) {
        this.reloadTime = reloadTime;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public ItemID getAmmoID() {
        return ammoID;
    }

    public void setAmmoID(ItemID ammoID) {
        this.ammoID = ammoID;
    }

    public int getDamage() {
        return damage;
    }
}
