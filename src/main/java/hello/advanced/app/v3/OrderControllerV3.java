package hello.advanced.app.v3;

import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.hellotrace.logtrace.LogTrace;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//v2처럼 메서드에 파라미터로 아이디를 전닳해주지 않아도 됨

//기대한 것과 전혀 다른 문제가 발생한다. 트랜잭션ID 도 동일하고, level 도 뭔가 많이 꼬인 것 같다
//분명히 테스트 코드로 작성할 때는 문제가 없었는데, 무엇이 문제일까?
//동시성 문제
//사실 이 문제는 동시성 문제이다.
//FieldLogTrace 는 싱글톤으로 등록된 스프링 빈이다.
//이 객체의 인스턴스가 애플리케이션에 딱 1 존재한다는 뜻이다.
//이렇게 하나만 있는 인스턴스의 FieldLogTrace.traceIdHolder 필드를
//여러 쓰레드가 동시에 접근하기 때문에 문제가 발생한다.
//실무에서 한번 나타나면 개발자를 가장 괴롭히는 문제도 바로 이러한 동시성 문제

//같은 url 여러 번 연속으로 하면 트랜잭션ID 도 동일하고, level 도 뭔가 많이 꼬인

//스레드로컬 사용하면
//로그를 직접 분리해서 확인해보면 각각의 쓰레드 nio-8080-exec-3 , nio-8080-exec-4 별로 로그가
//정확하게 나누어지고 아이디도 정확함
@RestController
@RequiredArgsConstructor
public class OrderControllerV3 {

    private final OrderServiceV3 orderService;
    //FieldLogTrace 를 수동으로 스프링 빈으로 등록
    //LogTraceConfig를 설정클래스로 등록해 빈으로 등록함
    private final LogTrace trace;

    @GetMapping("/v3/request")
    public String request(String itemId) {

        TraceStatus status = null;
        try {
            status = trace.begin("OrderController.request()");

            //traceid 안넘겨줌
            orderService.orderItem(itemId);

            //시작할때 만든 객체를 end()에
            trace.end(status);

            return "ok";
        } catch (Exception e) {
            trace.exception(status, e);

            throw e;//예외를 꼭 다시 던져주어야 한다.
        }
    }
}
