package hello.advanced.app.v0;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryV0 {

    public void save(String itemId) {

        //저장 로직
        //ex로 넘어오면 예외 발생
        if (itemId.equals("ex")) {

            throw new IllegalStateException("예외 발생!");
        }

        //상품을 저장하는데 1초가 걸린다고 가정
        sleep(1000);
    }

    private void sleep(int millis) {
        try {

            Thread.sleep(millis);
        } catch (InterruptedException e) {

            e.printStackTrace();
        }
    }
}
