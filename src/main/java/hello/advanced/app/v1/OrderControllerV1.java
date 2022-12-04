package hello.advanced.app.v1;

import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.hellotrace.HelloTraceV1;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//요구사항
//모든 PUBLIC 메서드의 호출과 응답 정보를 로그로 출력
//애플리케이션의 흐름을 변경하면 안됨
//로그를 남긴다고 해서 비즈니스 로직의 동작에 영향을 주면 안됨
//메서드 호출에 걸린 시간
//정상 흐름과 예외 흐름 구분
//예외 발생시 예외 정보가 남아야 함
//메서드 호출의 깊이 표현
//HTTP 요청을 구분
//HTTP 요청 단위로 특정 ID를 남겨서 어떤 HTTP 요청에서 시작된 것인지 명확하게 구분이 가능해야함
//HTTP 요청을 구분
//HTTP 요청 단위로 특정 ID를 남겨서 어떤 HTTP 요청에서 시작된 것인지 명확하게 구분이 가능해야함
//트랜잭션 ID (DB 트랜잭션X), 여기서는 하나의 HTTP 요청이 시작해서 끝날 때 까지를 하나의 트랜잭션이라 함

//예시
//정상 요청
//[796bccd9] OrderController.request()
//[796bccd9] |-->OrderService.orderItem()
//[796bccd9] | |-->OrderRepository.save()
//[796bccd9] | |<--OrderRepository.save() time=1004ms OrderRepository.save()실행시간
//[796bccd9] |<--OrderService.orderItem() time=1014ms OrderService.orderItem()실행시간
//[796bccd9] OrderController.request() time=1016ms OrderController.request() 실행시간

//[796bccd9] http요청의 아이디디
//예외 발생
//[b7119f27] OrderController.request()
//[b7119f27] |-->OrderService.orderItem()
//[b7119f27] | |-->OrderRepository.save()
//[b7119f27] | |<X-OrderRepository.save() time=0ms
//ex=java.lang.IllegalStateException: 예외 발생!
//[b7119f27] |<X-OrderService.orderItem() time=10ms
//ex=java.lang.IllegalStateException: 예외 발생!
//[b7119f27] OrderController.request() time=11ms
//ex=java.lang.IllegalStateException: 예외 발생!

//애플리케이션의 모든 로직에 직접 로그를 남겨도 되지만, 그것보다는 더 효율적인 개발 방법이 필요하다.
//특히 트랜잭션ID와 깊이(로그 공백)를 표현하는 방법은 기존 정보를 이어 받아야 하기 때문에 단순히 로그만 남긴다고 해결할 수 있는 것은 아니다.
//요구사항에 맞추어 애플리케이션에 효과적으로 로그를 남기기 위한 로그 추적기를 개발

//HelloTraceV1 덕분에 직접 로그를 하나하나 남기는 것 보다는 편하게 여러가지 로그를 남길 수 있었다.
//하지만 로그를 남기기 위한 코드가 생각보다 너무 복잡

//아직 구현하지 못한 요구사항은 메서드 호출의 깊이를 표현하고, 같은 HTTP 요청이면 같은 트랜잭션 ID를 남기는 것이다.
//이 기능은 직전 로그의 깊이와 트랜잭션 ID가 무엇인지 알아야 할 수 있는 일이다.
//예를 들어서 OrderController.request() 에서 로그를 남길 때 어떤 깊이와 어떤 트랜잭션 ID를 사용했는지를
//그 다음에 로그를 남기는 OrderService.orderItem() 에서 로그를 남길 때 알아야한다.
//결국 현재 로그의 상태 정보인 트랜잭션ID 와 level 이 다음으로 전달되어야 한다.
//정리하면 로그에 대한 문맥( Context ) 정보가 필요
@RestController
@RequiredArgsConstructor
public class OrderControllerV1 {

    private final OrderServiceV1 orderService;
    private final HelloTraceV1 trace;

    @GetMapping("/v1/request")
    public String request(String itemId) {

        //trace.exception에서 status쓰기 위해
        //try 상위에 TraceStatus status 코드를 선언해야 한다.
        //만약 try 안에서 TraceStatus status 를 선언하면 try 블록안에서만 해당 변수가
        //유효하기 때문에 catch 블록에 넘길 수 없다. 따라서 컴파일 오류가 발생
        TraceStatus status = null;

        try {
            //trace.begin("OrderController.request()") : 로그를 시작할 때 메시지 이름으로 컨트롤러 이름 + 메서드 이름을.
            //이렇게 하면 어떤 컨트롤러와 메서드가 호출되었는지 로그로 편리하게 확인할 수있다. 물론 수작업이다.
            //단순하게 trace.begin() , trace.end() 코드 두 줄만 적용하면 될 줄 알았지만, 실상은 그렇지 않다.
            //trace.exception() 으로 예외까지 처리해야 하므로 지저분한 try , catch 코드가 추가된다.
            //begin() 의 결과 값으로 받은 TraceStatus status 값을 end() , exception() 에 넘겨야 한다
            status = trace.begin("OrderController.request()");

            //여기까지 수행하는데 예외가 터지면  다음 end메서드 호출 안됨, 때문에 try catch해줘야
            orderService.orderItem(itemId);

            //서비스에 아이템 아이디 던져주고 종료
            trace.end(status);

            return "ok";

        } catch (Exception e) {

            trace.exception(status, e);

            //예외를 꼭 다시 던져주어야 한다. 안 그러면 예외를 먹어버리므로로
            //이후에 정상흐름으로 동작한다. 로그는 애플리케이션에 흐름에 영향을 주면 안된다.
            //로그 때문에 예외가 사라지면 안된다.
            throw e;
        }
    }
}
