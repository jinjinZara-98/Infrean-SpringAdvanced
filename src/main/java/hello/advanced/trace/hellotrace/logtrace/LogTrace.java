package hello.advanced.trace.hellotrace.logtrace;

import hello.advanced.trace.TraceStatus;

//필드 동기화 - 개발
//앞서 로그 추적기를 만들면서 다음 로그를 출력할 때 트랜잭션ID 와 level 을 동기화 하는 문제가 있었다.
//이 문제를 해결하기 위해 TraceId 를 파라미터로 넘기도록 구현했다.
/**
 * 이렇게 해서 동기화는 성공했지만, 로그를 출력하는 모든 메서드에 TraceId 파라미터를 추가해야 하는 문제가 발생했다
 * TraceId 를 파라미터로 넘기지 않고 이 문제를 해결할 수 있는 방법은 없을까?
 **/
//이런 문제를 해결할 목적으로 새로운 로그 추적기를 만들어보자.
//이제 프로토타입 버전이 아닌 정식 버전으로 제대로 개발

//LogTrace 인터페이스에는 로그 추적기를 위한 최소한의 기능인 begin() , end() , exception() 를 정의했다.
//이제 파라미터를 넘기지 않고 TraceId 를 동기화 할 수 있는 FieldLogTrace 구현체 생성
public interface LogTrace {

    TraceStatus begin(String message);

    void end(TraceStatus status);

    void exception(TraceStatus status, Exception e);
}
