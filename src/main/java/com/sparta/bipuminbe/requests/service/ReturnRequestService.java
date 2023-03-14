package com.sparta.bipuminbe.requests.service;

import com.sparta.bipuminbe.requests.repository.ReturnRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReturnRequestService {
    private final ReturnRequestRepository returnRequestRepository;
}
