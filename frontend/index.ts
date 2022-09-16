import {Flow} from '@vaadin/flow-frontend/Flow';
import {Router} from '@vaadin/router';
import { registerStyles, css } from '@vaadin/vaadin-themable-mixin/register-styles.js';

registerStyles('vaadin-app-layout', css`
  ::-webkit-scrollbar {
    width: 5px;
    background-color: var(--lumo-base-color);
  }

  ::-webkit-scrollbar-thumb {
    background: var(--lumo-primary-color);
  }
`);

registerStyles('vaadin-grid', css`
  ::-webkit-scrollbar {
    width: 5px;
    background-color: var(--lumo-base-color);
  }

  ::-webkit-scrollbar-thumb {
    background: var(--lumo-primary-color);
  }
`);



const {serverSideRoutes} = new Flow({
  imports: () => import('../target/frontend/generated-flow-imports')
});

const routes = [
  // fallback to server-side Flow routes if no client-side routes match
  ...serverSideRoutes
];

const router = new Router(document.querySelector('#outlet'));
router.setRoutes(routes);

//const original = (window as any).Vaadin.Flow.loading;
//(window as any).Vaadin.Flow.loading = (action: boolean) => {
//  if (!action) {
//    document.querySelector('#splash-screen')!.classList.add('loaded');
//    (window as any).Vaadin.Flow.loading = original;
//  }
//};
