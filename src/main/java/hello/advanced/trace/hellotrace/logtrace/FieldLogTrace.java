package hello.advanced.trace.hellotrace.logtrace;

import hello.advanced.trace.TraceId;
import hello.advanced.trace.TraceStatus;
import lombok.extern.slf4j.Slf4j;

//TraceId 를 파라미터로 넘기지 않고

//FieldLogTrace 는 기존에 만들었던 HelloTraceV2 와 거의 같은 기능을 한다.
//TraceId 를 동기화 하는 부분만 파라미터를 사용하는 것에서 TraceId traceIdHolder 필드를 사용하도록 변경되었다.
//이제 직전 로그의 TraceId 는 파라미터로 전달되는 것이 아니라 FieldLogTrace 의 필드인 traceIdHolder 에 저장된다.
//여기서 중요한 부분은 로그를 시작할 때 호출하는 syncTraceId() 와 로그를 종료할 때 호출하는 releaseTraceId() 이다.

//syncTraceId()
//TraceId 를 새로 만들거나 앞선 로그의 TraceId 를 참고해서 동기화하고, level 도 증가한다.
//최초 호출이면 TraceId 를 새로 만든다.
//직전 로그가 있으면 해당 로그의 TraceId 를 참고해서 동기화하고, level 도 하나 증가한다.
//결과를 traceIdHolder 에 보관한다.

//releaseTraceId()
//메서드를 추가로 호출할 때는 level 이 하나 증가해야 하지만, 메서드 호출이 끝나면 level 이 하나 감소해야 한다.
//releaseTraceId() 는 level 을 하나 감소한다.
//만약 최초 호출( level==0 )이면 내부에서 관리하는 traceId 를 제거한다

//실행 결과를 보면 트랜잭션ID 도 동일하게 나오고, level 을 통한 깊이도 잘 표현된다.
//FieldLogTrace.traceIdHolder 필드를 사용해서 TraceId 가 잘 동기화 되는 것을 확인할 수 있다.
//이제 불필요하게 TraceId 를 파라미터로 전달하지 않아도 되고, 애플리케이션의 메서드 파라미터도 변경하지 않아도 된다
@Slf4j
public class FieldLogTrace implements LogTrace {

    private static final String START_PREFIX = "-->";
    private static final String COMPLETE_PREFIX = "<--";
    private static final String EX_PREFIX = "<X-";

    //traceId를 어딘가는 들고있어야함, 동기화, 동시성 이슈 발생
    //기존에는 파라미터로 넘겼다면 이번엔 보관해서 사용
    //직전 로그의 아이디는 파라미터로 전달하는게 아니라 이 필드에서 보관
    private TraceId traceIdHolder;

    @Override
    public TraceStatus begin(String message) {
        //아이디 활당 되있는지 판단해 값 넣는
        syncTraceId();
        //메서드 이후에는 id를 꺼내서 쓰는
        TraceId traceId = traceIdHolder;
        Long startTimeMs = System.currentTimeMillis();
        log.info("[{}] {}{}", traceId.getId(), addSpace(START_PREFIX, traceId.getLevel()), message);

        return new TraceStatus(traceId, startTimeMs, message);
    }

    //처음이면 id없으니 할당해주는, 있다면 level증가
    //이 메서드를 실행하고 나면 traceIdHolder 에 값이 들어가있는게 보장됨
    private void syncTraceId() {
        if (traceIdHolder == null) {
            traceIdHolder = new TraceId();
        } else {
            traceIdHolder = traceIdHolder.createNextId();
        }
    }

    //종료 할 떄 complete메서드 호출
    @Override
    public void end(TraceStatus status) {
        complete(status, null);

    }

    @Override
    public void exception(TraceStatus status, Exception e) {
        complete(status, e);
    }

    //리포지토리 가장 빨리 완료 메서드 출력하니 리포지토리에서 releaseTraceId() 가장 빨리 호출
    private void complete(TraceStatus status, Exception e) {
        Long stopTimeMs = System.currentTimeMillis();
        long resultTimeMs = stopTimeMs - status.getStartTimeMs();
        TraceId traceId = status.getTraceId();
        if (e == null) {
            log.info("[{}] {}{} time={}ms", traceId.getId(), addSpace(COMPLETE_PREFIX, traceId.getLevel()), status.getMessage(), resultTimeMs);
        } else {
            log.info("[{}] {}{} time={}ms ex={}", traceId.getId(), addSpace(EX_PREFIX, traceId.getLevel()), status.getMessage(), resultTimeMs, e.toString());
        }

        releaseTraceId();
    }

    //메서드를 추가로 호출할 때는 level 이 하나 증가해야 하지만,
    //메서드 호출이 끝나면 level 이 하나 감소
    //끝날때는 값을 빼주거나 제거해주는
    private void releaseTraceId() {

        //첫번쨰 lebel이면 들어가다 나와서 다시 마지막에 왔다는 의미
        if (traceIdHolder.isFirstLevel()) {
            traceIdHolder = null; //destroy
        //첫번째가 아닌 중간단계라면 같은 아이디 이전 레벨 넘겨주는
        } else {
            traceIdHolder = traceIdHolder.createPreviousId();
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
