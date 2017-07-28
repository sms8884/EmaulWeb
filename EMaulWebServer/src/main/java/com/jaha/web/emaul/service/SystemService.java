package com.jaha.web.emaul.service;

import java.util.List;
import java.util.Map;

import com.jaha.web.emaul.model.Provision;
import com.jaha.web.emaul.model.SystemFaq;
import com.jaha.web.emaul.model.SystemNotice;
import com.jaha.web.emaul.model.User;

/**
 * Created by shavrani on 16. 06. 02..
 */
public interface SystemService {

    Map<String, Object> getSystemNoticeList(User user, Map<String, Object> params);

    SystemNotice getSystemNotice(User user, Long id);

    SystemNotice getSystemNotice(User user, Map<String, Object> params);

    SystemNotice systemNoticeSave(SystemNotice systemNotice);

    void deleteSystemNotice(Long id);

    Map<String, Object> getSystemFaqList(User user, Map<String, Object> params);

    SystemFaq getSystemFaq(User user, Long id);

    SystemFaq getSystemFaq(User user, Map<String, Object> params);

    SystemFaq systemFaqSave(SystemFaq systemFaq);

    void deleteSystemFaq(Long id);

    List<Provision> getSystemProvisionList();

    Provision getSystemProvision(User user, Long id);

    Provision getSystemProvisionUseStatus(User user, Long id, String status);

    Provision systemProvisionSave(Provision provision);

}
