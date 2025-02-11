package ir.piana.dev.openidc.core.dto;

public class PaginationRequest {
    private int size;
    private int offset;

    public PaginationRequest() {
    }

    public PaginationRequest(int size, int offset) {
        this.size = size;
        this.offset = offset;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
