package com.cosma.annihilation.Items;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;

public class Tools {

    public static int findFreeIndex(Array<Item> itemList, int tableSize) {
        IntArray intArray = new IntArray();
        for (Item item : itemList) {
            intArray.add(item.getTableIndex());
        }
        intArray.sort();
        for (int i = 0; i < tableSize; i++) {
            if (!intArray.contains(i)) {
                return i;
            }
        }
        return 0;
    }
}
