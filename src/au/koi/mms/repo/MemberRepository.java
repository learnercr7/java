package au.koi.mms.repo;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import au.koi.mms.model.Member;

public interface MemberRepository {
    List<Member> findAll();
    void add(Member m);
    boolean update(Member m);
    boolean deleteById(String id);
    void clear();
    int size();

    void loadFromCsv(Path path) throws IOException;
    void saveToCsv(Path path) throws IOException;
}