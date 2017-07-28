package com.jaha.web.emaul.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jaha.web.emaul.constants.UserPrivacy;
import com.jaha.web.emaul.model.Apt;
import com.jaha.web.emaul.model.BoardCategory;
import com.jaha.web.emaul.repo.BoardCategoryRepository;

@Service
public class BoardCategoryServiceImpl implements BoardCategoryService {

    @Autowired
    private BoardCategoryRepository boardCategoryRepository;

    @Override
    public List<BoardCategory> fetchBoardCategories(Apt apt) {
        // return boardCategoryRepository.findByTypeNotAndAptId("today", apt.id, new Sort(Sort.Direction.ASC, "ord"));
        List<String> typeList = new ArrayList<String>();
        typeList.add("notice");
        typeList.add("community");
        typeList.add("complaint");
        typeList.add("tts");

        // return boardCategoryRepository.findByAptIdAndTypeInAndDelYn(apt.id, typeList, delYn, new Sort(Sort.Direction.ASC, "ord"));
        return boardCategoryRepository.findByAptIdAndTypeIn(apt.id, typeList, new Sort(Sort.Direction.ASC, "ord"));
    }

    @Override
    @Transactional
    public void modifyBoardCategories(Apt apt, List<String> ids, List<UserPrivacy> userPrivacies, List<String> ords) {
        List<BoardCategory> boardCategories = boardCategoryRepository.findByTypeNotAndAptId("today", apt.id, new Sort(Sort.Direction.ASC, "ord"));
        for (BoardCategory boardCategory : boardCategories) {
            for (int i = 0; i < ids.size(); i++) {
                if (boardCategory.id.equals(Long.parseLong(ids.get(i)))) {
                    boardCategory.setUserPrivacy(userPrivacies.get(i));
                    boardCategory.ord = Integer.parseInt(ords.get(i));
                    break;
                }
            }
        }
    }
}
