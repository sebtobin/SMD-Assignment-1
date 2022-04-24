package snakeladder.game;

import ch.aplu.jgamegrid.GGSound;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SLOPController {
    private NavigationPane np;
    private GamePane gp;

    SLOPController(NavigationPane np, GamePane gp) {
        this.np = np;
        np.setSC(this);

        this.gp = gp;
        gp.setSC(this);

    }

    //--------------------------------------Methods called by NP----------------------------------------

    // Any extra movement logic or method calls to be added in the future can go here and NP does not need to
    // know about it.
    void handleMovement(int nb, boolean minDiceRoll){
        gp.getPuppet().go(nb, minDiceRoll);
    }

    /* NavigationPane doesn't care how the dice is rolled, it just tells sc to handle it and give NP back information
     * for display if necessary. */
    void handleToggle() {
        gp.toggleConnection();
    }

    /* NERDI can make changes to the strategy here easily, even if it involves the use of some other classes
     * like other game entities or panes. */
    boolean toggleStrategy(int numDice) {
        if(gp.moreUpwardsConnections(numDice)){
            return true;
        } else {
            return false;
        }
    }

    void resetGame(){
        gp.resetAllPuppets();
    }

    void switchToNextPuppet(){
        gp.switchToNextPuppet();
    }

    //------------------------------------Methods called by Puppet---------------------------------------

    // The method calls to showStatus and playSound are kept here so that NP does not need to know about the Connection
    // stuff like Snake and Ladders.
    void connectionOutput(Connection currentCon) {
        if (currentCon instanceof Snake)
        {
            np.showStatus("Digesting...");
            np.playSound(GGSound.MMM);
        }
        else
        {
            np.showStatus("Climbing...");
            np.playSound(GGSound.BOING);
        }
    }

    void handleEndTurnRequest() {
        np.endTurn(gp.getPuppet().getCellIndex());
    }


    //--------------------------Getters and Setters----------------------------------------

    String fetchCurrentPuppetName(){
        return gp.getPuppet().getPuppetName();
    }

    int fetchCurrentPuppetNumber(){
        return gp.getCurrentPuppetIndex();
    }

    boolean fetchCurrentPuppetIsAuto(){
        return gp.getPuppet().isAuto();
    }

    List<String> fetchAllPuppetPositions(){
        return gp.getAllPuppetPositions();
    }

    int fetchPlayerNumber(){
        return gp.getNumberOfPlayers();
    }

    int fetchNumDice(){
        return np.fetchNumDice();
    }

    void addRollToCurrPuppet(int totalRoll){
        gp.addRollToCurrPlayer(totalRoll);
    }

    void printPuppetStats(){
        gp.printPuppetStats();
    }

}

