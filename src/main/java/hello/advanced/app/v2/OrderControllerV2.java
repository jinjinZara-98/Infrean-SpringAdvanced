package hello.advanced.app.v2;

import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.hellotrace.HelloTraceV2;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//HelloTraceV2 사용하는, id는 같도 lebel만 달라지는
//트랜잭션ID와 메서드 호출의 깊이를 표현하는 하는 가장 단순한 방법은
//첫 로그에서 사용한 트랜잭션ID와level 을 다음 로그에 넘겨주면 된다.
//현재 로그의 상태 정보인 트랜잭션ID 와 level 은 TraceId 에 포함되어 있다. 따라서 TraceId 를 다음로그에 넘겨주면 된다

//이렇게 하려면 처음 로그를 남기는 OrderController.request() 에서 로그를 남길 때 어떤 깊이와 어떤 트랜잭션 ID를 사용했는지
//다음 차례인 OrderService.orderItem() 에서 로그를 남기는 시점에 알아야한다.
//결국 현재 로그의 상태 정보인 트랜잭션ID 와 level 이 다음으로 전달되어야 한다.
//이 정보는 TraceStatus.traceId 에 담겨있다. 따라서 traceId 를 컨트롤러에서 서비스를 호출할 때 넘겨주면 된다

//실행 로그를 보면 같은 HTTP 요청에 대해서 트랜잭션ID 가 유지되고, level 도 잘 표현되는 것을 확인

//남은 문제
//HTTP 요청을 구분하고 깊이를 표현하기 위해서 TraceId 동기화가 필요하다.
//TraceId 의 동기화를 위해서 관련 메서드의 모든 파라미터를 수정해야 한다.
//만약 인터페이스가 있다면 인터페이스까지 모두 고쳐야 하는 상황이다.
//로그를 처음 시작할 때는 begin() 을 호출하고, 처음이 아닐때는 beginSync() 를 호출해야 한다.
/**만약에 컨트롤러를 통해서 서비스를 호출하는 것이 아니라, 다른 곳에서 서비스를 처음으로 호출하는
//상황이라면 파리미터로 넘길 TraceId 가 없다.**/
@RestController
@RequiredArgsConstructor
public class OrderControllerV2 {

    private final OrderServiceV2 orderService;
    private final HelloTraceV2 trace;

    @GetMapping("/v2/request")
    public String request(String itemId) {

        TraceStatus status = null;
        try {
            status = trace.begin("OrderController.request()");

            //id를 같이 넘겨줌, 같은 요청으로 돌아간 컨트롤러 서비스 리포지토리 같은 아이디
            orderService.orderItem(status.getTraceId(), itemId);
            trace.end(status);

            return "ok";
        } catch (Exception e) {
            trace.exception(status, e);

            throw e;//예외를 꼭 다시 던져주어야 한다.
        }
    }
}
