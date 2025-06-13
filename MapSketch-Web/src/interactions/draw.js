import Draw from 'ol/interaction/Draw.js';
import {addToSidebarList} from '../ui/sidebar.js';
import {map, source} from '../map/map.js';

export let drawInteraction = null;

export function clearInteraction() {
  if (drawInteraction === null) {
    return;
  }
  const type = drawInteraction.type_.toLowerCase();
  const btn = document.getElementById('btn-add-' + type);
  btn.classList.toggle('active');
  map.removeInteraction(drawInteraction);
  drawInteraction = null;
}

export function addInteraction(type) {
  // If a draw interaction is already active, disable it
  if (drawInteraction) {
    clearInteraction();
  }

  drawInteraction = new Draw({
    source: null,
    type,
  });
  map.addInteraction(drawInteraction);

  drawInteraction.on('drawend', async (evt) => {
    const feature = evt.feature;
    let name = prompt('Enter a name for the object:');

    if (!name) {
      const r = (Math.random() + 1).toString(36).substring(7);
      name = 'object-' + r;
    }
    feature.set('name', name);
    feature.set('isNewFeature', true);

    const tempId = 'tempId' + Date.now() + Math.floor(Math.random() * 1000);
    feature.set('tempId', tempId);

    const ok = addToSidebarList(tempId, name);
    if (!ok) {
      return;
    }

    source.addFeature(feature);
  });
}

export function removeInteraction() {
  // If a draw interaction is active, disable it
  map.removeInteraction(drawInteraction);
  drawInteraction = null;
}
