package com.sparta.bipuminbe.requests.service;

import com.sparta.bipuminbe.common.entity.ReturnRequest;
import com.sparta.bipuminbe.common.entity.Supply;
import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.requests.dto.RetrunRequestDto;
import com.sparta.bipuminbe.requests.repository.ReturnRequestRepository;
import com.sparta.bipuminbe.supply.repository.SupplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class ReturnRequestService {
    private final ReturnRequestRepository returnRequestRepository;

    private final SupplyRepository supplyRepository;

    @Transactional
    public ResponseEntity<RetrunRequestDto> creatRetrunRequest(RetrunRequestDto retrunRequestDto, User user){
        Supply supply = supplyRepository.findById(retrunRequestDto.getSupplyId())
                .orElseThrow(() -> new IllegalArgumentException("일치하는 제품번호가 없습니다" + retrunRequestDto.getSupplyId()));

        ReturnRequest returnRequest = ReturnRequest.builder()
                .retrunRequestDto(retrunRequestDto)
                .supply(supply)
                .user(user)
                .build();
        returnRequestRepository.save(returnRequest);
        return ResponseEntity.ok(RetrunRequestDto.of(returnRequest));
    }
}
