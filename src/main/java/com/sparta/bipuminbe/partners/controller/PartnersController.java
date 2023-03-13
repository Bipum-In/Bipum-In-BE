package com.sparta.bipuminbe.partners.controller;

import com.sparta.bipuminbe.partners.service.PartnersService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PartnersController {
    private final PartnersService partnersService;
}
