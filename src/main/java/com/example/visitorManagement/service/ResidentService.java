package com.example.visitorManagement.service;

import com.example.visitorManagement.dto.AllVisitsByPageResponseBody;
import com.example.visitorManagement.dto.VisitDto;
import com.example.visitorManagement.entity.Flat;
import com.example.visitorManagement.entity.User;
import com.example.visitorManagement.entity.Visit;
import com.example.visitorManagement.enums.VisitStatus;
import com.example.visitorManagement.repo.UserRepo;
import com.example.visitorManagement.repo.VisitRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ResidentService {

    @Autowired
    private VisitRepo visitRepo;

    @Autowired
    private UserRepo userRepo;

    public String updateVisit(VisitStatus newStatus, Long id){
        Visit visit = visitRepo.findById(id).get();
        if(VisitStatus.WAITING.equals(visit.getStatus())){
            visit.setStatus(newStatus);
            visitRepo.save(visit);
            return "Done";
        }
        else {

        }
        return "Error";
    }

    @Transactional
    public List<VisitDto> getPendingVisits(Long userId){
        List<VisitDto> visitDtoList = new ArrayList<>();
        User user = userRepo.findById(userId).get();
        if(user != null){
            Flat flat = user.getFlat();
            List<Visit> visitList = visitRepo.findByStatusAndFlat(VisitStatus.WAITING, flat);
            for(Visit visit: visitList){
                visitDtoList.add(fromVisit(visit));
            }
        }
        return visitDtoList;
    }


    @Transactional
    public AllVisitsByPageResponseBody getAllVisits(Long userId, Pageable pageable){
        AllVisitsByPageResponseBody responseBody = new AllVisitsByPageResponseBody();
        List<VisitDto> visitDtoList = new ArrayList<>();
        User user = userRepo.findById(userId).get();
        if(user != null){
            Flat flat = user.getFlat();
            Page<Visit> visitPage = visitRepo.findAll(pageable);
            List<Visit> visitList = visitPage.stream().toList();
            responseBody.setTotalPage(visitPage.getTotalPages());
            responseBody.setTotalRow(visitPage.getTotalElements());
            for(Visit visit: visitList){
                visitDtoList.add(fromVisit(visit));
            }
        }
        responseBody.setVisits(visitDtoList);
        return responseBody;
    }

    private VisitDto fromVisit(Visit visit){
        VisitDto visitDto = VisitDto.builder()
                .urlOfImage(visit.getImageUrl())
                .noOfPeople(visit.getNoOfPeople())
                .purpose(visit.getPurpose())
                .visitorName(visit.getVisitor().getName())
                .inTime(visit.getInTime())
                .outTime(visit.getOutTime())
                .idNumber(visit.getVisitor().getIdNumber())
                .status(visit.getStatus())
                .flatNumber(visit.getFlat().getNumber())
                .build();
        return visitDto;
    }

}
