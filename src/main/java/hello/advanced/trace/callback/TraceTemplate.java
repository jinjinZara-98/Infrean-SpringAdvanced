package hello.advanced.trace.callback;

import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.hellotrace.logtrace.LogTrace;

/**
 * v5에서 사용되는
 *
 * 템플릿 콜백 패턴에 사용되는
 * 변하지 않는 코드를 모아둠
 * 템플릿을 정의했지만 그냥 템플릿 패턴 처럼 변경되는 코드가 있는 call() 를 따로 만들지 않음
 * 콜백을 템플릿 메서드인 execute() 로 받음, 익명 클래스로
 *
 * TraceId는 아이디와 레벨 생성
 * TraceStatus는 시간 측정
 * ThreadLocalLogTrace는 위 두 클래스 이용해 아이디와 측정 시간 로그로 출력
 *
 * 아이디는 ThreadLocalLogTrace, TraceStatus는 여기서
 */
public class TraceTemplate {

    private final LogTrace trace;

    //@Autowired 생략됨, 자동 의존 주입으로 빈으로 등록된 ThreadLocalLogTrace가 주입됨
    public TraceTemplate(LogTrace trace) {
        this.trace = trace;
    }

    //메시지와 콟백 동시에 전달
    //실행 시점에 원한는 전략 전달
    //이전이랑 똑같이 이 메서드의 반환값은 콜백의 call() 반환값
    public <T> T execute(String message, TraceCallback<T> callback) {

        TraceStatus status = null;

        try {
            status = trace.begin(message);

            //로직 호출
            //오버라이딩한 콜백의 call()
            T result = callback.call();

            trace.end(status);
            return result;

        } catch (Exception e) {
            trace.exception(status, e);
            throw e;
        }
    }

}
