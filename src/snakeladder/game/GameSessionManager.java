package snakeladder.game;

import ch.aplu.jgamegrid.GGSound;

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
public class GameSessionManager {
    private NavigationPane np;
    private GamePane gp;




    GameSessionManager(Properties properties, NavigationPane np, GamePane gp) {
        this.np = np;
        np.setGsm(this);

        this.gp = gp;
        gp.setGsm(this);

    }

    public List<Puppet> getAllPuppets(){
        return gp.getAllPuppets();
    }

    public int getNumberOfPlayers(){
        return gp.getNumberOfPlayers();
    }

    //--------------------------------------Methods called by NP----------------------------------------

    // Any extra movement logic or method calls to be added in the future can go here and NP does not need to
    // know about it.
    public void handleMovement(int nb){
        gp.getPuppet().go(nb);
    }


    /* NavigationPane doesn't care how the dice is rolled, it just tells GSM to handle it and give NP back information
     * for display if necessary. */
    public void handleToggle() {
        gp.toggleConnection();
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

    public GamePane getGP() {
        return gp;
    }

    public Puppet getPuppet(){
        return gp.getPuppet();
    }

    public void resetAllPuppets(){
        gp.resetAllPuppets();
    }
    public String getPuppetName(){
        return gp.getPuppet().getPuppetName();
    }

    public void switchToNextPuppet(){
        gp.switchToNextPuppet();
    }

    public int getCurrentPuppetIndex(){
        return gp.getCurrentPuppetIndex();
    }

    public boolean puppetIsAuto(){
        return gp.getPuppet().isAuto();
    }

}

