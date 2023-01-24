package org.xpdojo.file.spring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SpringMultipartController {

    private final SpringMultipartService springMultipartService;

    @GetMapping("/multipart")
    public String multipart() {
        return "multipart";
    }

    @PostMapping(path = "/multipart", consumes = "multipart/form-data")
    public String multipart(
            // @RequestParam("singleFile") MultipartFile singleFile,
            @RequestParam("multipleFiles") List<MultipartFile> files
    ) throws IOException {
        // Spring MultipartFile
        springMultipartService.saveMultipart(files);
        return "redirect:/";
    }

}