package vn.hoidanit.laptopshop.service;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import vn.hoidanit.laptopshop.domain.Cart;
import vn.hoidanit.laptopshop.domain.CartDetail;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.repository.CartDetailRepository;
import vn.hoidanit.laptopshop.repository.CartRepository;
import vn.hoidanit.laptopshop.repository.ProductRepository;

import java.net.http.HttpRequest;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;
    private final UserService userService;

    public ProductService(ProductRepository productRepository,
            CartRepository cartRepository, CartDetailRepository cartDetailRepository,
            UserService userService) {
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.cartDetailRepository = cartDetailRepository;
        this.userService = userService;
    }

    public void handleSaveProduct(Product product) {
        productRepository.save(product);
    }

    public List<Product> getAllProduct() {
        return this.productRepository.findAll();
    }

    public void handleDeleteProduct(long id) {
        this.productRepository.deleteById(id);
    }

    public Product getDetailProduct(long id) {
        return this.productRepository.findById(id);
    }

    public void handleAddProductToCart(String email, long productId, HttpSession session) {
        User user = userService.getUserByEmail(email);
        if (user != null) {
            Cart cart = cartRepository.findByUser(user);
            if (cart == null) {
                Cart newCart = new Cart();
                newCart.setUser(user);
                newCart.setSum(0);
                cart = cartRepository.save(newCart);
            }

            Product product = productRepository.findById(productId);
            if (product != null) {
                CartDetail cartDetail = cartDetailRepository.findByCartAndProduct(cart, product);
                if (cartDetail == null) {
                    int sumCart = cart.getSum() + 1;
                    cart.setSum(sumCart);
                    cart = cartRepository.save(cart);
                    session.setAttribute("sumCart", sumCart);

                    cartDetail = new CartDetail();
                    cartDetail.setCart(cart);
                    cartDetail.setProduct(product);
                    cartDetail.setQuantity(1);
                    cartDetail.setPrice(product.getPrice());
                    cartDetailRepository.save(cartDetail);
                } else {
                    cartDetail.setQuantity(cartDetail.getQuantity() + 1);
                    cartDetailRepository.save(cartDetail);
                }

            }
        }
    }

    public List<CartDetail> getAllCartDetail(HttpSession session) {
        String email = (String) session.getAttribute("email");
        User user = userService.getUserByEmail(email);
        Cart cart = user.getCart();
        return cartDetailRepository.findAllByCart(cart);
    }
}
