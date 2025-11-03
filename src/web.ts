import { WebPlugin } from '@capacitor/core';

import type { HereNavigationPluginPlugin } from './definitions';

export class HereNavigationPluginWeb extends WebPlugin implements HereNavigationPluginPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
