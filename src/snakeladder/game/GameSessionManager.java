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
public class GameSessionManager {
    private NavigationPane np;
    private GamePane gp;


    private DiceManager dm;

    GameSessionManager(Properties properties, NavigationPane np, GamePane gp) {
        this.np = np;
        np.setGsm(this);

        this.gp = gp;
        gp.setGsm(this);


        int numberOfDice =  //Number of six-sided dice
                (properties.getProperty("dice.count") == null)
                        ? 1  // default
                        : Integer.parseInt(properties.getProperty("dice.count"));
        System.out.println("numberOfDice = " + numberOfDice);
        this.dm = new DiceManager(numberOfDice);
    }

    void initialiseGameSession(Properties properties) {
        np.createGui();
        np.checkAuto();

        gp.createGui(properties);

        dm.setupInitialDieValues(properties, gp.getAllPuppets(), gp.getNumberOfPlayers());
    }



    // Any extra movement logic in the future can go here and NP does not need to know about it.
    public void handleMovement(int nb){
        gp.getPuppet().go(nb);
    }

    /* NavigationPane doesn't care how the dice is rolled, it just tells GSM to handle it and give NP back information
     * for display if necessary. */
    public int rollDice(int nbRolls) {
        return dm.getDieValues(gp.getAllPuppets(), gp.getNumberOfPlayers(), nbRolls);
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
    public DiceManager getDm()
    {
        return dm;
    }


}

