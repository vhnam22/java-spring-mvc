package vn.hoidanit.laptopshop.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    // DI: dependency injection
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/")
    public String getHomePage(Model model) {
        List<User> userLst = userService.getAllUsersByEmail("admin@gmail.com");
        System.out.println(userLst);
        String test = this.userService.handleHello();
        model.addAttribute("eric", test);
        return "hello";
    }

    @RequestMapping("/admin/user/create")
    public String getUserPage(Model model) {
        model.addAttribute("newUser", new User());
        return "/admin/user/create";
    }

    @RequestMapping(value = "/admin/user/create", method = RequestMethod.POST)
    public String createUserPage(@ModelAttribute("newUser") User hoidanit) {
        System.out.println("run here" + hoidanit);
        this.userService.handleSaveUser(hoidanit);
        return "redirect:/admin/user";
    }

    @RequestMapping("/admin/user")
    public String userManage(Model model) {
        List<User> users = this.userService.getAllUsers();
        model.addAttribute("users", users);
        return "/admin/user/manage";
    }

    @RequestMapping("/admin/user/{id}")
    public String getUserDetailPage(Model model, @PathVariable long id) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "/admin/user/show";
    }

    @RequestMapping("/admin/user/update/{id}")
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
