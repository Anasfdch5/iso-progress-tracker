package com.iso_progress_tracker.controllers;

import com.iso_progress_tracker.entities.Action;
import com.iso_progress_tracker.services.ActionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/actions")
public class ActionController {

    private final ActionService actionService;

    public ActionController(ActionService actionService) {
        this.actionService = actionService;
    }



    @PostMapping("/save")
    public String saveAction(@ModelAttribute("newAction") Action action) {
        // Set default status value for newly minted tasks
        if (action.getStatus() == null || action.getStatus().isEmpty()) {
            action.setStatus("A faire");
        }

        // Save the action to the database
        actionService.saveAction(action);

        return "redirect:/processes/" + action.getProcess().getId();  //respecting inheritence here
    }
}