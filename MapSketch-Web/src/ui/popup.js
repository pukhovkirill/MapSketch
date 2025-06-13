/* eslint-disable no-console */
import Overlay from 'ol/Overlay.js';
import {deleteFeatureFromServer} from '../api/geoObjectsService.js';
import {map, source} from '../map/map.js';
import {removeFromSidebarList} from './sidebar.js';
import {selectInteraction} from '../interactions/modify.js';
import {toLonLat} from 'ol/proj.js';

export const popupElement = document.getElementById('popup');
export const overlay = new Overlay({
  element: popupElement,
  offset: [0, -15],
  positioning: 'bottom-center',
});

export let popupFeature = null;

export function initPopup() {
  map.addOverlay(overlay);
  map.addInteraction(selectInteraction);

  selectInteraction.on('select', (evt) => {
    const selected = evt.selected;
    if (selected.length > 0) {
      const feature = selected[0];
      popupFeature = feature;

      const geometry = feature.getGeometry();
      let coords;

      if (geometry.getType() === 'Point') {
        coords = geometry.getFirstCoordinate();
      } else {
        coords =
          geometry.getType() === 'LineString'
            ? geometry.getFirstCoordinate()
            : geometry.getInteriorPoint().getCoordinates();
      }

      popupFeature = feature.getProperties().features[0];

      // Convert screen coordinates (EPSG:3857) to lon/lat (EPSG:4326)
      const [lon, lat] = toLonLat(coords);

      popupElement.querySelector('#popup-title').textContent =
        popupFeature.get('name') || '(no title)';

      popupElement.querySelector('#popup-obj-lon').textContent =
        'longitude: ' + lon.toFixed(6);

      popupElement.querySelector('#popup-obj-lat').textContent =
        'latitude: ' + lat.toFixed(6);

      overlay.setPosition(coords);
      popupElement.style.display = 'block';
    } else {
      popupFeature = null;
      overlay.setPosition(undefined);
      popupElement.style.display = 'none';
    }
  });

  document.getElementById('btn-delete').addEventListener('click', async () => {
    if (!popupFeature) {
      return;
    }
    try {
      await deleteFeatureFromServer(popupFeature);
      source.removeFeature(popupFeature);
      let id = popupFeature.get('id');
      id = id === undefined ? popupFeature.get('tempId') : id;
      removeFromSidebarList(id);
      popupFeature = null;
      overlay.setPosition(undefined);
      popupElement.style.display = 'none';
    } catch (err) {
      console.error('Error deleting feature:', err);
      alert('Failed to delete feature on server.');
    }
  });
}
