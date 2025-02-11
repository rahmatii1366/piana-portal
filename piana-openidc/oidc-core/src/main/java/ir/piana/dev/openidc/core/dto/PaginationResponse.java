package ir.piana.dev.openidc.core.dto;

import lombok.Data;

import java.util.List;

@Data
public class PaginationResponse<T> {
    private final int count;
    private final List<T> data;
}
