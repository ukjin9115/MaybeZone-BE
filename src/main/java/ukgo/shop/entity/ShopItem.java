package ukgo.shop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "shop_items")
@NoArgsConstructor
@Data
@Getter
@Setter
@ToString
public class ShopItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer price;

    @Lob
    private String description;

    @Column(updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    private String filePath;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author; // Add this field
}
