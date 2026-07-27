package com.example.swinecore.controller;

import com.example.swinecore.entity.CustomerAccount;
import com.example.swinecore.entity.PigOrder;
import com.example.swinecore.entity.SemenOrder;
import com.example.swinecore.entity.Pig;
import com.example.swinecore.repository.FarmAdRepository;
import com.example.swinecore.service.*;
import com.example.swinecore.util.FileUploadUtil;
import com.example.swinecore.util.QrCodeUtil;
import com.example.swinecore.entity.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final PigService pigService;
    private final OrderService orderService;
    private final FarmAdRepository farmAdRepository;
    private final FileUploadUtil fileUploadUtil;
    private final PasswordEncoder passwordEncoder;
    private final QrCodeUtil qrCodeUtil;
    private final RuleScheduleService ruleService;

    // ---- Registration ----

    @GetMapping("/customer/register")
    public String registerForm(Model model) {
        model.addAttribute("customer", new CustomerAccount());
        return "customer/register";
    }

    @PostMapping("/customer/register")
    public String register(@RequestParam String name,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam(required = false) String phone,
                           RedirectAttributes ra) {
        try {
            CustomerAccount customer = new CustomerAccount();
            customer.setName(name.trim());
            customer.setEmail(email.trim().toLowerCase(java.util.Locale.ROOT));
            customer.setPassword(password);
            customer.setPhone(phone);

            CustomerAccount saved = customerService.register(customer);
            if (saved.getId() == null) throw new IllegalStateException("Customer account was not persisted.");
            return "redirect:/auth/login?registered=true";
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/customer/register";
        }
    }

    // ---- Public Marketplace ----

    @GetMapping("/marketplace")
    public String marketplace(Model model, Principal principal) {
        model.addAttribute("ads", farmAdRepository.findByActiveTrue());
        model.addAttribute("pigs", pigService.findListedForSale());
        model.addAttribute("boars", pigService.findBreedingBoarsForSemen());
        model.addAttribute("isLoggedIn", principal != null);
        return "customer/marketplace";
    }

    // ---- Customer Dashboard ----

    @GetMapping("/customer/dashboard")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String dashboard(Principal p, Model model) {
        CustomerAccount customer = getCustomer(p);
        model.addAttribute("customer", customer);
        model.addAttribute("pigOrders", orderService.getCustomerPaidPigOrders(customer));
        model.addAttribute("semenOrders", orderService.getCustomerPaidSemenOrders(customer));
        return "customer/dashboard";
    }

    // ---- Customer Profile ----

    @GetMapping("/customer/profile")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String profile(Principal p, Model model) {
        model.addAttribute("customer", getCustomer(p));
        return "customer/profile";
    }

    /** Update name, phone, address */
    @PostMapping("/customer/profile/update")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String updateProfile(@RequestParam String name,
                                 @RequestParam(required = false) String phone,
                                 @RequestParam(required = false) String address,
                                 Principal p, RedirectAttributes ra) {
        CustomerAccount customer = getCustomer(p);
        customer.setName(name.trim().isEmpty() ? customer.getName() : name.trim());
        customer.setPhone(phone);
        customer.setAddress(address);
        customerService.save(customer);
        ra.addFlashAttribute("success", "Profile updated successfully.");
        return "redirect:/customer/profile";
    }

    /** Upload / replace profile photo — called by the avatar form for instant preview */
    @PostMapping("/customer/profile/update-photo")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String updatePhoto(@RequestParam("profileImage") MultipartFile profileImage,
                               Principal p, RedirectAttributes ra) {
        if (profileImage == null || profileImage.isEmpty()) {
            ra.addFlashAttribute("error", "No image selected.");
            return "redirect:/customer/profile";
        }
        CustomerAccount customer = getCustomer(p);
        try {
            fileUploadUtil.deleteFile(customer.getProfileImagePath());
            String path = fileUploadUtil.saveFile(profileImage, "customers");
            customer.setProfileImagePath(path);
            customerService.save(customer);
            ra.addFlashAttribute("success", "Profile photo updated.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to upload image: " + e.getMessage());
        }
        return "redirect:/customer/profile";
    }

    /** Remove profile photo */
    @PostMapping("/customer/profile/remove-photo")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String removePhoto(Principal p, RedirectAttributes ra) {
        CustomerAccount customer = getCustomer(p);
        fileUploadUtil.deleteFile(customer.getProfileImagePath());
        customer.setProfileImagePath(null);
        customerService.save(customer);
        ra.addFlashAttribute("success", "Profile photo removed.");
        return "redirect:/customer/profile";
    }

    /** Change customer password */
    @PostMapping("/customer/profile/change-password")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String changePassword(@RequestParam String currentPassword,
                                  @RequestParam String newPassword,
                                  @RequestParam String confirmPassword,
                                  Principal p, RedirectAttributes ra) {
        CustomerAccount customer = getCustomer(p);
        if (!passwordEncoder.matches(currentPassword, customer.getPassword())) {
            ra.addFlashAttribute("error", "Current password is incorrect.");
            return "redirect:/customer/profile";
        }
        if (!newPassword.equals(confirmPassword)) {
            ra.addFlashAttribute("error", "New passwords do not match.");
            return "redirect:/customer/profile";
        }
        if (newPassword.length() < 8) {
            ra.addFlashAttribute("error", "Password must be at least 8 characters.");
            return "redirect:/customer/profile";
        }
        customer.setPassword(passwordEncoder.encode(newPassword));
        customerService.save(customer);
        ra.addFlashAttribute("success", "Password changed successfully.");
        return "redirect:/customer/profile";
    }

    // ---- Pig Purchase ----

    @GetMapping("/customer/pigs")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String pigListing(@RequestParam(required = false, defaultValue = "") String search,
                             @RequestParam(required = false) Long farmId,
                             @RequestParam(required = false, defaultValue = "") String category,
                             @RequestParam(required = false, defaultValue = "0") int page,
                             Model model) {
        List<Pig> all = pigService.findListedForSale();
        String needle = search.trim().toLowerCase(Locale.ROOT);
        List<Pig> filtered = all.stream()
            .filter(p -> farmId == null || p.getBuilding().getFarm().getId().equals(farmId))
            .filter(p -> category.isBlank() || p.getStatus().name().equalsIgnoreCase(category))
            .filter(p -> needle.isBlank() || p.getCode().toLowerCase(Locale.ROOT).contains(needle)
                || p.getGenetics().getName().toLowerCase(Locale.ROOT).contains(needle)
                || p.getBuilding().getFarm().getName().toLowerCase(Locale.ROOT).contains(needle))
            .toList();
        addCatalogPage(model, filtered, page, "pigs");
        model.addAttribute("farms", all.stream().map(p -> p.getBuilding().getFarm()).distinct().toList());
        model.addAttribute("categories", all.stream().map(p -> p.getStatus().name()).distinct().sorted().toList());
        model.addAttribute("search", search); model.addAttribute("farmId", farmId); model.addAttribute("category", category);
        return "customer/pigs";
    }

    @PostMapping("/customer/cart/checkout")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String checkout(@RequestParam(required = false) List<Long> pigIds, Principal p, RedirectAttributes ra) {
        try {
            PigOrder order = orderService.createPigOrder(getCustomer(p), pigIds);
            return "redirect:/customer/orders/pig/" + order.getId();
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/customer/pigs";
        }
    }

    @GetMapping("/customer/orders/pig/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String pigOrderDetail(@PathVariable Long id, Principal p, Model model) {
        PigOrder order = orderService.getCustomerPigOrder(id, getCustomer(p));
        model.addAttribute("order", order);
        model.addAttribute("qrCode", orderService.generatePigQrImage(order));
        return "customer/order-pig";
    }

    @GetMapping("/customer/orders/pig/{id}/status")
    @PreAuthorize("hasRole('CUSTOMER')")
    @ResponseBody
    public Map<String, String> pigOrderStatus(@PathVariable Long id, Principal p) {
        PigOrder order = orderService.getCustomerPigOrder(id, getCustomer(p));
        return Map.of("status", order.getStatus().name());
    }

    // ---- QR Confirm ----

    @GetMapping("/checkout/scan")
    public String scanPayment(@RequestParam String ref,
                              @RequestParam String type,
                              @RequestParam String amount,
                              @RequestParam String farm,
                              @RequestParam String details,
                              @RequestParam String sig,
                              Model model) {
        if (!qrCodeUtil.verifyCustomerPayment(ref, type, amount, farm, details, sig)) {
            model.addAttribute("error", "Invalid or modified payment QR code.");
            return "customer/confirm-payment";
        }
        try {
            model.addAttribute("voucher",
                orderService.getPaymentVoucher(ref, type, amount, farm, details));
            addSignedVoucherFields(model, ref, type, amount, farm, details, sig);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "customer/confirm-payment";
    }

    @PostMapping("/checkout/pay/{ref}")
    public String processPayment(@PathVariable String ref,
                                 @RequestParam String type,
                                 @RequestParam String amount,
                                 @RequestParam String farm,
                                 @RequestParam String details,
                                 @RequestParam String sig,
                                 Model model) {
        try {
            if (!qrCodeUtil.verifyCustomerPayment(ref, type, amount, farm, details, sig))
                throw new IllegalArgumentException("Invalid payment QR signature");
            model.addAttribute("voucher",
                orderService.confirmSignedPayment(ref, type, amount, farm, details));
            model.addAttribute("success", "Payment completed successfully.");
            addSignedVoucherFields(model, ref, type, amount, farm, details, sig);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "customer/confirm-payment";
    }

    private void addSignedVoucherFields(Model model, String ref, String type, String amount,
                                        String farm, String details, String sig) {
        model.addAttribute("ref", ref);
        model.addAttribute("type", type);
        model.addAttribute("amount", amount);
        model.addAttribute("farm", farm);
        model.addAttribute("details", details);
        model.addAttribute("sig", sig);
    }

    // ---- Semen Marketplace ----

    @GetMapping("/customer/semen")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String semenListing(@RequestParam(required = false, defaultValue = "") String search,
                               @RequestParam(required = false) Long farmId,
                               @RequestParam(required = false, defaultValue = "") String category,
                               @RequestParam(required = false, defaultValue = "0") int page,
                               Model model) {
        List<Pig> all = pigService.findBreedingBoarsForSemen();
        String needle = search.trim().toLowerCase(Locale.ROOT);
        List<Pig> filtered = all.stream()
            .filter(p -> farmId == null || p.getBuilding().getFarm().getId().equals(farmId))
            .filter(p -> category.isBlank() || p.getGenetics().getCode().equalsIgnoreCase(category))
            .filter(p -> needle.isBlank() || p.getCode().toLowerCase(Locale.ROOT).contains(needle)
                || p.getGenetics().getName().toLowerCase(Locale.ROOT).contains(needle)
                || p.getBuilding().getFarm().getName().toLowerCase(Locale.ROOT).contains(needle))
            .toList();
        addCatalogPage(model, filtered, page, "boars");
        model.addAttribute("farms", all.stream().map(p -> p.getBuilding().getFarm()).distinct().toList());
        model.addAttribute("categories", all.stream().map(Pig::getGenetics).distinct().toList());
        model.addAttribute("search", search); model.addAttribute("farmId", farmId); model.addAttribute("category", category);
        java.util.Map<Long, Double> semenPrices = new java.util.HashMap<>();
        all.forEach(boar -> semenPrices.put(boar.getId(),
            ruleService.getSemenPrice(boar.getBuilding().getFarm())));
        model.addAttribute("semenPrices", semenPrices);
        return "customer/semen";
    }

    @PostMapping("/customer/semen/order")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String orderSemen(@RequestParam Long boarId,
                              @RequestParam int quantity,
                              Principal p, RedirectAttributes ra) {
        try {
            SemenOrder order = orderService.createSemenOrder(getCustomer(p), boarId, quantity);
            return "redirect:/customer/orders/semen/" + order.getId();
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/customer/semen";
        }
    }

    @GetMapping("/customer/orders/semen/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String semenOrderDetail(@PathVariable Long id, Principal p, Model model) {
        SemenOrder order = orderService.getCustomerSemenOrder(id, getCustomer(p));
        model.addAttribute("order", order);
        model.addAttribute("qrCode", orderService.generateSemenQrImage(order));
        return "customer/order-semen";
    }

    @GetMapping("/customer/orders/semen/{id}/status")
    @PreAuthorize("hasRole('CUSTOMER')")
    @ResponseBody
    public Map<String, String> semenOrderStatus(@PathVariable Long id, Principal p) {
        SemenOrder order = orderService.getCustomerSemenOrder(id, getCustomer(p));
        return Map.of("status", order.getStatus().name());
    }

    private CustomerAccount getCustomer(Principal p) {
        return customerService.findByEmail(p.getName()).orElseThrow();
    }

    private <T> void addCatalogPage(Model model, List<T> items, int requestedPage, String attribute) {
        int pageSize = 15;
        int totalPages = Math.max(1, (int) Math.ceil(items.size() / (double) pageSize));
        int currentPage = Math.max(0, Math.min(requestedPage, totalPages - 1));
        int from = Math.min(currentPage * pageSize, items.size());
        int to = Math.min(from + pageSize, items.size());
        model.addAttribute(attribute, items.subList(from, to));
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", items.size());
    }
}
