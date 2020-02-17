package com.cosma.annihilation.Editor.CosmaMap.CosmaEditorLights;

import box2dLight.Light;
import com.badlogic.gdx.utils.Timer;

public class LightControler {

    public LightControler(final Light light){
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        light.setActive(true);

                    }
                }, 0.1f);
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        light.setActive(false);

                    }
                }, 0.1f);

            }
        }, 0.2f,0.2f);


    }
}
