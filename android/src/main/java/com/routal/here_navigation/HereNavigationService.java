package com.routal.here_navigation;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.app.Activity;
import com.getcapacitor.Logger;
import com.here.sdk.core.engine.AuthenticationMode;
import com.here.sdk.core.engine.SDKOptions;
import com.here.sdk.core.engine.SDKNativeEngine;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.Location;
import com.here.sdk.core.errors.InstantiationErrorException;
import com.here.sdk.mapview.MapMeasure;
import com.here.sdk.mapview.MapView;
import com.here.sdk.mapview.MapError;
import com.here.sdk.mapview.MapScene;
import com.here.sdk.mapview.MapScheme;
import com.here.sdk.navigation.VisualNavigator;
import com.here.sdk.navigation.LocationSimulator;
import com.here.sdk.navigation.LocationSimulatorOptions;
import com.here.sdk.routing.Route;
import com.here.sdk.routing.RoutingEngine;
import com.here.sdk.routing.RoutingError;
import com.here.sdk.routing.CarOptions;
import com.here.sdk.routing.Waypoint;
import com.here.sdk.routing.CalculateRouteCallback;

import java.util.Arrays;
import java.util.List;

public class HereNavigationService {

    private boolean isInitialized = false;
    private VisualNavigator visualNavigator;
    private RoutingEngine routingEngine;
    private Context context;
    private Route currentRoute;
    private LocationSimulator locationSimulator;
    private MapView mapView;
    private FrameLayout mapContainer;

    public HereNavigationService(Context context) {
        this.context = context;
    }

    private void initializeMapView() throws Exception {
        if (!(context instanceof Activity)) {
            throw new Exception("Context must be an Activity for MapView initialization");
        }
        
        Activity activity = (Activity) context;
        
        // Create MapView
        mapView = new MapView(context);
        
        // Create a container for the map
        mapContainer = new FrameLayout(context);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        );
        mapContainer.setLayoutParams(layoutParams);
        mapContainer.addView(mapView);
        
        // Initialize the map
        mapView.onCreate(null);
        mapView.onResume();
        
        // Set map scheme with detailed logging
        Logger.info("HereNavigationService", "Loading map scene with NORMAL_DAY scheme...");
        mapView.getMapScene().loadScene(MapScheme.HYBRID_NIGHT, new MapScene.LoadSceneCallback() {
            @Override
            public void onLoadScene(MapError mapError) {
                if (mapError == null) {
                    Logger.info("HereNavigationService", "✅ Map scene loaded successfully - tiles should be visible");
                    
                    // Set initial camera position to a known location for testing
                    GeoCoordinates berlinCenter = new GeoCoordinates(52.520008, 13.404954);
                    MapMeasure distance = new MapMeasure(MapMeasure.Kind.DISTANCE_IN_METERS,1000);
                    mapView.getCamera().lookAt(berlinCenter, distance); // 1km distance
                    Logger.info("HereNavigationService", "Camera positioned at Berlin for testing");
                } else {
                    Exception mapException = new Exception("Map scene loading failed: " + mapError.toString());
                    Logger.error("HereNavigationService", "❌ CRITICAL: Failed to load map scene - this causes white map!", mapException);
                    Logger.warn("HereNavigationService", "Map Error Details: " + mapError.toString());
                    Logger.warn("HereNavigationService", "Error Type: " + mapError.name());
                    
                    // Check for specific authentication errors
                    if (mapError.toString().contains("AUTHENTICATION")) {
                        Logger.warn("HereNavigationService", "🔑 AUTHENTICATION ERROR: HERE SDK credentials invalid for map tiles");
                        Logger.warn("HereNavigationService", "Solution: Verify HERE API key has Map API access enabled");
                    } else if (mapError.toString().contains("NETWORK")) {
                        Logger.warn("HereNavigationService", "🌐 NETWORK ERROR: Cannot download map tiles");
                        Logger.warn("HereNavigationService", "Check internet connection and firewall settings");
                    } else {
                        Logger.warn("HereNavigationService", "🔧 MAP LOADING ERROR: " + mapError.toString());
                    }
                }
            }
        });
    }

    public String echo(String value) {
        Logger.info("Echo", value);
        return value;
    }
    
    // Diagnostic method to test map tile loading
    public void testMapTileLoading() {
        if (mapView == null) {
            Logger.warn("HereNavigationService", "MapView not initialized for tile testing");
            return;
        }
        
        Logger.info("HereNavigationService", "=== TESTING MAP TILE LOADING ===");
        
        // Test different map schemes
        MapScheme[] schemes = {
            MapScheme.NORMAL_DAY,
            MapScheme.NORMAL_NIGHT,
            MapScheme.SATELLITE
        };
        
        for (MapScheme scheme : schemes) {
            Logger.info("HereNavigationService", "Testing scheme: " + scheme.toString());
            mapView.getMapScene().loadScene(scheme, new MapScene.LoadSceneCallback() {
                @Override
                public void onLoadScene(MapError mapError) {
                    if (mapError == null) {
                        Logger.info("HereNavigationService", "✅ Scheme " + scheme + " loaded successfully");
                    } else {
                        Logger.warn("HereNavigationService", "❌ Scheme " + scheme + " failed: " + mapError.toString());
                    }
                }
            });
        }
    }

    public boolean initialize(String accessKeyId, String accessKeySecret) {
        try {
            Logger.info("HereNavigationService", "Initializing HERE Navigation Service");
            
            // Initialize HERE SDK if not already initialized
            if (SDKNativeEngine.getSharedInstance() == null) {
                Logger.info("HereNavigationService", "Initializing HERE SDK with provided credentials");
                
                try {
                    AuthenticationMode authenticationMode = 
                        AuthenticationMode.withKeySecret(accessKeyId, accessKeySecret);
                    SDKOptions options = 
                        new SDKOptions(authenticationMode);
                    
                    SDKNativeEngine.makeSharedInstance(context, options);
                    Logger.info("HereNavigationService", "✅ HERE SDK initialized successfully");
                } catch (InstantiationErrorException e) {
                    Logger.error("HereNavigationService", "❌ HERE SDK initialization failed: " + e.error.name(), e);
                    return false;
                }
            } else {
                Logger.info("HereNavigationService", "HERE SDK already initialized - using existing instance");
            }
            
            // Initialize the navigation components
            return initializeComponents();
        } catch (Exception e) {
            Logger.error("HereNavigationService", "Initialization failed", e);
            return false;
        }
    }
    
    private boolean initializeComponents() {
        try {
            Logger.info("HereNavigationService", "Initializing HERE Navigation components");
            
            // Initialize routing engine
            try {
                routingEngine = new RoutingEngine();
                Logger.info("HereNavigationService", "RoutingEngine initialized");
            } catch (InstantiationErrorException e) {
                Logger.error("HereNavigationService", "Failed to initialize RoutingEngine", e);
                return false;
            }
            
            // Initialize MapView
            try {
                initializeMapView();
                Logger.info("HereNavigationService", "MapView initialized");
            } catch (Exception e) {
                Logger.error("HereNavigationService", "Failed to initialize MapView", e);
                return false;
            }

            // Initialize visual navigator
            try {
                visualNavigator = new VisualNavigator();
                Logger.info("HereNavigationService", "VisualNavigator initialized");
                isInitialized = true;
            } catch (InstantiationErrorException e) {
                Logger.error("HereNavigationService", "Failed to initialize VisualNavigator", e);
                return false;
            }
            
            Logger.info("HereNavigationService", "HERE Navigation Service components initialized successfully");
            return true;
        } catch (Exception e) {
            Logger.error("HereNavigationService", "Component initialization failed", e);
            return false;
        }
    }

    public boolean startNavigation(double lat, double lng) {
        try {
            Logger.info("HereNavigationService", "Starting navigation to: " + lat + ", " + lng);
            
            if (!isInitialized) {
                Logger.warn("HereNavigationService", "Service not initialized");
                return false;
            }

            // Create destination coordinates
            GeoCoordinates destination = new GeoCoordinates(lat, lng);
            
            // For this example, we'll use a fixed starting point (you might want to get current location)
            // In a real app, you would get the current location from LocationEngine
            GeoCoordinates startingPoint = new GeoCoordinates(52.520008, 13.404954); // Berlin example
            
            // Create waypoints
            Waypoint startWaypoint = new Waypoint(startingPoint);
            Waypoint destinationWaypoint = new Waypoint(destination);
            List<Waypoint> waypoints = Arrays.asList(startWaypoint, destinationWaypoint);
            
            // Calculate route
            CarOptions carOptions = new CarOptions();
            
            Logger.info("HereNavigationService", "Calculating route from Berlin (" + startingPoint.latitude + ", " + startingPoint.longitude + ") to destination: " + lat + ", " + lng);
            Logger.info("HereNavigationService", "SDK initialized successfully but routing authentication may still fail");
            Logger.info("HereNavigationService", "Attempting route calculation...");
            
            routingEngine.calculateRoute(waypoints, carOptions, new CalculateRouteCallback() {
                @Override
                public void onRouteCalculated(RoutingError routingError, List<Route> routes) {
                    if (routingError == null && !routes.isEmpty()) {
                        currentRoute = routes.get(0);
                        Logger.info("HereNavigationService", "Route calculated successfully");
                        
                        // Route calculated successfully - store it for navigation
                        Logger.info("HereNavigationService", "Route calculated with " + currentRoute.getLengthInMeters() + " meters");
                        Logger.info("HereNavigationService", "Estimated travel time: " + currentRoute.getDuration().getSeconds() + " seconds");
                        
                        // Start actual navigation
                        try {
                            // Set up location simulator for navigation
                            LocationSimulatorOptions locationSimulatorOptions = new LocationSimulatorOptions();
                            locationSimulatorOptions.speedFactor = 2; // 2x speed for testing
                            locationSimulator = new LocationSimulator(currentRoute, locationSimulatorOptions);
                            
                            // Start location simulation
                            locationSimulator.start();
                            
                            // Set the route for the visual navigator
                            visualNavigator.setRoute(currentRoute);
                            
                            // Start map rendering with navigation
                            if (mapView != null) {
                                visualNavigator.startRendering(mapView);
                                
                                // Center map on route with appropriate zoom
                                MapMeasure distance = new MapMeasure(MapMeasure.Kind.DISTANCE_IN_METERS,5000);
                                mapView.getCamera().lookAt(startingPoint, distance); // 5km distance for better overview
                                Logger.info("HereNavigationService", "Camera positioned at starting point: " + startingPoint.latitude + ", " + startingPoint.longitude);
                                
                                // Show the map overlay
                                showMapView();
                                
                                Logger.info("HereNavigationService", "Navigation started successfully with map rendering and overlay shown");
                            } else {
                                Logger.warn("HereNavigationService", "MapView not available - navigation started without rendering");
                            }
                            
                        } catch (Exception navException) {
                            Logger.error("HereNavigationService", "Failed to start navigation guidance", navException);
                            // Still consider route calculation as successful even if guidance fails
                        }
                    } else {
                        String errorMsg = routingError != null ? routingError.toString() : "No routes found";
                        Logger.warn("HereNavigationService", "Route calculation failed: " + errorMsg);
                        
                        // Check for specific authentication errors
                        if (routingError != null) {
                            Logger.warn("HereNavigationService", "Full routing error details: " + routingError.toString());
                            
                            if (errorMsg.contains("AUTHENTICATION_FAILED")) {
                                Logger.warn("HereNavigationService", "=== AUTHENTICATION ANALYSIS ===");
                                Logger.warn("HereNavigationService", "SDK initialized successfully BUT routing authentication failed");
                                Logger.warn("HereNavigationService", "This suggests:");
                                Logger.warn("HereNavigationService", "1. Credentials may be demo/trial keys with limited routing access");
                                Logger.warn("HereNavigationService", "2. Network connectivity issues during routing API call");
                                Logger.warn("HereNavigationService", "3. Credentials may need routing services enabled in HERE dashboard");
                                Logger.warn("HereNavigationService", "4. Possible firewall/proxy blocking HERE routing endpoints");
                                Logger.warn("HereNavigationService", "");
                                Logger.warn("HereNavigationService", "SOLUTION: Get production HERE SDK credentials with routing enabled");
                                Logger.warn("HereNavigationService", "Visit: https://developer.here.com > Create Project > Generate SDK Keys");
                            } else if (errorMsg.contains("NETWORK_ERROR")) {
                                Logger.warn("HereNavigationService", "Network error - check internet connection and firewall settings");
                            } else if (errorMsg.contains("INVALID_CREDENTIALS")) {
                                Logger.warn("HereNavigationService", "Invalid credentials - HERE SDK API keys are incorrect");
                            } else {
                                Logger.warn("HereNavigationService", "Unknown routing error - check HERE SDK documentation");
                            }
                        }
                        
                        // For testing purposes, you could implement a mock route here
                        Logger.info("HereNavigationService", "Consider implementing offline/mock navigation for testing");
                    }
                }
            });
            
            return true;
        } catch (Exception e) {
            Logger.error("HereNavigationService", "Failed to start navigation", e);
            return false;
        }
    }

    public boolean stopNavigation() {
        try {
            Logger.info("HereNavigationService", "Stopping navigation");
            
            if (!isInitialized) {
                Logger.warn("HereNavigationService", "Service not initialized");
                return true; // Consider this successful as there's nothing to stop
            }

            // Stop location simulation if running
            if (locationSimulator != null) {
                locationSimulator.stop();
                locationSimulator = null;
                Logger.info("HereNavigationService", "Location simulation stopped");
            }
            
            // Stop visual navigator rendering
            if (visualNavigator != null) {
                try {
                    visualNavigator.stopRendering();
                    visualNavigator.setRoute(null);
                    Logger.info("HereNavigationService", "Visual navigator rendering stopped and route cleared");
                } catch (Exception e) {
                    Logger.warn("HereNavigationService", "Error stopping visual navigator: " + e.getMessage());
                }
            }
            
            // Clear the current route and navigation state
            currentRoute = null;
            Logger.info("HereNavigationService", "Navigation session ended - route cleared");
            
            return true;
        } catch (Exception e) {
            Logger.error("HereNavigationService", "Failed to stop navigation", e);
            return false;
        }
    }
    
    // Additional helper methods
    
    public boolean isNavigating() {
        return isInitialized && visualNavigator != null && currentRoute != null;
    }
    
    public boolean isInitialized() {
        return isInitialized;
    }
    
    public Route getCurrentRoute() {
        return currentRoute;
    }
    
    // Method to add navigation listeners
    // public void addNavigationListeners() {
    //     if (visualNavigator == null) return;
        
    //     // Add destination reached listener
    //     visualNavigator.setDestinationReachedListener(() -> {
    //         Logger.info("HereNavigationService", "Destination reached!");
    //         // Automatically stop navigation when destination is reached
    //         stopNavigation();
    //     });
        
    //     // Add milestone reached listener
    //     visualNavigator.setMilestoneReachedListener((milestone) -> {
    //         Logger.info("HereNavigationService", "Milestone reached: " + milestone.toString());
    //     });
        
    //     // Add route progress listener
    //     visualNavigator.setRouteProgressListener((routeProgress) -> {
    //         int remainingDistanceInMeters = routeProgress.remainingDistanceInMeters;
    //         int remainingTimeInSeconds = routeProgress.remainingDurationInSeconds;
    //         Logger.info("HereNavigationService", 
    //             "Route progress - Remaining: " + remainingDistanceInMeters + "m, " + remainingTimeInSeconds + "s");
    //     });
        
    //     Logger.info("HereNavigationService", "Navigation listeners added");
    // }
    
    // Method to check if navigation is active (including location simulation)
    public boolean isNavigationActive() {
        return isNavigating() && locationSimulator != null;
    }
    
    // Get the MapView for integration with UI
    public MapView getMapView() {
        return mapView;
    }
    
    // Get the map container for adding to layouts
    public FrameLayout getMapContainer() {
        return mapContainer;
    }
    
    // MapView lifecycle methods
    public void onPause() {
        if (mapView != null) {
            mapView.onPause();
        }
    }
    
    public void onResume() {
        if (mapView != null) {
            mapView.onResume();
        }
    }
    
    // Show the MapView by adding it to the activity's content view
    public boolean showMapView() {
        try {
            if (mapContainer == null || !(context instanceof Activity)) {
                Logger.warn("HereNavigationService", "MapView not available or context is not Activity");
                return false;
            }
            
            Activity activity = (Activity) context;
            
            // Add the map container to the activity's root view
            FrameLayout rootView = activity.findViewById(android.R.id.content);
            if (rootView != null) {
                // Remove from any existing parent first
                if (mapContainer.getParent() != null) {
                    ((FrameLayout) mapContainer.getParent()).removeView(mapContainer);
                }
                
                // Add to the root view
                rootView.addView(mapContainer);
                mapContainer.setVisibility(View.VISIBLE);
                
                Logger.info("HereNavigationService", "MapView shown successfully");
                return true;
            } else {
                Logger.warn("HereNavigationService", "Could not find root view to attach map");
                return false;
            }
        } catch (Exception e) {
            Logger.error("HereNavigationService", "Failed to show MapView", e);
            return false;
        }
    }
    
    // Hide the MapView by removing it from the activity
    public boolean hideMapView() {
        try {
            if (mapContainer == null) {
                Logger.warn("HereNavigationService", "MapView not available");
                return false;
            }
            
            // Remove from parent if it has one
            if (mapContainer.getParent() != null) {
                ((FrameLayout) mapContainer.getParent()).removeView(mapContainer);
                Logger.info("HereNavigationService", "MapView hidden successfully");
            }
            
            return true;
        } catch (Exception e) {
            Logger.error("HereNavigationService", "Failed to hide MapView", e);
            return false;
        }
    }
    
    // Show the MapView within specific bounds (to fit within React container)
    public boolean showMapViewInBounds(int x, int y, int width, int height) {
        try {
            Logger.info("HereNavigationService", "=== SHOW MAP IN BOUNDS DEBUG ===");
            Logger.info("HereNavigationService", "Requested bounds: x=" + x + ", y=" + y + ", w=" + width + ", h=" + height);
            
            if (mapContainer == null) {
                Logger.warn("HereNavigationService", "❌ mapContainer is null!");
                return false;
            }
            
            if (!(context instanceof Activity)) {
                Logger.warn("HereNavigationService", "❌ Context is not Activity: " + context.getClass().getSimpleName());
                return false;
            }
            
            if (mapView == null) {
                Logger.warn("HereNavigationService", "❌ mapView is null!");
                return false;
            }
            
            Logger.info("HereNavigationService", "✅ All components available");
            
            Activity activity = (Activity) context;
            
            // Remove from any existing parent first
            if (mapContainer.getParent() != null) {
                Logger.info("HereNavigationService", "Removing from existing parent");
                ((FrameLayout) mapContainer.getParent()).removeView(mapContainer);
            }
            
            // Set specific layout parameters for positioning
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);
            layoutParams.leftMargin = x;
            layoutParams.topMargin = y;
            mapContainer.setLayoutParams(layoutParams);
            
            Logger.info("HereNavigationService", "Layout params set");
            
            // Add to the activity's root view
            FrameLayout rootView = activity.findViewById(android.R.id.content);
            if (rootView != null) {
                Logger.info("HereNavigationService", "Adding to root view");
                rootView.addView(mapContainer);
                mapContainer.setVisibility(View.VISIBLE);
                
                Logger.info("HereNavigationService", "✅ MapView shown successfully in bounds");
                Logger.info("HereNavigationService", "MapContainer visibility: " + (mapContainer.getVisibility() == View.VISIBLE ? "VISIBLE" : "HIDDEN"));
                Logger.info("HereNavigationService", "MapView child count: " + mapContainer.getChildCount());
                
                return true;
            } else {
                Logger.warn("HereNavigationService", "❌ Could not find root view (android.R.id.content)");
                return false;
            }
        } catch (Exception e) {
            Logger.error("HereNavigationService", "❌ Exception in showMapViewInBounds", e);
            return false;
        }
    }
    
    public void onDestroy() {
        // Clean up resources
        stopNavigation();
        
        if (mapView != null) {
            mapView.onDestroy();
            mapView = null;
        }
        
        if (mapContainer != null) {
            mapContainer.removeAllViews();
            mapContainer = null;
        }
        
        Logger.info("HereNavigationService", "Service destroyed and resources cleaned up");
    }
}
