package org.benchmarker.user.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import org.benchmarker.user.controller.dto.GroupAddDto;
import org.benchmarker.user.controller.dto.GroupInfo;
import org.benchmarker.user.controller.dto.GroupUpdateDto;
import org.benchmarker.user.helper.UserHelper;
import org.benchmarker.user.model.User;
import org.benchmarker.user.service.GroupService;
import org.benchmarker.user.service.UserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
    private UserContext userContext;
    @MockBean
    private GroupService groupService;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext,
        RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(documentationConfiguration(restDocumentation))
            .alwaysDo(print())
            .build();
    }

    @Test
    @WithMockUser(username = "userId", roles = {"USER"})
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
        User userStub = UserHelper.createDefaultUser();

        // when
        when(groupService.createGroup(any(), any())).thenReturn(infoStub);
        when(userContext.getCurrentUser()).thenReturn(userStub);

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

    @Test
    @WithMockUser(username = "adminId", roles = {"ADMIN"})
    @DisplayName("관리자가 그룹에 사용자를 추가할 수 있다")
    void test2() throws Exception {
        // given
        String groupId = "testGroupId";
        User userStub = UserHelper.createDefaultAdmin();
        String userId = userStub.getId();
        GroupInfo infoStub = GroupInfo.builder()
            .id(groupId)
            .users(Arrays.asList(userId))
            .build();

        // when
        when(groupService.addUserToGroupAdmin(anyString(), anyString())).thenReturn(infoStub);
        when(userContext.getCurrentUser()).thenReturn(userStub);

        // then
        mockMvc.perform(post("/api/groups/{group_id}/users/{user_id}", groupId, userId))
            .andDo((result) -> {
                System.out.println(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
            })
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(groupId))
            .andExpect(jsonPath("$.users").isArray())
            .andExpect(jsonPath("$.users", hasSize(1)))
            .andExpect(jsonPath("$.users[0]").value(userId));
    }

    @Test
    @WithMockUser(username = "userId", roles = {"USER"})
    @DisplayName("사용자가 그룹에서 나가기를 요청할 수 있다")
    void test3() throws Exception {
        // given
        String groupId = "testGroupId";
        String userId = "userId";
        GroupInfo infoStub = GroupInfo.builder()
            .id(groupId)
            .users(new ArrayList<>())
            .build();

        // when
        when(userContext.getCurrentUser()).thenReturn(UserHelper.createDefaultUser());
        when(groupService.deleteUserFromGroup(anyString(), anyString(), anyString(),
            anyBoolean())).thenReturn(infoStub);

        // then
        mockMvc.perform(delete("/api/groups/{group_id}/users/{user_id}", groupId, userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(groupId))
            .andExpect(jsonPath("$.users").isArray())
            .andExpect(jsonPath("$.users", hasSize(0)));
    }

    @Test
    @WithMockUser(username = "userId", roles = {"USER"})
    @DisplayName("사용자가 특정 그룹 정보를 조회할 수 있다")
    void test4() throws Exception {
        // given
        String groupId = "testGroupId";
        User defaultUser = UserHelper.createDefaultUser();

        GroupInfo infoStub = GroupInfo.builder()
            .id(groupId)
            .name("Test Group")
            .users(Arrays.asList(defaultUser.getId(), "otherUser"))
            .build();

        // when
        when(userContext.getCurrentUser()).thenReturn(defaultUser);
        when(groupService.getGroupInfo(any(), any())).thenReturn(infoStub);

        // then
        mockMvc.perform(get("/api/groups/{group_id}", groupId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(groupId))
            .andExpect(jsonPath("$.name").value("Test Group"))
            .andExpect(jsonPath("$.users").isArray())
            .andExpect(jsonPath("$.users", hasSize(2)));
    }

    @Test
    @WithMockUser(username = "userId", roles = {"USER"})
    @DisplayName("사용자가 그룹 정보를 업데이트할 수 있다")
    void test5() throws Exception {
        // given
        String groupId = "testGroupId";
        GroupUpdateDto dto = new GroupUpdateDto();
        User defaultUser = UserHelper.createDefaultUser();
        GroupInfo infoStub = GroupInfo.builder()
            .id(groupId)
            .name("Updated Group Name")
            .users(Arrays.asList(defaultUser.getId(), "userId2"))
            .build();

        // when
        when(userContext.getCurrentUser()).thenReturn(defaultUser);
        when(groupService.updateGroupUser(any(), any(), any())).thenReturn(infoStub);

        // then
        mockMvc.perform(patch("/api/groups/{group_id}", groupId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(groupId))
            .andExpect(jsonPath("$.name").value("Updated Group Name"))
            .andExpect(jsonPath("$.users").isArray())
            .andExpect(jsonPath("$.users", hasSize(2)));
    }

}