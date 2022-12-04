package hello.advanced.app.v4;

import hello.advanced.trace.hellotrace.logtrace.LogTrace;
import hello.advanced.trace.template.AbstractTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceV4 {

    private final OrderRepositoryV4 orderRepository;
    private final LogTrace trace;

    public void orderItem(String itemId) {

        //기존에 반환타입이 void여서 제네릭에서는 Void로

        //제네릭에서 반환 타입이 필요한데, 반환할 내용이 없으면 Void 타입을 사용하고
        //null 을 반환하면 된다. 참고로 제네릭은 기본 타입인 void , int 등을 선언할 수 없다.

        //제네릭으로 넣을 수 있는 타입에는 void 못 넣음 Void로
        //int도 Integer로
        AbstractTemplate<Void> template = new AbstractTemplate<>(trace) {
            @Override
            protected Void call() {
                orderRepository.save(itemId);
                return null;
            }
        };

        template.execute("OrderService.orderItem()");

    }
}
