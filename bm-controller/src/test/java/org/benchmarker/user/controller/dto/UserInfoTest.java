package org.benchmarker.user.controller.dto;

import org.benchmarker.user.model.User;
import org.benchmarker.user.model.UserGroup;
import org.benchmarker.user.model.UserGroupJoin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class UserInfoTest {

    @Mock
    private User mockUser;

    @Mock
    private UserGroupJoin mockUserGroupJoin1;

    @Mock
    private UserGroupJoin mockUserGroupJoin2;

    @Mock
    private UserGroup mockUserGroup1;

    @Mock
    private UserGroup mockUserGroup2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mocking user
        when(mockUser.getId()).thenReturn("1");
        when(mockUser.getSlackWebhookUrl()).thenReturn("https://example.com/slack");
        when(mockUser.getSlackNotification()).thenReturn(true);
        when(mockUser.getEmail()).thenReturn("user@example.com");
        when(mockUser.getEmailNotification()).thenReturn(false);

        // Mocking user groups
        when(mockUserGroup1.getId()).thenReturn("group1");
        when(mockUserGroup2.getId()).thenReturn("group2");

        // Mocking user group joins
        when(mockUserGroupJoin1.getUserGroup()).thenReturn(mockUserGroup1);
        when(mockUserGroupJoin2.getUserGroup()).thenReturn(mockUserGroup2);
        when(mockUser.getUserGroupJoin()).thenReturn(Arrays.asList(mockUserGroupJoin1, mockUserGroupJoin2));
    }

    @Test
    void testFrom() {
        // given
        UserInfo expectedUserInfo = UserInfo.builder()
            .id("1")
            .slackWebhookUrl("https://example.com/slack")
            .slackNotification(true)
            .email("user@example.com")
            .emailNotification(false)
            .userGroup(Arrays.asList(mockUserGroup1, mockUserGroup2))
            .build();

        // when
        UserInfo actualUserInfo = UserInfo.from(mockUser);

        // thens
        assertEquals(expectedUserInfo.getId(), actualUserInfo.getId());
        assertEquals(expectedUserInfo.getSlackWebhookUrl(), actualUserInfo.getSlackWebhookUrl());
        assertEquals(expectedUserInfo.getSlackNotification(), actualUserInfo.getSlackNotification());
        assertEquals(expectedUserInfo.getEmail(), actualUserInfo.getEmail());
        assertEquals(expectedUserInfo.getEmailNotification(), actualUserInfo.getEmailNotification());
        assertEquals(expectedUserInfo.getUserGroup(), actualUserInfo.getUserGroup());
    }
}
