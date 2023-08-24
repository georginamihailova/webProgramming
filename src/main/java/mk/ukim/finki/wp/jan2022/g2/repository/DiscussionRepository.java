package mk.ukim.finki.wp.jan2022.g2.repository;

import mk.ukim.finki.wp.jan2022.g2.model.Discussion;
import mk.ukim.finki.wp.jan2022.g2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DiscussionRepository  extends JpaRepository<Discussion,Long> {
    List<Discussion> findAllByParticipantsContaining(User participant);
    List<Discussion> findAllByDueDateBefore(LocalDate localDate);
    List<Discussion> findAllByDueDateBeforeAndParticipantsContaining(User user,LocalDate localDate);
}
