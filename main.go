package main

import (
	"errors"
	"fmt"
	"sync"
	"time"
)

type IdWorker struct {
	sequence           int64
	twepoch            int64
	datacenterId       int64
	workerId           int64
	workerIdBits       int64
	datacenterIdBits   int64
	sequenceBits       int64
	workerIdShift      int64
	datacenterIdShift  int64
	timestampLeftShift int64
	sequenceMask       int64
	lastTimestamp      int64
	mutex              sync.Mutex
}

func NewIdWorker(workerId, datacenterId int64) *IdWorker {
	return &IdWorker{
		sequence:           0,
		twepoch:            1288834974657,
		workerId:           workerId,
		datacenterId:       datacenterId,
		workerIdBits:       5,
		datacenterIdBits:   5,
		sequenceBits:       12,
		workerIdShift:      12,
		datacenterIdShift:  17,
		timestampLeftShift: 22,
		sequenceMask:       -1 ^ (-1 << 12),
		lastTimestamp:      -1,
		mutex:              sync.Mutex{},
	}
}

func (w *IdWorker) getId() int64 {
	return w.nextId()
}

func (w *IdWorker) nextId() int64 {
	w.mutex.Lock()
	defer w.mutex.Unlock()

	timestamp := w.timeGen()

	if timestamp < w.lastTimestamp {
		panic(errors.New("时钟滞后，禁止生成 ID"))
	}

	if w.lastTimestamp == timestamp {
		w.sequence = (w.sequence + 1) & w.sequenceMask
		if w.sequence == 0 {
			timestamp = w.tilNextMillis(w.lastTimestamp)
		}
	} else {
		w.sequence = 0
	}

	w.lastTimestamp = timestamp

	return ((timestamp - w.twepoch) << w.timestampLeftShift) |
		(w.datacenterId << w.datacenterIdShift) |
		(w.workerId << w.workerIdShift) |
		w.sequence
}

func (w *IdWorker) tilNextMillis(lastTimestamp int64) int64 {
	timestamp := w.timeGen()
	for timestamp <= lastTimestamp {
		timestamp = w.timeGen()
	}
	return timestamp
}

func (w *IdWorker) timeGen() int64 {
	return time.Now().UnixNano() / int64(time.Millisecond)
}

func main() {
	worker := NewIdWorker(2, 3)
	for i := 0; i < 100; i++ {
		fmt.Println(worker.nextId())
	}
}
