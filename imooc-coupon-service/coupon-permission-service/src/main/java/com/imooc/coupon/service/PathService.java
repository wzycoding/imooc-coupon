package com.imooc.coupon.service;

import com.imooc.coupon.dao.PathRepository;
import com.imooc.coupon.entity.Path;
import com.imooc.coupon.vo.CreatePathRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 路径相关的服务功能实现
 *
 * @author wzy
 * @date 2020-08-19 20:13
 **/
@Slf4j
@Service
public class PathService {
    /**
     * pathRepository
     */
    private final PathRepository pathRepository;


    public PathService(PathRepository pathRepository) {
        this.pathRepository = pathRepository;
    }

    /**
     * 添加新的Path到数据表中
     *
     * @return Path 数据记录的主键
     */
    public List<Integer> createPath(CreatePathRequest request) {
        List<CreatePathRequest.PathInfo> pathInfos = request.getPathInfos();
        List<CreatePathRequest.PathInfo> validRequests =
                new ArrayList<>(request.getPathInfos().size());
        // 获取数据库中的路径信息
        List<Path> currentPaths = pathRepository.findAllByServiceName(
                pathInfos.get(0).getServiceName()
        );
        if (CollectionUtils.isEmpty(currentPaths)) {
            for (CreatePathRequest.PathInfo pathInfo : pathInfos) {
                boolean isValid = true;
                for (Path currentPath : currentPaths) {
                    if (currentPath.getPathPattern()
                            .equals(pathInfo.getPathPattern())
                            && currentPath.getHttpMethod().equals(pathInfo.getHttpMethod())) {
                        isValid = false;
                        break;
                    }
                }
                // 如果无效就不保存到validRequests列表中
                if (isValid) {
                    validRequests.add(pathInfo);
                }
            }
        } else {
            validRequests = pathInfos;
        }
        List<Path> paths = new ArrayList<>(validRequests.size());
        validRequests.forEach(p -> paths.add(
                new Path(
                        p.getPathPattern(),
                        p.getHttpMethod(),
                        p.getPathName(),
                        p.getServiceName(),
                        p.getOpMode()
                )
        ));
        return pathRepository.saveAll(paths).stream()
                .map(Path::getId).collect(Collectors.toList());
    }
}
