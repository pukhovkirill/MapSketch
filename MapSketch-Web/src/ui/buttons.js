/* eslint-disable no-console */
import {
  addInteraction,
  clearInteraction,
  removeInteraction,
} from '../interactions/draw.js';

import {map, source} from '../map/map.js';
import {
  modifyInteraction,
  selectInteraction,
  translateInteraction,
} from '../interactions/modify.js';
import {overlay, popupElement} from './popup.js';
import {saveFeaturesToServer} from '../api/geoObjectsService.js';

export function initButtons() {
  // Disable modify interaction if active
  function disableModify() {
    const btn = document.getElementById('btn-modify');
    const active = btn.classList.contains('active');
    if (active) {
      btn.classList.toggle('active');
      map.removeInteraction(modifyInteraction);
      map.removeInteraction(translateInteraction);
    }
  }

  document.getElementById('btn-add-point').addEventListener('click', () => {
    const btn = document.getElementById('btn-add-point');
    const active = btn.classList.toggle('active');
    if (active) {
      disableModify();
      addInteraction('Point');
    } else {
      removeInteraction();
    }
  });

  document
    .getElementById('btn-add-linestring')
    .addEventListener('click', () => {
      const btn = document.getElementById('btn-add-linestring');
      const active = btn.classList.toggle('active');
      if (active) {
        disableModify();
        addInteraction('LineString');
      } else {
        removeInteraction();
      }
    });

  document.getElementById('btn-add-polygon').addEventListener('click', () => {
    const btn = document.getElementById('btn-add-polygon');
    const active = btn.classList.toggle('active');
    if (active) {
      disableModify();
      addInteraction('Polygon');
    } else {
      removeInteraction();
    }
  });

  document.getElementById('btn-save').addEventListener('click', async () => {
    disableModify();
    const newFeatures = source
      .getFeatures()
      .filter((f) => f.get('isNewFeature') || f.get('isModified'));
    if (newFeatures.length === 0) {
      alert('No new features to save.');
      return;
    }

    try {
      await saveFeaturesToServer(newFeatures);
      alert('New features saved successfully.');
    } catch (err) {
      console.error('Bulk save error:', err);
      alert('Failed to save new features.');
    }
  });

  // Toggle modify interactions
  document.getElementById('btn-modify').addEventListener('click', () => {
    const btn = document.getElementById('btn-modify');
    const active = btn.classList.toggle('active');
    if (active) {
      clearInteraction();
      map.addInteraction(modifyInteraction);
      map.addInteraction(translateInteraction);
    } else {
      map.removeInteraction(modifyInteraction);
      map.removeInteraction(translateInteraction);
    }
  });

  // Cancel interactions on Escape key
  window.addEventListener('keyup', (e) => {
    if (e.key === 'Escape') {
      disableModify();
      clearInteraction();
    }
  });

  // Deselect on map click (empty space) and hide popup
  map.on('click', (evt) => {
    if (!map.hasFeatureAtPixel(evt.pixel)) {
      selectInteraction.getFeatures().clear();
      overlay.setPosition(undefined);
      popupElement.style.display = 'none';
    }
  });
}
