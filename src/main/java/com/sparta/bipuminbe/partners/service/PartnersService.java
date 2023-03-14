package com.sparta.bipuminbe.partners.service;

import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.entity.Partners;
import com.sparta.bipuminbe.common.exception.CustomException;
import com.sparta.bipuminbe.common.exception.ErrorCode;
import com.sparta.bipuminbe.partners.dto.PartnersDto;
import com.sparta.bipuminbe.partners.repository.PartnersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PartnersService {

    private final PartnersRepository partnersRepository;

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
        checkPartners(partnersDto.getPartnersName());
        partnersRepository.save(Partners.builder().partnersDto(partnersDto).build());
        return ResponseDto.success("협력 업체 등록 완료.");
    }

    @Transactional
    public ResponseDto<String> updatePartners(Long partnersId, PartnersDto partnersDto) {
        checkPartners(partnersDto.getPartnersName());
        getPartners(partnersId).update(partnersDto);
        return ResponseDto.success("협력 업체 정보 수정 완료.");
    }

    @Transactional
    public ResponseDto<String> deletePartners(Long partnersId) {
        partnersRepository.delete(getPartners(partnersId));
        return ResponseDto.success("협력 업체 삭제 완료.");
    }

    private Partners getPartners(Long partnersId) {
        return partnersRepository.findById(partnersId).orElseThrow(
                () -> new CustomException(ErrorCode.NotFoundPartners));
    }

    private void checkPartners(String partnersName) {
        if(partnersRepository.existsByPartnersName(partnersName)){
            throw new CustomException(ErrorCode.DuplicatedPartners);
        }
    }
}
