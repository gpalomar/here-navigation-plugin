# HERE Navigation Plugin Setup

This plugin provides HERE SDK navigation functionality for Capacitor applications.

## Prerequisites

1. **HERE SDK License**: You must obtain your own HERE SDK for Android from HERE Technologies
2. **HERE API Keys**: You need valid HERE API access keys

## Installation & Setup

### 1. Install the Plugin

```bash
npm install @routal/here-navigation
```

### 2. Add HERE SDK to Your Android Project

**Important**: This plugin uses a `compileOnly` dependency pattern. You must provide the HERE SDK AAR file in your main Android application.

1. Download the HERE SDK for Android (version 4.24.3.0.237319 or compatible) from HERE Developer Portal
2. Place the `heresdk-navigate-android-4.24.3.0.237319.aar` file in your app's Android libs folder:
   ```
   your-app/android/app/libs/heresdk-navigate-android-4.24.3.0.237319.aar
   ```

### 3. Configure Android Build

Add the following to your `android/app/build.gradle`:

```gradle
android {
    // ... existing configuration
}

repositories {
    flatDir {
        dirs 'libs'
    }
}

dependencies {
    // ... existing dependencies
    
    // HERE SDK implementation - Required at runtime
    implementation(name: 'heresdk-navigate-android-4.24.3.0.237319', ext: 'aar')
}
```

### 4. Initialize HERE SDK

In your Android application class or main activity, initialize the HERE SDK with your API keys:

```javascript
import { HereNavigation } from '@routal/here-navigation';

// Initialize HERE SDK
await HereNavigation.initializeSDK({
  accessKeyId: 'your-access-key-id',
  accessKeySecret: 'your-access-key-secret'
});
```

## Architecture Notes

This plugin is designed to be distributed without bundling the proprietary HERE SDK. This approach:

- ✅ Allows plugin distribution without licensing issues
- ✅ Lets users provide their own HERE SDK version
- ✅ Reduces plugin size significantly
- ✅ Maintains compatibility across HERE SDK versions

## Development

If you're developing this plugin:

1. Place the HERE SDK AAR in `android/libs/` for compilation
2. The `.gitignore` prevents the SDK from being committed
3. The build.gradle automatically detects if the SDK is available locally

## Troubleshooting

### Build Errors

If you see compilation errors related to HERE SDK classes:

1. Verify the HERE SDK AAR is in `your-app/android/app/libs/`
2. Check that your app's `build.gradle` includes the HERE SDK dependency
3. Ensure you're using a compatible HERE SDK version

### Runtime Errors

If the app crashes at runtime:

1. Verify HERE SDK initialization in your app
2. Check that your HERE API keys are valid
3. Ensure proper permissions are set in AndroidManifest.xml