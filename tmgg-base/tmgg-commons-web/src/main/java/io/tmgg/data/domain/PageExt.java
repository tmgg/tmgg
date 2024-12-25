package io.tmgg.data.domain;

import lombok.Getter;
import lombok.Setter;
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
@Getter
@Setter
public class PageExt<T> extends org.springframework.data.domain.PageImpl<T> {

  private   String title;

    /**
     * 通常存放一些汇总数据，有点像excel的合计
     */
    private  String totalInfo;

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




}
