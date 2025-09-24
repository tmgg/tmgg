
package io.tmgg.modules.system.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.InputStreamResource;
import cn.hutool.core.io.resource.ResourceUtil;
import io.minio.GetObjectArgs;
import io.tmgg.lang.ann.PublicRequest;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.system.entity.SysFile;
import io.tmgg.modules.system.service.SysFileService;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.web.persistence.specification.JpaQuery;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * 文件
 */
@Slf4j
@RestController
@RequestMapping("sysFile")
public class SysFileController {

    @Resource
    private SysFileService service;



    @HasPermission
    @RequestMapping("page")
    public AjaxResult page(String dateRange,
                           String originName,
                           String objectName,
                           @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) {
        JpaQuery<SysFile> q = new JpaQuery<>();
        q.betweenIsoDateRange(SysFile.FIELD_CREATE_TIME, dateRange);
        q.eq(SysFile.Fields.originName, originName);
        q.eq(SysFile.Fields.objectName, objectName);
        Page<SysFile> page = service.findAll(q, pageable);
        return AjaxResult.ok().data(page);
    }


    /**
     * 上传文件
     */
    @PostMapping("upload")
    public AjaxResult upload(@RequestPart("file") MultipartFile file) throws Exception {
        SysFile sysFile = service.uploadFile(file);

        String location = service.getPreviewUrl(sysFile.getId());

        return AjaxResult.ok()
                .putExtData("location", location)    // 兼容 tiny mce
                .data("id", sysFile.getId())
                .data("name", sysFile.getOriginName());
    }

    /**
     * 下载文件
     */
    @GetMapping("download")
    public void download(String id, HttpServletResponse response) throws Exception {
        service.download(id, response);
    }

    @GetMapping("download/{fileId}")
    public void downloadFile(@PathVariable String fileId, HttpServletResponse response) throws Exception {
        service.download(fileId, response);
    }




    @GetMapping("detail")
    public AjaxResult detail(String id) {
        return AjaxResult.ok().data(service.findOne(id));
    }


    @HasPermission
    @RequestMapping("delete")
    public AjaxResult delete(String id) throws Exception {
        service.deleteById(id);
        return AjaxResult.ok();
    }

}
