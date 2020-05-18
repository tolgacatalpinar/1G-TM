package Controller;

import Model.*;
import Model.CardsAndTiles.CardsAndTiles;
import Model.FactionSubclasses.*;
import View.*;
import View.ActionsViews.ExchangeResourcesView;
import View.ActionsViews.PowerActionView;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.io.Serializable;
public class GameController implements Initializable, Serializable {

   final int ROW_NUMBER = 9;

   final int COLUMN_NUMBER = 13;

   private int selection = -1;
   public FileManager fm = new FileManager();
   public File save = fm.createSave("15");

   @FXML
   Pane mapPane;
   @FXML
   BorderPane borderPane;
   @FXML
   Button favorTilesButton;
   @FXML
   Button religions;
   @FXML
   Button bonusCardsButton;
   @FXML
   Button townTilesButton;
   @FXML
   Button scoringTilesButton;
   @FXML
   Button exchangeResourcesButton;
   @FXML
   Button scoreTable;
   @FXML
   Button passRound;
   @FXML
   Button skipTurn;
   @FXML
   Label testText;
   @FXML
   Button specialActions;
   @FXML
   Button terraform;
   @FXML
   Button upgradeShipping;
   @FXML
   Button upgradeStruct;
   @FXML
   Button sendPriest;
   @FXML
   Button powerActions;
   @FXML
   Button upgradeSpade;


   ArrayList<PlayerView> playerViewList;
   Button[][] terrains;
   Button[] actions;
   public Map map;
   Religion[] religionArr;
   Player[] playerList;
   CardsAndTiles cardsAndTiles;
   CardsAndTilesController cardsAndTilesController;
   RoundController roundController;
   PlayerHandler playerHandler;
   Player currentPlayer;


   public GameController() throws IOException {

   }


   @Override
   public void initialize(URL url, ResourceBundle resourceBundle) {
      //SoundController.playGameMusic(30);

      actions = new Button[]{specialActions, terraform, upgradeShipping, upgradeStruct, sendPriest, powerActions, upgradeSpade, passRound};
      System.out.println("Initialize is called");
      int index = 0;
      terrains = new Button[ROW_NUMBER][COLUMN_NUMBER];
      for (int i = 0; i < ROW_NUMBER * COLUMN_NUMBER; i++) {
         int row = i / 13;
         int col = i % 13;
         if (row % 2 == 0)
            terrains[row][col] = (Button) mapPane.getChildren().get(index);
         else if (col == 12) {
            terrains[row][col] = null;
            index--;
         } else
            terrains[row][col] = (Button) mapPane.getChildren().get(index);
         index++;
      }

      createSpaces();
      //testText.setText(playerList[0].getNickName());
      Thread thread = new Thread(new Runnable() {

         @Override
         public void run() {
            Runnable updater = new Runnable() {

               @Override
               public void run() {

                  if (playerList != null) {
                     HBox factionsView = new HBox(5);

                     playerViewList = new ArrayList<>();
                     for (int i = 0; i < playerList.length; i++) {
                        if (playerList[i] != null) {
                           playerViewList.add(new PlayerView(playerList[i]));

                        }
                     }
                     factionsView.getChildren().addAll(playerViewList);
                     //displayPlayerTurn(playerViewList);
                     factionsView.setPadding(new Insets(0, 0, 0, 20));
                     //factionsView.setStyle("fx-margin-bottom: 150;");
//                     VBox.setMargin(factionsView, new Insets(0,0,1500,0));
                     //factionsView.setMargin(,);

                     factionsView.setMaxWidth(200);
                     borderPane.setBottom(factionsView);
                     borderPane.setPadding(new Insets(0,0,50,0));
                     displayPlayerTurn(playerViewList);
                     showTown(terrains, map);

//                     if(playerList[roundController.currentPlayerId].isRoundPassed()){
//                        disableActions();
//                     }
//                     ImageView imview = new ImageView();
//                     imview.setImage(new Image("file:src/Images/FactionImages/Image_AleisterCrowley.jpeg"));
//                     imview.setFitHeight(150);
//                     imview.setFitWidth(50);
//                     borderPane.setBottom(imview);

                  }

               }
            };

            while (true) {
               try {
                  Thread.sleep(700);
               } catch (InterruptedException ex) {
               }

               // UI update is run on the Application thread
               Platform.runLater(updater);
            }
         }

      });
      // don't let thread prevent JVM shutdown
      thread.setDaemon(true);
      thread.start();
      setButtonClickForInitialDwellings();

   }

   @FXML
   public void saveGameClicked() throws IOException {
      fm.saveGame(this, roundController,save);
      System.out.println("saved");
   }

   @FXML
   public void loadGameClicked() throws IOException
   {
         this.map = fm.loadGame(save, this).map;
         System.out.println(map.spaces[3][2].getType());
         this.religionArr = fm.loadGame(save, this).religionArr;
         this.playerList = fm.loadGame(save, this).playerList;
         System.out.println(playerList[0].getGoldNum());
         this.cardsAndTiles = fm.loadGame(save, this).cardsAndTiles;
         this.cardsAndTilesController = fm.loadGame(save, this).cardsAndTilesController;
         this.playerHandler = fm.loadGame(save, this).playerHandler;
         this.currentPlayer = fm.loadGame(save, this).currentPlayer;
         this.roundController = fm.loadGame(save, this).roundController;
    //     this.loadInitialMap();
   }
   @FXML
   public void skipTurnClicked() {
      disableButtonClicks();
      enableTerrains();
      enableActions();
      if (roundController.currentRound == 0) {
         System.out.println("Current player was: " + playerList[roundController.getCurrentPlayerId()].getNickName());
         System.out.println(roundController.getCurrentPlayerId());
         roundController.endTurn(playerList);
         currentPlayer = playerList[roundController.getCurrentPlayerId()];
         if (currentPlayer.getBuildingNumber() < currentPlayer.getFaction().startingDwellingNum) {
            loadInitialMap();
            setButtonClickForInitialDwellings();
         }else{
            disableActions();
            passRound.setDisable(false);
            skipTurn.setDisable(true);

         }
         System.out.println("Current player is now: " + playerList[roundController.getCurrentPlayerId()].getNickName());
         System.out.println("--------------------------------------------------");
      } else {
         roundController.endTurn(playerList);
      }
   }

   @FXML
   public void passRoundClicked() {

      if(roundController.getCurrentRound() + 1 != roundController.getMAX_ROUND()) {
         cardsAndTilesController.showBonusCardsTable(cardsAndTiles, playerList[roundController.getCurrentPlayerId()], true);
      }
         int round1 = roundController.getCurrentRound();
         roundController.passRound(playerList);
      if (roundController.getCurrentRound() > 0 ){
            enableActions();
      }
         int round2 = roundController.getCurrentRound();
         cardsAndTiles.returnScoringTile(round1, round2, playerList, religionArr);
         //Reset advancement on religion for this round
         if (round2 != round1) {
            for (Religion religion : religionArr) {
               religion.resetRoundBasedPosition();
            }
         }

      System.out.println("Victory Point = " + engineerStrongholdAbility());
      if(roundController.isOver){
         this.scoreTableClicked();
         System.out.println("girdii");
      }
   }

   @FXML
   public void bonusCardsClicked() {
      cardsAndTilesController.showBonusCardsTable(cardsAndTiles, currentPlayer,false);
   }

   @FXML
   public void upgradeShippingClicked() {
      ActionController.showUpdateShippingDialogs(playerList[roundController.getCurrentPlayerId()], actions);
   }

   @FXML
   public void sendPriestClicked() {
      ReligionController religionController = new ReligionController();
      religionController.showChoices(playerList, religionArr, roundController.getCurrentPlayerId());

   }

   @FXML
   public void powerActionClicked() {
      showPowerActions(playerList[roundController.currentPlayerId]);


   }

   @FXML
   public void upgradeSpadeClicked() {
      ActionController.showUpdateSpadeDialogs(playerList[roundController.getCurrentPlayerId()], actions);
   }

   @FXML
   public void terraformClicked() {
      ActionController.terraform(playerList[roundController.currentPlayerId], terrains, map, actions, cardsAndTiles,religionArr);

   }

   @FXML
   public void upgradeStructureClicked() {

      ActionController.upgradeStructure(playerList[roundController.getCurrentPlayerId()] , terrains, map, actions, cardsAndTiles, religionArr);

   }
   @FXML
   public void religionsClicked() {
      ReligionController religionController = new ReligionController();
      religionController.showReligion(playerList, 0, religionArr, playerList.length, false);
   }

   @FXML
   public void scoreTableClicked() {
      ReligionController religionController = new ReligionController();
      //FOR RELIGION
      ArrayList<ArrayList<Integer>>[] scoresReligion = religionController.calculateReligionScores(religionArr, playerList);
      //FOR LONGEST PATH
      ArrayList<Integer>[] scoresLongestPath = map.calculatePathScores(playerList);
      System.out.println();
      showScoreTable(scoresReligion,scoresLongestPath);
      //Calculate victory points for the winner in last round

   }


   @FXML
   public void townTilesClicked() throws IOException {
      //todo
      cardsAndTilesController.showTownTilesTable(cardsAndTiles,playerList[roundController.currentPlayerId],religionArr,true);
   }

   @FXML
   public void scoringTilesClicked() {

      cardsAndTilesController.showScoringTilesTable(cardsAndTiles);
   }

   @FXML
   public void exchangeResourcesClicked() {
      showExchangeResources();
   }



   @FXML
   public void favorTilesClicked() {

      cardsAndTilesController.showFavorTilesTable(cardsAndTiles, playerList[roundController.getCurrentPlayerId()], religionArr,false);

   }


   @FXML
   public void specialActionClicked() {
      ActionController.showSpeacialActions( playerList, religionArr, roundController.getCurrentPlayerId(),map, mapPane, terrains,actions);
   }

   public void createSpaces() {
      System.out.println("Create is called");
      Space[][] spaces = new Space[ROW_NUMBER][COLUMN_NUMBER];
      for (int i = 0; i < ROW_NUMBER; i++) {
         for (int j = 0; j < COLUMN_NUMBER; j++) {
            Space space = new Space();
            if (terrains[i][j] == null)
               space.setType("Empty");
            else {
               switch (terrains[i][j].getId()) {
                  case "blackHexagon":
                     space.setType("Swamp");
                     break;
                  case "blueHexagon":
                     space.setType("Lakes");
                     break;
                  case "brownHexagon":
                     space.setType("Plains");
                     break;
                  case "greenHexagon":
                     space.setType("Forest");
                     break;
                  case "yellowHexagon":
                     space.setType("Desert");
                     break;
                  case "redHexagon":
                     space.setType("Wasteland");
                     break;
                  case "riverHexagon":
                     space.setType("River");
                     break;
                  case "whiteHexagon":
                     space.setType("Mountains");
                     break;
                  default:
                     space.setType("River");
                     break;
               }
            }
            spaces[i][j] = space;
            space.setX(i);
            space.setY(j);
         }
      }
      for (int i = 0; i < ROW_NUMBER; i++) {
         for (int j = 0; j < COLUMN_NUMBER; j++) {

            if (i == 0) {
               if (j == 2 || j == 6 || j == 10)
                  spaces[i][j].setBridgability(true);
            } else if (i == 1) {
               spaces[i][j].setBridgability(true);
            } else if (i == 2) {
               if (j == 2 || j == 4 || j == 6 || j == 8 || j == 10)
                  spaces[i][j].setBridgability(true);
            } else if (i == 3) {
               if (j == 0 || j == 2 || j == 5 || j == 6 || j == 8 || j == 10 || j == 11)
                  spaces[i][j].setBridgability(true);
            } else if (i == 4) {
               if (j == 4 || j == 7 || j == 10)
                  spaces[i][j].setBridgability(true);
            } else if (i == 5) {
               if (j == 0 || j == 1 || j == 4 || j == 5)
                  spaces[i][j].setBridgability(true);
            } else if (i == 6) {
               if (j == 3 || j == 5 || j == 7 || j == 9)
                  spaces[i][j].setBridgability(true);
            } else if (i == 7) {
               if (j == 0 || j == 1 || j == 6 || j == 7 || j == 9)
                  spaces[i][j].setBridgability(true);
            } else if (i == 8) {
               if (j == 5 || j == 8)
                  spaces[i][j].setBridgability(true);
            }

         }
      }

      map = new Map(spaces);
   }

   public void loadPlayers(ArrayList<Faction> factionList, ArrayList<String> playerNames, int totalPlayerNumber) {

      Player[] playerList = new Player[totalPlayerNumber];
      for (int i = 0; i < totalPlayerNumber; i++) {
         playerList[i] = new Player(factionList.get(i), playerNames.get(i), i);
      }
      this.playerList = playerList;
      playerHandler = new PlayerHandler();
      roundController = new RoundController(playerList);


   }

   public void loadCardsAndTiles() {
      cardsAndTiles = new CardsAndTiles(playerList.length, playerList);
      cardsAndTilesController = new CardsAndTilesController();
      cardsAndTiles.returnScoringTile(0, 1, playerList,religionArr);
   }

   public void loadReligion(int totalPlayerNumber) {
      religionArr = new Religion[4];
      int[] player_initial_islam = new int[totalPlayerNumber];
      int[] player_initial_chirst = new int[totalPlayerNumber];
      int[] player_initial_jew = new int[totalPlayerNumber];
      int[] player_initial_hindu = new int[totalPlayerNumber];
      for (int j = 0; j < totalPlayerNumber; j++) {
         player_initial_islam[j] = playerList[j].getInitialIslam();
         player_initial_chirst[j] = playerList[j].getInitialChristianity();
         player_initial_jew[j] = playerList[j].getInitialJudaism();
         player_initial_hindu[j] = playerList[j].getInitialHinduism();
      }
      religionArr[0] = new Religion(totalPlayerNumber, player_initial_islam);
      religionArr[1] = new Religion(totalPlayerNumber, player_initial_chirst);
      religionArr[2] = new Religion(totalPlayerNumber, player_initial_jew);
      religionArr[3] = new Religion(totalPlayerNumber, player_initial_hindu);
   }

   public void loadInitialMap() {

      disableActions();
      disableAllTerrains();
      for (int i = 0; i < 9; i++)
         for (int j = 0; j < 13; j++) {
            if (map.spaces[i][j].getType().equals(playerList[roundController.getCurrentPlayerId()].getFaction().TERRAIN_TILE)) {
               if (terrains[i][j] != null && !map.spaces[i][j].isOccupied())
                  terrains[i][j].setDisable(false);
            }
         }
   }

   /**
    * TODO
    * TAŞINACAK
    */
   public  void setButtonClickForInitialDwellings() {
      for (int i = 0; i < ROW_NUMBER; i++) {
         for (int j = 0; j < COLUMN_NUMBER; j++) {
            final int row = i;
            final int col = j;
            if (terrains[i][j] != null) {
               terrains[i][j].setOnMouseClicked(new EventHandler<MouseEvent>() {
                  @Override
                  public void handle(MouseEvent event) {
                     skipTurn.setDisable(false);
                     map.buildDwelling(map.spaces[row][col], map.spaces[row][col].getType(), true);
                     playerHandler.buildInitialDwelling(playerList[roundController.getCurrentPlayerId()]);
                     map.spaces[row][col].setPlayer(playerList[roundController.getCurrentPlayerId()]);
                     TerrainController.buildDwelling(terrains[row][col], map.spaces[row][col].getType());
                     map.spaces[row][col].setStructure("Dwelling");
                     for (int i = 0; i < ROW_NUMBER; i++) {
                        for (int j = 0; j < COLUMN_NUMBER; j++) {
                           if (terrains[i][j] != null)
                              terrains[i][j].setDisable(true);
                        }
                     }
                  }
               });
            }
         }
      }
   }


   /**
    * TODO
    * TAŞINACAK
    */
   public void disableButtonClicks() {
      for (int i = 0; i < ROW_NUMBER; i++) {
         for (int j = 0; j < COLUMN_NUMBER; j++) {
            if (terrains[i][j] != null)
               terrains[i][j].setOnMouseClicked(null);
         }
      }
   }

   /**
    * TODO
    * TAŞINACAK
    */
   public void enableTerrains() {
      for (int i = 0; i < ROW_NUMBER; i++) {
         for (int j = 0; j < COLUMN_NUMBER; j++) {
            if (terrains[i][j] != null && map.spaces[i][j].getType() != "River")
               terrains[i][j].setDisable(false);
         }
      }
   }

   /**
    * TODO
    * TAŞINACAK
    */
   public void disableAllTerrains() {
      for (int i = 0; i < ROW_NUMBER; i++) {
         for (int j = 0; j < COLUMN_NUMBER; j++) {
            if (terrains[i][j] != null)
               terrains[i][j].setDisable(true);
         }
      }
   }

   /**
    * TODO
    * TAŞINACAK
    */
   public void disableActions() {
      terraform.setDisable(true);
      upgradeShipping.setDisable(true);
      upgradeStruct.setDisable(true);
      sendPriest.setDisable(true);
      powerActions.setDisable(true);
      specialActions.setDisable(true);
      upgradeSpade.setDisable(true);
      passRound.setDisable(true);
   }

   /**
    * TODO
    * TAŞINACAK
    */
   public void enableActions() {
      terraform.setDisable(false);
      upgradeShipping.setDisable(false);
      upgradeStruct.setDisable(false);
      sendPriest.setDisable(false);
      powerActions.setDisable(false);
      specialActions.setDisable(false);
      upgradeSpade.setDisable(false);
      passRound.setDisable(false);
      skipTurn.setDisable(false);
   }

   /**
    * TODO
    * TAŞINACAK
    */
   public void displayPlayerTurn(ArrayList<PlayerView> playerViewList) {

//      for (PlayerView playerView : playerViewList) {
//         playerView.setStyle("");
//      }

      switch (playerList[roundController.getCurrentPlayerId()].getFaction().TERRAIN_TILE) {
         case "Wasteland":
            playerViewList.get(roundController.getCurrentPlayerId()).setStyle("-fx-effect: dropshadow( gaussian , rgba(224, 15, 0, 1) , 10,0.5,0,1 );");
            break;
         case "Forest":
            playerViewList.get(roundController.getCurrentPlayerId()).setStyle("-fx-effect: dropshadow( gaussian , rgba(23, 150, 26, 1) , 10,0.5,0,1 );");
            break;
         case "Lakes":
            playerViewList.get(roundController.getCurrentPlayerId()).setStyle("-fx-effect: dropshadow( gaussian , rgba(0, 189, 214, 1) , 10,0.5,0,1 );");
            break;
         case "Desert":
            playerViewList.get(roundController.getCurrentPlayerId()).setStyle("-fx-effect: dropshadow( gaussian , rgba(214, 175, 0, 1) , 10,0.5,0,1 );");
            break;
         case "Mountains":
            playerViewList.get(roundController.getCurrentPlayerId()).setStyle("-fx-effect: dropshadow( gaussian , rgba(191, 191, 191, 1), 10,0.5,0,1 );");
            break;
         case "Swamp":
            playerViewList.get(roundController.getCurrentPlayerId()).setStyle("-fx-effect: dropshadow( gaussian , rgba(22, 20, 8, 1), 10,0.5,0,1 );");
            break;
         case "Plains":
            playerViewList.get(roundController.getCurrentPlayerId()).setStyle("-fx-effect: dropshadow( gaussian , rgba(152, 93, 27, 1), 10,0.5,0,1 );");
            break;
      }



   }

   public void showPowerActions(Player currentPlayer) {

      PowerActionView powerActionView = new PowerActionView();
      Button select = powerActionView.getSelectButton();


      final Stage dialog = new Stage();
      dialog.initModality(Modality.APPLICATION_MODAL);
      Scene dialogScene = new Scene(powerActionView, 1100, 600);
      dialog.setScene(dialogScene);
      dialog.setTitle("Power Action");
      dialog.setResizable(false);

      select.setOnMouseClicked(new EventHandler<MouseEvent>() {

         @Override
         public void handle(MouseEvent event) {
            playerList[roundController.currentPlayerId].setBowlThreePower(12);
            playerList[roundController.currentPlayerId].setWorkerNum(1000);
            playerList[roundController.currentPlayerId].setGoldNum(1000);
            int chosen = powerActionView.getSelection();
            System.out.println("Selection: " + chosen);
            powerActionView.setSelection(chosen);
            dialog.close();
            if (powerActionView.getSelection() == 0) {
               boolean checkBridgability = false;
               for(int i = 0; i < ROW_NUMBER; i++) {
                  for (int j = 0; j < COLUMN_NUMBER; j++) {
                     if (map.spaces[i][j].getType().equals(playerList[roundController.currentPlayerId].getFaction().TERRAIN_TILE) && map.spaces[i][j].getBridgability() && !map.spaces[i][j].getBridgeConnection() && map.spaces[i][j].isOccupied()) {
                        System.out.println("lala");
                        checkBridgability = true;
                     }
                  }
               }
               if(checkBridgability) {
                  if (playerHandler.usePowerAction(0, currentPlayer)) {
                     System.out.println("Girdi");
                     disableAllTerrains();
                     TerrainController.buildBridge(playerList[roundController.currentPlayerId].getFaction().TERRAIN_TILE, terrains, map, mapPane, actions);
                     disableActions();
                     System.out.println("Köprü kuruldu");
                  }
               }
            } else if (powerActionView.getSelection() == 4) {
               if (playerHandler.usePowerAction(4, currentPlayer)) {
                  disableActions();
                  terraform.setDisable(false);
               }
            } else if (powerActionView.getSelection() == 5) {
               if (playerHandler.usePowerAction(5, currentPlayer)) {
                  disableActions();
                  terraform.setDisable(false);
                  if (currentPlayer.getFreeSpade() > 0) {
                     disableActions();
                     terraform.setDisable(false);
                  }
               }
            } else {
               if (playerHandler.usePowerAction(powerActionView.getSelection(), currentPlayer)) {
                  System.out.println("Köprü kuruldu");
               }
            }
         }
      });
      dialog.show();
   }
   private void showExchangeResources() {

      ExchangeResourcesView exchangeResourcesView = new ExchangeResourcesView();
      Button select = exchangeResourcesView.getSelectButton();
      final Stage dialog = new Stage();
      dialog.initModality(Modality.APPLICATION_MODAL);
      Scene dialogScene = new Scene(exchangeResourcesView, 1100, 600);
      dialog.setScene(dialogScene);
      dialog.setTitle("Power Action");
      dialog.setResizable(false);
      select.setOnMouseClicked(new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent event) {
            int chosen = exchangeResourcesView.getSelection();
            System.out.println("Selection: " + chosen);
            exchangeResourcesView.setSelection(chosen);
            playerHandler.exchangeResources(playerList[roundController.getCurrentPlayerId()], chosen);
            dialog.close();
         }
      });
      dialog.showAndWait();
   }


   public CardsAndTiles getCardsAndTiles() {
      return cardsAndTiles;
   }

   public void showTown(Button[][] terrains, Map map)

   {
      for (int i = 0; i < 9; i++) {
         for (int j = 0; j < 13; j++) {
            if( terrains[i][j] != null && map.spaces[i][j].isMarked())
               terrains[i][j].getStyleClass().add("townShadow");
         }
      }

   }

   public int engineerStrongholdAbility() {
      int counter = 0;
      for (int i = 0; i < ROW_NUMBER; i++) {
         for (int j = 0; j < COLUMN_NUMBER; j++) {
            if (map.spaces[i][j] != null) {
               if (map.spaces[i][j].getType().equals("Mountains") && map.spaces[i][j].getBridgeConnection()) {
                  ArrayList<Space> bridgables = map.bridgeables(map.spaces[i][j]);
                  for (int k = 0; k < bridgables.size(); k++)
                     if (bridgables.get(k).getBridgeConnection() && bridgables.get(k).getBridgeType().equals("Mountains") && bridgables.get(k).isOccupied()) {
                        counter++;
                     }
               }
            }
         }
      }
      return 3*(counter/2);
   }

   public void showScoreTable(ArrayList<ArrayList<Integer>>[] religionScores,ArrayList<Integer>[] pathScores) {
      BorderPane emptyPane = new BorderPane();
      final Stage dialog = new Stage();
      dialog.initModality(Modality.APPLICATION_MODAL);
      Scene dialogScene = new Scene(emptyPane, 1100, 600);
      dialog.setScene(dialogScene);
      dialog.setTitle("Score Table");
      dialog.setResizable(false);
      emptyPane.setBackground(new Background( new BackgroundImage( new Image("score_table_background.jpg"), BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
              BackgroundSize.DEFAULT)));


      VBox connection = new VBox();
      ImageView vic_p1 = new ImageView("victory_point.png");
      ImageView vic_p2 = new ImageView("victory_point.png");
      Label label1 = new Label("\n    8\n\n");
      ImageView sep_line = new ImageView("seperate_line.png");
      sep_line.setFitWidth(140);
      sep_line.setFitHeight(30);
      Label label2 = new Label("\n    4\n\n");
      ImageView sep_line2 = new ImageView("seperate_line.png");
      sep_line2.setFitWidth(140);
      sep_line2.setFitHeight(30);
      Label label3 = new Label("\n    2\n");
      label1.setFont(new Font("Stencil", emptyPane.getHeight()/17));
      label2.setFont(new Font("Stencil", emptyPane.getHeight()/17));
      label3.setFont(new Font("Stencil", emptyPane.getHeight()/17));
      vic_p1.setFitWidth(emptyPane.getWidth()/8);
      vic_p1.setFitHeight(emptyPane.getHeight()/5);
      vic_p2.setFitWidth(emptyPane.getWidth()/8);
      vic_p2.setFitHeight(emptyPane.getHeight()/5);
      VBox ranking1 = new VBox(vic_p2, label1 ,sep_line, label2,sep_line2, label3 );
      ranking1.setStyle("-fx-background-color: rgba(133,88,23,0.43);");
      HBox allTable = new HBox();
      allTable.getChildren().add(ranking1);
      for (int i = 0; i< 4;i++){
         VBox tempBox = new VBox();
         tempBox.setPrefHeight(500);
         tempBox.setCenterShape(true);
         ImageView religion_image;
         if (i == 0){
            religion_image = new ImageView("islam_symbol.png");
         }else if (i == 1){
            religion_image = new ImageView("chris_symbol.png");
         }else if (i == 2){
            religion_image = new ImageView("judaism_symbol.png");
         }else{
            religion_image = new ImageView("hinduism.png");
         }
         religion_image.setFitWidth(emptyPane.getWidth()/8);
         religion_image.setFitHeight(emptyPane.getHeight()/5);
         tempBox.getChildren().add(religion_image);

         for (int j = 0; j < 3; j++){
            HBox tempHBox = new HBox();
            if(j>0 && religionScores[i].get(j).size() > 0) {
               ImageView sep_line4 = new ImageView("seperate_line.png");
               sep_line4.setFitWidth(140);
               sep_line4.setFitHeight(50);
               tempBox.getChildren().add(sep_line4);
            }
            double align = religionScores[i].get(j).size();
            for(int shaped = 1; shaped < religionScores[i].get(j).size();shaped++) {
               Label label = new Label("\n");
               tempBox.getChildren().add(label);
               align = religionScores[i].get(j).size()/1.7;
            }
            for(int k = 0; k < religionScores[i].get(j).size(); k++){
               int player_id = religionScores[i].get(j).get(k);
               ImageView player_images = new ImageView(getImage(playerList[player_id]));
               if(religionScores[i].get(j).size() == 1){
                  System.out.println("giiiiii");
                  Label label = new Label("\t");
                  tempHBox.getChildren().add(label);
               }
               player_images.setFitHeight(emptyPane.getHeight()/(5*align));
               player_images.setFitWidth(emptyPane.getWidth()/(13*align));
               tempHBox.setFillHeight(true);
               tempHBox.getChildren().add(player_images);
            }
            tempBox.getChildren().add(tempHBox);
            tempBox.setStyle("-fx-background-color: rgba(133,88,23,0.43);");
         }

         allTable.getChildren().add(tempBox);
      }
      Label label4 = new Label("\n   18\n\n");
      Label label5 = new Label("\n   12\n\n");
      Label label6 = new Label("\n    6");
      label4.setFont(new Font("Stencil", emptyPane.getHeight()/17));
      label5.setFont(new Font("Stencil", emptyPane.getHeight()/17));
      label6.setFont(new Font("Stencil", emptyPane.getHeight()/17));
      ImageView sep_line6 = new ImageView("seperate_line.png");
      sep_line6.setFitWidth(140);
      sep_line6.setFitHeight(30);
      ImageView sep_line7 = new ImageView("seperate_line.png");
      sep_line7.setFitWidth(140);
      sep_line7.setFitHeight(30);
      VBox ranking2 = new VBox(vic_p1, label4 , sep_line6, label5, sep_line7, label6 );
      allTable.getChildren().add(ranking2);
      ImageView con_image = new ImageView("hiclipart.com (1).png");
      con_image.setFitWidth(emptyPane.getWidth()/8);
      con_image.setFitHeight(emptyPane.getHeight()/5);
      connection.getChildren().add(con_image);
      for (int j = 0; j < 3; j++){
         HBox tempHBox = new HBox();
         if(j>0 && pathScores[j].size() > 0) {
            ImageView sep_line8 = new ImageView("seperate_line.png");
            sep_line8.setFitWidth(140);
            sep_line8.setFitHeight(60);
            connection.getChildren().add(sep_line8);
         }
         double align = pathScores[j].size();
         for(int shaped = 1; shaped < pathScores[j].size();shaped++) {
            Label label = new Label("\n");
            connection.getChildren().add(label);
            align = pathScores[j].size()/1.7;
         }
         for(int k = 0; k < pathScores[j].size(); k++){
            int player_id = pathScores[j].get(k);
            ImageView player_images = new ImageView(getImage(playerList[player_id]));
            if(pathScores[j].size() == 1){
               System.out.println("giiiiii");
               Label label = new Label("\t");
               tempHBox.getChildren().add(label);
            }
            player_images.setFitHeight(emptyPane.getHeight()/(5*align));
            player_images.setFitWidth(emptyPane.getWidth()/(13*align));
            tempHBox.getChildren().add(player_images);
         }

         connection.getChildren().add(tempHBox);
         connection.setStyle("-fx-background-color: rgba(29,80,107,0.36);");
      }
      allTable.getChildren().add(connection);
      ranking2.setStyle("-fx-background-color: rgba(29,80,107,0.36);");
      emptyPane.setCenter(allTable);
      dialog.show();

      if(roundController.isOver())
      {
         System.out.println("girdi");
         dialog.setOnCloseRequest(e->Platform.exit());
      }
   }
   public Image getImage(Player player){
      Image image = null;
      if (player.getFaction() instanceof AliesterCrowley) {
         image = new Image("file:src/Images/FactionImages/Image_AleisterCrowley.jpeg");
      } else if (player.getFaction() instanceof AmerigoVespucci) {
         image = new Image("file:src/Images/FactionImages/Image_AmerigoVespucci.jpeg");
      } else if (player.getFaction() instanceof Buddha) {
         image = new Image("file:src/Images/FactionImages/Image_Buddha.jpeg");
      } else if (player.getFaction() instanceof DariusTheGreat) {
         image = new Image("file:src/Images/FactionImages/Image_DariusTheGreat.jpeg");
      } else if (player.getFaction() instanceof ErikTheRed) {
         image = new Image("file:src/Images/FactionImages/Image_ErikTheRed.jpeg");
      } else if (player.getFaction() instanceof Gilgamesh) {
         image = new Image("file:src/Images/FactionImages/Image_Gilgamesh.jpeg");
      } else if (player.getFaction() instanceof HelenOfTroy) {
         image = new Image("file:src/Images/FactionImages/Image_HelenOfTroy.jpeg");
      } else if (player.getFaction() instanceof HusseinTheTeaMaker) {
         image = new Image("file:src/Images/FactionImages/Image_HusseinTheTeaMaker.jpeg");
      } else if (player.getFaction() instanceof LeonardoDaVinci) {
         image = new Image("file:src/Images/FactionImages/Image_LeonardoDaVinci.jpeg");
      } else if (player.getFaction() instanceof MarieCurie) {
         image = new Image("file:src/Images/FactionImages/Image_MarieCurie.jpeg");
      } else if (player.getFaction() instanceof MorganLeFay) {
         image = new Image("file:src/Images/FactionImages/Image_MorganLeFay.jpeg");
      } else if (player.getFaction() instanceof Ramesses) {
         image = new Image("file:src/Images/FactionImages/Image_Ramesses.jpeg");
      } else if (player.getFaction() instanceof StPatrick) {
         image = new Image("file:src/Images/FactionImages/Image_StPatrick.jpeg");
      } else if (player.getFaction() instanceof VladTheImpaler) {
         image = new Image("file:src/Images/FactionImages/Image_VladTheImpaler.jpeg");
      }
      return image;
   }
   public Map getMap() {
      return map;
   }

   public ArrayList<PlayerView> getPlayerViewList() {
      return playerViewList;
   }

   public Player[] getPlayerList() {
      return playerList;
   }

   public Religion[] getReligionArr() {
      return religionArr;
   }

   public PlayerHandler getPlayerHandler() {
      return playerHandler;
   }

   public Player getCurrentPlayer() {
      return currentPlayer;
   }

   public RoundController getRoundController() {return roundController;}

   public void setMap(Map map) {
      this.map = map;
   }

   public void setPlayerList(Player[] playerList) {
      this.playerList = playerList;
   }

   public void setCardsAndTiles(CardsAndTiles cat) {
      this.cardsAndTiles = cat;
   }

   public void setReligionArr(Religion[] religions) {
      this.religionArr = religions;
   }

   public void setPlayerHandler(PlayerHandler ph) {
      this.playerHandler = ph;
   }

   public void setCurrentPlayer(Player p1) {
      this.currentPlayer = p1;
   }

   public void setRoundController(RoundController r1){this.roundController = r1;}


}