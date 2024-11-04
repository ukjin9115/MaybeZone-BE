package ukgo.shop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ukgo.shop.entity.ShopItem;
import ukgo.shop.entity.User;
import ukgo.shop.repository.ItemRepository;
import ukgo.shop.repository.UserRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/items")
public class ShopController {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private static final String UPLOAD_DIR = "C:/shopupload/";

    @PostMapping
    public ResponseEntity<ShopItem> addItem(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("price") Integer price,
            @RequestParam("description") String description) {

        String filePath = null;
        if (file != null && !file.isEmpty()) {
            try {
                filePath = saveFile(file);
            } catch (IOException e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User author = userRepository.findByUsername(username).orElse(null);

        ShopItem shopItem = new ShopItem();
        shopItem.setTitle(title);
        shopItem.setPrice(price);
        shopItem.setDescription(description);
        shopItem.setFilePath(filePath);
        shopItem.setAuthor(author);

        ShopItem savedShopItem = itemRepository.save(shopItem);
        return new ResponseEntity<>(savedShopItem, HttpStatus.CREATED);
    }

    private String saveFile(MultipartFile file) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        File dest = new File(uploadDir, fileName);
        file.transferTo(dest);
        return "uploads/" + fileName;
    }

    @GetMapping
    public ResponseEntity<List<ShopItem>> getAllItems() {
        List<ShopItem> shopItems = itemRepository.findAll();
        return new ResponseEntity<>(shopItems, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShopItem> getItemById(@PathVariable int id) {
        Optional<ShopItem> item = itemRepository.findById(id);
        return item.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(
            @PathVariable int id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("price") Integer price,
            @RequestParam("description") String description) {

        Optional<ShopItem> itemOptional = itemRepository.findById(id);
        if (!itemOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ShopItem shopItem = itemOptional.get();
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User currentUser = userRepository.findByUsername(username).orElse(null);

        if (currentUser == null || !shopItem.getAuthor().getId().equals(currentUser.getId())) {
            return new ResponseEntity<>("You are not the author of this item", HttpStatus.FORBIDDEN);
        }

        shopItem.setTitle(title);
        shopItem.setPrice(price);
        shopItem.setDescription(description);

        if (file != null && !file.isEmpty()) {
            try {
                String filePath = saveFile(file);
                shopItem.setFilePath(filePath);
            } catch (IOException e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        ShopItem updatedShopItem = itemRepository.save(shopItem);
        return new ResponseEntity<>(updatedShopItem, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable int id) {
        Optional<ShopItem> itemOptional = itemRepository.findById(id);
        if (!itemOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ShopItem shopItem = itemOptional.get();
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User currentUser = userRepository.findByUsername(username).orElse(null);

        if (currentUser == null || !shopItem.getAuthor().getId().equals(currentUser.getId())) {
            return new ResponseEntity<>("You are not the author of this item", HttpStatus.FORBIDDEN);
        }

        itemRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ShopItem>> searchItems(@RequestParam("title") String title) {
        List<ShopItem> items = itemRepository.searchItems(title);
        return new ResponseEntity<>(items, HttpStatus.OK);
    }
}
