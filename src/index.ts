import { registerPlugin } from '@capacitor/core';

import type { HereNavigationPluginPlugin } from './definitions';

const HereNavigationPlugin = registerPlugin<HereNavigationPluginPlugin>('here-navigation', {
  web: () => import('./web').then((m) => new m.HereNavigationPluginWeb()),
});

export * from './definitions';
export { HereNavigationPlugin };
