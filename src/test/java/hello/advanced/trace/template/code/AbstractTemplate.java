package hello.advanced.trace.template.code;

import lombok.extern.slf4j.Slf4j;

//템플릿메서드 패턴을 적용해 변하는 부분: 비즈니스 로직, 변하지 않는 부분: 시간 측정 을 분리

//디자인 패턴 중 하나 추상탬플렛에 변하지 않는 로직들 모아둠
//콜이라는 메서드 만들어 여기에 변하는 부분
//변하는 부분을 자식클래스에 오버라이딩해서 구성하도록
@Slf4j
public abstract class AbstractTemplate {

    //변하지 않는 부분
    //실행,
    public void execute() {
        long startTime = System.currentTimeMillis();

        //비즈니스 로직 실행
        call(); //상속
        //비즈니스 로직 종료

        //비즈니스 로직 종료 후에 측정시간 출력
        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;
        log.info("resultTime={}", resultTime);
    }

    //변하는 부분
    protected abstract void call();
}
