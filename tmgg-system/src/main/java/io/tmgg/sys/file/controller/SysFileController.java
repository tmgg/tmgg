
package io.tmgg.sys.file.controller;

import cn.hutool.core.lang.Dict;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.sys.file.entity.SysFile;
import io.tmgg.sys.file.service.SysFileService;
import io.tmgg.web.annotion.BusinessLog;
import io.tmgg.web.annotion.HasPermission;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 文件
 */
@RestController
@RequestMapping("sysFile")
public class SysFileController {

    @Resource
    private SysFileService service;

    @HasPermission
    @GetMapping("page")
    public AjaxResult page(SysFile param, @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) {
        return AjaxResult.success(null,service.findByExampleLike(param, pageable));
    }


    /**
     * 上传文件
     */
    @PostMapping("upload")
    public Dict upload(@RequestPart("file") MultipartFile file) throws IOException {
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
    public void download(String id, HttpServletResponse response) {
        service.download(id, response);
    }

    @GetMapping("download/{fileId}")
    public void downloadFile(@PathVariable String fileId, HttpServletResponse response) {
        service.download(fileId, response);
    }



    @GetMapping("preview")
    public void preview(String id, HttpServletResponse response) throws IOException {
        service.preview(id, response);
    }

    @GetMapping("preview/{id}")
    public void previewByPath(@PathVariable String id, HttpServletResponse response) throws IOException {
        service.preview(id, response);
    }


    @HasPermission
    @GetMapping("detail")
    public AjaxResult detail(String id) {
        return AjaxResult.success(null,service.findOne(id));
    }


    @HasPermission
    @PostMapping("delete")
    @BusinessLog("文件信息表_删除")
    public AjaxResult delete(String id) throws Exception {
        service.deleteById(id);
        return AjaxResult.success();
    }

}
