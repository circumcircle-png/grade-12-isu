package src;
import java.util.*;
public class SortByName implements Comparator <Leaderboard>{
    //Description: sort by name in ascending order, for sorting and searching
    // parameters: two leaderboard objects
    // return: int for Collections
    public int compare(Leaderboard lb1, Leaderboard lb2){
        return lb1.getName().compareToIgnoreCase(lb2.getName());
    }
    
}
