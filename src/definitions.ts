export interface HereNavigationPluginPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
  initialize(): Promise<{ success: boolean }>;
  startNavigation(options: { lat: number; lng: number }): Promise<{ success: boolean }>;
  stopNavigation(): Promise<{ success: boolean }>;
  isNavigating(): Promise<{ isNavigating: boolean }>;
  isNavigationActive(): Promise<{ isActive: boolean }>;
  getMapViewInfo(): Promise<{ hasMapView: boolean; isReady: boolean }>;
  showMap(): Promise<{ success: boolean }>;
  hideMap(): Promise<{ success: boolean }>;
  showMapInContainer(options: { x: number; y: number; width: number; height: number }): Promise<{ success: boolean }>;
}
