export interface HereNavigationPluginPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
