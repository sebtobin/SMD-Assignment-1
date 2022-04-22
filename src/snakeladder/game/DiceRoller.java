package snakeladder.game;

import snakeladder.utility.ServicesRandom;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class DiceRoller {

    private int numDice;

    private int numRolls = 0;
    private int total = 0;

    private List<LinkedList<Integer>> dieValues = new ArrayList<>();

    DiceRoller(int numDice) {
        this.numDice = numDice;
    }

    void setupInitialDieValues(Properties properties, int numberOfPlayers) {
        for (int i = 0; i < numberOfPlayers; i++) {
            if (properties.getProperty("die_values." + i) != null) {
                LinkedList<Integer> dieValueForPlayer = new LinkedList<>();
                String dieValuesString = properties.getProperty("die_values." + i);
                String[] dieValueStrings = dieValuesString.split(",");
                for (String numString : dieValueStrings) {
                    dieValueForPlayer.add(Integer.parseInt(numString));
                }
                dieValues.add(dieValueForPlayer);

            } else {
                System.out.println("All players need to be set a die value for the full testing mode to run. " +
                        "Switching off the full testing mode");
                dieValues = null;
                break;
            }
        }
        System.out.println("dieValues = " + dieValues);
    }

    int getDieValues(int puppetNum) {
        if (dieValues == null) {
            return ServicesRandom.get().nextInt(6) + 1;
        }

        return dieValues.get(puppetNum).size() > 0 ? dieValues.get(puppetNum).removeFirst() :
                ServicesRandom.get().nextInt(6) + 1;
    }

    void registerRoll(int rollValue) {
        numRolls++;
        total += rollValue;
    }

    void resetValues() {
        numRolls = 0;
        total = 0;
    }

    int getNumDice() {
        return numDice;
    }

    int getNumRolls() {
        return numRolls;
    }

    int getTotal() {
        return total;
    }

}
