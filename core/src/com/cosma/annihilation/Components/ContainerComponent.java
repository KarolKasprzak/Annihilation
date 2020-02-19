package com.cosma.annihilation.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.cosma.annihilation.Items.Item;


public class ContainerComponent implements Component {
  public String name;
  public Array<Item> itemList;
}
