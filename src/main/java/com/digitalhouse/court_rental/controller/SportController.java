package com.digitalhouse.court_rental.controller;

import com.digitalhouse.court_rental.dto.SportDTO;
import com.digitalhouse.court_rental.service.SportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sports")
@CrossOrigin("*")
public class SportController {

    @Autowired
    private SportService sportService;

    @GetMapping("/status/5")
    public List<SportDTO> getSportsByStatusFive() {
        return sportService.findByStatusFive();
    }
}
