package pik.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RepositoryApplicationTests {

    @Autowired
    private MockMvc mocMvc;

    @Test
    void contextLoads() throws Exception {
        this.mocMvc.perform(post("/api/login")
                //.header("Authorization", base64ForTestUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"ala\",\"hash\":12345}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"token\":\"token\",\"email\":\"ala\",\"hash\":12345,\"name\":\"Alicja\",\"surname\":\"Turowska\"}"));
    }

}

