package com.svgs;

import java.util.ArrayList;
import java.util.Collections;

import com.google.gson.Gson;

import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.options;
import static spark.Spark.post;

public class Main {

    static boolean gameStarted = false;
    static int playerTurn = 0;
    static ArrayList<Player> playerList = new ArrayList<>();
    static Gson gson = new Gson();
    static ArrayList<Card> cardDeck = new ArrayList<Card>();

    public static void main(String[] args) {
        disableCORS();

        post("/joinGame", (req, res) -> {
            String playern = req.body();
            Player p = new Player(playern);
            boolean playerExists = false;
            for (int i = 0; i < playerList.size(); i++) {
                if (playerList.get(i).name.equals(playern)) {
                    playerExists = true;
                }
            }
            if (!playerExists) {
                playerList.add(p);
            }
            JoinGamesReturnObject joingame = new JoinGamesReturnObject();
            joingame.gameState = gameStarted;
            joingame.players = playerList;
            return gson.toJson(joingame);
            //This post basically gets a player's name from the front end to make it into an object. It returns a list 
            //of these player objects and the gameStarted boolean. YOU (Mr. Tyler) said you would poll this, though
            //you don't have to if you dont want to.
        });

        post("/start", (req, res) -> {
            if(gameStarted==false){
            ShuffleUpNDealEm(playerList.size());
            //this will take the list of players and shuffle some cards and deal them. 
            Player diller = new Player(1);
            diller.cards.add(cardDeck.remove(0));
            diller.cards.add(cardDeck.remove(0));
            playerList.add(diller);
            //it returns an array of people and their cards.
            //the dealer is the last one in the list.
            gameStarted = true;
            }
            return gson.toJson(playerList);
        });

        get("/whoseTurn", (req, res) -> {
            TurnObject rturn = new TurnObject();
            rturn.playerList = playerList;
            rturn.playerTurn = playerTurn;
            return gson.toJson(rturn);
        });
        
        //the variable "playerTurn" tells who's turn it is according to the array list of players. 
        // at the end of either of these moves, it adds 1 to playerTurn.
        post("/stand", (req, res) -> {
            playerTurn++;
            if(playerTurn==playerList.size()-1){
                doDealerThing();
            }
            return gson.toJson(playerList.get(playerTurn - 1));
        });

        post("/hit", (req, res) -> {
            //ace card logic
            playerList.get(playerTurn).cards.add(cardDeck.remove(0));
            if(lost(playerTurn)){
                playerList.get(playerTurn).haveLost = true;
                playerTurn++;
            }
            if(playerTurn==playerList.size()-1){
                doDealerThing();
                return gson.toJson(playerList);
            }
            return(gson.toJson(playerList.get(playerTurn)));
        });
    }

    public static void doDealerThing(){
        ArrayList<Card> Dhand = playerList.get(playerTurn).cards;
        int dealerCards = 0;
        for (Card x : Dhand) {
                if(x.value<10 && !(x.value==0)){
                dealerCards += (x.value+1);
                }
                else if (x.value>=10){
                    dealerCards+= 10;
                }
                else if (x.value == 0){
                    if(dealerCards<=10){
                        dealerCards+=11;
                    }
                    else if(dealerCards>10){
                        dealerCards+=1;
                    }
                }
            }
            if(dealerCards<=17){
                playerList.get(playerList.size()-1).cards.add(cardDeck.remove(0));
                doDealerThing();
                        
                    }
        for(int i = 0; i<(playerList.size()-2); i++){
            if(lost(i)){
                playerList.get(i).haveLost=true;
            }
        }

        }

    //method to determine if the player has lost. If they have, it returns true.
    public static boolean lost(int playerTurn) {
        ArrayList<Card> hand = playerList.get(playerTurn).cards;
        int handValueWAce = 0;
        for (Card x : hand) {
                if(x.value<10 && !(x.value==0)){
                handValueWAce += (x.value+1);
                }
                else if (x.value>=10){
                    handValueWAce += 10;
                }
                else if (x.value == 0){
                    if(handValueWAce<=10){
                        handValueWAce+=11;
                    }
                    else if(handValueWAce>10){
                        handValueWAce+=1;
                    }
                }
                if(handValueWAce>21){
                return true;
                }
        }
        return false;
    }

    public static void ShuffleUpNDealEm(int playerCount) {
        cardDeck.clear();
        //add cards, 52
        //suits are 0-3, 0=spades, 1=clubs, 2=hearts, 3=diamonds
        //from 0-12, in ascending order, ace, 1-9, jack, king, queen. face cards all count as 10 in blackjack, however, and ace counts as 11 or 1.
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 13; j++) {
                cardDeck.add(new Card(j, i));
            }
        }
        Collections.shuffle(cardDeck);
        for (int l = 0; l < playerList.size(); l++) {
            playerList.get(l).cards.add(cardDeck.remove(0));
            playerList.get(l).cards.add(cardDeck.remove(0));
            //do it twice to deal two cards 
            System.out.println(playerList.get(l).cards);
            //just a little test to see if its even doing things.
        }

    }

    public static void disableCORS() {
        before((req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            res.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        });

        options("/*", (req, res) -> {
            String accessControlRequestHeaders = req.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                res.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = req.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                res.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        });
    }
}
