package com.first.head.id;

/**
 * 0-00000000000000000000000000000000000000000-0000000000-000000000000
 * | |_______________________________________| |________| |__________|
 * 1               41                              10          12
 * 符号            时间戳                      5数据中心 + 5机器  序列号
 */
public class IdWorker {
    private Long sequence;
    private final Long twepoch = 1288834974657L;
    private Long datacenterId;
    private Long workerId;

    private Long workerIdBits = 5L;
    private Long datacenterIdBits = 5L;

    private final Long sequenceBits = 12L;

    private final Long workerIdShift = sequenceBits;
    private final Long datacenterIdShift = sequenceBits + workerIdBits;
    private final Long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    private final Long sequenceMask = ~(-1L << sequenceBits);

    private Long lastTimestamp = -1L;

    public IdWorker(Long workerId, Long datacenterId) {
        this.sequence = 0L;
        this.workerId = workerId;
        this.datacenterId = datacenterId;
        Long maxWorkerId = ~(-1L << workerIdBits);
        long maxDatacenterId = ~(-1L << datacenterIdBits);
        if (workerId > maxWorkerId || workerId < 0) {
            throw new RuntimeException("worker Id can't be greater than %d or less than 0".formatted(maxWorkerId));
        }

        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new RuntimeException("datacenter Id can't be greater than %d or less than 0".formatted(datacenterId));
        }

    }

    public Long getId() {
        return nextId();
    }

    private synchronized Long nextId() {
        Long timestamp = timeGen();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException("时钟滞后，禁止生成 ID");
        }
        if (lastTimestamp.equals(timestamp)) {
            // 防溢出，sequence 在 0～sequenceMask 范围内
            sequence = (sequence + 1) & sequenceMask;
            // sequence 为 0 意味着相同时间戳内的序列号溢出了，需要再次生成 timestamp
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;
        // 四个分段的值做或运算
        return ((timestamp - twepoch) << timestampLeftShift) |
                (datacenterId << datacenterIdShift) |
                (workerId << workerIdShift) |
                sequence;
    }

    private Long tilNextMillis(Long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private Long timeGen() {
        return System.currentTimeMillis();
    }
}
