package io.tmgg.data.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 *
 * （包名参考 org.springframework.data.domain.Page）
 */
public class PageExt<T> extends org.springframework.data.domain.PageImpl<T> {

    String title;

    /**
     * 通常存放一些汇总数据，如excel的合计
     */
    String footer;

    public PageExt(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public PageExt(Page<T> page) {
        super(page.getContent(), page.getPageable(),page.getTotalElements());
    }

    public static  <T,R> PageExt<R>  convert(Page<T> page, Function<T,R> fn){
        List<R> content = page.getContent().stream().map(fn).collect(Collectors.toList());
        return new PageExt<>(content, page.getPageable(), page.getTotalElements());
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }
}
