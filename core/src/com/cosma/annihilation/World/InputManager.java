package com.cosma.annihilation.World;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.cosma.annihilation.Components.PlayerComponent;
import com.cosma.annihilation.EntityEngine.core.Engine;
import com.cosma.annihilation.EntityEngine.signals.Signal;
import com.cosma.annihilation.Systems.ActionSystem;
import com.cosma.annihilation.Systems.ShootingSystem;
import com.cosma.annihilation.Systems.UserInterfaceSystem;
import com.cosma.annihilation.Utils.Enums.GameEvent;

public class InputManager implements InputProcessor {
    private Signal<GameEvent> signal;
    private PlayerComponent playerComponent = null;

    public InputManager(Engine engine) {
        signal = new Signal<>();
        signal.add(engine.getSystem(ActionSystem.class));
//        signal.add(engine.getSystem(ShootingSystem.class));
        signal.add(engine.getSystem(UserInterfaceSystem.class));
    }

    public void update(Engine engine){
        playerComponent = engine.getPlayerEntity().getComponent(PlayerComponent.class);
    }


    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.SPACE && playerComponent.canJump && playerComponent.isPlayerControlEnable && playerComponent.isWeaponHidden) {
            playerComponent.jump = true;
        }

        if (keycode == Input.Keys.CONTROL_LEFT && playerComponent.onGround && playerComponent.isPlayerControlEnable && playerComponent.isWeaponHidden) {
            playerComponent.isPlayerCrouch = !playerComponent.isPlayerCrouch;
        }

        if (keycode == Input.Keys.R && playerComponent.isPlayerControlEnable && !playerComponent.isWeaponHidden && playerComponent.onGround ) {
            playerComponent.reloadWeapon = true;
            signal.dispatch(GameEvent.WEAPON_RELOAD);
        }
        if (keycode == Input.Keys.I || keycode == Input.Keys.ESCAPE) {
            playerComponent.isPlayerControlEnable = false;
            signal.dispatch(GameEvent.OPEN_MENU);
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        if (button == Input.Buttons.LEFT) {
            signal.dispatch(GameEvent.ACTION_BUTTON_TOUCH_DOWN);
            signal.dispatch(GameEvent.PERFORM_ACTION);
            playerComponent.isLeftMouseButtonPressed = true;
        }

        if (button == Input.Buttons.RIGHT) {
            playerComponent.prepareWeapon = true;
            signal.dispatch(GameEvent.WEAPON_TAKE_OUT);
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            signal.dispatch(GameEvent.ACTION_BUTTON_TOUCH_UP);
            playerComponent.isLeftMouseButtonPressed = false;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
