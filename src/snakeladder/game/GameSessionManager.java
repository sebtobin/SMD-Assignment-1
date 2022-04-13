package snakeladder.game;

import ch.aplu.jgamegrid.GGButton;
import ch.aplu.jgamegrid.GGSound;
import ch.aplu.util.Monitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class GameSessionManager {
    private NavigationPane np;
    private GamePane gp;

    private volatile boolean isGameOver = false;
    private boolean isAuto;

    private GamePlayCallback gamePlayCallback;

    /* Going to go into dice manager */
    private int nbRolls = 0;

    GameSessionManager(Properties properties, NavigationPane np, GamePane gp) {
        this.np = np;
        this.gp = gp;
        isAuto = Boolean.parseBoolean(properties.getProperty("autorun"));
        System.out.println("autorun = " + isAuto);
    }

    public void initialiseGameSession() {
        np.createNPGui(isAuto);
        np.setGsm(this);
        gp.createPlayerGui();
        np.checkAuto();
    }

    // To be finished
    private void createPlayers() {
        for (int i = 0; i < numberOfPlayers; i++) {
            boolean isAuto = playerManualMode.get(i);

            Puppet puppet = new Puppet(this, puppetImage);
            puppet.setAuto(isAuto);
            puppet.setPuppetName("Player " + (i + 1));
            addActor(puppet, startLocation);
            puppets.add(puppet);
        }
    }
    public void prepareRoll(int currentIndex)
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
            gamePlayCallback.finishGameWithResults(nbRolls % gp.getNumberOfPlayers(), playerPositions);
            gp.resetAllPuppets();
        }
        else
        {
            np.playSound(GGSound.CLICK);
            np.showStatus("Done. Click the hand!");
            String result = gp.getPuppet().getPuppetName() + " - pos: " + currentIndex;
            np.showResult(result);
            gp.switchToNextPuppet();
            // System.out.println("current puppet - auto: " + gp.getPuppet().getPuppetName() + "  " + gp.getPuppet().isAuto() );

            if (isAuto) {
                Monitor.wakeUp();
            } else if (gp.getPuppet().isAuto()) {
                Monitor.wakeUp();
            } else {
                np.getHandBtn().setEnabled(true);
            }
        }
    }

    void startMoving(int nb)
    {
        np.showStatus("Moving...");
        np.showPips("Pips: " + nb);
        np.showScore("# Rolls: " + (++nbRolls));
        gp.getPuppet().go(nb);
    }

    void rollDie(int tag) {
        prepareBeforeRoll();
        // roll method will be moved into dice manager
        // roll(tag);
    }

    void prepareBeforeRoll() {
        np.getHandBtn().setEnabled(false);
        if (isGameOver)  // First click after game over
        {
            isGameOver = false;
            nbRolls = 0;
        }

    // ------ All this shit goes into Dice Manager -------
    /*void prepareBeforeRoll() {
        np.getHandBtn().setEnabled(false);
        if (isGameOver)  // First click after game over
        {
            isGameOver = false;
            nbRolls = 0;
        }
    }
    public void buttonClicked(GGButton btn)
    {
        System.out.println("hand button clicked");
        prepareBeforeRoll();
        roll(getDieValue());
    }*/

    void setGamePlayCallback(GamePlayCallback gamePlayCallback) {
        this.gamePlayCallback = gamePlayCallback;
    }

}
