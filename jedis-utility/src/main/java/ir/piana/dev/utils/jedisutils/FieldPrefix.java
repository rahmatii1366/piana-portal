package ir.piana.dev.utils.jedisutils;

public record FieldPrefix(String prefix) {
    public FieldPrefix() {
        this("");
    }

    public FieldPrefix(String prefix) {
        prefix = prefix == null ? "" : prefix;
        this.prefix = (prefix.isEmpty() || prefix.endsWith(".")) ? prefix : prefix + ".";
    }
}
