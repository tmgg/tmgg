package io.tmgg.lang;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class DateRange {

    private Date begin;
    private Date end;

    public DateRange() {
    }

    public DateRange(Date begin, Date end) {
        if (begin.getTime() > end.getTime()) {
            throw new IllegalArgumentException("Start date must not be after end date");
        }
        this.begin = begin;
        this.end = end;
    }


}
