package au.koi.mms.service;

import au.koi.mms.algorithms.Sorts;
import au.koi.mms.algorithms.Searches;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.Optional;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Collections;

import au.koi.mms.model.Member;
import au.koi.mms.repo.MemberRepository;

public class MemberService {
    private final MemberRepository repo;
    public MemberService(MemberRepository repo){ this.repo = repo; }

    public Optional<Member> findById(String id){
        if(id==null) return Optional.empty();
        return repo.findAll().stream().filter(m -> m.getId().equalsIgnoreCase(id.trim())).findFirst();
    }

    public List<Member> findByNameContains(String namePart){
        if(namePart==null||namePart.trim().isEmpty()) return Collections.emptyList();
        String n = namePart.toLowerCase();
        return repo.findAll().stream()
                .filter(m -> m.getName()!=null && m.getName().toLowerCase().contains(n))
                .collect(Collectors.toList());
    }

    public List<Member> findByPerformance(int rating){
        return repo.findAll().stream().filter(m -> m.getPerformanceRating()==rating).collect(Collectors.toList());
    }

    public double calculateFeeFor(String id){ return findById(id).map(Member::calculateFee).orElse(-1.0); }

    public void applyDiscount(String id, int percent){
        findById(id).ifPresent(m -> m.setDiscountPercent(Math.max(0, Math.min(100, percent))));
    }
    public java.util.List<au.koi.mms.model.Member> sortByNameMerge(){
    	  ArrayList<Member> copy = new ArrayList<>(repo.findAll());
    	  Sorts.mergeSort(copy, Comparator.comparing(m -> m.getName().toLowerCase()));
    	  return copy;
    	}
    	public java.util.List<au.koi.mms.model.Member> sortByFeeQuick(){
    	  ArrayList<Member> copy = new ArrayList<>(repo.findAll());
    	  Sorts.quickSort(copy, Comparator.comparingDouble(au.koi.mms.model.Member::calculateFee));
    	  return copy;
    	}
    	public Optional<au.koi.mms.model.Member> binarySearchById(String id){
    	  ArrayList<Member> copy = new ArrayList<>(repo.findAll());
    	  Sorts.mergeSort(copy, Comparator.comparing(m -> m.getId().toLowerCase()));
    	  int idx = Searches.binarySearch(copy, dummy(id), Comparator.comparing(m -> m.getId().toLowerCase()));
    	  return idx>=0 ? Optional.of(copy.get(idx)) : Optional.empty();
    	}
    	private au.koi.mms.model.Member dummy(String id){
    	  return new au.koi.mms.model.RegularMember(id,"","","",0,0,false,0);
    	}

}
