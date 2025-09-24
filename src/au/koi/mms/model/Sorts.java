package au.koi.mms.algorithms;
import java.util.*;
public final class Sorts {
  private Sorts(){}
  public static <T> void mergeSort(List<T> a, Comparator<T> c){
    if (a.size()<2) return;
    List<T> aux = new ArrayList<>(a);
    rec(a, aux, 0, a.size(), c);
  }
  private static <T> void rec(List<T> a, List<T> aux, int lo, int hi, Comparator<T> c){
    if (hi-lo<2) return;
    int m=(lo+hi)>>>1;
    rec(a,aux,lo,m,c); rec(a,aux,m,hi,c);
    int i=lo,j=m,k=lo;
    while(i<m && j<hi) aux.set(k++, c.compare(a.get(i),a.get(j))<=0 ? a.get(i++):a.get(j++));
    while(i<m) aux.set(k++, a.get(i++));
    while(j<hi) aux.set(k++, a.get(j++));
    for(k=lo;k<hi;k++) a.set(k, aux.get(k));
  }
  public static <T> void quickSort(List<T> a, Comparator<T> c){ qs(a,0,a.size()-1,c); }
  private static <T> void qs(List<T> a,int lo,int hi,Comparator<T> c){
    if(lo>=hi) return;
    int i=lo,j=hi; T p=a.get((lo+hi)>>>1);
    while(i<=j){ while(c.compare(a.get(i),p)<0)i++; while(c.compare(a.get(j),p)>0)j--; if(i<=j) Collections.swap(a,i++,j--); }
    if(lo<j) qs(a,lo,j,c); if(i<hi) qs(a,i,hi,c);
  }
}
