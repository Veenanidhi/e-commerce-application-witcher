package com.witcher.e_commerce.application.witcher.controller.admin;


import com.witcher.e_commerce.application.witcher.dao.CategoryRepository;
import com.witcher.e_commerce.application.witcher.dao.OrderRepository;
import com.witcher.e_commerce.application.witcher.dto.ProductDTO;
import com.witcher.e_commerce.application.witcher.entity.Category;
import com.witcher.e_commerce.application.witcher.entity.Coupon;
import com.witcher.e_commerce.application.witcher.entity.Orders;
import com.witcher.e_commerce.application.witcher.entity.Product;
import com.witcher.e_commerce.application.witcher.service.category.CategoryService;
import com.witcher.e_commerce.application.witcher.service.coupon.CouponService;
import com.witcher.e_commerce.application.witcher.service.dashboard.DashboardService;
import com.witcher.e_commerce.application.witcher.service.order.OrderService;
import com.witcher.e_commerce.application.witcher.service.product.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@Slf4j
@RequestMapping("/admin")
public class AdminController {

    private final CategoryService categoryService;

    private final ProductService productService;

    private final OrderService orderService;

    private final DashboardService dashboardService;

    private final OrderRepository orderRepository;

    private final CategoryRepository categoryRepository;

    private final CouponService couponService;

    public static String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/productImage";
    @Autowired
    public AdminController(CategoryService categoryService, ProductService productService, OrderService orderService, DashboardService dashboardService, OrderRepository orderRepository, CategoryRepository categoryRepository, CouponService couponService) {
        this.categoryService = categoryService;
        this.productService = productService;
        this.orderService = orderService;
        this.dashboardService = dashboardService;
        this.orderRepository = orderRepository;
        this.categoryRepository = categoryRepository;
        this.couponService = couponService;
    }


    @GetMapping("/dashboard")
    public String adminHome(
            @RequestParam(value = "filter", defaultValue = "daily") String filter,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            Model model) {

        LocalDate today = LocalDate.now();
        Date start = null;
        Date end = null;

        try {
            if ("daily".equals(filter)) {
                start = java.sql.Date.valueOf(today);
                end = java.sql.Date.valueOf(today.plusDays(1));
            } else if ("weekly".equals(filter)) {
                start = java.sql.Date.valueOf(today.minusDays(7));
                end = java.sql.Date.valueOf(today.plusDays(1));
            } else if ("monthly".equals(filter)) {
                start = java.sql.Date.valueOf(today.withDayOfMonth(1));
                end = java.sql.Date.valueOf(today.plusMonths(1).withDayOfMonth(1));
            } else if ("custom".equals(filter)) {
                if (startDate != null && endDate != null) {
                    start = java.sql.Date.valueOf(LocalDate.parse(startDate));
                    end = java.sql.Date.valueOf(LocalDate.parse(endDate).plusDays(1)); // Include end date
                } else {
                    throw new IllegalArgumentException("Custom date range requires both startDate and endDate.");
                }
            }

            Integer salesCount = orderRepository.findSalesWithinDateRange(start, end);
            Double revenue = orderRepository.findRevenueWithinDateRange(start, end);

            model.addAttribute("filter", filter);
            model.addAttribute("salesCount", salesCount);
            model.addAttribute("revenue", revenue);
            model.addAttribute("totalUsers", dashboardService.getTotalUsers());
            model.addAttribute("todaySales", dashboardService.getTodaySalesCount());
            model.addAttribute("todayRevenue", dashboardService.getTodayRevenue());
            model.addAttribute("totalRevenue", dashboardService.getTotalRevenue());
            model.addAttribute("mostSoldItems", dashboardService.getMostSoldItems());
            model.addAttribute("monthlyRevenue", dashboardService.getMonthlyRevenue());
            model.addAttribute("topCategories", dashboardService.getTopSellingCategories());
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error fetching the data. Please verify the input.");
            e.printStackTrace();
        }

        return "admin-page";
    }


    // Category Section//




    @GetMapping("/categories")
    public String showCategory(Model model,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC,"id"));
        Page<Category> categoryPage = categoryService.findAllPaginated(pageable);
        System.out.println("pagination called");
        model.addAttribute("categories", categoryPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", categoryPage.getTotalPages());
        model.addAttribute("totalItems", categoryPage.getTotalElements());
        model.addAttribute("size", size);

        return "categories";
    }


    @GetMapping("/categories/add")
    public String addCategory(Model model){
        model.addAttribute("category", new Category());
        return "categoriesAdd";
    }

    @PostMapping("/categories/add")
    public String postCategory(@ModelAttribute("category") Category category){
        categoryService.addCategory(category);
        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Long id){
        categoryService.deleteCategoryById(id);
        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/update/{id}")
    public String updateCategory(@PathVariable Long id,Model model){
        Optional<Category> category=categoryService.getCategoryById(id);

        if (category.isPresent()){
            model.addAttribute("category", category.get());
            return "categoriesAdd";
        }else
            return "404";

    }

    // Product Section//

    @GetMapping("/products")
    public String adminProductPage(Model model,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "20") int size,
                                   @RequestParam(required = false) String searchQuery) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC,"id"));
        Page<Product> productPage = productService.getAllProducts(pageable);

        log.info("adminProductPage method called with page={} and size={}", page, size);

        if (searchQuery != null && !searchQuery.isEmpty()) {
            // If searchQuery is provided, search for matching products
            productPage = productService.searchProductsByNameOrDescription(searchQuery, pageable);
        } else {
            // Otherwise, return all products in descending order
            productPage = productService.getAllProducts(pageable);
        }

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("searchQuery", searchQuery);

        return "products";
    }


    @GetMapping("/products/add")
    public String adminProductAddGet(Model model){

        model.addAttribute("products", new Product());
        model.addAttribute("category", categoryService.findAll());
        return "productsAdd";
    }


    @PostMapping("/products/add")
    public String productAddPost(@ModelAttribute("products")Product product,
                                 @RequestParam("productImage") MultipartFile file,
                                 @RequestParam("imgName") String imgName) throws IOException{

        product.setId(product.getId());
        product.setName(product.getName());
        product.setCategory(product.getCategory());
        product.setPrice(product.getPrice());
        product.setStock(product.getStock());
        product.setDescription(product.getDescription());
        product.setColor(product.getColor());
        product.setSize(product.getSize());
        //product.setVariants(product.getVariants());
//        System.out.println("variantsssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +product.getVariants());

//        //product.setStatus(product.getStatus());
//        product.setVariants(product.getVariants());

        String imageUUID;
        if (!file.isEmpty()){
            imageUUID= file.getOriginalFilename();
            Path fileNameAndPath= Paths.get(uploadDir, imageUUID);
            //uploaddir- holds the file path
            Files.write(fileNameAndPath, file.getBytes());
            //means file path save kardo- files.write
            //file.getButes- actual image file

        } else {
            imageUUID= imgName;
        }
        product.setImageName(imageUUID);
        productService.addProduct(product);

        return "redirect:/admin/products";
          }


    //requestparam- means when a request is snd athile parameters oke attached ayit snd akn

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id){
        productService.removeProductById(id);
        return "redirect:/admin/products";
    }

    @GetMapping("/products/update/{id}")
    public String updateProduct(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setCategory(product.getCategory().getId());
        productDTO.setPrice(product.getPrice());
        productDTO.setStock(product.getStock());
        productDTO.setDescription(product.getDescription());

      //  productDTO.setStatus(product.getStatus());
        productDTO.setImageName(product.getImageName());
        productDTO.setDeleted(product.isDeleted());

        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("productDTO", productDTO);

        return "productUpdate";
    }

    // Order Section//

    @GetMapping("/orders")
    public String orderManagement(Model model){
        List<Orders> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "order";
    }

    @PostMapping("/orders/updateStatus")
    public String updateOrderStatus(@RequestParam("orderId") Long orderId, @RequestParam("newStatus") String newStatus) {
        orderService.updateOrderStatus(orderId, newStatus);
        return "redirect:/admin/orders";
    }

    @GetMapping("/orders/details/{orderItemId}")
    public String getOrderDetails(@PathVariable Long orderItemId, Model model) {
        Orders order = orderService.findById(orderItemId);
        if (order == null) {
            return "404-page";
        }
        model.addAttribute("order", order);
        return "order-details";
    }

    // Coupon Section//

        @GetMapping("/coupons")
        private String listCoupons(Model model){
            model.addAttribute("coupons", couponService.getAllCoupons());
            return "coupons";
        }

        @GetMapping("/coupons/add")
        public String showAddCouponForm(Model model) {
            model.addAttribute("coupon", new Coupon());
            return "coupon-add";
        }

        @PostMapping("/coupons/add")
        public String addCoupon(@ModelAttribute("coupon") Coupon coupon) {
            couponService.createCoupon(coupon);
            return "redirect:/admin/coupons";
        }

        @GetMapping("/coupons/edit/{id}")
        public String showEditCouponForm(@PathVariable("id") Long id, Model model) {
            Optional<Coupon> optionalCoupon = couponService.getCouponById(id);
            if (optionalCoupon.isPresent()) {
                model.addAttribute("coupon", optionalCoupon.get());
            } else {
                return "redirect:/admin/coupons";
            }
            return "coupon-edit";
        }

        @PostMapping("/coupons/edit/{id}")
        public String updateCoupon(@PathVariable("id") Long id, @ModelAttribute("coupon") Coupon coupon) {
            couponService.updateCoupon(id, coupon);
            return "redirect:/admin/coupons";
        }

        @PostMapping("/coupons/delete/{id}")
        public String deleteCoupon(@PathVariable("id") Long id) {
            couponService.deleteCoupon(id);
            return "redirect:/admin/coupons";
        }












}






























