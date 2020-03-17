package Model.FactionSubclasses;

import Model.Faction;

public class VladTheImpaler extends Faction { // chaos magician
    public VladTheImpaler() {
        INITIAL_WORKER = 4;
        INITIAL_GOLD = 15;
        INITIAL_PRIEST = 0;
        INITIAL_POWER = 0;
        INITIAL_BUDISM = 2; //fire
        INITIAL_ISLAM = 0;  //water
        INITIAL_JUDAISM = 0;  //earth     
        INITIAL_CHRISTIANITY = 0; //air
        INITIAL_VICTORY_POINT = 19;
        INITIAL_DWELLING_NUMBER = 1;
        DWELLING_GOLD_COST = 2;
        DWELLING_WORKER_COST = 1;
        DWELLING_WORKER_INCOME = 1;

    }
}