package snakeladder.game;

import ch.aplu.jgamegrid.GameGrid;
import snakeladder.utility.ServicesRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DiceManager {

    int numDice;

    private List<List<Integer>> dieValues = new ArrayList<List<Integer>>();

    public DiceManager(int numDice) {
        this.numDice = numDice;
    }

    public void setupInitialDieValues(Properties properties, List<Puppet> players, int numberOfPlayers) {
        for (int i = 0; i < numberOfPlayers; i++) {
            if (properties.getProperty("die_values." + i) != null) {
                String dieValuesString = properties.getProperty("die_values." + i);
                String[] dieValueStrings = dieValuesString.split(",");
                players.get(i).setupPlayerDieValues(dieValueStrings);

            } else {
                System.out.println("All players need to be set a die value for the full testing mode to run. " +
                        "Switching off the full testing mode");
                dieValues = null;
                break;
            }
        }
        System.out.println("dieValues = " + dieValues);
    }
    public int getDieValues(int numberOfPlayers, List<Puppet> players) {
        if (dieValues == null) {
            return ServicesRandom.get().nextInt(6) + 1;
        }
        int playerIndex = nbRolls % numberOfPlayers;
        if (players.get(playerIndex).getNbRollsPuppet() < dieValues.get(playerIndex)){
            Puppet currPlayer = players.get(playerIndex);
            currPlayer.setNbRollsPuppet(currPlayer.getNbRollsPuppet() + 1);
            return dieValues.get(currPlayer.getNbRollsPuppet() - 1);
        }

        return ServicesRandom.get().nextInt(6) + 1;
    }

    public List<List<Integer>> getDieValues()
    {
        return dieValues;
    }
}
