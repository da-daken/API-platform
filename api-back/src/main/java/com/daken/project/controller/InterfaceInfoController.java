package com.daken.project.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.daken.apiclientsdk.client.ApiClient;
import com.daken.common.entity.InterfaceInfo;
import com.daken.common.entity.User;
import com.daken.common.entity.UserInterfaceInfo;
import com.daken.project.annotation.AuthCheck;
import com.daken.project.common.*;
import com.daken.project.constant.CommonConstant;
import com.daken.project.exception.BusinessException;
import com.daken.project.mapper.UserInterfaceInfoMapper;
import com.daken.project.model.dto.interfaceinfo.InterfaceInfoAddRequest;
import com.daken.project.model.dto.interfaceinfo.InterfaceInfoInvokeRequest;
import com.daken.project.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.daken.project.model.dto.interfaceinfo.InterfaceInfoUpdateRequest;
import com.daken.project.model.dto.userInterfaceInfo.UserInterfaceInfoBuyDto;
import com.daken.project.model.enums.InterfaceInfoStatusEnum;
import com.daken.project.service.InterfaceInfoService;
import com.daken.project.service.UserInterfaceInfoService;
import com.daken.project.service.UserService;
import com.daken.project.utils.BeanCopyUtils;
import com.daken.project.utils.JsonUtils;
import com.google.gson.Gson;
import io.swagger.util.Json;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.xml.soap.Text;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * ??????????????????
 *
 * @author daken
 */
@RestController
@RequestMapping("/interfaceInfo")
@Slf4j
public class InterfaceInfoController {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserService userService;

    @Resource
    private ApiClient apiClient;

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    // region ????????????

    /**
     * ??????
     *
     * @param interfaceInfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request) {
        if (interfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoAddRequest, interfaceInfo);
        // ??????
        interfaceInfoService.validInterfaceInfo(interfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        interfaceInfo.setUserId(loginUser.getId());
        boolean result = interfaceInfoService.save(interfaceInfo);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        long newInterfaceInfoId = interfaceInfo.getId();
        return ResultUtils.success(newInterfaceInfoId);
    }

    /**
     * ??????
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // ??????????????????
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // ??????????????????????????????
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = interfaceInfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * ??????
     *
     * @param interfaceInfoUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest,
                                                     HttpServletRequest request) {
        if (interfaceInfoUpdateRequest == null || interfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoUpdateRequest, interfaceInfo);
        // ????????????
        interfaceInfoService.validInterfaceInfo(interfaceInfo, false);
        User user = userService.getLoginUser(request);
        long id = interfaceInfoUpdateRequest.getId();
        // ??????????????????
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // ??????????????????????????????
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * ?????? id ??????
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<InterfaceInfo> getInterfaceInfoById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        return ResultUtils.success(interfaceInfo);
    }

    /**
     * ???????????????????????????????????????
     *
     * @param interfaceInfoQueryRequest
     * @return
     */
    @AuthCheck(mustRole = "admin")
    @GetMapping("/list")
    public BaseResponse<List<InterfaceInfo>> listInterfaceInfo(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        if (interfaceInfoQueryRequest != null) {
            BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        List<InterfaceInfo> interfaceInfoList = interfaceInfoService.list(queryWrapper);
        return ResultUtils.success(interfaceInfoList);
    }

    /**
     * ??????????????????(?????????????????????)
     *
     * @param interfaceInfoQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<InterfaceInfo>> listInterfaceInfoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest, HttpServletRequest request) {
        if (interfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();
        String description = interfaceInfoQuery.getDescription();
        // description ?????????????????????
        interfaceInfoQuery.setDescription(null);
        // ????????????
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        queryWrapper.like(StringUtils.isNotBlank(description), "content", description);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(interfaceInfoPage);
    }

//    /**
//     * ??????????????????(?????????????????????)
//     *
//     * @param interfaceInfoQueryRequest
//     * @param request
//     * @return
//     */
//    @GetMapping("/list/adminPage")
//    public BaseResponse<Page<InterfaceInfo>> AdminlistInterfaceInfoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest, HttpServletRequest request) {
//        if (interfaceInfoQueryRequest == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
//        BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
//        long current = interfaceInfoQueryRequest.getCurrent();
//        long size = interfaceInfoQueryRequest.getPageSize();
//        String sortField = interfaceInfoQueryRequest.getSortField();
//        String sortOrder = interfaceInfoQueryRequest.getSortOrder();
//        String description = interfaceInfoQuery.getDescription();
//        // description ?????????????????????
//        interfaceInfoQuery.setDescription(null);
//        // ????????????
//        if (size > 50) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
//        queryWrapper.like(StringUtils.isNotBlank(description), "content", description);
//        queryWrapper.eq("status","1");
//        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
//                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
//        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size), queryWrapper);
//        return ResultUtils.success(interfaceInfoPage);
//    }

    // endregion

    /**
     * ??????
     *
     * @param idRequest
     * @param request
     * @return
     */
    @AuthCheck(mustRole = "admin")
    @PostMapping("/online")
    public BaseResponse<Boolean> onlineInterfaceInfo(@RequestBody IdRequest idRequest,
                                                     HttpServletRequest request) throws UnsupportedEncodingException {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //???????????????????????????
        long id = idRequest.getId();
        if (interfaceInfoService.getById(id) == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        //?????????????????????????????????
        com.daken.apiclientsdk.model.User user = new com.daken.apiclientsdk.model.User();
        user.setUsername("test");
        String username = apiClient.getUsernameByPost(user);
        if (StringUtils.isBlank(username)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "??????????????????");
        }
        //??????????????????????????????????????????1
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id);
        interfaceInfo.setStatus(InterfaceInfoStatusEnum.ONLINE.getValue());
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);

    }

    /**
     * ??????
     *
     * @param idRequest
     * @param request
     * @return
     */
    @AuthCheck(mustRole = "admin")
    @PostMapping("/offline")
    public BaseResponse<Boolean> offlineInterfaceInfo(@RequestBody IdRequest idRequest,
                                                      HttpServletRequest request) {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //???????????????????????????
        long id = idRequest.getId();
        if (interfaceInfoService.getById(id) == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        //??????????????????????????????????????????1
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id);
        interfaceInfo.setStatus(InterfaceInfoStatusEnum.OFFLINE.getValue());
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * ??????????????????
     *
     * @param interfaceInfoInvokeRequest
     * @param request
     * @return
     */
    @PostMapping("/invoke")
    public BaseResponse<Object> invokeInterfaceInfo(@RequestBody InterfaceInfoInvokeRequest interfaceInfoInvokeRequest,
                                                    HttpServletRequest request) throws NoSuchMethodException, ClassNotFoundException, InvocationTargetException, IllegalAccessException {
        if (interfaceInfoInvokeRequest == null || interfaceInfoInvokeRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = interfaceInfoInvokeRequest.getId();
        String userRequestParams = interfaceInfoInvokeRequest.getUserRequestParams();
        // ??????????????????
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (oldInterfaceInfo.getStatus() == InterfaceInfoStatusEnum.OFFLINE.getValue()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "???????????????");
        }
        User loginUser = userService.getLoginUser(request);
        String accessKey = loginUser.getAccessKey();
        String secretKey = loginUser.getSecretKey();
        ApiClient tempClient = new ApiClient(accessKey, secretKey);
        Gson gson = new Gson();
        Object result = null;
        // ??? json ????????????????????????????????????
        if (oldInterfaceInfo.getMethod().equals("POST")) {
            String paramsType = oldInterfaceInfo.getParamsType();
            if (paramsType == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "??????????????????");
            }
            Object o = JsonUtils.getJsonToBean(userRequestParams, Class.forName(paramsType));
            Method method = tempClient.getClass().getMethod(oldInterfaceInfo.getName(), Class.forName(paramsType));
            result = method.invoke(tempClient, o);
        }
        else if (oldInterfaceInfo.getMethod().equals("GET")){
            String[] paramsTypes = oldInterfaceInfo.getParamsType().split(";");
            // ???????????????????????????????????????
            if (paramsTypes.length == 0){
                Method method = tempClient.getClass().getMethod(oldInterfaceInfo.getName());
                method.invoke(tempClient);
            } else {
                // ??????????????????
                List<Class<?>> clazz = new ArrayList<>();
                for (String paramType : paramsTypes){
                    Class<?> aClass = Class.forName(paramType);
                    clazz.add(aClass);
                }
                Class<?>[] classes = clazz.toArray(new Class[clazz.size()]);
                // ?????????????????????
                Map paramsMap = (Map)JSON.parse(userRequestParams);
                List<Object> params = new ArrayList<>();
                for (Object map : paramsMap.entrySet()) {
                    params.add(((Map.Entry) map).getValue());
                }
                Object[] paramsss = params.toArray();
                Method method = tempClient.getClass().getMethod(oldInterfaceInfo.getName(), classes) ;
                result = method.invoke(tempClient, paramsss);
            }
        }
//        com.daken.apiclientsdk.model.User user = gson.fromJson(userRequestParams, com.daken.apiclientsdk.model.User.class);
//        String result = tempClient.getUsernameByPost(user);
        return ResultUtils.success(result);
    }

    @GetMapping("/sdk")
    public BaseResponse<String> sdkInfo(){
        String sdk = "        <dependency>\n" +
                "            <groupId>com.daken</groupId>\n" +
                "            <artifactId>api-client-sdk</artifactId>\n" +
                "            <version>0.0.1-SNAPSHOT</version>\n" +
                "        </dependency>";
        return ResultUtils.success(sdk);
    }


    public static final String buySuccessMsg = "????????????";
    public static final String buyFailMsg = "????????????????????????";

    /**
     * ??????????????????????????????
     * @param buyDto
     * @return
     */
    @PostMapping("/buyInterface")
    @Transactional
    public BaseResponse<String> buyInterface(@RequestBody UserInterfaceInfoBuyDto buyDto){
        // 1. ??????userId???interfaceInfoId??????????????????????????????
        QueryWrapper<UserInterfaceInfo> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("userId", buyDto.getUserId());
        queryWrapper.eq("interfaceInfoId", buyDto.getInterfaceInfoId());
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoService.getOne(queryWrapper);
        if(!ObjectUtil.isEmpty(userInterfaceInfo)){
            // 2. ???????????????????????????????????????leftNum ???update???
            boolean update = userInterfaceInfoMapper.addLeftNum(buyDto.getLeftNum(), buyDto.getUserId(), buyDto.getInterfaceInfoId());
            // 4. ??????????????????
            if(update){
                return ResultUtils.success(buySuccessMsg);
            } else {
                return ResultUtils.error(ErrorCode.OPERATION_ERROR, buyFailMsg);
            }
        } else {
            // 3. ??????????????????????????????????????????insert???
            UserInterfaceInfo userInterfaceInfo1 = BeanCopyUtils.copyBean(buyDto, UserInterfaceInfo.class);
            boolean save = userInterfaceInfoService.save(userInterfaceInfo1);
            // 4. ??????????????????
            if(save){
                return ResultUtils.success(buySuccessMsg);
            } else {
                return ResultUtils.error(ErrorCode.OPERATION_ERROR, buyFailMsg);
            }
        }

    }

}
