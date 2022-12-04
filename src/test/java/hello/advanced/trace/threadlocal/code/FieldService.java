package hello.advanced.trace.threadlocal.code;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FieldService {

    private String nameStore;

    public String logic(String name) {
        log.info("저장 name = {} -> nameStore ={ }", name, nameStore);
        nameStore = name;

        sleep(1000);
        //name을 저장했따가 1초동안 일이 발생한 다음 조회하는데 저장한 nameStore를 저장
        log.info("조회 nameStore = {}", nameStore);

        return nameStore;
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);

        } catch (InterruptedException e) {

            e.printStackTrace();
        }
    }
}
