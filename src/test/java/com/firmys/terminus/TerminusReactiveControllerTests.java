package com.firmys.terminus;

import com.firmys.terminus.controllers.TestLatestReactiveController;
import com.firmys.terminus.controllers.TestLatestV0ReactiveController;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest
@ActiveProfiles("reactive")
@Import({TestLatestReactiveController.class, TestLatestV0ReactiveController.class})
public class TerminusReactiveControllerTests {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldReturnOkStatusForRootPath() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(TerminusConstants.TERMINUS_VERSION_HEADER, "0");

        webTestClient.get()
                .uri("/test")
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .exchange()
                .expectStatus().isOk();
    }

}
