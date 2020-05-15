package com.cosma.annihilation.Utils;

public class CollisionID {
    public static final short LIGHT =0x0001;
    public static final short PLAYER = 0x0002;
    public static final short SCENERY = 0x0004;
    public static final short SCENERY_BACKGROUND_OBJECT = 0x0008;
    public static final short ENEMY = 16;
    public static final short NPC = 32;
    public static final short SCENERY_PHYSIC_OBJECT = 64;
    public static final short BULLET = 128;
    public static final short ACTION = 164;
    public static final short SCENERY_BACKGROUND_BULLET = 256;

    public static final short MASK_LIGHT = SCENERY;
    public static final short MASK_PLAYER = ENEMY | SCENERY | SCENERY_PHYSIC_OBJECT|ACTION;
    public static final short MASK_SCENERY = -1;
    public static final short MASK_SCENERY_PHYSIC_OBJECT = -1;
    public static final short MASK_SCENERY_BACKGROUND_OBJECT = SCENERY;
    public static final short MASK_ENEMY = SCENERY | PLAYER | BULLET;
    public static final short MASK_NPC = SCENERY | ENEMY;
    public static final short MASK_BULLET = ENEMY | NPC | SCENERY_PHYSIC_OBJECT | SCENERY | SCENERY_BACKGROUND_BULLET;
    public static final short MASK_SCENERY_BULLET = SCENERY_PHYSIC_OBJECT | SCENERY | BULLET;
    public static final short MASK_ACTION = PLAYER;
}
