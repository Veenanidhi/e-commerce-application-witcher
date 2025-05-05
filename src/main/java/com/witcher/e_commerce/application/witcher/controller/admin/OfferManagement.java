package com.witcher.e_commerce.application.witcher.controller.admin;

import com.witcher.e_commerce.application.witcher.dao.CategoryOfferRepository;
import com.witcher.e_commerce.application.witcher.dao.CategoryRepository;
import com.witcher.e_commerce.application.witcher.dao.ProductOfferRepository;
import com.witcher.e_commerce.application.witcher.dao.ProductRepository;
import com.witcher.e_commerce.application.witcher.dto.CategoryOfferDTO;
import com.witcher.e_commerce.application.witcher.dto.ProductOfferDTO;
import com.witcher.e_commerce.application.witcher.entity.Category;
import com.witcher.e_commerce.application.witcher.entity.CategoryOffer;
import com.witcher.e_commerce.application.witcher.entity.Product;
import com.witcher.e_commerce.application.witcher.entity.ProductOffer;
import com.witcher.e_commerce.application.witcher.service.categoryOffer.CategoryOfferService;
import com.witcher.e_commerce.application.witcher.service.productOffer.ProductOfferService;
import com.witcher.e_commerce.application.witcher.service.category.CategoryService;
import com.witcher.e_commerce.application.witcher.service.order.OrderService;
import com.witcher.e_commerce.application.witcher.service.product.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/offers")
public class OfferManagement {

    private final ProductOfferRepository productOfferRepository;

    @Autowired
    private ProductOfferService productOfferService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryOfferService categoryOfferService;

    @Autowired
    private CategoryService categoryService;


    private final OrderService orderService;


    private CategoryRepository categoryRepository;

    private final ProductRepository productRepository;

    @Autowired
    private  CategoryOfferRepository categoryOfferRepository;

    public OfferManagement(ProductOfferRepository productOfferRepository, OrderService orderService, ProductRepository productRepository) {
        this.productOfferRepository = productOfferRepository;
        this.orderService = orderService;
        this.productRepository = productRepository;
    }


    @GetMapping("/listProductOffers")
    public String listProductOffersGet(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "5") int size,HttpSession session, Model model){


        Pageable pageable = PageRequest.of(page, size, Sort.by("productOfferId").descending());
        Page<ProductOffer> productOfferPage = productOfferRepository.findAll(pageable);

        model.addAttribute("productOfferSuccess",(String)session.getAttribute("productOfferSuccess"));
        model.addAttribute("productOfferError",(String)session.getAttribute("productOfferError"));
        session.removeAttribute("productOfferSuccess");
        session.removeAttribute("productOfferError");

        List<ProductOffer> productOfferList = productOfferRepository.findAll();
        List<Product> productList = productRepository.findAll();

        model.addAttribute("productOfferPage", productOfferPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productOfferPage.getTotalPages());

        model.addAttribute("productOfferList",productOfferList);

        for (ProductOffer productOffers : productOfferList){
            System.out.print("offerrrrrrrrrrrrrrrrrrrr" +productOffers.getProductOfferName());
            for (Product products : productOffers.getProducts()){
                System.out.print("productssssssssssssssssssss" +products.getName());


            }
            System.out.println();

        }

        return "listProductOffers";
    }

    @GetMapping("/addProductOffer")
    public String addProductOfferGet(Model model) {
        model.addAttribute("productOffer", new ProductOfferDTO());
        model.addAttribute("products", productService.getAllProduct());
        return "addProductOffer";
    }

    @PostMapping("/addProductOffer")
    public String addProductOfferPost(@ModelAttribute("productOffer") ProductOfferDTO productOfferDTO, HttpSession session) {
        ProductOffer savedOffer = productOfferService.saveProductOffer(productOfferDTO);
        if (savedOffer != null) {
            session.setAttribute("productOfferSuccess", "Product offer added successfully!");
            return "redirect:/admin/offers/listProductOffers";
        } else {
            session.setAttribute("productOfferError", "Failed to add product offer.");
            return "redirect:/admin/offers/addProductOffer";
        }
    }

    @GetMapping("/editProductOffer/{id}")
    public String editProductOffer(@PathVariable Long id, Model model) {
        ProductOffer productOffer = productOfferService.findProductOfferById(id);
        if (productOffer == null) {
            return "redirect:/admin/offers/listProductOffers";
        }

        // Convert entity to DTO
        ProductOfferDTO productOfferDTO = new ProductOfferDTO();
        productOfferDTO.setProductOfferId(productOffer.getProductOfferId());
        productOfferDTO.setProductOfferName(productOffer.getProductOfferName());
        productOfferDTO.setDiscountPercentage(productOffer.getDiscountPercentage());
        productOfferDTO.setStartDate(productOffer.getStartDate());
        productOfferDTO.setExpiryDate(productOffer.getExpiryDate());
        List<Long> productIds = productOffer.getProducts().stream()
                .map(Product::getId)
                .collect(Collectors.toList());
        productOfferDTO.setProductIds(productIds);

        model.addAttribute("productOfferDTO", productOfferDTO);
        model.addAttribute("productList", productService.getAllProduct());
        return "editProductOffer";
    }


    @PostMapping("/updateProductOffer")
    public String updateProductOffer(@ModelAttribute("productOfferDTO") ProductOfferDTO productOfferDTO, HttpSession session) {
        ProductOffer updatedOffer = productOfferService.updateProductOffer(productOfferDTO);
        if (updatedOffer == null) {
            session.setAttribute("productOfferError", "Product Offer update failed");
            return "redirect:/admin/offers/editProductOffer/" + productOfferDTO.getProductOfferId();
        }
        session.setAttribute("productOfferSuccess", "Product Offer updated successfully");
        return "redirect:/admin/offers/listProductOffers";
    }


    // Delete product offer
    @GetMapping("/deleteProductOffer/{id}")
    public String deleteProductOffer(@PathVariable Long id, HttpSession session) {
        boolean isDeleted = productService.deleteProductOffer(id);
        if (isDeleted) {
            session.setAttribute("productOfferSuccess", "Product offer successfully deleted.");
        } else {
            session.setAttribute("productOfferError", "Failed to delete product offer.");
        }
        return "redirect:/admin/offers/listProductOffers";
    }

      private ProductOfferDTO convertToDTO(ProductOffer productOffer) {
        ProductOfferDTO dto = new ProductOfferDTO();
        dto.setProductOfferId(productOffer.getProductOfferId());
        dto.setProductOfferName(productOffer.getProductOfferName());
        dto.setDescription(productOffer.getDescription());
        dto.setDiscountPercentage(productOffer.getDiscountPercentage());
        dto.setStartDate(productOffer.getStartDate());
        dto.setExpiryDate(productOffer.getExpiryDate());
        List<Long> productIds = new ArrayList<>();
        for (Product product : productOffer.getProducts()) {
            productIds.add(product.getId());
        }
        dto.setProductIds(productIds);
        return dto;
    }

    // ---------------- Category Offer Management ----------------

    @GetMapping("/listCategoryOffers")
    public String listCategoryOffers(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "5") int size,
                                     HttpSession session, Model model) {
        model.addAttribute("categoryOfferSuccess", (String) session.getAttribute("categoryOfferSuccess"));
        model.addAttribute("categoryOfferError", (String) session.getAttribute("categoryOfferError"));
        session.removeAttribute("categoryOfferSuccess");
        session.removeAttribute("categoryOfferError");

        Pageable pageable = PageRequest.of(page, size, Sort.by("categoryOfferId").descending());
        Page<CategoryOffer> categoryOfferPage = categoryOfferRepository.findAll(pageable);
        model.addAttribute("categoryOfferPage", categoryOfferPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", categoryOfferPage.getTotalPages());

        List<CategoryOffer> categoryOfferList = categoryOfferRepository.findAll();
        model.addAttribute("categoryOfferList", categoryOfferList);

        return "listCategoryOffers";
    }


    @GetMapping("/addCategoryOffer")
    public String addCategoryOfferGet(Model model) {
        model.addAttribute("categoryOffer", new CategoryOfferDTO());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "addCategoryOffer";
    }



    @PostMapping("/addCategoryOffer")
    public String addCategoryOfferPost(@ModelAttribute("categoryOffer") CategoryOfferDTO categoryOfferDTO, HttpSession session) {
        System.out.println("Received CategoryOfferDTO: " + categoryOfferDTO);

        CategoryOffer savedOffer = categoryOfferService.saveCategoryOffer(categoryOfferDTO);
        if (savedOffer != null) {
            session.setAttribute("categoryOfferSuccess", "Category offer added successfully!");
            return "redirect:/admin/offers/listCategoryOffers";
        } else {
            session.setAttribute("categoryOfferError", "Failed to add category offer.");
            return "redirect:/admin/offers/addCategoryOffer";
        }
    }



    @GetMapping("/editCategoryOffer/{id}")
    public String editCategoryOffer(@PathVariable Long id, Model model) {
        CategoryOffer categoryOffer = categoryOfferService.findCategoryOfferById(id);


        if (categoryOffer == null) {

            return "redirect:/admin/offers/listCategoryOffers";
        }


        CategoryOfferDTO categoryOfferDTO = convertToDTO(categoryOffer);


        List<Category> categories = categoryService.getAllCategories();


        model.addAttribute("categoryOfferDTO", categoryOfferDTO);
        model.addAttribute("categories", categories);

        return "edit-category-offer";
    }


    @PostMapping("/updateCategoryOffer")
    public String updateCategoryOffer(@ModelAttribute("categoryOfferDTO") CategoryOfferDTO categoryOfferDTO, HttpSession session) {
        CategoryOffer updatedOffer = categoryOfferService.updateCategoryOffer(categoryOfferDTO);
        if (updatedOffer == null) {
            session.setAttribute("categoryOfferError", "Category offer update failed.");
            return "redirect:/admin/offers/editCategoryOffer/" + categoryOfferDTO.getCategoryOfferId();
        }
        session.setAttribute("categoryOfferSuccess", "Category offer updated successfully.");
        return "redirect:/admin/offers/listCategoryOffers";
    }



    private CategoryOfferDTO convertToDTO(CategoryOffer categoryOffer) {
        CategoryOfferDTO dto = new CategoryOfferDTO();
        dto.setCategoryOfferId(categoryOffer.getCategoryOfferId());
        dto.setCategoryOfferName(categoryOffer.getCategoryOfferName());
        dto.setDescription(categoryOffer.getDescription());
        dto.setDiscountPercentage(categoryOffer.getDiscountPercentage());
        dto.setStartDate(categoryOffer.getStartDate());
        dto.setExpiryDate(categoryOffer.getExpiryDate());

        Category category = categoryOffer.getCategory();
        Long categoryId = category != null ? category.getId() : null;
        dto.setCategoryId(categoryId);

        return dto;
    }


    @PostMapping("/deleteCategoryOffer/{id}")
    public String deleteCategoryOffer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean deleted = categoryOfferService.deleteCategoryOfferById(id);
        if (deleted) {
            redirectAttributes.addFlashAttribute("success", "Category Offer deleted successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to delete Category Offer.");
        }
        return "redirect:/admin/offers/listCategoryOffers";
    }







}
