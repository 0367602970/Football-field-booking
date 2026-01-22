package vti.group10.football_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vti.group10.football_booking.model.Comment;
import vti.group10.football_booking.model.FieldCluster;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    // Lấy tất cả comment theo field cluster
    List<Comment> findByFieldCluster(FieldCluster fieldCluster);

    // Có thể thêm tìm kiếm theo user nếu cần
    List<Comment> findByUserId(Integer userId);
}

