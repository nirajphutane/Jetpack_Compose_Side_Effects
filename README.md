# Android 🤖
---
## Jetpack Compose Side Effects 🦾
---

### 📌 LaunchedEffect

🟣 LaunchedEffect provides a Compose lifecycle-aware CoroutineScope to run suspend functions or coroutines tied to the composition.
It uses Dispatchers.Main.immediate by default, which runs coroutines on the UI thread and executes immediately.
It implicitly starts when the Composable enters the Composition and cancels when the Composable leaves the Composition. No need to handle it explicitly.

🟣 It is used to run asynchronous code — such as suspend functions and coroutines — safely in Compose without leaking coroutines.
It is not recommended for running purely synchronous code.

🟣 When a Composable function is initially added to the Composition, LaunchedEffect starts its coroutine.
When the Composable leaves the Composition, LaunchedEffect cancels its internal scope and passes the cancellation signal to the coroutine launched inside it.
If you launch an external coroutine from within it, cancellation of that coroutine depends on how you manage the external scope.

🟣 LaunchedEffect helps prevent unnecessary re-initiation and re-execution of asynchronous code due to recomposition.
During recomposition, if the state changes, a new instance of the Composable is created while the old one is removed and becomes eligible for garbage collection.
Without LaunchedEffect, already running asynchronous code could be canceled and restarted unnecessarily, or multiple parallel instances of the same task could be launched.
With LaunchedEffect, if the key does not change, the same LaunchedEffect instance is retained and prevents the new coroutine launch. The restart behavior of LaunchedEffect is controlled through its optional key parameter.

🟣 LaunchedEffect accepts an optional key parameter:
  - Without a key, or with the same constant key → runs once when the Composable is added to the Composition and will not restart on recomposition.
  - With a different key value → immediately cancels the current coroutine scope, passes the cancellation to the coroutine inside it, and restarts from scratch with the new key. This restart is triggered by key change, not by recomposition alone.

🟣 If the Composable leaves the Composition and re-enters, LaunchedEffect will start fresh even if the key is the same, constant, or absent. In this case, the key does not affect restart behavior.

---

### 📌 DisposableEffect

🟣 DisposableEffect is a Compose lifecycle-aware side-effect API used for synchronous, non-suspending setup and cleanup logic tied to the composition.
It runs setup code implicitly when the Composable enters the Composition and runs cleanup code when the Composable leaves. No need to handle it explicitly.

🟣 It is used to run synchronous code with side effects with an onDispose callback when the Composable leaves the Composition.
It is not recommended to launch any coroutine in this block.

🟣 When a Composable function is initially added to the Composition, DisposableEffect starts.
When the Composable leaves the Composition, DisposableEffect calls onDispose {} lambda to run mostly the cleanup logic.

🟣 DisposableEffect helps prevent unnecessary re-initiation and re-execution of synchronous code due to recomposition.
During recomposition, if the state changes, a new instance of the Composable is created while the old one is removed and becomes eligible for garbage collection.
Without DisposableEffect, already running synchronous code could be canceled and restarted unnecessarily.
During recomposition, if the key does not change, the same DisposableEffect instance is retained and prevents the  setup/cleanup repetition. The restart behavior of DisposableEffect is controlled through its optional key parameter.

🟣 DisposableEffect accepts an optional key parameter:
  - Without a key, or with the same constant key → runs once when the Composable is added to the Composition and will not restart on recomposition.
  - With a different key value → immediately cancels the current run, and restarts from scratch with the new key. This restart is triggered by key change, not by recomposition alone.

🟣 If the Composable leaves the Composition and re-enters, DisposableEffect will start fresh even if the key is the same, constant, or absent. In this case, the key does not affect restart behavior.

---

### 📌 rememberUpdatedState

🟣 In Jetpack Compose, we often build a parent–child Composable hierarchy.

🟣 If a Child Composable requires a lambda function callback or a value parameter, the Parent Composable passes it down as an argument.

🟣 Generally, when these values or callbacks are used synchronously inside the Child Composable, there is no visible side effect.

🟣 However, if these arguments are used asynchronously inside a side-effect block such as LaunchedEffect or rememberCoroutineScope, something different happens:
  - On the initial composition, the side-effect block captures the current instance of the lambda or value parameter.
  - If the Parent Composable recomposes later, then the old one is removed and becomes eligible for garbage collection and it will create a new instance of the lambda or a new value for the parameter and pass it to the Child Composable.
  - But the asynchronous side-effect block inside the Child Composable still holds the old captured instance from the first composition.
  - As a result:
    - If the lambda’s implementation changes in the parent, the side-effect block will still call the old implementation.
    - If the variable’s value changes in the parent, the side-effect block will still see the old value.
  - This leads to unexpected behavior because the side-effect block is not automatically updated with the new references or values.

🟣 To prevent this problem, we use rememberUpdatedState.
  - rememberUpdatedState wraps a value or lambda and ensures the latest version is always available inside side-effect blocks.
  - Even if the side-effect block was created during the initial composition, it will read the updated reference from rememberUpdatedState on every use.
  - This guarantees that any asynchronous block always works with the most recent value or lambda passed from the parent.

---

### 📌 rememberCoroutineScope

🟣 rememberCoroutineScope is a Compose lifecycle-aware CoroutineScope to run suspend functions or coroutines tied to the Composition and confined to the UI thread’s dispatcher by default.

🟣 But unlike LaunchedEffect, it will not start implicitly when the Composable enters the Composition. It is created but not started until launch is explicitly called. It will, however, automatically cancel when the Composable leaves the Composition.

🟣 LaunchedEffect is a Composable function that runs immediately during composition, but normal callbacks from Compose UI components are non-Composable. Calling LaunchedEffect from such callbacks will cause a compile-time error.

🟣 On the other hand, rememberCoroutineScope() is declared inside a Composable during composition and returns a CoroutineScope instance that can be used later. This allows you to safely launch coroutines in non-Composable callbacks while still being lifecycle-aware. So, rememberCoroutineScope is typically used for handling events like gesture listeners.

🟣 When to use:
  - If you need to start a coroutine automatically during composition, use LaunchedEffect.
  - If you need to launch a coroutine later in response to a non-Composable event or callback, use rememberCoroutineScope, because it’s the only option to start a Compose lifecycle-aware coroutine outside the composition phase.

```

@Composable
fun MyComposable() {
    val scope = rememberCoroutineScope()

    Button(onClick = {
        scope.launch {
            println("🔥 Coroutine started")
            delay(3000)
            println("✅ Coroutine finished")
        }
    }) {
        Text("Start Work")
    }
}

```

🟣 Here, when the button's onClick is invoked, the instance from rememberCoroutineScope() launches a coroutine that is lifecycle-aware with respect to the Composable.

🟣 This coroutine will complete naturally—either by finishing its execution, by throwing a runtime exception, or by being automatically cancelled when the Composable leaves the Composition.

🟣 Also, if the callback is invoked multiple times, then each invocation launches a new coroutine. The existing coroutines will continue to run independently, and multiple coroutines may run concurrently or in parallel (depending on the dispatcher).

🟣 To prevent this behavior (i.e., multiple concurrent coroutines), scope.launch {} returns an ordinary Job instance, which can be stored. You can cancel the currently running job before launching a new one, as shown below:

```

@Composable
fun MyComposable() {
    val scope = rememberCoroutineScope()
    var job: Job? = null

    Button(onClick = {
        job?.cancel() // Cancel the previous coroutine if it's still running
        job = scope.launch {
            println("🔥 Coroutine started")
            delay(3000)
            println("✅ Coroutine finished")
        }
    }) {
        Text("Start Work")
    }
}


```

---

### 📌 ProduceState

🟣 ProduceState is a lifecycle-aware side-effect function in Compose that launches a coroutine to update the state and returns a read-only State<T> object, whose value is updated asynchronously and can be read synchronously within Composables. It uses Dispatchers.Main.immediate by default, which runs the coroutine on the UI thread and starts execution immediately, ensuring it interacts with the UI without waiting.

🟣 When a Composable function is initially added to the Composition, produceState exhibits eager behavior — it starts producing values immediately, even before the state is read. As a result, there’s a potential for missing initial or intermediate values if there’s a delay in reading the state, because Compose retains only the latest value. When the Composable leaves the Composition, the coroutine is canceled and stops producing values. This helps prevent unnecessary operations when the UI element is no longer visible.

🟣 ProduceState acts as a bridge for asynchronous work directly in Composables. It allows suspend functions, flows, or other asynchronous data sources to be used in Composables without the need for a ViewModel. This makes it easier to handle asynchronous operations directly in the UI layer.

🟣 ProduceState returns a read-only state object, so the value can be read synchronously or asynchronously, but you cannot update the value directly from outside the coroutine. The value can only be updated from within the coroutine using this.value inside the produceState block.

🟣 ProduceState accepts an optional key parameter:
  - Without a key, or with the same constant key → runs once when the Composable is added to the Composition and will not restart on recomposition.
  - With a different key value → immediately cancels the current coroutine and restarts from scratch with the new key. The state is reset to the initial value, and the coroutine starts fresh, producing values from the beginning. This restart is triggered by key change, not by recomposition alone.

---

### 📌 snapshotFlow

🟣 snapshotFlow is a Jetpack Compose API that converts a snapshot-aware value (such as one created by remember { mutableStateOf(...) }) into a cold Kotlin Flow.

🟣 It is distinct in nature because it emits only when the observed Compose state value actually changes.

🟣 It works only inside a CoroutineScope.

🟣 It is primarily used to observe state changes inside Compose — particularly gesture-driven, scroll-based, or user input events in the UI layer.

🟣 snapshotFlow is safe from triggering due to normal recompositions when used inside proper side-effect blocks like LaunchedEffect or produceState. However, it will restart and be re-collected if the parent side-effect is restarted due to key changes.

🟣 When combined with produceState, like this:
```
val index by produceState(initialValue = 0) {
    snapshotFlow { scrollState.value }
        .collect { value = it }
}
```
…it behaves like a reactive State backed by a Flow.

🟣 snapshotFlow is the only Compose-native reactive Flow utility for observing snapshot state changes such as:
  - Scroll position
  - Input field changes
  - Gesture progress
  - Animation frame changes

🟣 It is not recommended for business logic because:
  - snapshotFlow is re-created every time the surrounding side-effect restarts (not strictly on every recomposition unless the side-effect restarts)
  - It is tied to the UI lifecycle only
  - It observes Compose snapshot reads (state values), not Flows or repositories
  - It is not connected to repositories, databases, or app-wide state flows

---

### 📌 derivedStateOf

🟣 When a computation is derived based on a state in a Composable, it will be executed and the result will be consumed immediately when the Composable enters the composition.
Also, when the state changes, the calculation should be re-performed. This is an expected behavior and completely normal.

🟣 But, a Composable can recompose unexpectedly, and due to recomposition, the calculation will be re-executed even if the state has not changed.
Due to this unnecessary recalculation, a new instance of the result may be created even if its value is the same as previous, which causes the Composable to be re-rendered unnecessarily.

🟣 Leads to Unnecessary Recompositions

```
@Composable
fun MyScreen(userList: List<User>) {
    val sortedList = userList.sortedBy { it.name } // Recomputed on every recomposition!

    LazyColumn {
        items(sortedList) { user ->
            Text(user.name)
        }
    }
}
```
What’s wrong?
  - Every time MyScreen recomposes, userList.sortedBy { it.name } is recomputed.
  - Even if userList hasn't changed, a new instance of sortedList is created.
  - Compose detects the new object reference, assumes it’s different, and triggers unnecessary recompositions.

```
@Composable
fun MyScreen() {
    var count by remember { mutableStateOf(1) }

    // Recomputed on every recomposition
    val start = ((count - 1) / 10) * 10 + 1
    val end = start + 9
    val rangeLabel = "Range $start–$end"

    Column {
        Text("Count: $count")
        Text(rangeLabel)

        Button(onClick = { count++ }) {
            Text("Increase")
        }
    }
}
```
What’s the issue?
  - Every time you tap Increase, count changes → recomposition is triggered.
  - The range is recalculated every time: val rangeLabel = ...
  - Even when count goes from 2 → 3 → 4 (still within 1–10), the range label stays the same.
  - But Compose sees a new String object each time — still "Range 1–10", but newly allocated — so it unnecessarily re-renders the Text.

🟣 For good performance, when state changes, only the Composables whose derived result has changed should re-render. Otherwise, it wastes resources and can hurt performance.

🟣 derivedStateOf is a Compose State utility that returns a State<T> whose value is derived from one or more other State objects.

🟣 When the Composable enters the Composition, the calculation runs and the result is consumed immediately. When the Composable leaves the Composition, it’s no longer active and becomes eligible for Garbage Collection (GC).

🟣 It emits a new state only when the derived state changes and the new value is different from the previous one.

🟣 When will not emit the new state:
  - If the derived value has not changed, it does not emit a new state.
  - Even if the input state changes, but the computed result is the same as the previous one, it won’t trigger a recomposition.
  - This prevents unnecessary recalculation and avoids re-rendering with identical results — saving CPU cycles, memory, and avoiding flickers or UI lag.

```
@Composable
fun MyScreen(userList: List<User>) {
    val sortedList by remember(userList) {
        derivedStateOf { userList.sortedBy { it.name } }
    }

    LazyColumn {
        items(sortedList) { user ->
            Text(user.name)
        }
    }
}
```
Here:
  - derivedStateOf ensures sorting happens only if userList actually changes.
  - If the sorted list is structurally the same as before, Compose skips recomposing the children.

```
@Composable
fun MyScreen() {
    var count by remember { mutableStateOf(1) }

    val rangeLabel by remember {
        derivedStateOf {
            val start = ((count - 1) / 10) * 10 + 1
            val end = start + 9
            "Range $start–$end"
        }
    }

    Column {
        Text("Count: $count")
        Text(rangeLabel)

        Button(onClick = { count++ }) {
            Text("Increase")
        }
    }
}
```
Here:
  - derivedStateOf caches the computed rangeLabel.
  - It recomputes only if the final value changes.
  - If count increases within the same range (e.g., 2 → 3 → 4), it returns the same string → Text skips recomposition.
  - When count jumps from 10 → 11, the label changes to "Range 11–20" → recomposition occurs.
  - Even though this example is simple, the pattern is general: you derive a value from other State objects, and Compose only re-renders when the derived value changes.

🟣 Performance note:
  - derivedStateOf is not free — it adds extra State observation and dependency tracking in Compose.
  - Use it when:
     - The computation is expensive (measure time and space complexity).
     - You want to minimize re-rendering when only the derived value changes — especially when the amount of UI being recomposed is large, as this can improve performance.

---

### 📌 SideEffect

🟣 SideEffect is a lifecycle-aware, synchronous block that runs on the main/UI thread after a composition or recomposition has completed — unlike other side-effect blocks that execute immediately when the composable enters the composition.

🟣 Think of it as a post-composition notifier: it does nothing during the composition phase itself but is guaranteed to run after the UI tree is successfully committed.

🟣 Registration & Execution Order:
  - When a composable enters the composition, Compose first runs the composition phase (building the UI tree, executing child composables from top to bottom).
  - When the SideEffect line is reached, Compose does not execute it immediately — it registers it.
  - Once all children have finished composing successfully, Compose enters the apply changes phase and then executes all registered SideEffect blocks.
  - Execution order is bottom-to-top in the composition tree (deepest child’s effect first, then its parent, then grandparent, etc.) because effects are tied to the UI node lifecycle.

🟣 It does not prevent recomposition — it only runs after recomposition. If no recomposition occurs, SideEffect does not run.

🟣 What SideEffect is NOT:
  - A way to skip UI rendering.
  - A performance optimization by itself.
  - A place for heavy work or suspend functions.

🟣 Think of SideEffect like a feedback loop after UI building:
  - SideEffect = “Run this small, main-thread action after Compose has successfully committed the UI.”
  - Build UI → queue effects → after everything is built → flush effects deepest-to-topmost.

```
@Composable
fun Parent() {
    SideEffect { println("Parent SideEffect") }
    Child()
}

@Composable
fun Child() {
    SideEffect { println("Child SideEffect") }
}

Output:
Child SideEffect
Parent SideEffect
```

---
