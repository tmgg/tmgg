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

    private PageExt(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    private PageExt(Page<T> page) {
        super(page.getContent(), page.getPageable(),page.getTotalElements());
    }

    /**
     * 调整 Page的内容为另外一个类型
     * @param page
     * @param fn
     * @return
     * @param <T>
     * @param <R>
     */
    public static  <T,R> PageExt<R>  convert(Page<T> page, Function<T,R> fn){
        List<R> content = page.getContent().stream().map(fn).collect(Collectors.toList());
        return new PageExt<>(content, page.getPageable(), page.getTotalElements());
    }


    public static  <T> PageExt<T>  of(Page<T> page){
        return new PageExt<>(page);
    }
    public static  <T> PageExt<T>  of(Page<T> page,String totalInfo){
        PageExt<T> pageExt = of(page);
        pageExt.setTotalInfo(totalInfo);
        return pageExt;
    }

}
