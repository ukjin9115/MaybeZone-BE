package ukgo.shop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import ukgo.shop.entity.Comment;
import ukgo.shop.entity.User;
import ukgo.shop.repository.UserRepository;
import ukgo.shop.service.CommentDTO;
import ukgo.shop.service.CommentService;
import java.util.Map;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByItemId(@PathVariable Integer itemId) {
        List<CommentDTO> comments = commentService.getCommentsByItemId(itemId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @PostMapping("/create/{itemId}")
    public ResponseEntity<CommentDTO> createComment(@PathVariable Integer itemId, @RequestBody Map<String, String> requestBody) {
        String content = requestBody.get("content");
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        CommentDTO commentDTO = commentService.createComment(itemId, user.getId(), content);
        return new ResponseEntity<>(commentDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable Integer commentId, @RequestBody Map<String, String> requestBody) {
        String content = requestBody.get("content");
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        CommentDTO commentDTO = commentService.updateComment(commentId, content);
        return new ResponseEntity<>(commentDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer commentId) {
        commentService.deleteComment(commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
