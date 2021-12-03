package uk.ac.ed.inf;

import java.util.ArrayList;

/**
 * Used to parse the GSON file in
 */
public class Shop {
    private String location;
    private ArrayList<MenuItem> menu;

    public String getLocation(){
        return location;
    }

    public ArrayList<MenuItem> getMenu(){
        return menu;
    }

    public static class MenuItem {
        private String item;
        private int pence;

        public String getItem() {
            return item;
        }

        public int getPence() {
            return pence;
        }
    }
}
