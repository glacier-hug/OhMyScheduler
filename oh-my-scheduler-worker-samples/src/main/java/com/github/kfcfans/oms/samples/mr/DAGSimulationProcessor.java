package com.github.kfcfans.oms.samples.mr;

import com.github.kfcfans.oms.worker.core.processor.ProcessResult;
import com.github.kfcfans.oms.worker.core.processor.TaskContext;
import com.github.kfcfans.oms.worker.core.processor.TaskResult;
import com.github.kfcfans.oms.worker.core.processor.sdk.MapReduceProcessor;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * 模拟 DAG 的处理器（在正式提供DAG支持前，可用该方法代替）
 *
 * ROOT -> A -> B  -> REDUCE
 *           -> C
 *
 * @author tjq
 * @since 2020/5/15
 */
public class DAGSimulationProcessor extends MapReduceProcessor {

    @Override
    public ProcessResult process(TaskContext context) throws Exception {

        if (isRootTask()) {
            // L1. 执行根任务

            // 执行完毕后产生子任务 A，需要传递的参数可以作为 TaskA 的属性进行传递
            TaskA taskA = new TaskA();
            return map(Lists.newArrayList(taskA), "LEVEL1_TASK_A");
        }

        if (context.getSubTask() instanceof TaskA) {
            // L2. 执行A任务

            // 执行完成后产生子任务 B，C（并行执行）
            TaskB taskB = new TaskB();
            TaskC taskC = new TaskC();
            return map(Lists.newArrayList(taskB, taskC), "LEVEL2_TASK_BC");
        }

        if (context.getSubTask() instanceof TaskB) {
            // L3. 执行B任务
            return new ProcessResult(true, "xxx");
        }
        if (context.getSubTask() instanceof TaskC) {
            // L3. 执行C任务
            return new ProcessResult(true, "xxx");
        }

        return new ProcessResult(false, "UNKNOWN_TYPE_OF_SUB_TASK");
    }

    @Override
    public ProcessResult reduce(TaskContext context, List<TaskResult> taskResults) {
        // L4. 执行最终 Reduce 任务，taskResults保存了之前所有任务的结果
        taskResults.forEach(taskResult -> {
            // do something...
        });
        return new ProcessResult(true, "reduce success");
    }

    private static class TaskA {
    }
    private static class TaskB {
    }
    private static class TaskC {

    }
}
