package io.tmgg.weixin.controller;

import cn.hutool.core.util.HexUtil;
import io.tmgg.lang.dao.BaseController;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.sys.vo.UploadResult;
import io.tmgg.web.CommonQueryParam;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.weixin.entity.WeixinPayChannel;
import io.tmgg.weixin.service.WeixinPayChannelService;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("weixinPayChannel")
public class WeixinPayChannelController extends BaseController<WeixinPayChannel> {

    @Resource
    WeixinPayChannelService service;


    @HasPermission
    @PostMapping("page")
    public AjaxResult page(@RequestBody CommonQueryParam param, @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) throws Exception {
        JpaQuery<WeixinPayChannel> q = new JpaQuery<>();

        // 关键字搜索，请补全字段
        q.searchText(param.getKeyword(), WeixinPayChannel.Fields.name);

        Page<WeixinPayChannel> page = service.findAll(q, pageable);
        return AjaxResult.ok().data(page);
    }




    @HasPermission("weixinPayChannel:save")
    @PostMapping("uploadP12")
    public AjaxResult uploadP12(MultipartFile file) throws Exception {
        byte[] bytes = file.getBytes();

        String id = HexUtil.encodeHexStr(bytes);

        UploadResult uploadResult = new UploadResult();
        uploadResult.setId(id);
        uploadResult.setData(id);


        return uploadResult;


    }


}

