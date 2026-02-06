package com.svgs;

public class Card {
    
int value;
int suit;

public Card(int val, int suits){
    value=val;
    // values are just denoting what card they are, 0 is ace and can be worth 1 or 11 points, and 10-12 are face cards and are worth ten each.
    suit=suits;
    //suits are 0-3
    // 0 = spades
    // 1 = clubs
    // 2 = hearts
    // 3 = diamonds
    }

    public String toString(){
        return value + " of " + suit;
    }
   

}
