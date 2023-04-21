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
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PartnersService {

    private final PartnersRepository partnersRepository;
    private final SupplyRepository supplyRepository;


    // 협력 업체 리스트.
    @Transactional(readOnly = true)
    public ResponseDto<List<PartnersDto>> getPartnersList() {
        List<Partners> partnersList = partnersRepository.findByDeletedFalse();
        List<PartnersDto> partnersDtoList = new ArrayList<>();
        for (Partners partners : partnersList) {
            partnersDtoList.add(PartnersDto.of(partners));
        }
        return ResponseDto.success(partnersDtoList);
    }


    // 협력 업체 생성.
    @Transactional
    public ResponseDto<String> createPartners(PartnersDto partnersDto) {
        if (checkPartners(partnersDto.getPartnersName())) {
            throw new CustomException(ErrorCode.DuplicatedPartners);
        }
        checkDeletedPartners(partnersDto.getPartnersName());
        partnersRepository.save(Partners.builder().partnersDto(partnersDto).build());
        return ResponseDto.success("협력 업체 등록 완료.");
    }


    // 삭제된 협력업체 체크
    private void checkDeletedPartners(String partnersName) {
        Optional<Partners> partners = partnersRepository.findByPartnersNameAndDeletedTrue(partnersName);
        if (partners.isPresent()) {
            partners.get().reEnroll();
        }
    }


    // 협력 업체 수정.
    @Transactional
    public ResponseDto<String> updatePartners(Long partnersId, PartnersDto partnersDto) {
        Partners partners = getPartners(partnersId);
        if (!partners.getPartnersName().equals(partnersDto.getPartnersName()) && checkPartners(partnersDto.getPartnersName())) {
            throw new CustomException(ErrorCode.DuplicatedPartners);
        }
        checkDeletedPartners(partnersDto.getPartnersName());
        partners.update(partnersDto);
        return ResponseDto.success("협력 업체 정보 수정 완료.");
    }


    // 협력 업체 삭제.
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
        return partnersRepository.findByPartnersIdAndDeletedFalse(partnersId).orElseThrow(
                () -> new CustomException(ErrorCode.NotFoundPartners));
    }

    private Boolean checkPartners(String partnersName) {
        return partnersRepository.existsByPartnersNameAndDeletedFalse(partnersName);
    }


    // 협력 업체 페이지. 검색 가능.
    @Transactional(readOnly = true)
    public ResponseDto<Page<PartnersDto>> getPartnersPage(String keyword, int page, int size) {
        Pageable pageable = getPageable(page, size);

        Page<Partners> partners = partnersRepository.getPartnersList(keyword, pageable);

        List<PartnersDto> partnersDtoList = convertToDto(partners.getContent());

        return ResponseDto.success(new PageImpl<>(partnersDtoList, partners.getPageable(), partners.getTotalElements()));
    }

    private Pageable getPageable(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        return PageRequest.of(page - 1, size, sort);
    }

    private List<PartnersDto> convertToDto(List<Partners> partners) {
        List<PartnersDto> partnersDtoList = new ArrayList<>();
        for (Partners partner : partners) {
            partnersDtoList.add(PartnersDto.of(partner));
        }
        return partnersDtoList;
    }
}
