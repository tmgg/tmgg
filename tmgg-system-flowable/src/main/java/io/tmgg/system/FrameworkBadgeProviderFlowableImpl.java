package io.tmgg.system;

import io.tmgg.config.external.MenuBadgeProvider;
import io.tmgg.flowable.FlowableManager;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 左侧菜单的角标
 */
@Component
public class FrameworkBadgeProviderFlowableImpl implements MenuBadgeProvider {



    @Resource
    FlowableManager fm;



    @Override
    public List<MenuBadge> getData() {
        String menuCode = "flowableTask";
        long count = fm.taskTodoCount();

        return Arrays.asList(new MenuBadge(menuCode, (int) count));
    }


}
