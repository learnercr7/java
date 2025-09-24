package au.koi.mms.ui;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import au.koi.mms.model.Member;
import au.koi.mms.model.PTMember;
import au.koi.mms.model.RegularMember;
import au.koi.mms.repo.InMemoryMemberRepository;
import au.koi.mms.repo.MemberRepository;
import au.koi.mms.service.MemberService;
import au.koi.mms.service.PerformanceService;

public class ConsoleApp {
    private final Scanner sc = new Scanner(System.in);
    private final MemberRepository repo = new InMemoryMemberRepository();
    private final MemberService memberService = new MemberService(repo);
    private final PerformanceService perfService = new PerformanceService(repo);
    private Path currentFile = Paths.get("data/member_data.csv");

    public void run(){
        while(true){
            printMenu();
            String choice = sc.nextLine().trim();
            switch(choice){
                case "1": loadCsv(); break;
                case "2": addMembersAndSave(); break;
                case "3": updateMemberAndSave(); break;
                case "4": deleteMemberAndSave(); break;
                case "5": viewQuerySubmenu(); break;
                case "6": generateLetters(); break;
                case "7": awardDiscounts(); break;
                case "8": System.out.println("Bye!"); return;
                default: System.out.println("Invalid option.");
            }
        }
    }

    private void printMenu(){
        System.out.println("\n===== Member Management System =====");
        System.out.println("Current CSV: " + currentFile.toAbsolutePath());
        System.out.println("Loaded records: " + repo.size());
        System.out.println("1. Load records from a CSV file");
        System.out.println("2. Add new members and SAVE to a NEW file");
        System.out.println("3. Update member information and SAVE to a NEW file");
        System.out.println("4. Delete member and SAVE to a NEW file");
        System.out.println("5. View / Query / Sort / Search members");
        System.out.println("6. Generate appreciation/reminder letters");
        System.out.println("7. Award discounts to goal achievers and SAVE");
        System.out.println("8. Exit");
        System.out.print("Choose an option: ");
    }

    private void loadCsv(){
        try{
            System.out.print("Enter CSV path (default data/member_data.csv): ");
            String p = sc.nextLine().trim();
            if(!p.isEmpty()) currentFile = Paths.get(p);
            repo.clear();
            repo.loadFromCsv(currentFile);
            System.out.println("Loaded "+repo.size()+" records from: "+currentFile.toAbsolutePath());

            if (repo.size() > 0) {
                System.out.println("\n--- Member Records ---");
                for (Member m : repo.findAll()) {
                    System.out.println(m + " | GoalAchieved=" + m.isGoalAchieved()
                            + " | Discount=" + m.getDiscountPercent() + "%");
                }
                System.out.println("----------------------");
            }
        }catch(IOException e){
            System.out.println("Error loading file: "+e.getMessage());
        }
    }

    private boolean ensureLoaded(){
        if (repo.size() > 0) return true;
        try {
            repo.clear();
            repo.loadFromCsv(currentFile);
            System.out.println("Auto-loaded "+repo.size()+" records from: "+currentFile.toAbsolutePath());
            return repo.size() > 0;
        } catch (IOException e) {
            System.out.println("No records loaded. Use option 1 first. ("+e.getMessage()+")");
            return false;
        }
    }

    private void addMembersAndSave(){
        while(true){
            System.out.print("ID (blank to stop): "); String id = sc.nextLine().trim();
            if(id.isEmpty()) break;

            System.out.print("Name: "); String name = sc.nextLine();
            System.out.print("Type (REGULAR/PT): "); String type = sc.nextLine().trim().toUpperCase();
            System.out.print("Email: "); String email = sc.nextLine();
            System.out.print("Phone: "); String phone = sc.nextLine();
            double base= askDouble("Base fee: ");
            int rating= askInt("Performance rating (1-5): ");
            boolean goal= askBool("Goal achieved? (true/false): ");
            int disc= askInt("Discount percent (0-100): ");

            Member m = type.equals("PT")
                    ? new PTMember(id,name,email,phone,base,rating,goal,disc)
                    : new RegularMember(id,name,email,phone,base,rating,goal,disc);
            repo.add(m);
            System.out.println("Added: "+m);
        }
        saveToNewFile();
    }

    private void updateMemberAndSave(){
        if (!ensureLoaded()) return;
        System.out.print("Enter member ID to update: "); String id = sc.nextLine().trim();
        java.util.Optional<Member> opt = memberService.findById(id);
        if(!opt.isPresent()){ System.out.println("Not found."); return; }
        Member m = opt.get();

        System.out.print("New name (blank keep): "); String name = sc.nextLine(); if(!name.trim().isEmpty()) m.setName(name);
        System.out.print("New email (blank keep): "); String email = sc.nextLine(); if(!email.trim().isEmpty()) m.setEmail(email);
        System.out.print("New phone (blank keep): "); String phone = sc.nextLine(); if(!phone.trim().isEmpty()) m.setPhone(phone);
        String bf = askOptional("New base fee (blank keep): "); if(!bf.trim().isEmpty()) m.setBaseFee(Double.parseDouble(bf));
        String pr = askOptional("New performance rating 1-5 (blank keep): "); if(!pr.trim().isEmpty()) m.setPerformanceRating(Integer.parseInt(pr));
        String ga = askOptional("Goal achieved true/false (blank keep): "); if(!ga.trim().isEmpty()) m.setGoalAchieved(Boolean.parseBoolean(ga));
        String dc = askOptional("Discount 0-100 (blank keep): "); if(!dc.trim().isEmpty()) m.setDiscountPercent(Integer.parseInt(dc));

        if(repo.update(m)) System.out.println("Updated: "+m); else System.out.println("Update failed.");
        saveToNewFile();
    }

    private void deleteMemberAndSave(){
        if (!ensureLoaded()) return;
        System.out.print("Enter member ID to delete: ");
        String id = sc.nextLine().trim();
        if(repo.deleteById(id)) System.out.println("Deleted."); else System.out.println("ID not found.");
        saveToNewFile();
    }

    private void viewQuerySubmenu(){
        if (!ensureLoaded()) return;
        System.out.println("\n-- View/Query/Sort/Search --");
        System.out.println("a) Find by ID");
        System.out.println("b) Find by Name contains");
        System.out.println("c) Find by Performance rating");
        System.out.println("d) Sort by Name (MergeSort)");
        System.out.println("e) Sort by Fee (QuickSort)");
        System.out.println("f) Binary Search by ID");
        System.out.print("Choose: "); String c = sc.nextLine().trim().toLowerCase();

        switch(c){
            case "a": {
                System.out.print("Enter ID: "); String id = sc.nextLine();
                java.util.Optional<Member> fm = memberService.findById(id);
                if (fm.isPresent()) { System.out.println(fm.get()); System.out.println("Fee: $"+fm.get().calculateFee()); }
                else { System.out.println("Not found."); }
                break;
            }
            case "b": {
                System.out.print("Enter name part: "); String q = sc.nextLine();
                List<Member> byName = memberService.findByNameContains(q);
                byName.forEach(System.out::println);
                System.out.println("Found: "+byName.size());
                break;
            }
            case "c": {
                int r = askInt("Enter rating (1-5): ");
                List<Member> byPerf = memberService.findByPerformance(r);
                byPerf.forEach(System.out::println);
                System.out.println("Found: "+byPerf.size());
                break;
            }
            case "d": {
                java.util.List<Member> sorted = memberService.sortByNameMerge();
                sorted.forEach(System.out::println);
                System.out.println("Total: "+sorted.size());
                break;
            }
            case "e": {
                java.util.List<Member> sorted = memberService.sortByFeeQuick();
                sorted.forEach(System.out::println);
                System.out.println("Total: "+sorted.size());
                break;
            }
            case "f": {
                System.out.print("Enter ID to search: "); String id = sc.nextLine();
                java.util.Optional<Member> res = memberService.binarySearchById(id);
                System.out.println(res.isPresent() ? res.get() : "Not found.");
                break;
            }
            default:
                System.out.println("Invalid choice.");
        }
    }

    private void generateLetters(){
        if (!ensureLoaded()) return;
        perfService.enqueueMonthlyLetters();
        java.util.List<au.koi.mms.model.Letter> letters = perfService.processAll();
        System.out.println("Generated "+letters.size()+" letters:");
        letters.forEach(l -> System.out.println(" - "+l.summaryLine()));

        System.out.print("Save letters to folder? (y/n): ");
        String ans = sc.nextLine().trim();
        if(ans.equalsIgnoreCase("y")){
            try{
                perfService.enqueueMonthlyLetters(); // requeue
                perfService.saveLettersToFolder("data/letters");
                System.out.println("Saved to data/letters/");
            }catch(IOException e){ System.out.println("Save error: "+e.getMessage()); }
        }
    }

    private void awardDiscounts(){
        if (!ensureLoaded()) return;
        int applied = 0;
        for (Member m : repo.findAll()){
            if (m.isGoalAchieved() && m.getDiscountPercent() < 10){
                m.setDiscountPercent(10); // policy: 10% minimum for achievers
                applied++;
            }
        }
        System.out.println("Awarded discounts to "+applied+" members.");
        saveToNewFile();
    }

    private void saveToNewFile(){
        try{
            System.out.print("Enter NEW output CSV (e.g., data/members_v2.csv): ");
            String out = sc.nextLine().trim();
            if(out.isEmpty()){ System.out.println("Skipped save."); return; }
            repo.saveToCsv(Paths.get(out));
            System.out.println("Saved "+repo.size()+" records to: "+Paths.get(out).toAbsolutePath());
        }catch(IOException e){
            System.out.println("Save error: "+e.getMessage());
        }
    }

    // --- input helpers ---
    private double askDouble(String msg){
        while(true){
            try{ System.out.print(msg); return Double.parseDouble(sc.nextLine().trim()); }
            catch(Exception e){ System.out.println("Please enter a number."); }
        }
    }
    private int askInt(String msg){
        while(true){
            try{ System.out.print(msg); return Integer.parseInt(sc.nextLine().trim()); }
            catch(Exception e){ System.out.println("Please enter an integer."); }
        }
    }
    private boolean askBool(String msg){
        while(true){
            System.out.print(msg);
            String s = sc.nextLine().trim().toLowerCase();
            if(s.equals("true")||s.equals("t")||s.equals("yes")||s.equals("y")) return true;
            if(s.equals("false")||s.equals("f")||s.equals("no")||s.equals("n")) return false;
            System.out.println("Type true/false or y/n.");
        }
    }
    private String askOptional(String msg){ System.out.print(msg); return sc.nextLine().trim(); }
}
