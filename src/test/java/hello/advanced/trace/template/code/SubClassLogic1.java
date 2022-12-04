package hello.advanced.trace.template.code;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SubClassLogic1 extends AbstractTemplate {

    //변하는 부분인 call메서드를 오버리이딩해 이 클래스에 맞게 변경
    @Override
    protected void call() {
        log.info("비즈니스 로직1 실행");
    }
}
