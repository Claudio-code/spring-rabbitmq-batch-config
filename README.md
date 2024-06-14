# Spring with Rabbitmq consuming in batch with size/time limit

This project use spring with rabbitmq to set custom containerFactory with configurations to consume messages in batch.

-  **BatchListener**: If true it app consume queue in batch.
-  **RetryTemplate**: Sets retry strategy used in containerFactory.
-  **BatchSize**: Integer set max size to listener queue (when get to the number of batch size starts consume messages).
-  **ReceiveTimeout**: Sets max time wait if not reach the batch size limit but time is running out consume queue.
-  **MessageConverter**: Sets objectMapper to convert json to dto record.
-  **ConsumerBatchEnabled**: If true it app consume queue in batch based on BatchSize.
-  **DefaultRequeueRejected and ErrorHandler**: Sets error handler to containerFactory.
