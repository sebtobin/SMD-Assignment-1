package snakeladder.game;

import java.util.HashMap;
import java.util.Map;

public class StatTracker
{
    private final HashMap<Integer, Integer> rollTracker;
    private final HashMap<String, Integer> traversalTracker;
    private final String[] traversalDirections = {"up", "down"};
    private final int NUM_SIDE_DIE = 6;
    public StatTracker(int numDice){
        this.rollTracker = new HashMap<>();
        for (int i = numDice; i <= numDice * NUM_SIDE_DIE; i++){
            rollTracker.put(i, 0);
        }
        this.traversalTracker = new HashMap<>();
        for (String direction : traversalDirections){
            traversalTracker.put(direction, 0);
        }
    }

    void addRoll(int roll){
        rollTracker.put(roll, rollTracker.get(roll)+1);
    }

    void addTraversal(String direction){
        traversalTracker.put(direction, traversalTracker.get(direction) + 1);
    }
    void printStats(String puppetName){
        printRolls(puppetName);
        printTraversals(puppetName);
    }
    private void printRolls(String puppetName){
        int numPosRolls = rollTracker.size(), i = 1;
        System.out.print(puppetName + " rolled: ");
        printTracker(rollTracker, numPosRolls);
    }

    private void printTraversals(String puppetName){
        System.out.print(puppetName+" traversed: ");
        int i = 1;
        printTracker(traversalTracker, traversalDirections.length);
    }
    // checks whether or not to print a comma and space to separate values
    private void printSeparator(int curr, int max){
        if (curr < max){
            System.out.print(", ");
        }
    }
    // prints the information of a specific tracker
    private void printTracker(Map<?, ?> tracker, int max){
        int i = 1;
        for (Map.Entry entry : tracker.entrySet()){
            System.out.print(entry.getKey()+"-"+entry.getValue());
            printSeparator(i, max);
            i++;
        }
        System.out.println();
    }
}
