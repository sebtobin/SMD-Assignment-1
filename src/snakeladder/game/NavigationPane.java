package snakeladder.game;

import ch.aplu.jgamegrid.*;
import java.awt.*;
import ch.aplu.util.*;
import snakeladder.game.custom.CustomGGButton;
import snakeladder.utility.ServicesRandom;

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
        handBtn.show(1);
        completeRoll(gsm.rollDice());
        delay(1000);
        handBtn.show(0);
      }
    }

  }

  private final int DIE1_BUTTON_TAG = 1;
  private final int DIE2_BUTTON_TAG = 2;
  private final int DIE3_BUTTON_TAG = 3;
  private final int DIE4_BUTTON_TAG = 4;
  private final int DIE5_BUTTON_TAG = 5;
  private final int DIE6_BUTTON_TAG = 6;
  private final int RANDOM_ROLL_TAG = -1;

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

  private GamePane gp;
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
  private Properties properties;
  private GamePlayCallback gamePlayCallback;

  private GameSessionManager gsm;

  NavigationPane(Properties properties)
  {
    this.properties = properties;
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
        playDieAnimation(tag);
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
        gsm.handleToggle();
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
    completeRoll(gsm.rollDice());
  }

  public void buttonPressed(GGButton btn)
  {
  }

  public void buttonReleased(GGButton btn)
  {
  }

  public void verifyGameStatus(int currentIndex)
  {
    gp = gsm.getGP();
    if (currentIndex == 100)  // Game over
    {
      playSound(GGSound.FADE);
      showStatus("Click the hand!");
      showResult("Game over");
      isGameOver = true;
      handBtn.setEnabled(true);

      java.util.List  <String> playerPositions = new ArrayList<>();
      for (Puppet puppet: gp.getAllPuppets()) {
        playerPositions.add(puppet.getCellIndex() + "");
      }

      gamePlayCallback.finishGameWithResults(nbRolls % gp.getNumberOfPlayers(), playerPositions);
      gp.resetAllPuppets();
    }
    else
    {
      playSound(GGSound.CLICK);
      showStatus("Done. Click the hand!");
      String result = gp.getPuppet().getPuppetName() + " - pos: " + currentIndex;
      showResult(result);
      gp.switchToNextPuppet();
      System.out.println("current puppet - auto: " + gp.getPuppet().getPuppetName() +
              "  " + gp.getPuppet().isAuto() );

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

  private void playDieAnimation(int rollNumber) {
    showStatus("Rolling...");
    showPips("");

    removeActors(Die.class);
    Die die = new Die(rollNumber, this);
    addActor(die, dieBoardLocation);
  }

  public void nextRoll(){
    if (gameSessionIsAuto) {
      Monitor.wakeUp();
    } else if (gp.getPuppet().isAuto()) {
      Monitor.wakeUp();
    } else {
      handBtn.setEnabled(true);
    }
  }

  public void checkNextRoll(){
    if (gsm.checkLastRoll()){
      startMoving(gsm.getDm().getTotal());
      gsm.getDm().resetValues();
    }else{
      nextRoll();
    }
  }

  public void completeRoll(int rollValue){
    playDieAnimation(rollValue);
    gsm.getDm().registerRoll(rollValue);
  }

  /* In the act() method of Die class, if getIDVisible == 6, then we disable the Die to act in further
   *  ticks until the player has finished their movement and reached the goal.  */
  public void startMoving(int nb)
  {
    showStatus("Moving...");
    showPips("Pips: " + nb);

    nbRolls++;
    showScore("# Rolls: " + (nbRolls));

    gsm.handleMovement(nb);
  }

  public void checkAuto() {
    if (gameSessionIsAuto) Monitor.wakeUp();
  }

  public void setGsm(GameSessionManager gsm) {
    this.gsm = gsm;
  }
  public GameSessionManager getGsm()
  {
    return gsm;
  }

}

