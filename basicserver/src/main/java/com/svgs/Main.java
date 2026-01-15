package com.svgs;

import java.util.ArrayList;

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
    public static void main(String[] args) {
        disableCORS();
    port(2121);
    
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
        shuffleUpNDealEm(playerList.size());
        //this will take the list of players and shuffle some cards and deal them. I haven't written the code for that just yet.
            
        return "";
    });
        

    }

    static public void shuffleUpNDealEm(int playerCount){
        // TODO: Write this
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