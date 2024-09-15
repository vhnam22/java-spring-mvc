package vn.hoidanit.laptopshop.controller.client;

import java.net.http.HttpRequest;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import vn.hoidanit.laptopshop.domain.CartDetail;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.domain.dto.RegisterDTO;
import vn.hoidanit.laptopshop.service.ProductService;
import vn.hoidanit.laptopshop.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomePageController {

    private final ProductService productService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public HomePageController(ProductService productService,
            UserService userService,
            PasswordEncoder passwordEncoder) {
        this.productService = productService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String GetMapping(Model model) {
        List<Product> products = productService.getAllProduct();
        model.addAttribute("products", products);
        return "client/homepage/show";
    }

    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        model.addAttribute("registerUser", new RegisterDTO());
        return "client/auth/register";
    }

    @PostMapping("/register")
    public String postRegisterUse(@ModelAttribute("registerUser") @Valid RegisterDTO registerUser,
            BindingResult registerUserBindingResult) {
        if (registerUserBindingResult.hasErrors()) {
            return "client/auth/register";
        }
        User user = userService.registerDTOToUser(registerUser);
        String hashPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        user.setRole(userService.getRoleByName("USER"));

        userService.handleSaveUser(user);

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String getLoginPage(Model model) {
        return "client/auth/login";
    }

    @GetMapping("/access-deny")
    public String getDenyPage() {
        return "client/auth/deny";
    }

    @GetMapping("/cart")
    public String getCartPage(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        List<CartDetail> cartDetails = productService.getAllCartDetail(session);
        model.addAttribute("cartDetails", cartDetails);
        double total = 0;
        for (CartDetail cartDetail : cartDetails) {
            total += cartDetail.getQuantity() * cartDetail.getPrice();
        }
        model.addAttribute("subTotal", total);
        return "client/cart/show";
    }

}
