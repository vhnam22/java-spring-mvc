package vn.hoidanit.laptopshop.controller.admin;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.service.UploadService;
import vn.hoidanit.laptopshop.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    // DI: dependency injection
    private final UserService userService;
    private final UploadService uploadService;
    private PasswordEncoder PasswordEncoder;

    public UserController(UserService userService, UploadService uploadService,
            PasswordEncoder PasswordEncoder) {
        this.userService = userService;
        this.uploadService = uploadService;
        this.PasswordEncoder = PasswordEncoder;
    }

    @GetMapping("/")
    public String getHomePage(Model model) {
        List<User> userLst = userService.getAllUsersByEmail("admin@gmail.com");
        System.out.println(userLst);
        String test = this.userService.handleHello();
        model.addAttribute("eric", test);
        return "hello";
    }

    @GetMapping("/admin/user/create")
    public String getUserPage(Model model) {
        model.addAttribute("newUser", new User());
        return "/admin/user/create";
    }

    @PostMapping("/admin/user/create")
    public String createUserPage(@ModelAttribute("newUser") User hoidanit,
            @RequestParam("imgFile") MultipartFile file) {

        String avatar = uploadService.handleSaveUploadFile(file, "avatar");
        String hashPassword = this.PasswordEncoder.encode(hoidanit.getPassword());
        hoidanit.setAvatar(avatar);
        hoidanit.setPassword(hashPassword);
        hoidanit.setRole(this.userService.getRoleByName(hoidanit.getRole().getName()));
        this.userService.handleSaveUser(hoidanit);
        return "redirect:/admin/user";
    }

    @GetMapping("/admin/user")
    public String userManage(Model model) {
        List<User> users = this.userService.getAllUsers();
        model.addAttribute("users", users);
        return "/admin/user/show";
    }

    @GetMapping("/admin/user/{id}")
    public String getUserDetailPage(Model model, @PathVariable long id) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "/admin/user/detail";
    }

    @GetMapping("/admin/user/update/{id}")
    public String getUpdateUser(@PathVariable long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("newUser", user);
        return "/admin/user/update";
    }

    @PostMapping("admin/user/update")
    public String postUpdateUserPage(@ModelAttribute("newUser") User newUser) {
        User user = userService.getUserById(newUser.getId());
        if (user != null) {
            user.setPassword(newUser.getPassword());
            user.setFullName(newUser.getFullName());
            user.setAddress(newUser.getAddress());
            user.setPhone(newUser.getPhone());
            userService.handleSaveUser(user);
        }
        return "redirect:/admin/user/" + user.getId();
    }

    @GetMapping("admin/user/delete/{id}")
    public String getDeleteUserPage(@PathVariable long id, Model model) {
        model.addAttribute("id", id);
        return "/admin/user/delete";
    }

    @GetMapping("admin/user/confirmDelete/{id}")
    public String getMethodName(@PathVariable long id) {
        userService.deleteUserById(id);
        return "redirect:/admin/user";
    }

}

// @RestController
// public class UserController {

// // DI: dependency injection
// private UserService userService;

// public UserController(UserService userService) {
// this.userService = userService;
// }

// @GetMapping("/")
// public String getHomePage() {
// return this.userService.handleHello();
// }
// }
