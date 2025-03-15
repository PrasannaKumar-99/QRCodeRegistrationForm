package com.example.qrgenerator.comtroller;

import java.awt.image.BufferedImage;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.qrgenerator.model.User;
import com.example.qrgenerator.repo.UserRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
public class RegistrationController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("message", "Scan the QR Code to Register");
        return "home";
    }

    @GetMapping("/qrcode")
    public void generateQRCode(HttpServletResponse response) throws Exception {
        String registrationUrl = "http://localhost:8080/register";
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(registrationUrl, BarcodeFormat.QR_CODE, 200, 200);
        
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        response.setContentType("image/png");
        ImageIO.write(qrImage, "PNG", response.getOutputStream());
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "registration";
    }


    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, 
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "registration";
        }

        List<String> errors = new ArrayList<>();

        // Check for existing email
        if (userRepository.existsByEmail(user.getEmail())) {
            errors.add("Email is already registered!");
        }

        // Check for existing mobile
        if (userRepository.existsByMobileNumber(user.getMobileNumber())) {
            errors.add("Mobile number is already registered!");
        }

        // Return if any errors
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            return "registration";
        }

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            handleDuplicateEntry(ex, model);
            return "registration";
        }

        model.addAttribute("success", "Registration Successful!");
        model.addAttribute("adminLink", "/admin");
        return "success";
    }

    private void handleDuplicateEntry(DataIntegrityViolationException ex, Model model) {
        Throwable rootCause = ex.getRootCause();
        if (rootCause instanceof SQLIntegrityConstraintViolationException) {
            SQLIntegrityConstraintViolationException sqlEx = 
                (SQLIntegrityConstraintViolationException) rootCause;
            String errorMessage = sqlEx.getMessage();
            
            List<String> errors = new ArrayList<>();
            if (errorMessage.contains("email")) {
                errors.add("Email is already registered!");
            }
            if (errorMessage.contains("mobile_number")) {
                errors.add("Mobile number is already registered!");
            }
            
            model.addAttribute("errors", errors);
        }
    }


    @GetMapping("/admin")
    public String adminPanel(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin";
    }
}