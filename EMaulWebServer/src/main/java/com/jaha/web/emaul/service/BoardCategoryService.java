package com.jaha.web.emaul.service;

import com.jaha.web.emaul.constants.UserPrivacy;
import com.jaha.web.emaul.model.Apt;
import com.jaha.web.emaul.model.BoardCategory;

import java.util.List;

public interface BoardCategoryService {

    List<BoardCategory> fetchBoardCategories(Apt apt);

    void modifyBoardCategories(Apt apt, List<String> ids, List<UserPrivacy> userPrivacies, List<String> ords);
}
