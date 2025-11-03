# @routal/here-navigation

Capacitor plugin for HERE SDK Navigation

## Install

```bash
npm install @routal/here-navigation
npx cap sync
```

## API

<docgen-index>

* [`echo(...)`](#echo)
* [`initialize()`](#initialize)
* [`startNavigation(...)`](#startnavigation)
* [`stopNavigation()`](#stopnavigation)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### echo(...)

```typescript
echo(options: { value: string; }) => Promise<{ value: string; }>
```

| Param         | Type                            |
| ------------- | ------------------------------- |
| **`options`** | <code>{ value: string; }</code> |

**Returns:** <code>Promise&lt;{ value: string; }&gt;</code>

--------------------


### initialize()

```typescript
initialize() => Promise<{ success: boolean; }>
```

**Returns:** <code>Promise&lt;{ success: boolean; }&gt;</code>

--------------------


### startNavigation(...)

```typescript
startNavigation(options: { lat: number; lng: number; }) => Promise<{ success: boolean; }>
```

| Param         | Type                                       |
| ------------- | ------------------------------------------ |
| **`options`** | <code>{ lat: number; lng: number; }</code> |

**Returns:** <code>Promise&lt;{ success: boolean; }&gt;</code>

--------------------


### stopNavigation()

```typescript
stopNavigation() => Promise<{ success: boolean; }>
```

**Returns:** <code>Promise&lt;{ success: boolean; }&gt;</code>

--------------------

</docgen-api>
