package com.sparta.bipuminbe.requests.service;

import com.sparta.bipuminbe.common.entity.Supply;
import com.sparta.bipuminbe.common.entity.SupplyRequest;
import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.requests.dto.SupplyRequestDto;
import com.sparta.bipuminbe.requests.repository.SupplyRequestRepository;
import com.sparta.bipuminbe.supply.repository.SupplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SupplyRequestService {

    private final SupplyRequestRepository supplyRequestRepository;

    private final SupplyRepository supplyRepository;

    public ResponseEntity<SupplyRequestDto> creatSupplyRequest(SupplyRequestDto supplyRequestDto, User user){

        Supply supply = supplyRepository.findById(supplyRequestDto.getSupplyId())
                .orElseThrow(() -> new IllegalArgumentException("일치하는 제품번호가 없습니다" + supplyRequestDto.getSupplyId()));

        SupplyRequest supplyRequest = SupplyRequest.builder()
                .supplyRequestDto(supplyRequestDto)
                .supply(supply)
                .user(user)
                .build();
        supplyRequestRepository.save(supplyRequest);
        return ResponseEntity.ok(SupplyRequestDto.of(supplyRequest));
    }
}
