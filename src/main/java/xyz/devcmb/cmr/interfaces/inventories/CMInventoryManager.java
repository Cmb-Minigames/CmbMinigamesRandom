package xyz.devcmb.cmr.interfaces.inventories;

import java.util.HashMap;
import java.util.Map;

public class CMInventoryManager {
    public static Map<String, HandledInventory> inventories = new HashMap<>();
    public static void registerInventory(String name, HandledInventory inventory){
        inventories.put(name, inventory);
    }

    public static void registerAllInventories(){

    }
}
