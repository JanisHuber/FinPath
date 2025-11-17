import { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'ch.finpath.app',
  appName: 'FinGuide',
  webDir: 'dist/finpath-frontend-mobile/browser', // bleibt für Bundle-Builds
  server: {
    url: 'http://10.50.204.26:4200', // ⟵ deine LAN-IP + Port
    cleartext: true                  // http erlauben (Dev)
  }
};

export default config;
