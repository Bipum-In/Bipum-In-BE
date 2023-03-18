package com.sparta.bipuminbe.common.setting.service;

import com.sparta.bipuminbe.category.dto.CategoryDto;
import com.sparta.bipuminbe.category.repository.CategoryRepository;
import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.entity.Category;
import com.sparta.bipuminbe.common.entity.Department;
import com.sparta.bipuminbe.common.entity.Partners;
import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.exception.CustomException;
import com.sparta.bipuminbe.common.exception.ErrorCode;
import com.sparta.bipuminbe.common.setting.dto.SettingResponseDto;
import com.sparta.bipuminbe.common.setting.dto.SwitchResponseDto;
import com.sparta.bipuminbe.department.dto.DepartmentDto;
import com.sparta.bipuminbe.department.repository.DepartmentRepository;
import com.sparta.bipuminbe.partners.dto.PartnersDto;
import com.sparta.bipuminbe.partners.repository.PartnersRepository;
import com.sparta.bipuminbe.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SettingService {
    private final PartnersRepository partnersRepository;
    private final DepartmentRepository departmentRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public ResponseDto<SettingResponseDto> getSettingPage() {
        // 파트너 전체 목록 조회
        List<Partners> partners = partnersRepository.findAll();
        List<PartnersDto> partnersDtos = new ArrayList<>();

        for(Partners partner : partners){
            partnersDtos.add(PartnersDto.of(partner));
        }

        // 부서 전체 목록 조회.. 내용물이 너무 없는데요?? 부서별 인원 수라던지 추가 정보 있으면 좋을듯..
        List<Department> departments = departmentRepository.findAll();
        List<DepartmentDto> departmentDtos = new ArrayList<>();

        for(Department department : departments){
            departmentDtos.add(DepartmentDto.of(department));
        }

        // 카테고리 전체 목록 조회
        List<Category> categories = categoryRepository.findAll();
        List<CategoryDto> categoryDtos = new ArrayList<>();

        for(Category category : categories){
            categoryDtos.add(CategoryDto.of(category));
        }

        return ResponseDto.success(SettingResponseDto.of(partnersDtos, departmentDtos, categoryDtos));
    }

    @Transactional
    public ResponseDto<SwitchResponseDto> switchAlarm(User user) {
        User foundUser = userRepository.findById(user.getId()).orElseThrow(
                () -> new CustomException(ErrorCode.NotFoundUser)
        );

        foundUser.switchAlarm(foundUser.getAlarm());
        String message = foundUser.getAlarm() == true ? "알림 On" : "알림 Off";

        return ResponseDto.success(SwitchResponseDto.of(message, foundUser.getAlarm()));
    }
}
