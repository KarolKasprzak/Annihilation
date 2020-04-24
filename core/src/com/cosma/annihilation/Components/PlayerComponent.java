package com.cosma.annihilation.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Items.Item;

import java.util.ArrayList;

public class PlayerComponent implements Component {

    public float velocity = 1.7f;
    public int numFootContacts = 0;
    public boolean hide = false;
    public String mapName;

    public boolean jump = false;

    public Array<Entity> collisionEntityArray = new Array<Entity>();
    public Item activeWeapon = Annihilation.getItem("fist");
    public Entity processedEntity;

    public boolean canPerformAction = true;
    public boolean isWeaponHidden = true;
    public boolean canShoot = true;

    public boolean canJump = true;
    public boolean onGround = false;
    public boolean climbing = false;
    public boolean canClimb = false;
    //------Control-----
    public boolean goLeft = false;
    public boolean goRight = false;
    public boolean goUp = false;
    public boolean goDown = false;

    public boolean canMoveOnSide = true;
    public boolean isPlayerControlEnable = true;
    public boolean canClimbDown = false;
    public boolean isPlayerCrouch = false;


}
