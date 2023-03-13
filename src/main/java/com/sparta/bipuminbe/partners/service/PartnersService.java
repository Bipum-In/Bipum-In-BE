package com.sparta.bipuminbe.partners.service;

import com.sparta.bipuminbe.partners.repository.PartnersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PartnersService {
    private final PartnersRepository partnersRepository;
}
