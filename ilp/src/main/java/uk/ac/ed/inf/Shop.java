package uk.ac.ed.inf;

import java.util.ArrayList;

/**
 * Used to parse the GSON file in
 */
public class Shop {
    public String name;
    public String location;
    public ArrayList<MenuItem> menu;

    public static class MenuItem {
        public String item;
        public int pence;
    }
}
