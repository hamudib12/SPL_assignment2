# Micro-Service Framework

## General Guidelines:
- Read the javadocs of all the interfaces provided.
  - Stick to the java documentation of each class and method.
  - For classes, add data members only with the allowed access levels.
  - For methods, do not change return value type, parameters, and Exceptions types.
  - Do not throw exceptions not specified in the java documentation.
  - Add "synchronized" to methods if needed.
- Aim for concurrent and efficient code.
  - Utilize Java's concurrent data structures.
  - Understand the concurrency level and features of each data structure.
  - Synchronize as little as possible without sacrificing correctness.

## Introduction:
This assignment involves implementing a Micro-Service framework and a system for managing university compute resources. The Micro-Services architecture allows composing complex applications from small, independent services communicating through messages.

## Preliminary:
Implement a basic `Future` class representing a promised result. It has methods:
- `T get()`: Retrieves the result, waits if not yet completed.
- `resolve(T result)`: Sets the result once computation completes.
- `isDone()`: Returns true if resolved.
- `T get(long timeout, TimeUnit unit)`: Retrieves the result with a timeout.

## Part 1 - Synchronization MicroServices Framework:
Build a Micro-Service framework consisting of a Message-Bus and Micro-Services. Micro-Services are threads exchanging messages through the Message-Bus. Two types of messages are Events and Broadcasts.

### Events:
- Define actions to be processed (e.g., training a model).
- Each Micro-Service specializes in processing specific events.
- Event results are represented in a template `Future` object.
- Micro-Services may create new events while processing and send them to the Message-Bus.

### Broadcasts:
- Represent global announcements.
- Delivered to all subscribed Micro-Services.

### Round Robin Pattern:
- Events assigned to Micro-Services in a round-robin manner if multiple specialize in the same event.

### Message Loop:
- Implement the Message Loop design pattern.
- Micro-Services run a loop, fetching and processing messages.

### Implementation Details:
- MessageBus: Shared object for communication between Micro-Services.
  - Register/unregister Micro-Services.
  - Subscribe/unsubscribe for events and broadcasts.
  - Send events and broadcasts.
  - Complete events and await messages.
- MicroService: Abstract class for Micro-Services.
  - Registration, Initialization, and Unregistration inside the run method.
  - Execute callbacks inside its own message-loop.

## Part 2 - Compute Resources Management System:
Build a system for managing university compute resources using the Micro-Service framework.

### Objects:
- Student, Data, DataBatch, Model, ConferenceInformation, GPU, CPU, Cluster.
- Statistics: Hold information about trained models, CPU processing, GPU time used.

### Messages:
- TrainModelEvent: Sent by students for model training.
- TestModelEvent: Sent by students for model testing.
- PublishResultsEvent: Sent by students for publishing model results.
- PublishConferenceBroadcast: Sent by conferences for broadcasting aggregated results.
- TickBroadcast: Sent on each tick for timing.

### MicroServices:
- TimeService: Global system timer.
- StudentService: Each student's service for sending events and subscribing to broadcasts.
- GPUService: Each GPU's service for handling TrainModelEvent and TestModelEvent.
- CPUService: Each CPU's service for updating CPU time.
- ConferenceService: Each conference's service for aggregating and publishing results.

### Input File:
- JSON format containing students, GPUs, CPUs, conferences, tick time, and duration.

### Output File:
- Text file containing student details, conference publications, GPU time used, CPU time used, and batches processed.

### Program Execution:
- Parse input, construct the system, and subscribe Micro-Services.
- Students send models for training, testing, and publishing.
- Conferences aggregate and publish results at set times.
- Program finishes execution based on the duration and creates the output file.

## Part 3 - TDD (Test-Driven Development):
Write unit tests for the following classes using Junit:
- MessageBus
- GPU
- CPU
- Future

## Building the Application:
Use Maven as the build tool. The provided pom.xml file includes dependencies.

## Instructions for Unit Tests:
1. Download the interfaces.
2. Extract them.
3. Import Maven Project into your IDE.
4. Add tests in src/test/java.
5. Compile with Maven.
6. Submit your package with all classes.

**GOOD LUCK!!!**
