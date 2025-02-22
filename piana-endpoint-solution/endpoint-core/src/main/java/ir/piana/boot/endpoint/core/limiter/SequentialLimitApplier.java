package ir.piana.boot.endpoint.core.limiter;

import io.github.bucket4j.Bucket;

import java.util.List;

public class SequentialLimitApplier {
    private final List<Bucket> bucketSequentialList;

    public SequentialLimitApplier(List<Bucket> bucketSequentialList) {
        this.bucketSequentialList = bucketSequentialList;
    }

    public void applyAndThrows() throws Bucket4jLimitationException {
        long count = this.bucketSequentialList.stream()
                .map(bucket -> bucket.tryConsume(1))
                .takeWhile(isLimit -> isLimit)
                .filter(isLimit -> isLimit)
                .count();
        if (count != this.bucketSequentialList.size()) {
            throw new Bucket4jLimitationException();
        }
    }

    public boolean apply() {
        long count = this.bucketSequentialList.stream()
                .map(bucket -> bucket.tryConsume(1))
                .takeWhile(isLimit -> isLimit)
                .filter(isLimit -> isLimit)
                .count();
        return count == this.bucketSequentialList.size();
    }
}
