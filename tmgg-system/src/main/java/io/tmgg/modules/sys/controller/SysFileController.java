
package io.tmgg.modules.sys.controller;

import cn.hutool.core.lang.Dict;
import io.tmgg.lang.ann.PublicApi;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.sys.entity.SysFile;
import io.tmgg.modules.sys.service.SysFileService;
import io.tmgg.web.annotion.HasPermission;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
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


    @HasPermission
    @RequestMapping("page")
    public AjaxResult page(@RequestBody SysFile param, @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) {
        return AjaxResult.ok().data(service.findByExampleLike(param, pageable));
    }


    /**
     * 上传文件
     */
    @PostMapping("upload")
    public Dict upload(@RequestPart("file") MultipartFile file) throws Exception {
        SysFile sysFile = service.uploadFile(file);
        Dict result = new Dict();

        // 兼容 tiny mce
        String location = "/sysFile/preview/" + sysFile.getId();
        result.put("location", location);
        result.put("id", sysFile.getId());

        return result;
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


    @PublicApi
    @GetMapping("preview/{id}")
    public void previewByPath(@PathVariable String id, HttpServletResponse response) throws Exception {
        try {
            service.preview(id, response);
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
