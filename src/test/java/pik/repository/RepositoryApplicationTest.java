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
class RepositoryApplicationTest {

    @Autowired
    private MockMvc mocMvc;

    @Test
    void contextLoads1() throws Exception {
        /*this.mocMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"ala\",\"password\":\"12345\"}"))
                .andDo(print())
                .andExpect(status().isOk());*/

        this.mocMvc.perform(post("/oauth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"password\":\"12345\"}"))
                .andDo(print())
                .andExpect(status().is(400));


        /*this.mocMvc.perform(post("/api/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"ala\",\"password\":\"12345\",\"name\":\"alicja\", \"surname\":\"turowska\"}"))
                .andDo(print())
                .andExpect(status().isOk());*/

        this.mocMvc.perform(post("/oauth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"ala\",\"name\":\"alicja\", \"surname\":\"turowska\"}"))
                .andDo(print())
                .andExpect(status().is(400));
    }

}

