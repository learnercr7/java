package au.koi.mms.algorithms;
import java.util.*;
public final class Searches {
  private Searches(){}
  public static <T> int binarySearch(List<T> a, T key, Comparator<T> c){
    int lo=0, hi=a.size()-1;
    while(lo<=hi){
      int m=(lo+hi)>>>1, cmp=c.compare(a.get(m), key);
      if(cmp==0) return m;
      if(cmp<0) lo=m+1; else hi=m-1;
    }
    return -1;
  }
}
