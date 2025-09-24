package au.koi.mms.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

import au.koi.mms.model.Letter;
import au.koi.mms.model.LetterType;
import au.koi.mms.model.Member;
import au.koi.mms.repo.MemberRepository;

/** Builds monthly appreciation/reminder letters from current repository state. */
public class PerformanceService {
    private final MemberRepository repo;
    private final Deque<Letter> queue = new ArrayDeque<>();

    public PerformanceService(MemberRepository repo){ this.repo = repo; }

    /** Rebuild the queue for the current month from all members. */
    public void enqueueMonthlyLetters(){
        queue.clear();
        for (Member m : repo.findAll()){
            double fee = m.calculateFee();
            if (m.isGoalAchieved()){
                String body = "Congratulations " + m.getName()
                        + "! You achieved your goal this month.\n"
                        + "Your next month's fee is $" + fee + ". Keep it up!";
                queue.add(new Letter(m.getId(), m.getName(), LetterType.APPRECIATION, body));
            } else {
                String body = "Hi " + m.getName()
                        + ", keep going toward your goal. Book a session to stay on track.\n"
                        + "Your projected fee is $" + fee + ".";
                queue.add(new Letter(m.getId(), m.getName(), LetterType.REMINDER, body));
            }
        }
    }

    /** Returns all queued letters and clears the queue. */
    public List<Letter> processAll(){
        List<Letter> out = new ArrayList<>(queue);
        queue.clear();
        return out;
    }

    /** Peek letters without clearing. */
    public List<Letter> getQueuedSnapshot(){
        return new ArrayList<Letter>(queue);
    }

    /** Save queued letters as individual .txt files. Does NOT clear the queue. */
    public void saveLettersToFolder(String folder) throws IOException {
        Path dir = Paths.get(folder);
        Files.createDirectories(dir);
        LocalDate today = LocalDate.now();

        for (Letter l : queue){
            String fileName = String.format(
                    "%s_%s_%s.txt",
                    sanitize(l.getMemberId()),
                    l.getType().name().toLowerCase(),
                    today
            );
            Path p = dir.resolve(fileName);
            Files.write(p, l.toString().getBytes(StandardCharsets.UTF_8));
        }
    }

    public int queuedCount(){ return queue.size(); }

    // --- helpers ---
    private static String sanitize(String s){
        return s.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
