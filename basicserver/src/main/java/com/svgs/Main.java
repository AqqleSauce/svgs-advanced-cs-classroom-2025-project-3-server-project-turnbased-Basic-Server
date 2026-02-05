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
        ShuffleUpNDealEm(12);
        System.out.println(cardDeck);
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
            gameStarted = true;
            ShuffleUpNDealEm(playerList.size());
            //this will take the list of players and shuffle some cards and deal them. 
            StartObjectReturnObject starterObject = new StartObjectReturnObject(playerList.size() + 1);
            for (int i = 0; i < playerList.size(); i++) {
                starterObject.playerReturnList[i] = playerList.get(i);
            }
            // adding the dealer at the very end, might not work idk.
            starterObject.playerReturnList[starterObject.playerReturnList.length - 1] = new Player(1);
            starterObject.playerReturnList[starterObject.playerReturnList.length - 1].cards.add(cardDeck.remove(0));
            starterObject.playerReturnList[starterObject.playerReturnList.length - 1].cards.add(cardDeck.remove(0));
            //it returns an array of people and their cards.
            //the dealer is the last one in the list.
            return gson.toJson(starterObject);
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
            return gson.toJson(playerList.get(playerTurn - 1));
        });

        post("/hit", (req, res) -> {
            //ace card logic
            lost(playerTurn);
            return "";
        });
    }

    public static void lost(int playerTurn) {
        ArrayList<Card> hand = playerList.get(playerTurn).cards;
        int handValueSansAce = 0;
        for (Card x : hand) {
            if(!(x.value==0)){
                if(x.value>10)
                handValueSansAce += x.value;
            }
        }
    }

    public static void ShuffleUpNDealEm(int playerCount) {
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
