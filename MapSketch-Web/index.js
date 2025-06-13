import {initButtons} from './src/ui/buttons.js';
import {initPopup} from './src/ui/popup.js';
import {
  loadFeaturesFromServer,
  setServerDomain,
} from './src/api/geoObjectsService.js';

import './config.js';
import './src/interactions/draw.js';
import './src/interactions/modify.js';
import './src/map/map.js';
import './src/styles/featureStyles.js';
import './src/ui/sidebar.js';

setServerDomain(window.SERVER_HOST);

const preloader = document.getElementById('preloader');
preloader.style.opacity = '1';

loadFeaturesFromServer().finally(() => {
  preloader.style.opacity = '0';
  setTimeout(() => {
    preloader.style.display = 'none';
  }, 300);
});

initPopup();
initButtons();
