package com.cosma.annihilation.Components;

import com.cosma.annihilation.EntityEngine.core.Component;

public class PlayerStatsComponent implements Component {
    public int strength = 5;
    public int agility = 5;
    public int perception = 5;

    public int ballisticWeapons = 10;
    public int energeticWeapons = 10;
    public int meleeWeapons = 10;
    public int lockpicking = 10;
    public int technology = 10;
    public int medic = 10;
}
