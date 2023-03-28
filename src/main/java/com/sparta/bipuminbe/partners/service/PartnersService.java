package com.sparta.bipuminbe.partners.service;

import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.entity.Partners;
import com.sparta.bipuminbe.common.entity.Supply;
import com.sparta.bipuminbe.common.exception.CustomException;
import com.sparta.bipuminbe.common.exception.ErrorCode;
import com.sparta.bipuminbe.partners.dto.PartnersDto;
import com.sparta.bipuminbe.partners.repository.PartnersRepository;
import com.sparta.bipuminbe.supply.repository.SupplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PartnersService {

    private final PartnersRepository partnersRepository;
    private final SupplyRepository supplyRepository;

    @Transactional(readOnly = true)
    public ResponseDto<List<PartnersDto>> getPartnersList() {
        List<Partners> partnersList = partnersRepository.findAll();
        List<PartnersDto> partnersDtoList = new ArrayList<>();
        for (Partners partners : partnersList) {
            partnersDtoList.add(PartnersDto.of(partners));
        }
        return ResponseDto.success(partnersDtoList);
    }

    @Transactional
    public ResponseDto<String> createPartners(PartnersDto partnersDto) {
        if (checkPartners(partnersDto.getPartnersName())) {
            throw new CustomException(ErrorCode.DuplicatedPartners);
        }
        partnersRepository.save(Partners.builder().partnersDto(partnersDto).build());
        return ResponseDto.success("협력 업체 등록 완료.");
    }

    @Transactional
    public ResponseDto<String> updatePartners(Long partnersId, PartnersDto partnersDto) {
        Partners partners = getPartners(partnersId);
        if (!partners.getPartnersName().equals(partnersDto.getPartnersName()) && checkPartners(partnersDto.getPartnersName())) {
            throw new CustomException(ErrorCode.DuplicatedPartners);
        }
        partners.update(partnersDto);
        return ResponseDto.success("협력 업체 정보 수정 완료.");
    }

    @Transactional
    public ResponseDto<String> deletePartners(Long partnersId) {
        // 연관관계 끊기.
        List<Supply> supplyList = supplyRepository.findByPartners_PartnersId(partnersId);
        for (Supply supply : supplyList) {
            supply.deletePartners();
        }
        partnersRepository.delete(getPartners(partnersId));
        return ResponseDto.success("협력 업체 삭제 완료.");
    }

    private Partners getPartners(Long partnersId) {
        return partnersRepository.findById(partnersId).orElseThrow(
                () -> new CustomException(ErrorCode.NotFoundPartners));
    }

    private Boolean checkPartners(String partnersName) {
        return partnersRepository.existsByPartnersName(partnersName);
    }
}
