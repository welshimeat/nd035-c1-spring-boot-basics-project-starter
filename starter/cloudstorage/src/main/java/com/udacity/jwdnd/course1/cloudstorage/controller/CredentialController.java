package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.mapper.CredentialMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import com.udacity.jwdnd.course1.cloudstorage.services.EncryptionService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.security.SecureRandom;
import java.util.Base64;

@Controller
public class CredentialController {
    private final UserService userService;
    private final CredentialMapper credentialMapper;
    private final EncryptionService encryptionService;

    public CredentialController(UserService userService, CredentialMapper credentialMapper, EncryptionService encryptionService) {
        this.userService = userService;
        this.credentialMapper = credentialMapper;
        this.encryptionService = encryptionService;
    }


    @PostMapping("/credential")
    public String insertCredential(@ModelAttribute Credential credential, Principal principal, Model model){
        String error = null;
        String username = principal.getName();
        Integer userId = userService.getUser(username).getUserId();

        SecureRandom random = new SecureRandom();
        byte[] key = new byte[16];
        random.nextBytes(key);
        credential.setKey(Base64.getEncoder().encodeToString(key));
        credential.setPassword(encryptionService.encryptValue(credential.getPassword(), credential.getKey()));

        if(credential.getCredentialId() != null){
            int result = credentialMapper.updateCredential(credential);
            if(result < 0){
                error = "There was an error upload your credential. Please try again.";
            }
        }
        else{

            int result = credentialMapper.insertCredential(credential, userId);
            if(result < 0){
                error = "There was an error edit your credential. Please try again.";
            }
        }
        if(error != null){
            model.addAttribute("error", error);
        }
        else{
            model.addAttribute("success", true);
        }
        return "result";
    }

    @GetMapping("/credential/delete")
    public String deleteCredential(@RequestParam Integer credentialId, Model model){
        String error = null;

        int result = credentialMapper.deleteCredential(credentialId);
        if(result < 0){
            error = "There was an error delete your credential. Please try again.";
        }
        if(error != null){
            model.addAttribute("error", error);
        }
        else{
            model.addAttribute("success", true);
        }
        return "result";
    }

}
