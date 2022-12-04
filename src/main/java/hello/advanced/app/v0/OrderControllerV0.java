package hello.advanced.app.v0;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderControllerV0 {

    private final OrderServiceV0 orderService;

    // http://localhost:8080/v0/request?itemId=hello
    //@RequestParam 생략되어있음
    @GetMapping("/v0/request")
    public String request(String itemId) {

        orderService.orderItem(itemId);

        return "ok";
    }
//    public String request(Student student) {
//
//        System.out.println(student.grade + " " + student.name);
//
//        return "ok";
//    }

//    @Data
//    class Student {
//        int grade;
//        String name;
//    }
}
