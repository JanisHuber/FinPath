import { setupWorker } from 'msw/browser';
import { handlers } from './handlers';

// onUnhandledRequest: 'bypass' -> echte Calls gehen durch, wenn kein Mock definiert ist.
export const worker = setupWorker(...handlers);
