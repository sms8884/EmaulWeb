package com.jaha.web.emaul.controller;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.jaha.web.emaul.model.GasCheck;
import com.jaha.web.emaul.model.User;
import com.jaha.web.emaul.service.HouseService;
import com.jaha.web.emaul.service.UserService;
import com.jaha.web.emaul.util.Responses;
import com.jaha.web.emaul.util.SessionAttrs;
import com.jaha.web.emaul.util.Thumbnails;

/**
 * Created by doring on 15. 4. 4..
 */
@Controller
public class GasCheckController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GasCheckController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private HouseService houseService;

    @RequestMapping(method = RequestMethod.POST, value = "/api/gas-check/upload")
    public @ResponseBody GasCheck handleGasCheckUpload(HttpServletRequest req, @RequestParam(value = "file") MultipartFile file) {
        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        long userId = user.id;
        long current = System.currentTimeMillis();

        try {
            File dir = new File(String.format("/nas/EMaul/gas-check/image/%s", userId));
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File dest = new File(dir, String.format("%s.jpg", current));
            dest.createNewFile();
            file.transferTo(dest);
            Thumbnails.create(dest);
        } catch (IOException e) {
            LOGGER.error("", e);
        }

        GasCheck gas = new GasCheck();
        gas.date = new Date(current);
        gas.imageUri = String.format("/api/gas-check/image/%s/%s", userId, current);
        gas.user = user;

        houseService.saveAndFlush(gas);

        return gas;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/gas-check/list")
    public @ResponseBody List<GasCheck> handleGasCheckList(HttpServletRequest req) {
        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        return houseService.getGasCheckList(user.id);
    }

    @RequestMapping(value = "/api/gas-check/image/{userId}/{dateUtc}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> handleImageRequest(@PathVariable("userId") String userId, @PathVariable("dateUtc") String dateUtc) {

        File toServeUp = new File("/nas/EMaul/gas-check/image", String.format("/%s/%s.jpg", userId, dateUtc));

        return Responses.getFileEntity(toServeUp, dateUtc + ".jpg");
    }

    @RequestMapping(value = "/admin/gas-check/list", method = RequestMethod.GET)
    public String getGasCheck(HttpServletRequest req, Model model) {

        Long userId = SessionAttrs.getUserId(req.getSession());

        return "admin/gas-list";
    }

}
