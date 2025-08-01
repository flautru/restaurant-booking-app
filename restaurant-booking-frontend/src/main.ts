import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { App } from './app/app';
import { akitaDevtools } from '@datorama/akita';

bootstrapApplication(App, appConfig).catch((err) => console.error(err));

akitaDevtools();
