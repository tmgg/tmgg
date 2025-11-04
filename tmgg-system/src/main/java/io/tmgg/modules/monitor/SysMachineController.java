
package io.tmgg.modules.monitor;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.unit.DataSizeUtil;
import cn.hutool.system.JvmInfo;
import cn.hutool.system.OsInfo;
import cn.hutool.system.SystemUtil;
import cn.hutool.system.oshi.CpuInfo;
import cn.hutool.system.oshi.OshiUtil;
import io.tmgg.lang.PastTimeFormatTool;
import io.tmgg.dto.AjaxResult;
import io.tmgg.modules.monitor.dto.ChartResult;
import io.tmgg.modules.monitor.dto.ChartTimeType;
import io.tmgg.modules.monitor.service.SysMetricRecordService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import oshi.software.os.OSFileStore;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * 系统属性监控控制器
 */
@RestController
@RequestMapping("sysMachine")
public class SysMachineController {

    @Resource
    private SysMetricRecordService sysMetricRecordService;



    @GetMapping("cpu")
    public AjaxResult cpu() {
        CpuInfo cpuInfo = OshiUtil.getCpuInfo();

        return AjaxResult.ok().data(cpuInfo);
    }

    @GetMapping("chart/{name}")
    public AjaxResult chart(@PathVariable String name, ChartTimeType type) {
        List<ChartResult> list = sysMetricRecordService.chart(name, type);

        return AjaxResult.ok().data(list);
    }


    @GetMapping("mem")
    public AjaxResult mem() {
        GlobalMemory m = OshiUtil.getMemory();


        long total = m.getTotal();
        long free = m.getAvailable();
        long used = total - free;

        return AjaxResult.ok()
                .data("total", DataSizeUtil.format(total))
                .data("free", DataSizeUtil.format(free))
                .data("used", DataSizeUtil.format(used))
                .data("usage", (int) ((used * 1F / total) * 100))
                ;
    }


    @GetMapping("jvmMem")
    public AjaxResult jvmMem() {

        long total = Runtime.getRuntime().totalMemory();
        long free = Runtime.getRuntime().freeMemory();
        long used = total - free;

        return AjaxResult.ok()
                .data("total", DataSizeUtil.format(total))
                .data("free", DataSizeUtil.format(free))
                .data("used", DataSizeUtil.format(used))
                .data("usage", (int) ((used * 1F / total) * 100))
                ;
    }

    @GetMapping("osInfo")
    public AjaxResult osInfo() throws UnknownHostException {

        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        OsInfo osInfo = SystemUtil.getOsInfo();
        return AjaxResult.ok()
                .data(osInfo)
                .data("hostAddress", hostAddress)
                ;
    }


    @GetMapping("jvmInfo")
    public AjaxResult jvmInfo() {
        JvmInfo jvmInfo = SystemUtil.getJvmInfo();

        RuntimeMXBean mxBean = ManagementFactory.getRuntimeMXBean();
        long time = mxBean.getStartTime();
        return AjaxResult.ok()
                .data(jvmInfo)
                .data("startTime", DateUtil.formatDateTime(new Date(time)))
                .data("pastTime", PastTimeFormatTool.formatPastTime(new Date(time)))
                .data("home", SystemUtil.getJavaRuntimeInfo().getHomeDir())
                .data("userDir", System.getProperty("user.dir"))
                ;
    }

    @GetMapping("disks")
    public AjaxResult disks() {
        SystemInfo si = new SystemInfo();
        List<OSFileStore> fileStores = si.getOperatingSystem().getFileSystem().getFileStores();

        List<Map<String, Object>> list = fileStores.stream().map(f -> {
            Map<String, Object> d = new HashMap<>();
            d.put("name", f.getName());
            d.put("mount", f.getMount());
            d.put("type", f.getType());
            long total = f.getTotalSpace();
            long free = f.getUsableSpace();
            long used = total - free;

            d.put("total",DataSizeUtil.format(total));
            d.put("free",DataSizeUtil.format(free));
            d.put("used",DataSizeUtil.format(used));
            d.put("usage", (int) ((used * 1F / total) * 100));

            return d;
        }).toList();

        return AjaxResult.ok()
                .data(list)
                ;
    }

}
