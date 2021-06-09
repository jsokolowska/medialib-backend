package pik.repository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class RepositoryApplicationSpec extends Specification{

    @Autowired
    private MockMvc mvc;

    def "test /oauth/login - missing emai;"(){
        expect: "status_code==400"
        mvc.perform(post("/oauth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"password\":\"12345\"}"))
                .andDo(print())
                .andExpect(status().is(400));
    }

    def "test /oauth/signup - wrong data"(){
        expect: "status_code==400"
        mvc.perform(post("/oauth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"ala\",\"name\":\"alicja\", \"surname\":\"turowska\"}"))
                .andDo(print())
                .andExpect(status().is(400));
    }

    def "test /api/upload - right data"(){
        expect:
        MvcResult mocRes =  mvc.perform(get("/api/upload")
                .param("fileId", "asdjasdwdasd.txt"))
                .andExpect(status().isOk())
                .andReturn()
        mocRes.getResponse().getContentAsString().contains("X-AUTH-TOKEN")
        mocRes.getResponse().getContentAsString().contains("url")
    }

    def "test /api/upload - wrong data"(){
        expect:
        mvc.perform(get("/api/upload")
                .param("fileId", "asdjasd wdasd.txt"))
                .andExpect(status().is(400))
    }
}
