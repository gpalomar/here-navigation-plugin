# @routal/here-navigation

Capacitor plugin for HERE SDK Navigation

## Install

```bash
npm install @routal/here-navigation
npx cap sync
```

## Setup

**Important**: This plugin requires additional setup steps to provide the HERE SDK. See [SETUP.md](./SETUP.md) for detailed instructions.

### Quick Setup Summary

1. Obtain HERE SDK for Android from HERE Technologies
2. Place `heresdk-navigate-android-4.24.3.0.237319.aar` in `android/app/libs/`
3. Add HERE SDK dependency to your app's `build.gradle`
4. Initialize with your HERE API keys

For complete setup instructions, see [SETUP.md](./SETUP.md).

## API

<docgen-index>

* [`echo(...)`](#echo)
* [`initialize(...)`](#initialize)
* [`startNavigation(...)`](#startnavigation)
* [`stopNavigation()`](#stopnavigation)
* [`isNavigating()`](#isnavigating)
* [`isNavigationActive()`](#isnavigationactive)
* [`getMapViewInfo()`](#getmapviewinfo)
* [`showMap()`](#showmap)
* [`hideMap()`](#hidemap)
* [`showMapInContainer(...)`](#showmapincontainer)

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


### initialize(...)

```typescript
initialize(options: { accessKeyId: string; accessKeySecret: string; }) => Promise<{ success: boolean; }>
```

| Param         | Type                                                           |
| ------------- | -------------------------------------------------------------- |
| **`options`** | <code>{ accessKeyId: string; accessKeySecret: string; }</code> |

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


### isNavigating()

```typescript
isNavigating() => Promise<{ isNavigating: boolean; }>
```

**Returns:** <code>Promise&lt;{ isNavigating: boolean; }&gt;</code>

--------------------


### isNavigationActive()

```typescript
isNavigationActive() => Promise<{ isActive: boolean; }>
```

**Returns:** <code>Promise&lt;{ isActive: boolean; }&gt;</code>

--------------------


### getMapViewInfo()

```typescript
getMapViewInfo() => Promise<{ hasMapView: boolean; isReady: boolean; }>
```

**Returns:** <code>Promise&lt;{ hasMapView: boolean; isReady: boolean; }&gt;</code>

--------------------


### showMap()

```typescript
showMap() => Promise<{ success: boolean; }>
```

**Returns:** <code>Promise&lt;{ success: boolean; }&gt;</code>

--------------------


### hideMap()

```typescript
hideMap() => Promise<{ success: boolean; }>
```

**Returns:** <code>Promise&lt;{ success: boolean; }&gt;</code>

--------------------


### showMapInContainer(...)

```typescript
showMapInContainer(options: { x: number; y: number; width: number; height: number; }) => Promise<{ success: boolean; }>
```

| Param         | Type                                                                  |
| ------------- | --------------------------------------------------------------------- |
| **`options`** | <code>{ x: number; y: number; width: number; height: number; }</code> |

**Returns:** <code>Promise&lt;{ success: boolean; }&gt;</code>

--------------------

</docgen-api>
