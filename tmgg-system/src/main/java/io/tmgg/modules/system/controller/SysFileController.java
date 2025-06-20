
package io.tmgg.modules.system.controller;

import io.tmgg.lang.ann.PublicRequest;
import io.tmgg.web.persistence.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.system.entity.SysFile;
import io.tmgg.modules.system.service.SysFileService;
import io.tmgg.web.annotion.HasPermission;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件
 */
@Slf4j
@RestController
@RequestMapping("sysFile")
public class SysFileController {

    @Resource
    private SysFileService service;

    @Data
    public static class QueryParam {
        private String dateRange;
        private String fileOriginName;
        private String fileObjectName;
    }

    @HasPermission
    @RequestMapping("page")
    public AjaxResult page(QueryParam param, @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) {
        JpaQuery<SysFile> q = new JpaQuery<>();
        q.betweenIsoDateRange(SysFile.FIELD_CREATE_TIME, param.dateRange);

        q.eq(SysFile.Fields.fileOriginName, param.getFileOriginName());
        q.eq(SysFile.Fields.fileObjectName, param.getFileObjectName());
        Page<SysFile> page = service.findAll(q, pageable);
        return AjaxResult.ok().data(page);
    }


    /**
     * 上传文件
     */
    @PostMapping("upload")
    public AjaxResult upload(@RequestPart("file") MultipartFile file) throws Exception {
        SysFile sysFile = service.uploadFile(file);

        String location = "/sysFile/preview/" + sysFile.getId();


        return AjaxResult.ok()
                .putExtData("location", location)    // 兼容 tiny mce
                .data("id",sysFile.getId())
                .data("name",sysFile.getFileOriginName());
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


    /**
     * 可以加后缀，如 /sysFile/preview/113.png , 这样对某些设备友好
     *
     * @param id
     * @param response
     * @param req
     * @throws Exception
     */
    @PublicRequest
    @GetMapping(value = {"preview/{id}", "preview/{id}.*"})
    public void previewByPath(@PathVariable String id, HttpServletRequest req, HttpServletResponse response) throws Exception {
        try {
            service.preview(id, req, response);
        } catch (Exception e) {
            log.error("预览文件失败:{}", e.getMessage());
            // TODO 裂开的图片
        }

    }


    @GetMapping("detail")
    public AjaxResult detail(String id) {
        return AjaxResult.ok().data(service.findOne(id));
    }


    @HasPermission
    @PostMapping("delete")
    public AjaxResult delete(String id) throws Exception {
        service.deleteById(id);
        return AjaxResult.ok();
    }

}
