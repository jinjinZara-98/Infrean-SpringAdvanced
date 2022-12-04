package hello.advanced.trace.hellotrace;

import hello.advanced.trace.TraceId;
import hello.advanced.trace.TraceStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

//로그 추적기 V2 - 파라미터로 동기화 개발
//트랜잭션ID와 메서드 호출의 깊이를 표현하는 하는 가장 단순한 방법은 첫 로그에서 사용한 트랜잭션ID와 level 을 다음 로그에 넘겨주면 된다.
//현재 로그의 상태 정보인 트랜잭션ID 와 level 은 TraceId 에 포함되어 있다. 따라서 TraceId 를 다음로그에 넘겨주면 된다

@Slf4j
@Component
public class HelloTraceV2 {

    private static final String START_PREFIX = "-->";
    private static final String COMPLETE_PREFIX = "<--";
    private static final String EX_PREFIX = "<X-";

    //HelloTrace1과 똑같음
    public TraceStatus begin(String message) {
        TraceId traceId = new TraceId();
        Long startTimeMs = System.currentTimeMillis();
        log.info("[" + traceId.getId() + "] " + addSpace(START_PREFIX, traceId.getLevel()) + message);
        return new TraceStatus(traceId, startTimeMs, message);
    }

    //V2에서 추가, TraceId를 받도록
    //같은 아이디 받도록 서비스부터 이 메서드 사용 컨트롤러는 그냥 위에꺼
    public TraceStatus beginSync(TraceId beforeTraceId, String message) {
        //createNextId() id는 그대로 유지 lebel만 증가한 TraceId객체 반환
        TraceId nextId = beforeTraceId.createNextId();
        Long startTimeMs = System.currentTimeMillis();

        //createNextId()로 증가한 레벨 파라미터로 넣으니 깊이에 맞게 메시지 출력되는
        log.info("[" + nextId.getId() + "] " + addSpace(START_PREFIX, nextId.getLevel()) + message);

        return new TraceStatus(nextId, startTimeMs, message);
    }

    public void end(TraceStatus status) {
        complete(status, null);
    }

    public void exception(TraceStatus status, Exception e) {
        complete(status, e);
    }

    private void complete(TraceStatus status, Exception e) {
        Long stopTimeMs = System.currentTimeMillis();
        long resultTimeMs = stopTimeMs - status.getStartTimeMs();
        TraceId traceId = status.getTraceId();
        if (e == null) {
            log.info("[" + traceId.getId() + "] " + addSpace(COMPLETE_PREFIX, traceId.getLevel()) + status.getMessage() + " time=" + resultTimeMs + "ms");
        } else {
            log.info("[" + traceId.getId() + "] " + addSpace(EX_PREFIX, traceId.getLevel()) + status.getMessage() + " time=" + resultTimeMs + "ms" + " ex=" + e);
        }
    }

    private static String addSpace(String prefix, int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append((i == level - 1) ? "|" + prefix : "|   ");
        }
        return sb.toString();
    }

}