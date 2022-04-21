package snakeladder.game;

import ch.aplu.jgamegrid.GGSound;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SLOPController {
    private NavigationPane np;
    private GamePane gp;

    SLOPController(Properties properties, NavigationPane np, GamePane gp) {
        this.np = np;
        np.setSC(this);

        this.gp = gp;
        gp.setSC(this);

    }

    //--------------------------------------Methods called by NP----------------------------------------

    // Any extra movement logic or method calls to be added in the future can go here and NP does not need to
    // know about it.
    public void handleMovement(int nb, boolean minDiceRoll){
        gp.getPuppet().go(nb, minDiceRoll);
    }

    /* NavigationPane doesn't care how the dice is rolled, it just tells sc to handle it and give NP back information
     * for display if necessary. */
    public void handleToggle() {
        gp.toggleConnection();
    }

    /* NERDI can make changes to the strategy here easily, even if it involves the use of some other classes
     * like other game entities or panes. */
    public boolean toggleStrategy(int numDice) {
        if(gp.moreUpwardsConnections(numDice)){
            return true;
        } else {
            return false;
        }
    }

    public void resetGame(){
        gp.resetAllPuppets();
    }

    public void switchToNextPuppet(){
        gp.switchToNextPuppet();
    }

    //------------------------------------Methods called by Puppet---------------------------------------

    // The method calls to showStatus and playSound are kept here so that NP does not need to know about the Connection
    // stuff like Snake and Ladders.
    public void connectionOutput(Connection currentCon) {
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

    public void handleCheckGameStatusRequest(int curPuppetCellIndex) {
        np.verifyGameStatus(curPuppetCellIndex);
    }

    //--------------------------Getters and Setters----------------------------------------

    public String getPuppetName(){
        return gp.getPuppet().getPuppetName();
    }

    public int fetchCurrentPuppetNumber(){
        return gp.getCurrentPuppetIndex();
    }

    public boolean puppetIsAuto(){
        return gp.getPuppet().isAuto();
    }

    public List<String> fetchAllPuppetPositions(){
        List<String> playerPositions = new ArrayList<>();
        for(Puppet puppet: gp.getAllPuppets()) {
            playerPositions.add(puppet.getCellIndex() + "");
        }
        return playerPositions;
    }

    public int fetchPlayerNumber(){
        return gp.getNumberOfPlayers();
    }

}

