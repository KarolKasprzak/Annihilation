package com.cosma.annihilation.Utils;

import com.badlogic.gdx.utils.Array;

public class CollisionID {
    public static final short NULL = 0x000;
    public static final short LIGHT =0x0001;
    public static final short PLAYER = 0x0002;
    public static final short SCENERY = 0x0004;
    public static final short SCENERY_BACKGROUND_OBJECT = 0x0008;
    public static final short ENEMY = 16;
    public static final short NPC = 32;
    public static final short SCENERY_PHYSIC_OBJECT = 64;
    public static final short BULLET = 128;
    public static final short ACTION = 164;

    public static final short MASK_LIGHT = SCENERY;
    public static final short MASK_PLAYER = ENEMY | SCENERY | SCENERY_PHYSIC_OBJECT|ACTION;
    public static final short MASK_SCENERY = -1;
    public static final short MASK_SCENERY_PHYSIC_OBJECT = -1;
    public static final short MASK_SCENERY_BACKGROUND_OBJECT = SCENERY;
    public static final short MASK_ENEMY = SCENERY | PLAYER;
    public static final short MASK_NPC = SCENERY | ENEMY;
    public static final short MASK_BULLET = ENEMY | NPC | SCENERY_PHYSIC_OBJECT | SCENERY;
    public static final short MASK_ACTION = PLAYER;
    private Array<ContactFilterValue> categoryArray,maskArray;

    public CollisionID (){
        categoryArray = new Array<>();
        maskArray = new Array<>();

        categoryArray.add(new ContactFilterValue("select",NULL));
        categoryArray.add(new ContactFilterValue("light",LIGHT));
        categoryArray.add(new ContactFilterValue("player",PLAYER));
        categoryArray.add(new ContactFilterValue("scenery",SCENERY));
        categoryArray.add(new ContactFilterValue("scenery_bg",SCENERY_BACKGROUND_OBJECT));
        categoryArray.add(new ContactFilterValue("enemy",ENEMY));
        categoryArray.add(new ContactFilterValue("npc",NPC));
        categoryArray.add(new ContactFilterValue("scenery_phy",SCENERY_PHYSIC_OBJECT));

        maskArray.add(new ContactFilterValue("select",NULL));
        maskArray.add(new ContactFilterValue("light",MASK_LIGHT));
        maskArray.add(new ContactFilterValue("player",MASK_PLAYER));
        maskArray.add(new ContactFilterValue("scenery",MASK_SCENERY));
        maskArray.add(new ContactFilterValue("scenery_bg",MASK_SCENERY_BACKGROUND_OBJECT));
        maskArray.add(new ContactFilterValue("enemy",MASK_ENEMY));
        maskArray.add(new ContactFilterValue("npc",MASK_NPC));
        maskArray.add(new ContactFilterValue("scenery_phy",MASK_SCENERY_PHYSIC_OBJECT));
    }


    public Array<ContactFilterValue> getCategoryArray() {
        return categoryArray;
    }

    public Array<ContactFilterValue> getMaskArray() {
        return maskArray;
    }


    public class ContactFilterValue {
        String name;
        short value;

        @Override
        public String toString() {
            return name + " " + value;
        }

        ContactFilterValue(String name, short value) {
            this.name = name;
            this.value = value;
        }

        public short getValue() {
            return value;
        }
    }


}
