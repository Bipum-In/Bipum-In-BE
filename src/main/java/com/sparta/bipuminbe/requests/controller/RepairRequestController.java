package com.sparta.bipuminbe.requests.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RepairRequestController {

    private final RepairRequestService repairRequestService;
}
