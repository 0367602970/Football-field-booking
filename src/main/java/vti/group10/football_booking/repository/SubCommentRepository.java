package vti.group10.football_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vti.group10.football_booking.model.Comment;
import vti.group10.football_booking.model.SubComment;

import java.util.List;

@Repository
public interface SubCommentRepository extends JpaRepository<SubComment, Integer> {

    // Lấy tất cả subcomment theo comment gốc
    List<SubComment> findByComment(Comment comment);

    // Lấy tất cả subcomment theo user
    List<SubComment> findByUserId(Integer userId);
}

