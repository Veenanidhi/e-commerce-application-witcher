package com.witcher.e_commerce.application.witcher.controller.admin;

import com.witcher.e_commerce.application.witcher.dao.CategoryOfferRepository;
import com.witcher.e_commerce.application.witcher.dao.CategoryRepository;
import com.witcher.e_commerce.application.witcher.dao.ProductOfferRepository;
import com.witcher.e_commerce.application.witcher.dao.ProductRepository;
import com.witcher.e_commerce.application.witcher.dto.OfferDTO;
import com.witcher.e_commerce.application.witcher.entity.Category;
import com.witcher.e_commerce.application.witcher.entity.CategoryOffer;
import com.witcher.e_commerce.application.witcher.entity.Product;
import com.witcher.e_commerce.application.witcher.entity.ProductOffer;
import com.witcher.e_commerce.application.witcher.service.category.CategoryService;
import com.witcher.e_commerce.application.witcher.service.order.OrderService;
import com.witcher.e_commerce.application.witcher.service.product.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class OfferManagement {

    private final ProductOfferRepository productOfferRepository;

     @Autowired
     private ProductService productService;

     @Autowired
     private ProductRepository productRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private final OrderService orderService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryOfferRepository categoryOfferRepository;

    public OfferManagement(ProductOfferRepository productOfferRepository, ProductService productService, CategoryService categoryService, OrderService orderService, CategoryRepository categoryRepository, CategoryOfferRepository categoryOfferRepository) {
        this.productOfferRepository = productOfferRepository;
        this.productService = productService;
        this.categoryService = categoryService;
        this.orderService = orderService;

        this.categoryRepository = categoryRepository;
        this.categoryOfferRepository = categoryOfferRepository;
    }

    @GetMapping("/offers")
    public String showOfferPage(){
        return "offerManagement";
    }

    @GetMapping("/offers/listProductOffers")
    public String listProductOffersGet(HttpSession session, Model model){
        model.addAttribute("productOfferSuccess",(String)session.getAttribute("productOfferSuccess"));
        model.addAttribute("productOfferError",(String)session.getAttribute("productOfferError"));
        session.removeAttribute("productOfferSuccess");
        session.removeAttribute("productOfferError");

        List<ProductOffer> productOfferList = productOfferRepository.findAll();
//        List<Product> productList = productRepository.findAll();

        model.addAttribute("productOfferList",productOfferList);

        return "listProductOffers";
    }

    @GetMapping("/offers/addProductOffer")
    public String addProductOfferGet(Model model){
        model.addAttribute("productOffer", new OfferDTO());
        model.addAttribute("products", productService.getAllProduct());
        return "addProductOffer";
    }

    @PostMapping("/offers/addProductOffer")
    public String addProductOfferPost(@ModelAttribute ("productOffer")OfferDTO offerDTO, HttpSession session){
        ProductOffer saved = productService.saveProductOffer(offerDTO);
        if (saved != null) {
            session.setAttribute("productOfferSuccess", "Product offer successfully added.");
            return "redirect:/admin/offers/listProductOffers";
        }
        session.setAttribute("productOfferError", "Failed to add product offer.");
        return "/offers/addProductOffer";
    }

    @GetMapping("/offers/editProductOffer/{id}")
    public String editProductOffersGet(@PathVariable Long id, Model model){
         ProductOffer offer= productOfferRepository.findById(id).orElseThrow(() -> new RuntimeException("Offer not found"));
        List<Product> products = productService.getAllProduct();
        model.addAttribute("productOffer", offer);
        model.addAttribute("products", productService.getAllProduct());
        return "editProductOffer"; // Create this template to edit offers
    }

    @PostMapping("/offers/editProductOffer")
    public String editProductOfferPost(@ModelAttribute("productOffer") OfferDTO offerDTO, HttpSession session) {
        ProductOffer updated = productService.updateProductOffer(offerDTO);
        if (updated != null) {
            session.setAttribute("productOfferSuccess", "Product offer successfully updated.");
            return "redirect:admin/offers/listProductOffers";
        }
        session.setAttribute("productOfferError", "Failed to update product offer.");
        return "redirect:/addProductOffer" + offerDTO.getProductOfferId(); // Redirect back to edit on failure
    }

    // Delete product offer
    @GetMapping("/offers/deleteProductOffer/{id}")
    public String deleteProductOffer(@PathVariable Long id, HttpSession session) {
        boolean isDeleted = productService.deleteProductOffer(id);
        if (isDeleted) {
            session.setAttribute("productOfferSuccess", "Product offer successfully deleted.");
        } else {
            session.setAttribute("productOfferError", "Failed to delete product offer.");
        }
        return "redirect:/admin/offers/listProductOffers";
    }

    // ---------------- Category Offer Management ----------------

    @GetMapping("/offers/listCategoryOffers")
    public String listCategoryOffers(HttpSession session, Model model){
        model.addAttribute("categoryOfferSuccess", (String) session.getAttribute("categoryOfferSuccess"));
        model.addAttribute("categoryOfferError", (String) session.getAttribute("categoryOfferError"));
        session.removeAttribute("categoryOfferSuccess");
        session.removeAttribute("categoryOfferError");
        List<CategoryOffer> categoryOfferList = categoryOfferRepository.findAll();
        model.addAttribute("categoryOfferList", categoryOfferList);
        return "listCategoryOffers";
    }

    @GetMapping("/offers/addCategoryOffer")
    public String addCategoryOfferGet(Model model) {
        model.addAttribute("categoryOffer", new OfferDTO());

        List<Category> categoryList = categoryRepository.findAll();
        model.addAttribute("categories", categoryList);
        return "addCategoryOffer";
    }

    @PostMapping("/offers/addCategoryOffer")
    public String addCategoryOfferPost(@ModelAttribute("categoryOffer") OfferDTO offerDTO, HttpSession session) {
        CategoryOffer saved = categoryService.saveCategoryOffer(offerDTO);
        if (saved != null) {
            session.setAttribute("categoryOfferSuccess", "Category offer successfully added.");
            return "redirect:/admin/offers/listCategoryOffers";
        }
        session.setAttribute("categoryOfferError", "Failed to add category offer.");
        return "redirect:/admin/offers/listCategoryOffers";
    }

    @GetMapping("/editCategoryOffer")
    public String editCategoryOffer(@RequestParam("id") Long categoryOfferId, Model model){

        CategoryOffer categoryOffer = categoryService.findCategoryOfferById(categoryOfferId);
        OfferDTO offerDTO = convertToDTO(categoryOffer);
        model.addAttribute("offer", offerDTO);

        List<Category> categories = categoryService.findAll();
        model.addAttribute("categories", categories);

        return "edit-category-offer";
    }

    private OfferDTO convertToDTO(CategoryOffer categoryOffer) {
        OfferDTO offerDTO = new OfferDTO();
        offerDTO.setCategoryOfferId(categoryOffer.getCategoryOfferId());
        offerDTO.setCategoryOfferName(categoryOffer.getCategoryOfferName());
        offerDTO.setDiscountPercentage(categoryOffer.getDiscountPercentage());
//        offerDTO.setStartDate(categoryOffer.getStartDate());
//        offerDTO.setExpiryDate(categoryOffer.getExpiryDate());
        offerDTO.setDescription(categoryOffer.getDescription());
        offerDTO.setCategories(categoryOffer.getCategories());
        return offerDTO;
    }

    @PostMapping("/updateCategoryOffer")
    public String updateCategoryOffer(@ModelAttribute OfferDTO offerDTO, RedirectAttributes ra){

        if (offerDTO.getCategoryOfferId() == null){
            ra.addFlashAttribute("categoryOfferError", "Category offer ID is missing.");
            return "redirect:/admin/offers/listCategoryOffers";
        }
        CategoryOffer updatedOffer = categoryService.updateCategoryOffer(offerDTO);
        if (updatedOffer != null){
            ra.addFlashAttribute("categoryOfferSuccess", "Category offer updated successfully!");
        } else {
            ra.addFlashAttribute("categoryOfferError", "Failed to update category offer.");
        }
        return "redirect:/admin/listCategoryOffers";
    }


    @PostMapping("/deleteCategoryOffer")
    public String deleteCategoryOffer(@RequestParam Long id) {
        // Call your service to delete the category offer
        orderService.deleteCategoryOfferById(id);
        return "redirect:/admin/offers"; // Redirect after deletion
    }














}
