# Android 🤖
---
## Jetpack Compose Side Effects 🦾
---

### LaunchedEffect

🟣 LaunchedEffect provides a Compose lifecycle-aware CoroutineScope to run suspend functions or coroutines tied to the composition.
It automatically starts when conditions are met and cancels when the Composable leaves the Composition.

🟣 It is used to run asynchronous code — such as suspend functions and coroutines — safely in Compose without leaking coroutines.
It is not recommended for running purely synchronous code.

🟣 When a Composable function is initially added to the Composition, LaunchedEffect starts its coroutine.
When the Composable leaves the Composition, LaunchedEffect cancels its internal scope and passes the cancellation signal to the coroutine launched inside it.
If you launch an external coroutine from within it, cancellation of that coroutine depends on how you manage the external scope.

🟣 LaunchedEffect helps prevent unnecessary re-initiation and re-execution of asynchronous code due to recomposition.
During recomposition, a new instance of the Composable is created while the old one is removed and eligible for garbage collection.
Without LaunchedEffect, already running asynchronous code could be canceled and restarted unnecessarily, or multiple parallel instances of the same task could be launched.
With LaunchedEffect, coroutine restarts are controlled through its key parameter.

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

