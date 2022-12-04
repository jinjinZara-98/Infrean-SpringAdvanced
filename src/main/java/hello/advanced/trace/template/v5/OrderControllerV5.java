package hello.advanced.trace.template.v5;

import hello.advanced.trace.callback.TraceCallback;
import hello.advanced.trace.callback.TraceTemplate;
import hello.advanced.trace.hellotrace.logtrace.LogTrace;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderControllerV5 {

    private final OrderServiceV5 orderService;
    //템플릿 콜백 패턴에 사용되는 틀
    private final TraceTemplate template;

    //@Autowired 생략, 의존관계 주입받으면서 필요한 TraceTemplate 생성
    public OrderControllerV5(OrderServiceV5 orderService, LogTrace trace) {
        this.orderService = orderService;
        this.template = new TraceTemplate(trace);
    }

    @GetMapping("/v5/request")
    public String request(String itemId) {

        //메시지와 콜백 파라미터로 넘겨줌
        //콜백 인터페이스를 익명 클래스로
        //콜백 메서드의 결과를 받아 그걸 반환값으로로
        //익명 내부 클래스, 람다
       return template.execute("OrderController.request()", new TraceCallback<>() {

            @Override
            public String call() {
                orderService.orderItem(itemId);
                return "ok";
            }
        });
    }
}
