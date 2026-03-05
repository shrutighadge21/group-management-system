package com.shru.group_management_system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class GroupController {

    @Autowired
    private GroupRepository groupRepository;

    @GetMapping("/dashboard")
    public String dashboard(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        int pageSize = 5;

        Page<Group> groupPage;

        if (keyword.isEmpty()) {
            groupPage = groupRepository.findAll(PageRequest.of(page, pageSize));
        } else {
            groupPage = groupRepository.findByNameContainingIgnoreCase(
                    keyword,
                    PageRequest.of(page, pageSize));
        }

        long totalCount = groupRepository.count();
        long activeCount = groupRepository.findAll()
                .stream().filter(Group::isActive).count();
        long inactiveCount = totalCount - activeCount;

        model.addAttribute("groups", groupPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", groupPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("inactiveCount", inactiveCount);

        return "dashboard";
    }

    @GetMapping("/add-group")
    public String addGroupPage(Model model) {
        model.addAttribute("group", new Group());
        return "add-group";
    }

    @PostMapping("/save-group")
    public String saveGroup(@ModelAttribute Group group) {
        group.setActive(true);
        groupRepository.save(group);
        return "redirect:/dashboard";
    }

    @GetMapping("/delete-group/{id}")
    public String deleteGroup(@PathVariable Long id) {
        groupRepository.deleteById(id);
        return "redirect:/dashboard";
    }

    @GetMapping("/toggle-status/{id}")
    public String toggleStatus(@PathVariable Long id) {
        Group group = groupRepository.findById(id).orElse(null);
        if (group != null) {
            group.setActive(!group.isActive());
            groupRepository.save(group);
        }
        return "redirect:/dashboard";
    }
}