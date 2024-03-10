package org.benchmarker.user.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.benchmarker.user.controller.dto.GroupAddDto;
import org.benchmarker.user.controller.dto.GroupInfo;
import org.benchmarker.user.controller.dto.UserInfo;
import org.benchmarker.user.repository.UserGroupJoinRepository;
import org.benchmarker.user.repository.UserGroupRepository;
import org.benchmarker.user.service.GroupService;
import org.benchmarker.user.service.UserContext;
import org.benchmarker.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.util.annotations.RestDocsTest;

@SpringBootTest
@RestDocsTest
class GroupApiControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @MockBean
    private UserContext userContext;
    @MockBean
    private UserGroupRepository userGroupRepository;
    @MockBean
    private UserGroupJoinRepository userGroupJoinRepository;
    @MockBean
    private GroupService groupService;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext,
        RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(documentationConfiguration(restDocumentation))
            .build();
    }

    @Test
    @WithMockUser(username = "userId", roles={"USER"})
    @DisplayName("그룹 생성 시 그룹 정보를 반환한다")
    void test1() throws Exception {
        // given
        String groupId = "testId";
        String groupName = "testName";
        String userId = "userId";
        GroupAddDto dto = GroupAddDto.builder()
            .id(groupId)
            .name(groupName)
            .build();
        GroupInfo infoStub = GroupInfo.builder()
            .id(groupId)
            .name(groupName)
            .users(Arrays.asList(userId))
            .build();

        // when
        when(groupService.createGroup(any(),any())).thenReturn(infoStub);

        // then
        mockMvc.perform(post("/api/group")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(dto)))
            .andDo((result) -> {
                assertThat(result.getResponse().getStatus()).isEqualTo(200);
            })
            .andExpect(jsonPath("$.id").value(groupId))
            .andExpect(jsonPath("$.name").value(groupName))
            .andExpect(jsonPath("$.users").isArray())
            .andExpect(jsonPath("$.users").isNotEmpty());
    }

}