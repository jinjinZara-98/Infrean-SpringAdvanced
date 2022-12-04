package hello.advanced.trace.callback;

/**
 *  v5에서 사용되는
 *
 *  변하는 코드 실행하는
 *  반환 타입 다를 수 있으니 제네릭
 * */
public interface TraceCallback<T> {

    //반환타입 제네릭
    T call();
}
