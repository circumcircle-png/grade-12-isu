package src;
import java.util.*;
public class SortByTime implements Comparator <Leaderboard>{
    public int compare(Leaderboard lb1, Leaderboard lb2){
        return lb1.getTime()-lb2.getTime();
    }
    
}