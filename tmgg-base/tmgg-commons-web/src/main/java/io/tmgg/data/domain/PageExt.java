package io.tmgg.data.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
     * 通常存放一些汇总数据，有点像excel的合计, pc端会自动显示在表格最下面。
     */
    private  String extInfo;

    private Map<String, Object> extData = new HashMap<>();

    private PageExt(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    private PageExt(Page<T> page) {
        super(page.getContent(), page.getPageable(),page.getTotalElements());
    }

    /**
     * 调整 Page的内容为另外一个类型
     */
    public <R> PageExt<R>  convert( Function<T,R> fn){
        List<R> content = this.getContent().stream().map(fn).collect(Collectors.toList());
        return new PageExt<>(content, this.getPageable(), this.getTotalElements());
    }


    public static  <T> PageExt<T>  of(Page<T> page){
        return new PageExt<>(page);
    }


    public static  <T> PageExt<T>  of(Page<T> page,String extInfo){
        PageExt<T> pageExt = of(page);
        pageExt.setExtInfo(extInfo);
        return pageExt;
    }
    public static  <T> PageExt<T>  of(Page<T> page,String key, Object value){
        PageExt<T> pageExt = of(page);
        pageExt.putExtData(key,value);
        return pageExt;
    }

    public static  <T> PageExt<T>  of(Page<T> page,String key, Object value,String key2, Object value2){
        PageExt<T> pageExt = of(page);
        pageExt.putExtData(key,value);
        pageExt.putExtData(key2,value2);
        return pageExt;
    }

    public PageExt<T> putExtData(String key, Object value){
            extData.put(key,value);
            return this;
    }


}
