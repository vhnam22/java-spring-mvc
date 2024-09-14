package vn.hoidanit.laptopshop.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.validation.Valid;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.service.ProductService;
import vn.hoidanit.laptopshop.service.UploadService;

import org.springframework.web.bind.annotation.PostMapping;
import java.util.List;

@Controller
public class ProductController {

    private final UploadService uploadService;
    private final ProductService productService;

    public ProductController(UploadService uploadService,
            ProductService productService) {
        this.uploadService = uploadService;
        this.productService = productService;
    }

    @GetMapping("/admin/product")
    public String getAllProduct(Model model) {
        List<Product> products = productService.getAllProduct();
        model.addAttribute("products", products);
        return "admin/product/show";
    }

    @GetMapping("/admin/product/create")
    public String getCreateProductPage(Model model) {
        model.addAttribute("newProduct", new Product());
        return "admin/product/create";
    }

    @PostMapping("/admin/product/create")
    public String handleCreateProduct(@ModelAttribute("newProduct") @Valid Product product,
            BindingResult newProductBindingResult,
            @RequestParam("imgFile") MultipartFile file) {
        if (newProductBindingResult.hasErrors()) {
            return "admin/product/create";
        }
        String image = uploadService.handleSaveUploadFile(file, "product");
        product.setImage(image);
        productService.handleSaveProduct(product);
        return "redirect:/admin/product";
    }

    @GetMapping("/admin/product/delete/{id}")
    public String handleDeleteProduct(@PathVariable long id, Model model) {
        model.addAttribute("id", id);
        return "admin/product/delete";
    }

    @GetMapping("/admin/product/confirmDelete/{id}")
    public String confirmDeleteProduct(@PathVariable long id) {
        productService.handleDeleteProduct(id);
        return "redirect:/admin/product";
    }

    @GetMapping("/admin/product/{id}")
    public String detailProduct(@PathVariable long id, Model model) {
        Product product = productService.getDetailProduct(id);
        model.addAttribute("product", product);
        return "admin/product/detail";
    }

    @GetMapping("/admin/product/update/{id}")
    public String getUpdateProductPage(@PathVariable long id, Model model) {
        Product product = productService.getDetailProduct(id);
        model.addAttribute("newProduct", product);
        return "admin/product/update";
    }

    @PostMapping("/admin/product/update")
    public String postMethodName(@ModelAttribute("newProduct") @Valid Product newProduct,
            BindingResult newProductBindingResult, @RequestParam("imgFile") MultipartFile file) {
        if (newProductBindingResult.hasErrors()) {
            return "admin/product/update";
        }

        Product product = productService.getDetailProduct(newProduct.getId());
        if (product != null) {
            if (!file.isEmpty()) {
                String image = uploadService.handleSaveUploadFile(file, "product");
                product.setImage(image);
            } else {

            }

            product.setName(newProduct.getName());
            product.setDetailDesc(newProduct.getDetailDesc());
            product.setShortDesc(newProduct.getShortDesc());
            product.setPrice(newProduct.getPrice());
            product.setQuantity(newProduct.getQuantity());
            product.setSold(newProduct.getSold());
            product.setFactory(newProduct.getFactory());
            product.setTarget(newProduct.getTarget());

            productService.handleSaveProduct(product);
        }

        return "redirect:/admin/product";
    }

}
