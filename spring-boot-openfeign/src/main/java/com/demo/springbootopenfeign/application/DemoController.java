package com.demo.springbootopenfeign.application;

import com.demo.springbootopenfeign.application.dto.DemoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.demo.springbootopenfeign.application.dto.DemoResponse.success;

@RestController
@RequiredArgsConstructor
public class DemoController {

    private final DemoService demoService;

    @GetMapping("/hello")
    public DemoResponse<String> hello() {
        return success(demoService.hello());
    }

    @GetMapping("/hello2")
    public DemoResponse<Void> hello2() {
        demoService.hello2();
        return success();
    }

}
