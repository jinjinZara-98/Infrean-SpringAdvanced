package hello.advanced.trace.template;

import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.hellotrace.logtrace.LogTrace;

//AbstractTemplate 은 템플릿 메서드 패턴에서 부모 클래스이고, 템플릿 역할
//추상템플릿, 직접 생성하지 않으니 추상으로
public abstract class AbstractTemplate<T> {

    private final LogTrace trace;

    //객체를 생성할 때 내부에서 사용할 LogTrace trace 를 전달 받는다
    public AbstractTemplate(LogTrace trace) {
        this.trace = trace;
    }

    //로그에 출력할 message 를 외부에서 파라미터로 전달받는다
    //반환타입이 다 다르니 제네릭 넣어줌, 추상 템플릿 관련된걸 생성할때 매개변수로 들어온 타입으로 맞춰지는
    public T execute(String message) {

        TraceStatus status = null;

        try {
            //위에 생성자로 받은 LogTrace 객체로 메시지 받아
            //아이디 생성 하고 시간 측정 시작
            status = trace.begin(message);

            //로직 호출, 추상화
            T result = call();

            //시작할 때 만든 TraceStatus객체 파라미터로
            trace.end(status);

            //핵심로직의 결과를 반환
            return result;
        } catch (Exception e) {

            trace.exception(status, e);
            throw e;
        }
    }

    //abstract T call() 은 변하는 부분을 처리하는 메서드, 상속해서 구현
    //반환값 제네릭으로
    protected abstract T call();
}
