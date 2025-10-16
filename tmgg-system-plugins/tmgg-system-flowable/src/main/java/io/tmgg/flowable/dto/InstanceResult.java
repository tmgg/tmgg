package io.tmgg.flowable.dto;

import io.tmgg.flowable.dto.response.CommentResult;
import lombok.Data;

import java.util.List;

@Data
public class InstanceResult {

    List<CommentResult> commentList;
}
