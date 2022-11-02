package org.tronproject.onecall.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tronproject.onecall.model.vo.R;

/**
 * @author Admin
 * @version 1.0
 * @time 2022-10-20 11:16
 */
@RestController
public class HelloController {

    @GetMapping("/hi")
    public R hi() {
        return R.builder().status(true).message("hello").build();
    }

}
