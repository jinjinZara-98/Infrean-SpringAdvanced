package hello.advanced.app.v4;

import hello.advanced.trace.hellotrace.logtrace.LogTrace;
import hello.advanced.trace.template.AbstractTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**v0와 v3를 비교하면 v0는 핵심로직만 있지만 v3는 핵심로직과 부가기능이 섞여있는데
//부가기능코드가 더 맣은걸 볼 수 있음
//배보다 배꼽이 더 큰 상황
//컨트롤러 서비스 리포지토리 겹치는 부가기능을 하나로 모듈화해 껴넣는
//중복을 별도의 메서드로 뽑아내면 될 것 같다. 그런데, try ~ catch 는 물론이고,
//핵심 기능 부분이 중간에 있어서 단순하게 메서드로 추출하는 것은 어렵다.
//변하는 것과 변하지 않는 것을 분리
//좋은 설계는 변하는 것과 변하지 않는 것을 분리하는 것이다.
//핵심 기능 부분은 변하고, 로그 추적기를 사용하는 부분은 변하지 않는 부분
//템플릿 메서드 패턴(Template Method Pattern)은 이런 문제를 해결하는 디자인 패턴**/
@RestController
@RequiredArgsConstructor
public class OrderControllerV4 {

    private final OrderServiceV4 orderService;
    //@RequiredArgsConstructor로 생성자 만들어져 빈 객체 자동 의존 주입
    private final LogTrace trace;

    @GetMapping("/v4/request")
    public String request(String itemId) {

        //AbstractTemplate에 공통로직, 객체를 생성하면서 바로 상속받은 자식 클래스 정의
        //익명 클래스로
        //자동 의존 주입받은 ThreadLocalLogTrace를 파라미터로
        AbstractTemplate<String> template = new AbstractTemplate<>(trace) {

            //call메서드에 핵심로직만, 변하는 로직
            @Override
            protected String call() {
                orderService.orderItem(itemId);
                return "ok";
            }
        };

        //AbstractTemplate 의 execute 메서드 실행
        //execute 안에 핵심로직인 call() 호출함, 위에서 정의해둠
        //execute() 가 실행되면 call() 반환값을 리턴해줌
        //그래서 위에 만든 call()의 반환값 ok가 찍힘
        //로그로 남길 메시지 전달
        return template.execute("OrderController.request()");
    }
}
