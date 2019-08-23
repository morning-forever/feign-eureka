package demo.service;

import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @description: helloService
 * @author: guochaoyong
 * @date: 2019-08-21 20:18
 **/
public interface HelloServiceInterface {

    @RequestMapping("/helloFeign")
    String helloFeign();
}
