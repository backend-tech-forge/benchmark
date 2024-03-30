package org.benchmarker.bmcontroller.user.controller;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmcontroller.common.controller.annotation.GlobalControllerModel;
import org.benchmarker.bmcontroller.user.controller.dto.GroupAddDto;
import org.benchmarker.bmcontroller.user.controller.dto.GroupInfo;
import org.benchmarker.bmcontroller.user.service.GroupService;
import org.benchmarker.bmcontroller.user.service.UserContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
@GlobalControllerModel
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final UserContext userContext;


    @GetMapping("/group/register")
    @PreAuthorize("hasAnyRole('USER')")
    public String createGroupGet(@ModelAttribute(name = "groupAddDto") GroupAddDto groupAddDto) {
        return "group/register";
    }

    @PostMapping("/group/register")
    @PreAuthorize("hasAnyRole('USER')")
    public String createGroup(
        @ModelAttribute(name = "groupAddDto") GroupAddDto dto) {
        String userId = userContext.getCurrentUser().getId();
        GroupInfo group = groupService.createGroup(dto, userId);
        return "redirect:/groups/" + group.getId();
    }

    /**
     * Get group info
     * <p> if user does not join group exception occurred </p>
     *
     * @param group_id
     * @param model
     * @return group info page
     */
    @GetMapping("/groups/{group_id}")
    @PreAuthorize("hasAnyRole('USER')")
    public String groupGet(@PathVariable("group_id") String group_id, Model model) {
        GroupInfo groupInfo = null;
        if (userContext.getCurrentUser().getRole().isAdmin()) {
            groupInfo = groupService.getGroupInfoAdmin(group_id);
        } else {
            groupInfo = groupService.getGroupInfo(group_id, userContext.getCurrentUser().getId());
        }
        model.addAttribute("groupInfo", groupInfo);
        System.out.println(groupInfo);
        return "group/info";
    }

    @GetMapping("/groups")
    @PreAuthorize("hasAnyRole('USER')")
    public String groups(Pageable pageable, Model model) {
        List<GroupInfo> groupInfo = Arrays.asList();


        Page<GroupInfo> groupInfoPage;
        if (userContext.getCurrentUser().getRole().isAdmin()) {
            groupInfoPage = groupService.getAllGroupInfoAdmin(pageable);
        } else {
            groupInfoPage = groupService.getAllGroupInfo(userContext.getCurrentUser().getId(), pageable);
        }
        model.addAttribute("groupInfo", groupInfoPage.getContent());
        model.addAttribute("currentPage", pageable.getPageNumber());
        model.addAttribute("totalPages", groupInfoPage.getTotalPages());
        return "group/list";
    }


}
