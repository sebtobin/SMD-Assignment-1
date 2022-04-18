package snakeladder.game;

import ch.aplu.jgamegrid.GGSound;
import ch.aplu.jgamegrid.GameGrid;
import ch.aplu.util.Monitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/* A facade controller that represents the game system. It is a central class which contains an attribute of all
 * GameGrid objects in the game and is responsible for coordinating the classes of the game in handling user input
 * from NP. This is so that each GameGrid class or other classes that might be in the system can have a single
 * reference (an attribute of this class if absolutely necessary) instead of each of the classes such as GameGrid
 * needing to have an attribute of each other GameGrid.
 *
 * For example, suppose we decide to add a new PlayerSettingPane GameGrid class which requires coordination with
 * GamePane and NavigationPane. Persisting with the original design, GamePane would need to have an attribute of
 * NavigationPane and PlayerSettingPane, NavigationPane would need to have an attribute of GamePane and
 * PlayerSettingPane and vice versa. These dependencies get exponentially confusing to understand. However, with
 * GSM, each GameGrid class simply needs an attribute of GSM (if necessary), and GSM is responsible for handling
 * the coordination of these GameGrid classes when input from the user is given. */
public class GameSessionManager implements GamePlayCallback{

    private class SimulatedPlayer extends Thread
    {
        public void run()
        {
            while (true)
            {
                Monitor.putSleep();
                np.getHandBtn().show(1);
                dm.getDieValues(gp.getNumberOfPlayers(), gp.getAllPuppets());
                GameGrid.delay(1000);
                np.getHandBtn().show(0);
            }
        }

    }

    private NavigationPane np;
    private GamePane gp;
    private DiceManager dm;

    private volatile boolean isGameOver = false;
    private boolean isAuto;

    private GamePlayCallback gamePlayCallback;

    private int nbRolls = 0;

    GameSessionManager(Properties properties, NavigationPane np, GamePane gp) {
        this.np = np;
        this.gp = gp;

        isAuto = Boolean.parseBoolean(properties.getProperty("autorun"));
        System.out.println("autorun = " + isAuto);

        np.createNPGui(isAuto);
        np.setGsm(this);
        np.checkAuto();

        gp.setupPlayers(properties);

        int numberOfDice =  //Number of six-sided dice
                (properties.getProperty("dice.count") == null)
                        ? 1  // default
                        : Integer.parseInt(properties.getProperty("dice.count"));
        System.out.println("numberOfDice = " + numberOfDice);


        dm = new DiceManager(numberOfDice);

        dm.setupInitialDieValues(properties, gp.getAllPuppets(), gp.getNumberOfPlayers());

        new SimulatedPlayer().start();
    }

    /* Formerly prepareRoll, this is placed in GSM because it involves heavy coordination of the system (GSM represents
     * the system) as well as other elements of the system (GameGrids such as NP and GP). In the case that we choose to
     * extend the behaviour of the game with some other GameGrid or Actor, then Puppet would still only ever need a
     * reference to GSM through GamePane instead of having a reference to many different GameGrids or Actors that it
     * may need to properly function. */
    public void verifyGameStatus(int currentIndex)
    {
        if (currentIndex == 100)  // Game over
        {
            np.playSound(GGSound.FADE);
            np.showStatus("Click the hand!");
            np.showResult("Game over");
            isGameOver = true;
            np.getHandBtn().setEnabled(true);

            java.util.List  <String> playerPositions = new ArrayList<>();
            for (Puppet puppet: gp.getAllPuppets()) {
                playerPositions.add(puppet.getCellIndex() + "");
            }
            finishGameWithResults(nbRolls % gp.getNumberOfPlayers(), playerPositions);
            gp.resetAllPuppets();
        }
        else
        {
            np.playSound(GGSound.CLICK);
            np.showStatus("Done. Click the hand!");
            String result = gp.getPuppet().getPuppetName() + " - pos: " + currentIndex;
            np.showResult(result);
            gp.switchToNextPuppet();
            System.out.println("current puppet - auto: " + gp.getPuppet().getPuppetName() +
                    "  " + gp.getPuppet().isAuto() );

            if (isAuto) {
                Monitor.wakeUp();
            } else if (gp.getPuppet().isAuto()) {
                Monitor.wakeUp();
            } else {
                np.getHandBtn().setEnabled(true);
            }
        }
    }

    // Any extra movement logic in the future can go here and NP does not need to know about it.
    public void handleMovement(int nb){
        gp.getPuppet().go(nb);
    }

    /* NavigationPane doesn't care how the dice is rolled, it just tells GSM to handle it and give NP back information
     * for display if necessary. */
    public int rollDice() {
        return dm.getDieValues(gp.getNumberOfPlayers(), gp.getAllPuppets());
    }


    public void finishGameWithResults(int winningPlayerIndex, List<String> playerCurrentPositions) {
        System.out.println("DO NOT CHANGE THIS LINE---WINNING INFORMATION: " + winningPlayerIndex + "-" +
                String.join(",", playerCurrentPositions));
    }

    public void NewGame() {
        isGameOver = false;
        nbRolls = 0;
    }

    public void incrementGameNbRolls() {
        nbRolls = nbRolls++;
    }

    //---- Dice Manager stuff ----

    //---- Dice Manager stuff ----

    //--------------------------Getters and Setters----------------------------------------

    public NavigationPane getNP() {
        return np;
    }

    public GamePane getGP() {
        return gp;
    }

    public int getNbRolls() {
        return nbRolls;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

}

