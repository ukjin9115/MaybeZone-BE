package ukgo.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ukgo.shop.entity.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByItemId(Integer itemId);
}
