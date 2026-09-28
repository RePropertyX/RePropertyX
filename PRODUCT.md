# Product

<!-- impeccable:product-schema 1 -->

## Platform

web

## Users

Kotlin and Android developers who build reactive applications, manage persistence (SharedPreferences), implement UI animations, or coordinate Jetpack Compose state and need clean, boilerplate-free property delegation.

## Product Purpose

RePropertyX provides a composable, reactive property delegation toolkit for Kotlin and Android. It transforms standard property definitions (\`by\`) into declarative pipelines with operators like \`.map()\`, \`.distinctUntilChanged()\`, \`.animated()\`, \`.cached()\`, and \`.withHistory()\`. Success means developers write fewer manual getters, setters, and synchronizers while maintaining safety and testability.

## Positioning

Unlike traditional static delegate helpers or heavy monolithic state libraries, RePropertyX treats property delegation as a bi-directional value pipeline (Read \`getValue\` & Write \`setValue\`) that feels native to idiomatic Kotlin syntax.

## Operating Context

- Target environments: Android (API 21+), Jetpack Compose, Kotlin JVM.
- Build tools: Gradle (Kotlin DSL), Maven Central / JitPack.
- Standard IDEs: Android Studio and IntelliJ IDEA.

## Capabilities and Constraints

- Core delegate primitives: \`propertyOf()\`, \`mutablePropertyOf()\`, \`byAtomic()\`, \`byThreadLocal()\`.
- Pipeline operators: \`.map()\`, \`.distinctUntilChanged()\`, \`.onEach()\`, \`.orElse()\`, \`.validate()\`, \`.cached()\`.
- Storage integration: SharedPreferences delegates with reactive change listeners and batch editor scopes.
- UI & Animation: Android View property animation (\`.animated()\`, \`animatedFloatAwait()\`) and Compose state bridging (\`rememberPropertyState()\`).
- Zero reflection overhead for core delegates, zero ANR risk, and full testability.

## Brand Commitments

- Name: RePropertyX.
- Logo: Bracket delegation symbol with JetBrains IDE dark theme and signature Kotlin orange accent (\`#FF9800\`).
- Voice: Technical, clean, precise, and developer-focused.

## Evidence on Hand

- Verified unit test suite with 100% passing tests for delegates and operators.
- Published releases on JitPack (v1.1.0).
- Working Android demo application and Dokka-generated API documentation.

## Product Principles

1. **Kotlin-First & Idiomatic**: Leverage \`ReadWriteProperty\` and \`provideDelegate\` so client code feels like standard Kotlin.
2. **Bi-Directional Transparency**: Clean separation of read and write pipelines without hidden side-effects.
3. **Zero Boilerplate**: Eliminate repetitive getter/setter, animator, and storage synchronization logic.
4. **Composability**: Every operator returns a composable delegate that can be chained or adapted.
