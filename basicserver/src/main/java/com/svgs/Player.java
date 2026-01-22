package com.svgs;

import java.util.ArrayList;

public class Player {
    String name;
    ArrayList<Card> cards;
    
    public Player(String nam){
        name = nam;
        cards = new ArrayList<>();
    }

    public Player(int dil){
    // this one is only for the dealer
        name = "Dealer";
        cards = new ArrayList<>();
    }
}
