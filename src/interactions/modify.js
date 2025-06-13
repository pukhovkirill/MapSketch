import {Modify, Select, Translate} from 'ol/interaction.js';
import {source} from '../map/map.js';

export const selectInteraction = new Select();
export const modifyInteraction = new Modify({source});
export const translateInteraction = new Translate({
  features: selectInteraction.getFeatures(),
});

// Function to save the current geometry to the history stack
function saveGeometryToHistory(feature) {
  let history = feature.get('_history');
  if (!history) {
    history = [];
    feature.set('_history', history);
  }

  const geometryClone = feature.getGeometry().clone();
  history.push(geometryClone);
}

// On modification start, save the geometry to history
modifyInteraction.on('modifystart', (e) => {
  e.features.forEach((feature) => {
    // Auto-select if the feature is not already selected
    if (!selectInteraction.getFeatures().getArray().includes(feature)) {
      selectInteraction.getFeatures().push(feature);
    }
    feature.set('isModified', true);
    saveGeometryToHistory(feature);
  });
});

// On translation start, also save the geometry to history
translateInteraction.on('translatestart', (e) => {
  e.features.forEach((feature) => {
    feature.set('isModified', true);
    saveGeometryToHistory(feature);
  });
});

// Undo changes with Ctrl+Z
window.addEventListener('keydown', (e) => {
  const isFormElement = ['INPUT', 'TEXTAREA'].includes(
    document.activeElement.tagName,
  );
  if (isFormElement) {
    return;
  }

  if (e.ctrlKey && e.key === 'z') {
    e.preventDefault();
    selectInteraction.getFeatures().forEach((feature) => {
      const stack = feature.get('_history');
      if (stack && stack.length > 0) {
        const lastGeometry = stack.pop();
        feature.setGeometry(lastGeometry);
      }
      if (stack && stack.length == 0) {
        feature.set('isModified', false);
      }
    });
  }
});
