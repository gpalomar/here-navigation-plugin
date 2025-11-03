package com.routal.here_navigation;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "here-navigation")
public class HereNavigationPluginPlugin extends Plugin {

    private HereNavigationService implementation;

    @Override
    public void load() {
        // Initialize the service with the activity context
        implementation = new HereNavigationService(getActivity());
    }

    @Override
    protected void handleOnPause() {
        super.handleOnPause();
        if (implementation != null) {
            implementation.onPause();
        }
    }

    @Override
    protected void handleOnResume() {
        super.handleOnResume();
        if (implementation != null) {
            implementation.onResume();
        }
    }

    @Override
    protected void handleOnDestroy() {
        if (implementation != null) {
            implementation.onDestroy();
        }
        super.handleOnDestroy();
    }

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", implementation.echo(value));
        call.resolve(ret);
    }

    @PluginMethod
    public void initialize(PluginCall call) {
        try {
            boolean success = implementation.initialize();
            JSObject ret = new JSObject();
            ret.put("success", success);
            call.resolve(ret);
        } catch (Exception e) {
            call.reject("Initialization failed", e);
        }
    }

    @PluginMethod
    public void startNavigation(PluginCall call) {
        try {
            double lat = call.getDouble("lat", 0.0);
            double lng = call.getDouble("lng", 0.0);
            
            boolean success = implementation.startNavigation(lat, lng);
            JSObject ret = new JSObject();
            ret.put("success", success);
            call.resolve(ret);
        } catch (Exception e) {
            call.reject("Navigation start failed", e);
        }
    }

    @PluginMethod
    public void stopNavigation(PluginCall call) {
        try {
            boolean success = implementation.stopNavigation();
            JSObject ret = new JSObject();
            ret.put("success", success);
            call.resolve(ret);
        } catch (Exception e) {
            call.reject("Navigation stop failed", e);
        }
    }

    @PluginMethod
    public void isNavigating(PluginCall call) {
        try {
            boolean isNavigating = implementation.isNavigating();
            JSObject ret = new JSObject();
            ret.put("isNavigating", isNavigating);
            call.resolve(ret);
        } catch (Exception e) {
            call.reject("Failed to check navigation status", e);
        }
    }

    @PluginMethod
    public void isNavigationActive(PluginCall call) {
        try {
            boolean isActive = implementation.isNavigationActive();
            JSObject ret = new JSObject();
            ret.put("isActive", isActive);
            call.resolve(ret);
        } catch (Exception e) {
            call.reject("Failed to check navigation activity", e);
        }
    }

    @PluginMethod
    public void getMapViewInfo(PluginCall call) {
        try {
            boolean hasMapView = implementation.getMapView() != null;
            boolean isReady = hasMapView && implementation.isInitialized();
            
            JSObject ret = new JSObject();
            ret.put("hasMapView", hasMapView);
            ret.put("isReady", isReady);
            call.resolve(ret);
        } catch (Exception e) {
            call.reject("Failed to get MapView info", e);
        }
    }

    @PluginMethod
    public void showMap(PluginCall call) {
        try {
            getActivity().runOnUiThread(() -> {
                try {
                    boolean success = implementation.showMapView();
                    JSObject ret = new JSObject();
                    ret.put("success", success);
                    call.resolve(ret);
                } catch (Exception e) {
                    call.reject("Failed to show map", e);
                }
            });
        } catch (Exception e) {
            call.reject("Failed to show map", e);
        }
    }

    @PluginMethod
    public void hideMap(PluginCall call) {
        try {
            getActivity().runOnUiThread(() -> {
                try {
                    boolean success = implementation.hideMapView();
                    JSObject ret = new JSObject();
                    ret.put("success", success);
                    call.resolve(ret);
                } catch (Exception e) {
                    call.reject("Failed to hide map", e);
                }
            });
        } catch (Exception e) {
            call.reject("Failed to hide map", e);
        }
    }

    @PluginMethod
    public void showMapInContainer(PluginCall call) {
        try {
            int x = call.getInt("x", 0);
            int y = call.getInt("y", 0);
            int width = call.getInt("width", 0);
            int height = call.getInt("height", 0);
            
            getActivity().runOnUiThread(() -> {
                try {
                    boolean success = implementation.showMapViewInBounds(x, y, width, height);
                    JSObject ret = new JSObject();
                    ret.put("success", success);
                    call.resolve(ret);
                } catch (Exception e) {
                    call.reject("Failed to show map in container", e);
                }
            });
        } catch (Exception e) {
            call.reject("Failed to show map in container", e);
        }
    }
}
