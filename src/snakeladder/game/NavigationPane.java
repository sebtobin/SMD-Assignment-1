package snakeladder.game;

import ch.aplu.jgamegrid.*;
import java.awt.*;
import ch.aplu.util.*;
import snakeladder.game.custom.CustomGGButton;

import java.util.ArrayList;
import java.util.Properties;

@SuppressWarnings("serial")
public class NavigationPane extends GameGrid
        implements GGButtonListener
{

  private class SimulatedPlayer extends Thread
  {
    public void run()
    {
      while (true)
      {
        Monitor.putSleep();
        while (dr.getNumRolls() < dr.getNumDice()){
          handBtn.show(1);
          completeRoll(rollDice());
          delay(1000);
          handBtn.show(0);
        }

      }
    }

  }

  private final int DIE1_BUTTON_TAG = 1;
  private final int DIE2_BUTTON_TAG = 2;
  private final int DIE3_BUTTON_TAG = 3;
  private final int DIE4_BUTTON_TAG = 4;
  private final int DIE5_BUTTON_TAG = 5;
  private final int DIE6_BUTTON_TAG = 6;

  private final Location handBtnLocation = new Location(110, 70);
  private final Location dieBoardLocation = new Location(100, 180);
  private final Location pipsLocation = new Location(70, 230);
  private final Location statusLocation = new Location(20, 330);
  private final Location statusDisplayLocation = new Location(100, 320);
  private final Location scoreLocation = new Location(20, 430);
  private final Location scoreDisplayLocation = new Location(100, 430);
  private final Location resultLocation = new Location(20, 495);
  private final Location resultDisplayLocation = new Location(100, 495);

  private final Location autoChkLocation = new Location(15, 375);
  private final Location toggleModeLocation = new Location(95, 375);

  private final Location die1Location = new Location(20, 270);
  private final Location die2Location = new Location(50, 270);
  private final Location die3Location = new Location(80, 270);
  private final Location die4Location = new Location(110, 270);
  private final Location die5Location = new Location(140, 270);
  private final Location die6Location = new Location(170, 270);

  private GGButton handBtn = new GGButton("sprites/handx.gif");

  private GGButton die1Button = new CustomGGButton(DIE1_BUTTON_TAG, "sprites/Number_1.png");
  private GGButton die2Button = new CustomGGButton(DIE2_BUTTON_TAG, "sprites/Number_2.png");
  private GGButton die3Button = new CustomGGButton(DIE3_BUTTON_TAG, "sprites/Number_3.png");
  private GGButton die4Button = new CustomGGButton(DIE4_BUTTON_TAG, "sprites/Number_4.png");
  private GGButton die5Button = new CustomGGButton(DIE5_BUTTON_TAG, "sprites/Number_5.png");
  private GGButton die6Button = new CustomGGButton(DIE6_BUTTON_TAG, "sprites/Number_6.png");

  private GGTextField pipsField;
  private GGTextField statusField;
  private GGTextField resultField;
  private GGTextField scoreField;

  private boolean gameSessionIsAuto;
  private GGCheckButton autoChk;

  private boolean isToggle = false;
  private GGCheckButton toggleCheck =
          new GGCheckButton("Toggle Mode", YELLOW, TRANSPARENT, isToggle);

  private int nbRolls = 0;
  private volatile boolean isGameOver = false;
  private GamePlayCallback gamePlayCallback;

  private SLOPController sc;
  private DiceRoller dr;
  NavigationPane(Properties properties)
  {
    gameSessionIsAuto = Boolean.parseBoolean(properties.getProperty("autorun"));
    autoChk = new GGCheckButton("Auto Run", YELLOW, TRANSPARENT, gameSessionIsAuto);
    System.out.println("autorun = " + gameSessionIsAuto);
    setSimulationPeriod(200);
    setBgImagePath("sprites/navigationpane.png");
    setCellSize(1);
    setNbHorzCells(200);
    setNbVertCells(600);
    doRun();
    new SimulatedPlayer().start();
    int numberOfDice =  //Number of six-sided dice
            (properties.getProperty("dice.count") == null)
                    ? 1  // default
                    : Integer.parseInt(properties.getProperty("dice.count"));
    System.out.println("numberOfDice = " + numberOfDice);
    this.dr = new DiceRoller(numberOfDice);
  }

  void setGamePlayCallback(GamePlayCallback gamePlayCallback) {
    this.gamePlayCallback = gamePlayCallback;
  }

  class ManualDieButton implements GGButtonListener {
    @Override
    public void buttonPressed(GGButton ggButton) {

    }

    @Override
    public void buttonReleased(GGButton ggButton) {

    }

    @Override
    public void buttonClicked(GGButton ggButton) {
      System.out.println("manual die button clicked");
      if (ggButton instanceof CustomGGButton) {
        CustomGGButton customGGButton = (CustomGGButton) ggButton;
        int tag = customGGButton.getTag();
        System.out.println("manual die button clicked - tag: " + tag);
        prepareBeforeRoll();
        completeRoll(tag);
      }
    }
  }
  void addDieButtons() {
    ManualDieButton manualDieButton = new ManualDieButton();

    addActor(die1Button, die1Location);
    addActor(die2Button, die2Location);
    addActor(die3Button, die3Location);
    addActor(die4Button, die4Location);
    addActor(die5Button, die5Location);
    addActor(die6Button, die6Location);

    die1Button.addButtonListener(manualDieButton);
    die2Button.addButtonListener(manualDieButton);
    die3Button.addButtonListener(manualDieButton);
    die4Button.addButtonListener(manualDieButton);
    die5Button.addButtonListener(manualDieButton);
    die6Button.addButtonListener(manualDieButton);
  }

  void createGui()
  {
    addActor(new Actor("sprites/dieboard.gif"), dieBoardLocation);

    handBtn.addButtonListener(this);
    addActor(handBtn, handBtnLocation);
    addActor(autoChk, autoChkLocation);
    autoChk.addCheckButtonListener(new GGCheckButtonListener() {
      @Override
      public void buttonChecked(GGCheckButton button, boolean checked)
      {
        gameSessionIsAuto = checked;
        if (gameSessionIsAuto)
          Monitor.wakeUp();
      }
    });

    addActor(toggleCheck, toggleModeLocation);
    toggleCheck.addCheckButtonListener(new GGCheckButtonListener() {
      @Override
      public void buttonChecked(GGCheckButton ggCheckButton, boolean checked) {
        isToggle = checked;
        sc.handleToggle();
      }
    });

    addDieButtons();

    pipsField = new GGTextField(this, "", pipsLocation, false);
    pipsField.setFont(new Font("Arial", Font.PLAIN, 16));
    pipsField.setTextColor(YELLOW);
    pipsField.show();

    addActor(new Actor("sprites/linedisplay.gif"), statusDisplayLocation);
    statusField = new GGTextField(this, "Click the hand!", statusLocation, false);
    statusField.setFont(new Font("Arial", Font.PLAIN, 16));
    statusField.setTextColor(YELLOW);
    statusField.show();

    addActor(new Actor("sprites/linedisplay.gif"), scoreDisplayLocation);
    scoreField = new GGTextField(this, "# Rolls: 0", scoreLocation, false);
    scoreField.setFont(new Font("Arial", Font.PLAIN, 16));
    scoreField.setTextColor(YELLOW);
    scoreField.show();

    addActor(new Actor("sprites/linedisplay.gif"), resultDisplayLocation);
    resultField = new GGTextField(this, "Current pos: 0", resultLocation, false);
    resultField.setFont(new Font("Arial", Font.PLAIN, 16));
    resultField.setTextColor(YELLOW);
    resultField.show();
  }

  void showPips(String text)
  {
    pipsField.setText(text);
    if (text != "") System.out.println(text);
  }

  void showStatus(String text)
  {
    statusField.setText(text);
    System.out.println("Status: " + text);
  }

  void showScore(String text)
  {
    scoreField.setText(text);
    System.out.println(text);
  }

  void showResult(String text)
  {
    resultField.setText(text);
    System.out.println("Result: " + text);
  }

  public void buttonClicked(GGButton btn)
  {
    System.out.println("hand button clicked");

    prepareBeforeRoll();
    completeRoll(rollDice());
    // enable the hand button if not all rolls have been done
    if (dr.getNumDice() > dr.getNumRolls()){
      handBtn.setEnabled(true);
    }
  }

  public void buttonPressed(GGButton btn)
  {
  }

  public void buttonReleased(GGButton btn)
  {
  }

  public void endTurn(int currentIndex)
  {
    if (currentIndex == 100)  // Game over
    {
      playSound(GGSound.FADE);
      showStatus("Click the hand!");
      showResult("Game over");
      isGameOver = true;
      handBtn.setEnabled(true);
      sc.printPuppetStats();
      java.util.List  <String> playerPositions = sc.fetchAllPuppetPositions();

      gamePlayCallback.finishGameWithResults(nbRolls % sc.fetchPlayerNumber(), playerPositions);
      sc.resetGame();
    }
    else
    {
      playSound(GGSound.CLICK);
      showStatus("Done. Click the hand!");
      String result = sc.fetchCurrentPuppetName() + " - pos: " + currentIndex;
      showResult(result);

      sc.switchToNextPuppet();
      /*System.out.println("current puppet - auto: " + sc.fetchCurrentPuppetName() +
              "  " + sc.fetchCurrentPuppetIsAuto());*/
      nextRoll();
    }
  }

  private void prepareBeforeRoll(){
    handBtn.setEnabled(false);
    if (isGameOver)  // First click after game over
    {
      isGameOver = false;
      nbRolls = 0;
    }
  }

  // Creates a new die with the roll value and plays the animation on NP
  private void playDieAnimation(int rollNumber) {
    showStatus("Rolling...");
    showPips("");

    removeActors(CosmeticDie.class);
    // only instruct the die to start moving if it's the last one
    CosmeticDie cosmeticDie = new CosmeticDie(rollNumber, this, dr.getNumRolls() == dr.getNumDice() - 1);
    addActor(cosmeticDie, dieBoardLocation);
  }
  // invoke the next roll depending if the game is auto or not
  public void nextRoll(){
    if (gameSessionIsAuto) {
      System.out.println("Wake up");
      Monitor.wakeUp();
    } else if (sc.fetchCurrentPuppetIsAuto()) {
      Monitor.wakeUp();
    } else {
      handBtn.setEnabled(true);
    }
  }

  public void completeRoll(int rollValue){
    playDieAnimation(rollValue);
    dr.registerRoll(rollValue);
    nbRolls++;

    showScore("# Rolls: " + (nbRolls));
  }

  /* In the act() method of Die class, if getIDVisible == 6, then we disable the Die to act in further
   *  ticks until the player has finished their movement and reached the goal.  */
  public void startMoving()
  {
    showStatus("Moving...");
    showPips("Pips: " + dr.getTotal());

    boolean minDiceRoll = (dr.getTotal() == dr.getNumDice());
    sc.handleMovement(dr.getTotal(), minDiceRoll);
    sc.addRollToCurrPuppet(dr.getTotal());
    dr.resetValues();

    // Determine toggle strategy after moving to minimise advantage of opponents
    if (sc.toggleStrategy(dr.getNumDice())) {
      isToggle = !isToggle;
      toggleCheck.setChecked(isToggle);
      sc.handleToggle();
    }
  }

  public boolean checkLastRoll(){
    return dr.getNumRolls() == dr.getNumDice();
  }

  public int rollDice() {
    return dr.getDieValues(sc.fetchCurrentPuppetNumber());
  }

  void initialiseDiceValues(Properties properties) {
    dr.setupInitialDieValues(properties, sc.fetchPlayerNumber());
  }

  public void checkAuto() {
    if (gameSessionIsAuto) Monitor.wakeUp();
  }

  public void setSC(SLOPController sc) {
    this.sc = sc;
  }

  int fetchNumDice(){
    return dr.getNumDice();
  }

}

