package com.cosma.annihilation.Components;


import com.cosma.annihilation.EntityEngine.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.cosma.annihilation.Ai.Tasks.Task;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.EntityEngine.core.Entity;
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
    //------Variables-----
    public Vector2 climbingTargetPosition;
    public Vector2 climbingStartPosition;
    public Task activeTask;
    //------Control-----
    public boolean goLeft = false;
    public boolean goRight = false;
    public boolean goUp = false;
    public boolean goDown = false;

    public boolean canMoveOnSide = true;
    public boolean isPlayerControlEnable = true;
    public boolean canSwitchSide = true;
    public boolean canClimbDown = false;
    public boolean isPlayerCrouch = false;
    public boolean canUseWeapon = true;
}
