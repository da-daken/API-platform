package com.daken.project.service.impl.inner;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.daken.common.ErrorCode;
import com.daken.common.entity.UserInterfaceInfo;
import com.daken.common.service.InnerUserInterfaceInfoService;
import com.daken.project.exception.BusinessException;
import com.daken.project.service.UserInterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {

    private static final String LOCK_NAME = ":lock:";

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Resource
    private RedissonClient redissonClient;

    /**
     * 调用次数 + 1
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        return userInterfaceInfoService.invokeCount(interfaceInfoId,userId);
    }

    /**
     * 查询是否还有次数
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    @Override
    public boolean isLeftnum(long interfaceInfoId, long userId) {
        UserInterfaceInfo userInterfaceInfo = null;
        if (interfaceInfoId <= 0 || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 1. 可以用 Syn 进行加锁
//        synchronized (InnerUserInterfaceInfoServiceImpl.class) {
//            // 用户interfaceId，和userId查出 leftNum的值
//            QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
//            queryWrapper.eq("interfaceInfoId", interfaceInfoId);
//            queryWrapper.eq("userId", userId);
//            userInterfaceInfo = userInterfaceInfoService.getOne(queryWrapper);
//        }
        // 2. 分布式锁(单个节点)
        RLock rLock = redissonClient.getLock(String.valueOf(userId) + LOCK_NAME);
        try {
            if (rLock.tryLock(80, -1, TimeUnit.MILLISECONDS)) {
                // 用户interfaceId，和userId查出 leftNum的值
                QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("interfaceInfoId", interfaceInfoId);
                queryWrapper.eq("userId", userId);
                userInterfaceInfo = userInterfaceInfoService.getOne(queryWrapper);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
        // 3. redLock(redis集群)
        

        if(ObjectUtil.isEmpty(userInterfaceInfo)){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if(userInterfaceInfo.getLeftNum() < 1){
            return false;
        }
        return true;
    }

}
