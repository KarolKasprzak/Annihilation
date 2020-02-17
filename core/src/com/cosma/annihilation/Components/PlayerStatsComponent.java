package com.cosma.annihilation.Components;

import com.badlogic.ashley.core.Component;

public class PlayerStatsComponent implements Component {
    public int strenght = 5;
    public int agility = 5;
    public int perception = 5;

    public int smallWeapons = 10;
    public int energeticWeapons = 10;
    public int meleeWeapons = 10;
    public int lockpicking = 10;
    public int technology = 10;
    public int medic = 10;
}
