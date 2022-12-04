package hello.advanced.trace.hellotrace.logtrace;

import hello.advanced.trace.TraceId;
import hello.advanced.trace.TraceStatus;
import lombok.extern.slf4j.Slf4j;
//동시성 문제 해결

//스레드 로컬 동기화 개발
//FieldLogTrace 에서 발생했던 동시성 문제를 ThreadLocal 로 해결
//1초안에 연속 2번 호출해도 정상적
@Slf4j
public class ThreadLocalLogTrace implements LogTrace {

    private static final String START_PREFIX = "-->";
    private static final String COMPLETE_PREFIX = "<--";
    private static final String EX_PREFIX = "<X-";

    //아이디를 보관해 그 아이디에 맞는걸 반환하는
    private ThreadLocal<TraceId> traceIdHolder = new ThreadLocal<>();

    @Override
    public TraceStatus begin(String message) {
        syncTraceId();
        //스레드로컽로부터 아이디 가져오는
        TraceId traceId = traceIdHolder.get();
        Long startTimeMs = System.currentTimeMillis();
        log.info("[{}] {}{}", traceId.getId(), addSpace(START_PREFIX, traceId.getLevel()), message);
        return new TraceStatus(traceId, startTimeMs, message);
    }

    private void syncTraceId() {
        //먼저 값을 꺼내야함
        TraceId traceId = traceIdHolder.get();

        //없으면 새로운 id
        if (traceId == null) {
            traceIdHolder.set(new TraceId());
        //값 있으면 레벨 올리는
        } else {
            traceIdHolder.set(traceId.createNextId());
        }
    }

    @Override
    public void end(TraceStatus status) {
        complete(status, null);
    }

    @Override
    public void exception(TraceStatus status, Exception e) {
        complete(status, e);
    }

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

    //releaseTraceId() 를 통해 level 이 점점 낮아져서 2 1 0이 되면 로그를 처음 호출한 부분으로 돌아온 것이다.
    //따라서 이 경우 연관된 로그 출력이 끝난 것이다. 이제 더 이상 TraceId 값을 추적하지 않아도 된다.
    //그래서 traceId.isFirstLevel() ( level==0 )인 경우
    //ThreadLocal.remove() 를 호출해서 쓰레드 로컬에 저장된 값을 제거
    private void releaseTraceId() {
        //값을 꺼내고
        TraceId traceId = traceIdHolder.get();

        //첫번째면 즉 가장 마지막이면 제거, 스레드 로컬에 해당 전용 보관소만 제거되는
        //생성된 스레드는 스레드풀에 다시 반환, 스레드 생성 비용이 비싸서 다시 생성하기 부담
        //다시 반환될때 스레드에 값을 제거해주지 않으면 나중에 스레드풀에서 스레드를 생성할때 뭐가 나올지 모르므로 예전에 반환됫던
        //스레드를 다시 꺼낼 수가 있음. 만약 반횐되었던 스레드를 꺼내 같은 로직을 실행하면 존재했던 값을 불러들여 원치 않는 결과 얻을 수도
        if (traceId.isFirstLevel()) {
            traceIdHolder.remove(); //destroy
            System.out.println();
        } else {
            traceIdHolder.set(traceId.createPreviousId());
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
