package hello.advanced;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//v0은 그냥 틀

//v1은 측정 시간 로그  HelloTraceV1
//HelloTraceV1 사용

//v2는 같은 요청 사이클이면 아이디가 같게 하고 깊이를 화살표로 표시 HelloTraceV2
//HelloTraceV2 사용

//v3는 아이디를 파라미터로 넘겨줄 필요가 없는 FieldLogTrace 하지만 FieldLogTrace를 사용하면 동시성 문제
//동시성 문제 해결하기 위해, 아이디가 겹치지 않게 하고 로그 안썩이게 ThreadLocalLogTrace

//v4눈 템플릿 사용, 변하지 않는 코드는 템플릿에 넣어놓고 변하는 코드는 템플릿 메서드 상속해 구현하는, AbstractTemplate

//v5는 템플릿 콜백 패턴 사용, 변하지 않는 코드 Context(문맥) 변하는 코드 Strategy(전략) 으로 정의해
//v4처럼 생성자 파라미터가 아닌 문맥의 메서드에 실행 할 때 마다 원하는 전략 주입해 실행
//문맥 메서드 안에 Strategy(전략)의 call() 실행
@SpringBootApplication
public class AdvancedApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdvancedApplication.class, args);
	}

}
