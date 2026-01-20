package com.svgs;

import java.util.ArrayList;
import java.util.Collections;

import com.google.gson.Gson;

import static spark.Spark.before;
import static spark.Spark.options;
import static spark.Spark.port;
import static spark.Spark.post;

public class Main {
    static boolean gameStarted = false;
    static int playerTurn = 0;
    static ArrayList<Player> playerList;
    static Gson gson = new Gson();
    static ArrayList<Card> cardDeck = new ArrayList<Card>();
    public static void main(String[] args) {
        ShuffleUpNDealEm(3);
        port(2121);
        disableCORS();
    
    post("/joinGame",(req,res)->{
        String playern = req.body();
        Player p = new Player(playern);
        playerList.add(p);
        JoinGamesReturnObject joingame = new JoinGamesReturnObject();
        joingame.gameState = gameStarted;
        joingame.players = playerList;
        return gson.toJson(joingame);
        //This post basically gets a player's name from the front end to make it into an object. It returns a list 
        //of these player objects and the gameStarted boolean. YOU (Mr. Tyler) said you would poll this, though
        //you don't have to if you dont want to.
    });

    post("/start",(req,res)->{
        gameStarted=true;
        ShuffleUpNDealEm(playerList.size());
        //this will take the list of players and shuffle some cards and deal them. I haven't written the code for that just yet.
            
        return "";
    });
        

    }

    static public void ShuffleUpNDealEm(int playerCount){
        //add cards, 52
        //suits are 0-3, 0=spades, 1=clubs, 2=hearts, 3=diamonds
        //from 1-13, in ascending order, ace, 2-10, jack, king, queen. face cards all count as 10 in blackjack, however.
        for(int i = 0; i<4; i++){
            for(int j =0; j<13; j++){
                cardDeck.add(new Card(i,j));
            }
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