package com.winter.ai4j.common.model;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * ClassName: BaseVO
 * <blockquote><pre>
 * Description: [描述]
 * </pre></blockquote>
 *
 * @author WYH4J
 * Date: 2024/10/31 10:10
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseVO<T> implements Serializable {

    @ApiModelProperty(value = "页码")
    private Long pageNum;

    @ApiModelProperty(value = "每页数量")
    private Long pageSize;

    @ApiModelProperty(value = "总数")
    private Long total;

    @ApiModelProperty(value = "总页数")
    private Long totalPages;

    @ApiModelProperty(value = "数据")
    private List<T> data;

    public BaseVO(Page<T> page){
        this.data = page.getRecords();
        this.pageNum = page.getCurrent();
        this.pageSize = page.getSize();
        this.total = page.getTotal();
        this.totalPages = page.getPages();
    }

}