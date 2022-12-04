package hello.advanced.trace.hellotrace;

import hello.advanced.trace.TraceId;
import hello.advanced.trace.TraceStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

//TraceId TraceStatus 사용
//HelloTraceV1 을 사용해서 실제 로그를 시작하고 종료할 수 있고
//로그를 출력하고 실행시간도 측정할 수 있다

//하지만 트랜잭션 아이디는 다르고 레벨값이 항상 0
@Slf4j
@Component
public class HelloTraceV1 {

    private static final String START_PREFIX = "-->";
    private static final String COMPLETE_PREFIX = "<--";
    private static final String EX_PREFIX = "<X-";

    //시작, 예시 [796bccd9] OrderController.request() //로그 시작
    public TraceStatus begin(String message) {

        //메시지 들어오면 아이디 생성, 실행시간 측정
        TraceId traceId = new TraceId();
        Long startTimeMs = System.currentTimeMillis();

        //로그 출력. addSpace에서 레벨에 따라 화살표가 어떻게 나올지 결정해 출력
        log.info("[{}] {}{}", traceId.getId(), addSpace( START_PREFIX, traceId.getLevel() ), message);

        //나갔다가 end할때 들어오는
        //반환하는 이 객체를 컨트롤러 시버시 리포지토리가 갖고있다 end메서드에 다시 넣어줌
        return new TraceStatus(traceId, startTimeMs, message);
    }

    //end나 exception은 둘중에 선택됨,

    //로그를 정상 종료
    //끝, 예시 [796bccd9] OrderController.request() time=1016ms
    //begin() 로 만든 객체를 컨트롤러 시버시 리포지토리가갖고 있다 end() 파라미터로 주입해줌
    public void end(TraceStatus status) {
        complete(status, null);
    }

    //exception는 로그에서 예외가 터지면
    public void exception(TraceStatus status, Exception e) {
        complete(status, e);
    }


    private void complete(TraceStatus status, Exception e) {
        //종료시간 찍고
        Long stopTimeMs = System.currentTimeMillis();

        //종료시간 - 시작시간, 측정 시간
        long resultTimeMs = stopTimeMs - status.getStartTimeMs();

        //TraceId가져옴
        TraceId traceId = status.getTraceId();

        if (e == null) {//예외 없으면
            //아이디 방향 메시지 측정시간
            log.info("[{}] {}{} time={}ms", traceId.getId(), addSpace(COMPLETE_PREFIX, traceId.getLevel()),
                    status.getMessage(), resultTimeMs);

        } else {//예외 있다면
            log.info("[{}] {}{} time={}ms ex={}", traceId.getId(), addSpace(EX_PREFIX, traceId.getLevel()),
                    status.getMessage(), resultTimeMs, e.toString());
        }
    }

    //들어온 화살표와 레벨을 이용해

    //prefix: -->
    //level 0:
    //level 1: |-->
    //level 2: | |-->

    //prefix: <--
    //level 0:
    //level 1: |<--
    //level 2: | |<--

    //prefix: <Xlevel 0:
    //level 1: |<Xlevel 2: | |<X
    private static String addSpace(String prefix, int level) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < level; i++) {

            sb.append((i == level - 1) ? "|" + prefix : "|   ");
        }
        return sb.toString();
    }

}
//이것은 온전한 테스트 코드가 아니다. 일반적으로 테스트라고 하면 자동으로 검증하는 과정이 필요하다.
//이 테스트는 검증하는 과정이 없고 결과를 콘솔로 직접 확인해야 한다.
//이렇게 응답값이 없는 경우를 자동으로 검증하려면 여러가지 테스트 기법이 필요하다.
//이번 강의에서는 예제를 최대한 단순화 하기 위해 검증 테스트를 생략