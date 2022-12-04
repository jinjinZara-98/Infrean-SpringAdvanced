package hello.advanced.app.v2;

import hello.advanced.trace.TraceId;
import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.hellotrace.HelloTraceV2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceV2 {

    private final OrderRepositoryV2 orderRepository;
    //같은 요청으로 돌아가는 컨트롤러 서비스 리포지토리가 같은 아이디를 공유하도록
    //새로 만듬
    private final HelloTraceV2 trace;

    public void orderItem(TraceId traceId, String itemId) {

        TraceStatus status = null;
        try {
            //메시지와 아이디 같이 넘겨줌
            status = trace.beginSync(traceId, "OrderService.orderItem()");

            //리포지토리에도 id를 같이 넘겨줌
            orderRepository.save(status.getTraceId(), itemId);
            trace.end(status);

        } catch (Exception e) {
            trace.exception(status, e);

            throw e;
        }

    }
}
