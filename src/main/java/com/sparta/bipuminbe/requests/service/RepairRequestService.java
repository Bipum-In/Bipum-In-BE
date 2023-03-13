package com.sparta.bipuminbe.requests.service;

import com.sparta.bipuminbe.common.entity.RepairRequest;
import com.sparta.bipuminbe.common.entity.Supply;
import com.sparta.bipuminbe.common.entity.SupplyRequest;
import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.requests.dto.RepairRequestDto;
import com.sparta.bipuminbe.requests.dto.SupplyRequestDto;
import com.sparta.bipuminbe.requests.repository.RepairRequestRepository;
import com.sparta.bipuminbe.supply.repository.SupplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class RepairRequestService {

    private final RepairRequestRepository repairRequestRepository;

    private final SupplyRepository supplyRepository;

    @Transactional
    public ResponseEntity<RepairRequestDto> creatRepairRequest(RepairRequestDto repairRequestDto, User user){

        Supply supply = supplyRepository.findById(repairRequestDto.getSupplyId())
                .orElseThrow(() -> new IllegalArgumentException("일치하는 제품번호가 없습니다" + repairRequestDto.getSupplyId()));

        RepairRequest repairRequest = RepairRequest.builder()
                .repairRequestDto(repairRequestDto)
                .supply(supply)
                .user(user)
                .build();
        repairRequestRepository.save(repairRequest);
        return ResponseEntity.ok(RepairRequestDto.of(repairRequest));
    }
}
