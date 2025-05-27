package com.firmys.terminus;

import com.firmys.terminus.filters.TerminusVersionFilter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TerminusTestApp.class)
@AutoConfigureMockMvc
class TerminusApplicationTests {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private TerminusVersionFilter terminusVersionFilter;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).addFilter(terminusVersionFilter).build();
    }

    @Test
    void shouldReturnOkStatusForRootPath() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add(TerminusConstants.TERMINUS_VERSION_HEADER, "0");
        var result = this.mockMvc.perform(get("/test")
                .headers(headers)).andExpect(status().isOk()).andReturn();
        Assertions.assertNotNull(result);
    }
}
