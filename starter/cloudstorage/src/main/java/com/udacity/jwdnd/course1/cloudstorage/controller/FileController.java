package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.mapper.FileMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.File;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@Controller
public class FileController {
    private final UserService userService;
    private final FileMapper fileMapper;

    public FileController(UserService userService, FileMapper fileMapper) {
        this.userService = userService;
        this.fileMapper = fileMapper;
    }

    @PostMapping("/file")
    public String uploadFile(@RequestParam("fileUpload")MultipartFile fileUpload, Principal principal, Model model) throws IOException {
        String username = principal.getName();
        Integer userId = userService.getUser(username).getUserId();
        String error = null;
        if(fileUpload.isEmpty()){
            error = "There is no file to upload.";
        }
        else if(fileUpload.getSize() > 1048576){
            error = "Too large file size to upload. Maximum 1MB allowed";
        }
        else if(fileMapper.getFile(userId, fileUpload.getOriginalFilename()) != null){
            error = "Filename already exist.";
        }
        else{
            File file = new File(null, fileUpload.getOriginalFilename(), fileUpload.getContentType(), Long.toString(fileUpload.getSize()), userId, fileUpload.getBytes());
            int result = fileMapper.insertFile(file);
            if(result < 0){
                error = "There was an error upload your file. Please try again.";
            }
            else {
                model.addAttribute("success", true);
            }
        }

        if(error != null){
            model.addAttribute("error", error);
        }
        return "result";
    }

    @GetMapping("/file")
    public ResponseEntity download(@RequestParam String fileName, Principal principal){
        String username = principal.getName();
        Integer userId = userService.getUser(username).getUserId();
        File file = fileMapper.getFile(userId, fileName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file.getFileData());
    }

    @GetMapping("/file/delete")
    public String deleteFile(@RequestParam Integer fileId, Model model){
        String error = null;

        int result = fileMapper.deleteFile(fileId);
        if(result < 0){
            error = "There was an error delete your file. Please try again.";
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
