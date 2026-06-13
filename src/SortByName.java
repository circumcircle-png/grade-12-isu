package src;
import java.util.*;
public class SortByName implements Comparator <Leaderboard>{
    public int compare(Leaderboard lb1, Leaderboard lb2){
        return lb1.getName().compareTo(lb2.getName());
    }
    
}
