package com.star.client;

import com.star.framework.annotation.StarryServiceClientScan;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @Author: zzStar
 * @Date: 05-30-2021 18:02
 */
@StarryServiceClientScan(basePackage = "com.star.client")
public class SpringClientTest {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SpringClientTest.class);
        TestController controller = (TestController) context.getBean("testController");
        controller.test();
    }

}
