package com.app.manager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Controller
public class IndexController {

    private static String UPLOAD_FOLDER = "/Users/LiuYujie/Project/javaProject/javaAPM/temp/";

    @GetMapping("/hello")
    @ResponseBody
    public String hello(@RequestParam(value = "name", defaultValue = "world") String name) {
        return String.format("hellow %s!", name);
    }

    @GetMapping("/ipaupload")
    public String ipaupload() {
        return "/index.html";
    }

    @PostMapping("/uploadFiles")
    public String singleFileUpload(@RequestParam("book_files") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "请选择一个上传文件");
        }

        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOAD_FOLDER + file.getOriginalFilename());
            Files.write(path, bytes);
            redirectAttributes.addFlashAttribute("message", "已经将 '" + file.getOriginalFilename() + "' 的文件上传成功");

            File ipainfoFile = IPAUtil.getIpaInfoPath(path.toFile());
            Map<String, String> map = IPAUtil.getIpaInfoMap(ipainfoFile);

            System.out.println(map.toString() + map.keySet().toString());

            QiNiuUtil.uploadIPAToQiNiu(path.toFile().getAbsolutePath());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/uploadStatus";
    }

    @GetMapping("/uploadStatus")
    public String uploadStatus() {
        return "ios";
    }


    @GetMapping("/index")
    public String index() {
        return "download";
    }
}
