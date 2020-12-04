/**
 * 
 */
package com.alibaba.datax.plugin.writer.gdbwriter.mapping;

import com.alibaba.datax.common.element.Record;
import com.alibaba.datax.plugin.writer.gdbwriter.model.GdbElement;

import java.util.function.Function;

/**
 * @author jerrywang
 *
 */
public interface GdbMapper {
    Function<Record, GdbElement> getMapper(MappingRule rule);
}
