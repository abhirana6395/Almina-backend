package com.almina.ecommerce.config;

import com.almina.ecommerce.entity.Cart;
import com.almina.ecommerce.entity.Category;
import com.almina.ecommerce.entity.Product;
import com.almina.ecommerce.entity.Role;
import com.almina.ecommerce.entity.User;
import com.almina.ecommerce.repository.CartRepository;
import com.almina.ecommerce.repository.CategoryRepository;
import com.almina.ecommerce.repository.ProductRepository;
import com.almina.ecommerce.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner seedData() {
        return args -> {
            seedAdmin();
            seedCatalog();
        };
    }

    private void seedAdmin() {
        if (userRepository.existsByEmail("admin@almina.com")) {
            return;
        }
        User admin = new User();
        admin.setFullName("ALMINA Admin");
        admin.setEmail("admin@almina.com");
        admin.setPassword(passwordEncoder.encode("Admin@123"));
        admin.setActive(true);
        admin.setRole(Role.ADMIN);
        User saved = userRepository.save(admin);

        Cart cart = new Cart();
        cart.setUser(saved);
        cartRepository.save(cart);
    }

    private void seedCatalog() {
        if (categoryRepository.count() > 0 || productRepository.count() > 0) {
            return;
        }

        Category women = createCategory("Women", "women",
                "https://images.unsplash.com/photo-1483985988355-763728e1935b?auto=format&fit=crop&w=1200&q=80",
                "Refined silhouettes and elevated daily staples.");
        Category men = createCategory("Men", "men",
                "https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=1200&q=80",
                "Modern tailoring, layering essentials, and statement outerwear.");
        Category accessories = createCategory("Accessories", "accessories",
                "https://images.unsplash.com/photo-1523170335258-f5ed11844a49?auto=format&fit=crop&w=1200&q=80",
                "Finishing touches that complete the wardrobe.");

        productRepository.save(createProduct("Sculpted Wool Coat", "ALM-W-001", women, new BigDecimal("8999"), new BigDecimal("7499"),
                List.of("https://images.unsplash.com/photo-1541099649105-f69ad21f3246?auto=format&fit=crop&w=1200&q=80"),
                List.of("S", "M", "L"), List.of("Ivory", "Black"), true, true));
        productRepository.save(createProduct("Structured Linen Set", "ALM-W-002", women, new BigDecimal("6999"), new BigDecimal("5999"),
                List.of("https://images.unsplash.com/photo-1496747611176-843222e1e57c?auto=format&fit=crop&w=1200&q=80"),
                List.of("S", "M", "L"), List.of("Sand", "Olive"), true, false));
        productRepository.save(createProduct("Tailored Utility Jacket", "ALM-M-001", men, new BigDecimal("7999"), new BigDecimal("6799"),
                List.of("https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=1200&q=80"),
                List.of("M", "L", "XL"), List.of("Slate", "Stone"), false, true));
        productRepository.save(createProduct("Signature Leather Tote", "ALM-A-001", accessories, new BigDecimal("4999"), null,
                List.of("https://images.unsplash.com/photo-1524504388940-b1c1722653e1?auto=format&fit=crop&w=1200&q=80"),
                List.of("One Size"), List.of("Tan", "Black"), true, true));
    }

    private Category createCategory(String name, String slug, String imageUrl, String description) {
        Category category = new Category();
        category.setName(name);
        category.setSlug(slug);
        category.setImageUrl(imageUrl);
        category.setDescription(description);
        return categoryRepository.save(category);
    }

    private Product createProduct(String name, String sku, Category category, BigDecimal price, BigDecimal discountPrice,
                                  List<String> images, List<String> sizes, List<String> colors, boolean featured, boolean trending) {
        Product product = new Product();
        product.setName(name);
        product.setSku(sku);
        product.setDescription("A premium ALMINA staple designed with sharp lines, rich texture, and all-day ease.");
        product.setPrice(price);
        product.setDiscountPrice(discountPrice);
        product.setStockQuantity(30);
        product.setGender("Unisex");
        product.setFeatured(featured);
        product.setTrending(trending);
        product.setCategory(category);
        product.setImages(images);
        product.setSizes(sizes);
        product.setColors(colors);
        return product;
    }
}
