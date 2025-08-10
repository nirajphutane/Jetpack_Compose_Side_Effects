# Android 🤖
---
## Jetpack Compose Side Effects 🦾
---

### 📌 LaunchedEffect

🟣 LaunchedEffect provides a Compose lifecycle-aware CoroutineScope to run suspend functions or coroutines tied to the composition.
It automatically starts when conditions are met and cancels when the Composable leaves the Composition.

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

🟣 Flow Diagram:
```
                ┌─────────────────────────┐
                │ Composable enters       │
                │ the Composition         │
                └───────────┬─────────────┘
                            │
                            ▼
                ┌─────────────────────────┐
                │ LaunchedEffect starts   │
                │ coroutine in its scope  │
                └───────────┬─────────────┘
                            │
          ┌─────────────────┼─────────────────┐
          ▼                 ▼                 ▼
   (Recomposition)   (Key changes)    (Composable leaves Composition)
  ┌────────────────┐ ┌─────────────┐ ┌────────────────────────────────┐
  │ Key same/none  │ │ New key     │ │ Dispose LaunchedEffect         │
  │ → No restart   │ │ → Cancel &  │ │ → Cancel coroutine scope       │
  │ Coroutine keeps│ │ restart     │ │ → Scope removed from memory    │
  │ running        │ │ coroutine   │ └───────────┬────────────────────┘
  └────────────────┘ └─────────────┘             │
                                                  ▼
                                     ┌────────────────────────┐
                                     │ Composable re-enters   │
                                     │ the Composition        │
                                     └───────────┬────────────┘
                                                 │
                                                 ▼
                                    (Fresh start like initial)

```

---

### 📌 DisposableEffect

🟣 DisposableEffect is a Compose lifecycle-aware side-effect API used for synchronous, non-suspending setup and cleanup logic tied to the composition.
It runs setup code when the Composable enters the Composition and runs cleanup code when the Composable leaves.

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

```
                ┌─────────────────────────┐
                │ Composable enters       │
                │ the Composition         │
                └───────────┬─────────────┘
                            │
                            ▼
                ┌─────────────────────────┐
                │ Run setup code (sync)   │
                └───────────┬─────────────┘
                            │
          ┌─────────────────┼─────────────────┐
          ▼                 ▼                 ▼
   (Recomposition)   (Key changes)    (Composable leaves Composition)
  ┌────────────────┐ ┌─────────────┐ ┌────────────────────────────────┐
  │ Key same/none  │ │ New key     │ │ Run cleanup code               │
  │ → No restart   │ │ → Run       │ │ Release resources, unregister  │
  │ Setup stays    │ │ cleanup,    │ │ listeners, close handles, etc. │
  │ active         │ │ then setup  │ └───────────┬────────────────────┘
  └────────────────┘ └─────────────┘             │
                                                  ▼
                                     ┌────────────────────────┐
                                     │ Composable re-enters   │
                                     │ the Composition        │
                                     └───────────┬────────────┘
                                                 │
                                                 ▼
                                    (Fresh setup like initial)

```

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
### Thanks 🙏🏻

* #### Side Effects

https://developer.android.com/develop/ui/compose/side-effects

https://www.youtube.com/watch?v=gxWcfz3V2QE

https://medium.com/@mortitech/exploring-side-effects-in-compose-f2e8a8da946b

https://proandroiddev.com/mastering-side-effects-in-jetpack-compose-b7ee46162c01

https://medium.com/@manuaravindpta/effect-handlers-in-jetpack-compose-440ac9bd9f1e#:~:text=The%20rememberCoroutineScope%20function%20in%20Jetpack,managed%20by%20the%20Compose%20runtime.

https://medium.com/@rzmeneghelo/side-effects-in-jetpack-compose-a-comprehensive-guide-db1171adc0c4

* #### SideEffect

https://stackoverflow.com/questions/71291835/side-effects-in-jetpack-compose


* #### DerivedStateOf

https://www.youtube.com/watch?v=yJheLbxYd10
https://www.youtube.com/watch?v=a5BzCvVcDnE
https://medium.com/androiddevelopers/jetpack-compose-when-should-i-use-derivedstateof-63ce7954c11b
https://stackoverflow.com/questions/74080243/remember-derivedstateof-or-not


* #### Snapshot Flow

https://www.youtube.com/watch?v=tevua-OwRkE

