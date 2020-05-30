package com.cosma.annihilation.Ai;

import com.cosma.annihilation.EntityEngine.core.Entity;

public class HumanAiBasic extends AiCore{

    @Override
    public void update(Entity entity, float deltaTime) {
        if (isEnemyInSight(entity)) {
            if (isEnemyInWeaponRange(entity, 8)) {
                shoot(entity);
            } else {
                followEnemy(entity);
            }
        } else {
            if (isHearEnemy(entity)) {
                searchEnemy(entity);

            }else{
                patrol(entity);
            }
        }
    }

    public HumanAiBasic() {

    }

}
