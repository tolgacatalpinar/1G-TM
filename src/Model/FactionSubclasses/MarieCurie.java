package Model.FactionSubclasses;

import Model.Faction;

public class MarieCurie extends Faction { //alchemist
  
  public MarieCurie()
  {
        INITIAL_WORKER = 3;
        INITIAL_GOLD = 15;
        INITIAL_PRIEST = 0;
        INITIAL_POWER = 0;
        INITIAL_BUDISM = 1; // fire
        INITIAL_ISLAM = 1;  //water
        INITIAL_JUDAISM = 0;  //earth     
        INITIAL_CHRISTIANITY = 0; //air
        INITIAL_VICTORY_POINT = 27;
        INITIAL_DWELLING_NUMBER = 2;
        DWELLING_GOLD_COST = 2;
        DWELLING_WORKER_COST = 1;
        DWELLING_WORKER_INCOME = 1;    
  }
}