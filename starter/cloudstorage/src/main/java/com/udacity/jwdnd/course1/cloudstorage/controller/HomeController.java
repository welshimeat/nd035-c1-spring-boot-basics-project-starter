package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.mapper.CredentialMapper;
import com.udacity.jwdnd.course1.cloudstorage.mapper.FileMapper;
import com.udacity.jwdnd.course1.cloudstorage.mapper.NoteMapper;
import com.udacity.jwdnd.course1.cloudstorage.services.EncryptionService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/home")
public class HomeController {
    private final UserService userService;
    private final FileMapper fileMapper;
    private final NoteMapper noteMapper;
    private final CredentialMapper credentialMapper;
    private final EncryptionService encryptionService;


    public HomeController(UserService userService, FileMapper fileMapper, NoteMapper noteMapper, CredentialMapper credentialMapper, EncryptionService encryptionService){
        this.userService = userService;
        this.fileMapper = fileMapper;
        this.noteMapper = noteMapper;
        this.credentialMapper = credentialMapper;
        this.encryptionService = encryptionService;
    }

    @GetMapping()
    public String homeView(Model model, Principal principal){
        int userId = userService.getUser(principal.getName()).getUserId();

        model.addAttribute("fileList", fileMapper.getFiles(userId));
        model.addAttribute("noteList", noteMapper.getNotes(userId));
        model.addAttribute("credentialList", credentialMapper.getCredential(userId));
        model.addAttribute("encryptionService", encryptionService);

        return "home";
    }
}
